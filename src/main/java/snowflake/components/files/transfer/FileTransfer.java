package snowflake.components.files.transfer;

import snowflake.common.*;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.utils.PathUtils;
import snowflake.utils.SudoUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileTransfer implements Runnable, AutoCloseable {
    private FileSystem sourceFs, targetFs;
    //private ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private FileInfo[] files;
    private String sourceFolder, targetFolder;
    private BlockingQueue<ByteChunk> dataQueue = new ArrayBlockingQueue<>(10);
    //private ExecutorService runningThread = Executors.newSingleThreadExecutor();
    private long totalSize;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);

    private FileTransferProgress callback;
    private long processedBytes;
    private int processedFilesCount;
    private long totalFiles;
    private int conflictOnOverwrite = -1; // 0 -> overwrite, 1 -> auto rename, 2 -> skip

    public FileTransfer(FileSystem sourceFs,
                        FileSystem targetFs,
                        FileInfo[] files,
                        String sourceFolder,
                        String targetFolder,
                        FileTransferProgress callback,
                        int defaultConflictAction) {
        this.sourceFs = sourceFs;
        this.targetFs = targetFs;
        this.files = files;
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
        this.callback = callback;
        this.conflictOnOverwrite = defaultConflictAction;
    }

    private void transfer(String targetFolder) throws Exception {
        System.out.println("Copying to " + targetFolder);
        List<FileInfoHolder> fileList = new ArrayList<>();
        List<FileInfo> list = targetFs.list(targetFolder);
        List<FileInfo> dupList = new ArrayList<>();

        if (this.conflictOnOverwrite == -1) {
            this.conflictOnOverwrite = checkForConflict(dupList);
        }

        if (this.conflictOnOverwrite == -1) {
            return;
        }

        if(!sourceFs.isConnected()){
            sourceFs.connect();
        }
        if(!targetFs.isConnected()){
            targetFs.connect();
        }

        totalSize = 0;
        for (FileInfo file : files) {
            if (stopFlag.get()) {
                return;
            }

            String proposedName = null;
            if (isDuplicate(list, file.getName())) {
                if (conflictOnOverwrite == 1) {
                    proposedName = generateNewName(list, file.getName());
                    System.out.println("new name: " + proposedName);
                } else if (conflictOnOverwrite == 3) {
                    continue;
                }
            }

            if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
                fileList.addAll(createFileList(file, targetFolder, proposedName));
            } else {
                fileList.add(new FileInfoHolder(file, targetFolder, proposedName));
                totalSize += file.getSize();
            }
        }
        totalFiles = fileList.size();

        callback.init(totalSize, totalFiles, this);
        try (InputTransferChannel inc = sourceFs.inputTransferChannel();
             OutputTransferChannel outc = targetFs.outputTransferChannel()) {
            for (FileInfoHolder file : fileList) {
                System.out.println("Copying: " + file.info.getPath());
                if (stopFlag.get()) {
                    System.out.println("Operation cancelled by user");
                    return;
                }
                copyFile(file.info, file.targetPath, file.proposedName, inc, outc);
                System.out.println("Copying done: " + file.info.getPath());
                processedFilesCount++;
            }
        }
    }


    public void run() {
        try {
            try {
                transfer(this.targetFolder);
                callback.done(this);
            } catch (AccessDeniedException e) {
                if (targetFs instanceof SshFileSystem) {
                    if (JOptionPane.showConfirmDialog(null,
                            "Permission denied, do you want to copy files to a temporary folder first and copy them to destination with sudo?",
                            "Insufficient permission", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        throw e;
                    }
                    String tmpDir = "/tmp/" + UUID.randomUUID();
                    targetFs.mkdir(tmpDir);
                    transfer(tmpDir);
                    String command = "sh -c  \"cd '" + tmpDir + "'; cp -r * '" + this.targetFolder + "'\"";
                    //String command = "sh -c      cp -r \"" + tmpDir + "/*\" \"" + this.targetFolder + "\"";
                    System.out.println("Invoke sudo: " + command);
                    int ret = SudoUtils.runSudo(command.toString(), ((SshFileSystem) targetFs).getWrapper());
                    if (ret == 0) {
                        callback.done(this);
                        return;
                    }
                    throw e;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (stopFlag.get()) {
                System.out.println("Operation cancelled by user");
                callback.done(this);
                return;
            }
            callback.error("Error", this);
            return;
        }
    }

    private List<FileInfoHolder> createFileList(FileInfo folder, String target, String proposedName) throws Exception {
        if (stopFlag.get()) {
            throw new Exception("Interrupted");
        }
        String folderTarget = PathUtils.combineUnix(target, proposedName == null ? folder.getName() : proposedName);
        targetFs.mkdir(folderTarget);
        List<FileInfoHolder> fileInfoHolders = new ArrayList<>();
        List<FileInfo> list = sourceFs.list(folder.getPath());
        for (FileInfo file : list) {
            if (stopFlag.get()) {
                throw new Exception("Interrupted");
            }
            if (file.getType() == FileType.Directory) {
                fileInfoHolders.addAll(createFileList(file, folderTarget, null));
            } else if (file.getType() == FileType.File) {
                fileInfoHolders.add(new FileInfoHolder(file, folderTarget, null));
                totalSize += file.getSize();
            }
        }
        System.out.println("File list created");
        return fileInfoHolders;
    }

    private synchronized void copyFile(FileInfo file,
                                       String targetDirectory, String proposedName,
                                       InputTransferChannel inc,
                                       OutputTransferChannel outc) throws Exception {
        byte buf[] = new byte[8192];
        String outPath = PathUtils.combine(targetDirectory, proposedName == null ? file.getName() : proposedName, outc.getSeparator());
        String inPath = file.getPath();
        System.out.println("Copying -- " + inPath + " to " + outPath);
        try (InputStream in = inc.getInputStream(inPath);
             OutputStream out = outc.getOutputStream(outPath)) {
            long len = inc.getSize(inPath);
            while (len > 0 && !stopFlag.get()) {
                int x = in.read(buf);
                if (x == -1) throw new IOException("Unexpected EOF");
                out.write(buf, 0, x);
                len -= x;
                processedBytes += x;
                callback.progress(processedBytes, totalSize, processedFilesCount, totalFiles, this);
            }
        }


//        final CountDownLatch countDownLatch = new CountDownLatch(2);
//        AtomicBoolean error = new AtomicBoolean(false);
//        threadPool.submit(() -> {
//            try {
//                readData(file.getPath(), file.getSize());
//            } catch (Exception e) {
//                e.printStackTrace();
//                error.set(true);
//                threadPool.shutdownNow();
//            } finally {
//                countDownLatch.countDown();
//            }
//        });
//
//        threadPool.submit(() -> {
//            try {
//                writeData(PathUtils.combineUnix(targetDirectory, file.getName()), file.getSize());
//            } catch (Exception e) {
//                e.printStackTrace();
//                error.set(true);
//                threadPool.shutdownNow();
//            } finally {
//                countDownLatch.countDown();
//            }
//        });
//
//        try {
//
//            countDownLatch.await();
//        } catch (Exception e) {
//            e.printStackTrace();
//            threadPool.shutdownNow();
//        }
    }

    private void readData(String file, long size) throws Exception {
        try (InputStream in = sourceFs.getInputStream(file, 0)) {
            long len = size;
            while (len > 0 && !stopFlag.get()) {
                byte[] buf = new byte[8192];
                int x = in.read(buf);
                if (x == -1) throw new IOException("Unexpected EOF");
                dataQueue.put(new ByteChunk(buf, x));
                len -= x;
            }
        }
    }

    private void writeData(String file, long size) throws Exception {
        try (OutputStream out = targetFs.getOutputStream(file)) {
            long len = size;
            while (len > 0 && !stopFlag.get()) {
                ByteChunk chunk = dataQueue.take();
                out.write(chunk.getBuf(), 0, (int) chunk.getLen());
                len -= chunk.getLen();
                processedBytes += chunk.getLen();
                callback.progress(processedBytes, totalSize, processedFilesCount, totalFiles, this);
            }
        }
    }

    public void stop() {
        stopFlag.set(true);
        //runningThread.shutdownNow();
        //threadPool.shutdownNow();
    }

    @Override
    public void close() {
        try {
            this.sourceFs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.targetFs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class FileInfoHolder {
        FileInfo info;
        String targetPath;
        String proposedName;

        public FileInfoHolder(FileInfo info, String targetPath, String proposedName) {
            this.info = info;
            this.targetPath = targetPath;
            this.proposedName = proposedName;
        }
    }

    public FileInfo[] getFiles() {
        return files;
    }

    public String getTargetFolder() {
        return this.targetFolder;
    }

    private int checkForConflict(List<FileInfo> dupList) throws Exception {
        List<FileInfo> fileList = targetFs.list(targetFolder);
        for (FileInfo file : files) {
            for (FileInfo file1 : fileList) {
                if (file.getName().equals(file1.getName())) {
                    dupList.add(file);
                }
            }
        }

        int action = 0;
        if (dupList.size() > 0) {
            JComboBox<String> cmbs = new JComboBox<>(new String[]{"Overwrite", "Auto rename", "Skip"});
            if (JOptionPane.showOptionDialog(null, new Object[]{"Some file with the same name already exists. Please choose an action",
                    cmbs}, "Action required", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                action = cmbs.getSelectedIndex();
            } else {
                return -1;
            }
        }

        return action;
    }

    private boolean isDuplicate(List<FileInfo> list, String name) {
        for (FileInfo s : list) {
            System.out.println("Checking for duplicate: " + s.getName() + " --- " + name);
            if (s.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        System.out.println("Not duplicate: " + name);
        return false;
    }

    public String generateNewName(List<FileInfo> list, String name) {
        while (isDuplicate(list, name)) {
            name = "Copy-of-" + name;
        }
        return name;
    }

    public String getSourceName() {
        return this.sourceFs.getName();
    }

    public String getTargetName() {
        return this.targetFs.getName();
    }

    public void setCallback(FileTransferProgress callback) {
        this.callback = callback;
    }

    public FileSystem getSourceFs() {
        return sourceFs;
    }

    public FileSystem getTargetFs() {
        return targetFs;
    }


}
