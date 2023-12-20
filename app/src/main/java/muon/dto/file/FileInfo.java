package muon.dto.file;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FileInfo implements Serializable {
    private String path;
    private String name;
    private long size;
    private LocalDateTime modificationDate;
    private FileType fileType;
    private int permission;
    private String permissionString;
    private String owner;
    private Object extra;

    public FileInfo(String path,
                    String name,
                    long size,
                    LocalDateTime modificationDate,
                    FileType fileType,
                    String owner) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.modificationDate = modificationDate;
        this.fileType = fileType;
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public boolean isDirectory() {
        return fileType == FileType.Directory;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
