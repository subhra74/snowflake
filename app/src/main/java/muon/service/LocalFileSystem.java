package muon.service;

import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.dto.file.FileType;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSException;
import muon.util.AppUtils;
import muon.util.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class LocalFileSystem implements FileSystem {
    public FileList list(String folder) throws FSException {
        List<Path> list;
        try {
            var path = Paths.get(folder);
            list = Files.list(path).toList();
            var folders = list.stream().filter(f -> Files.isDirectory(f)).map(d -> {
                        var size = 0L;
                        var mtime = LocalDateTime.now();
                        try {
                            var attrs = Files.readAttributes(d, BasicFileAttributes.class);
                            mtime = AppUtils.getModificationTime(attrs);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return new FileInfo(
                                d.toAbsolutePath().toString(),
                                d.getFileName().toString(),
                                size,
                                mtime,
                                FileType.Directory,
                                getOwnerName(d)
                        );
                    }
            ).toList();

            var files = list.stream().filter(f -> !Files.isDirectory(f)).map(f -> {
                        var size = 0L;
                        var mtime = LocalDateTime.now();
                        try {
                            var attrs = Files.readAttributes(f, BasicFileAttributes.class);
                            size = attrs.size();
                            mtime = AppUtils.getModificationTime(attrs);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return new FileInfo(
                                f.toAbsolutePath().toString(),
                                f.getFileName().toString(),
                                size,
                                mtime,
                                FileType.File,
                                getOwnerName(f)
                        );
                    }
            ).toList();

            var name = PathUtils.getFileName(folder);
            if (Objects.isNull(name)) {
                name = "";
            }

            return new FileList(
                    files,
                    folders,
                    path.toAbsolutePath().toString(),
                    name);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FSAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getHome() throws FSException {
        return Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
    }

    @Override
    public void close() throws Exception {
    }

    private String getOwnerName(Path path) {
        FileOwnerAttributeView attributeView = Files.getFileAttributeView(path,
                FileOwnerAttributeView.class);
        try {
            return attributeView.getOwner().getName();
        } catch (IOException e) {
            return null;
        }
    }
}
