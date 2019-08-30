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

public class FileTransfer {
    private FileSystem sourceFs, targetFs;
    //private ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private FileInfo[] files;
    private String sourceFolder, targetFolder;
    private BlockingQueue<ByteChunk> dataQueue = new ArrayBlockingQueue<ByteChunk>(10);
    private ExecutorService runningThread = Executors.newSingleThreadExecutor();
    private long totalSize;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private FileTransferProgress callback;
    private long processedBytes;
    private int processedFilesCount;
    private long totalFiles;

    public FileTransfer(FileSystem sourceFs,
                        FileSystem targetFs,
                        FileInfo[] files,
                        String sourceFolder,
                        String targetFolder,
                        FileTransferProgress callback) {
        this.sourceFs = sourceFs;
        this.targetFs = targetFs;
        this.files = files;
        this.sourceFolder = sourceFolder;
        this.targetFolder = targetFolder;
        this.callback = callback;
    }

    private void transfer(String targetFolder) throws Exception {
        System.out.println("Copying to " + targetFolder);
        List<FileInfoHolder> fileList = new ArrayList<>();
        totalSize = 0;
        for (FileInfo file : files) {
            if (stopFlag.get()) {
                return;
            }
            if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
                fileList.addAll(createFileList(file, targetFolder));
            } else {
                fileList.add(new FileInfoHolder(file, targetFolder));
                totalSize += file.getSize();
            }
        }
        totalFiles = fileList.size();
        callback.init(sourceFs.getName(), targetFs.getName(), totalSize, totalFiles);
        try (InputTransferChannel inc = sourceFs.inputTransferChannel();
             OutputTransferChannel outc = targetFs.outputTransferChannel()) {
            for (FileInfoHolder file : fileList) {
                System.out.println("Copying: " + file.info.getPath());
                if (stopFlag.get()) {
                    System.out.println("Operation cancelled by user");
                    return;
                }
                copyFile(file.info, file.targetPath, inc, outc);
                System.out.println("Copying done: " + file.info.getPath());
                processedFilesCount++;
            }
        }
    }

    public void start() {
        runningThread.submit(() -> {
            try {
                try {
                    transfer(this.targetFolder);
                } catch (AccessDeniedException e) {
                    if (targetFs instanceof SshFileSystem) {
                        if (JOptionPane.showConfirmDialog(null, "Permission denied, do you want to copy files to a temporary folder first and copy them to destination with sudo?", "Insufficient permission", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                            throw e;
                        }
                        String tmpDir = "/tmp/" + UUID.randomUUID();
                        targetFs.mkdir(tmpDir);
                        transfer(tmpDir);
                        String command = "sh -c  \"cd '" + tmpDir + "'; cp -r * '" + this.targetFolder + "'\"";
                        //String command = "sh -c      cp -r \"" + tmpDir + "/*\" \"" + this.targetFolder + "\"";
                        System.out.println("Invoke sudo: " + command);
                        int ret = SudoUtils.runSudo(command.toString(), ((SshFileSystem) targetFs).getWrapper());
                        if (ret == -1) {
                            callback.error("Error");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (stopFlag.get()) {
                    System.out.println("Operation cancelled by user");
                    callback.done();
                    return;
                }
                callback.error("Error");
                return;
            } finally {
                callback.done();
                System.out.println("Copying done total ");
            }
        });
    }

    private List<FileInfoHolder> createFileList(FileInfo folder, String target) throws Exception {
        if (stopFlag.get()) {
            throw new Exception("Interrupted");
        }
        String folderTarget = PathUtils.combineUnix(target, folder.getName());
        targetFs.mkdir(folderTarget);
        List<FileInfoHolder> fileInfoHolders = new ArrayList<>();
        List<FileInfo> list = sourceFs.list(folder.getPath());
        for (FileInfo file : list) {
            if (stopFlag.get()) {
                throw new Exception("Interrupted");
            }
            if (file.getType() == FileType.Directory) {
                fileInfoHolders.addAll(createFileList(file, folderTarget));
            } else if (file.getType() == FileType.File) {
                fileInfoHolders.add(new FileInfoHolder(file, folderTarget));
                totalSize += file.getSize();
            }
        }
        System.out.println("File list created");
        return fileInfoHolders;
    }

    private synchronized void copyFile(FileInfo file,
                                       String targetDirectory,
                                       InputTransferChannel inc,
                                       OutputTransferChannel outc) throws Exception {
        byte buf[] = new byte[8192];
        String outPath = PathUtils.combine(targetDirectory, file.getName(), outc.getSeparator());
        String inPath = file.getPath();
        System.out.println("Copying -- " + inPath + " to " + outPath);
        try (InputStream in = inc.getInputStream(inPath);
             OutputStream out = outc.getOutputStream(outPath)) {
            long len = inc.getSize(inPath);
            while (len > 0 && !stopFlag.get()) {
                int x = in.read(buf);
                if (x == -1) throw new IOException("Unexpected EOF");
                out.write(buf, 0, x);
                Thread.sleep(0);
                len -= x;
                processedBytes += x;
                callback.progress(processedBytes, totalSize, processedFilesCount, totalFiles);
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
                callback.progress(processedBytes, totalSize, processedFilesCount, totalFiles);
            }
        }
    }

    public void stop() {
        stopFlag.set(true);
        runningThread.shutdownNow();
        //threadPool.shutdownNow();
    }


    static class FileInfoHolder {
        FileInfo info;
        String targetPath;

        public FileInfoHolder(FileInfo info, String targetPath) {
            this.info = info;
            this.targetPath = targetPath;
        }
    }

    public FileInfo[] getFiles() {
        return files;
    }

    public String getTargetFolder() {
        return this.targetFolder;
    }


}
