package muon.service;

import muon.dto.file.FileInfo;
import muon.exceptions.FSConnectException;
import muon.util.AppUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SftpUploadTask {
    private List<FileTransferItem> transferItems;
    private String localFolder;
    private List<FileInfo> localFiles;
    private String remoteFolder;
    private List<FileInfo> remoteFiles;
    private boolean dirTreeCreated;
    private List<String> dirItems;
    private boolean init;
    private SftpSession sftp;
    private int dirIndex = 0, fileIndex = 0;
    private long totalBytes = 0;
    private long transferredBytes = 0;
    private int lastProgress = 0;

    public SftpUploadTask(SftpSession sftp,
                          String localFolder,
                          List<FileInfo> localFiles,
                          String remoteFolder,
                          List<FileInfo> remoteFiles) {
        this.sftp = sftp;
        this.localFolder = localFolder;
        this.localFiles = localFiles;
        this.remoteFolder = remoteFolder;
        this.remoteFiles = remoteFiles;
    }

    public SftpSession getSftpSession() {
        return sftp;
    }

    public boolean isConnected() {
        return this.sftp.isConnected();
    }

    public void connect(InputBlocker inputBlocker) throws FSConnectException {
        var passwordUserAuthFactory = new GuiUserAuthFactory(this.sftp.getSessionInfo());
        var callback = new SshCallback(this.sftp.getSessionInfo());
        this.sftp.connect(callback, passwordUserAuthFactory);
    }

    public void start(Consumer<Integer> progress) throws IOException {
        if (!init) {
            init();
            init = true;
        }

        if (!dirTreeCreated) {
            for (var i = dirIndex; i < dirItems.size(); i++) {
                var path = dirItems.get(i);
                this.sftp.mkdir(path);
                dirIndex = i;
            }
            dirTreeCreated = true;
        }

        for (var i = fileIndex; i < transferItems.size(); i++) {
            var transferItem = transferItems.get(i);
            copy(transferItem, progress);
            fileIndex = i;
        }
    }

    public String getRemoteFolder() {
        return remoteFolder;
    }

    public List<FileInfo> getRemoteFiles() {
        return remoteFiles;
    }

    public String getLocalFolder() {
        return localFolder;
    }

    public List<FileInfo> getLocalFiles() {
        return localFiles;
    }

    private void init() throws IOException {
        transferItems = new ArrayList<>();
        dirItems = new ArrayList<>();
        for (var info :
                localFiles) {
            traverseLocal(info.getName(), localFolder, remoteFolder);
        }
    }

    private void traverseLocal(String name, String localPath, String remotePath) throws IOException {
        var p1 = Paths.get(localPath, name).toAbsolutePath();
        var p2 = Paths.get(remotePath, name).toAbsolutePath();
        System.out.println(p1);
        System.out.println(p2);
        if (Files.isRegularFile(p1)) {
            var ft = new FileTransferItem();
            ft.sourcePath = p1.toString();
            ft.targetPath = p2.toString();
            var attrs = Files.readAttributes(p1, BasicFileAttributes.class);
            var size = attrs.size();
            var mtime = AppUtils.getModificationTime(attrs).toEpochSecond(ZoneOffset.UTC);
            ft.size = size;
            ft.modificationTime = mtime;
            transferItems.add(ft);
            totalBytes += size;
            return;
        }

        if (Files.isDirectory(p1)) {
            var path = p2.toString().replace('\\', '/');
            dirItems.add(path);
            for (var filePath :
                    Files.list(p1).toList()) {
                traverseLocal(filePath.getFileName().toString(), p1.toString(), p2.toString());
            }
        }
    }

    private void copy(FileTransferItem item, Consumer<Integer> progress) throws IOException {
        byte[] b = new byte[8192];
        var c = item.transferred;
        try (var fis = new RandomAccessFile(item.sourcePath, "r");
             var fos = c > 0 ?
                     sftp.appendOutputStream(item.targetPath) :
                     sftp.createOutputStream(item.targetPath)) {
            if (c > 0) {
                fis.seek(c);
            }
            while (true) {
                var x = fis.read(b);
                if (x == -1) break;
                fos.write(b, 0, x);
                c += x;
                item.transferred = c;
                transferredBytes += x;
                if (totalBytes > 0) {
                    var prg = (int) (transferredBytes * 100.0 / totalBytes);
                    if (prg > lastProgress) {
                        lastProgress = prg;
                        progress.accept(prg);
                    }
                }
            }
        }
    }

    class FileTransferItem {
        String sourcePath;
        String targetPath;
        boolean complete;
        long size;
        long modificationTime;
        long transferred;
    }
}
