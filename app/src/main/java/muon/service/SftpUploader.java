package muon.service;

import muon.dto.file.FileInfo;
import muon.util.AppUtils;
import muon.util.PathUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class SftpUploader {
    private List<FileTransferItem> transferItems;
    private boolean dirTreeCreated;
    private List<String> dirItems;
    private boolean init;
    private SftpFileSystem sftp;
    private int dirIndex = 0, fileIndex = 0;

    public SftpUploader(SftpFileSystem sftp) {
        this.sftp = sftp;
    }

    public void uploadInForeground(
            String localFolder,
            List<FileInfo> localFiles,
            String remoteFolder,
            List<FileInfo> remoteFiles) throws IOException {
        if (!init) {
            transferItems = new ArrayList<>();
            dirItems = new ArrayList<>();
            for (var info :
                    localFiles) {
                traverse(info.getName(), localFolder, remoteFolder);
            }
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
            transfer(transferItem);
            fileIndex = i;
        }
    }

//    private void traverse(String localPath, String remoteDir) throws IOException {
//        var p1 = Paths.get(localPath).toAbsolutePath();
//        var p2 = Paths.get(remoteDir, p1.getFileName().toString()).toAbsolutePath();
//        System.out.println(p1);
//        System.out.println(p2);
//        if (Files.isRegularFile(p1)) {
//            var ft = new FileTransferItem();
//            ft.sourcePath = p1.toString();
//            ft.targetPath = p2.toString();
//            var attrs = Files.readAttributes(p1, BasicFileAttributes.class);
//            var size = attrs.size();
//            var mtime = AppUtils.getModificationTime(attrs).toEpochSecond(ZoneOffset.UTC);
//            ft.size = size;
//            ft.modificationTime = mtime;
//            transferItems.add(ft);
//            return;
//        }
//
//        if (Files.isDirectory(p1)) {
//            var path = p2.toString().replace('\\', '/');
//            dirItems.add(path);
//            for (var filePath :
//                    Files.list(p1).toList()) {
//                traverse(filePath.getFileName().toString(), p1.toString(), p2.toString());
//            }
//        }
//    }


    private void traverse(String name, String localPath, String remotePath) throws IOException {
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
            return;
        }

        if (Files.isDirectory(p1)) {
            var path = p2.toString().replace('\\', '/');
            dirItems.add(path);
            for (var filePath :
                    Files.list(p1).toList()) {
                traverse(filePath.getFileName().toString(), p1.toString(), p2.toString());
            }
        }
    }

    private void transfer(FileTransferItem item) throws IOException {
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
