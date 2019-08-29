package snowflake.components.files;

import java.util.Date;

public class FileInfo {
    private String path;
    private String name;
    private long size;
    private Date modificationDate;
    private String permissionString;
    private int permission;
    private String owner;
    private String group;
    private boolean directory;
    private boolean link;

    public FileInfo(String path, String name, long size,
                    Date modificationDate, String permissionString, int permission,
                    String owner, String group, boolean directory,
                    boolean link) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.modificationDate = modificationDate;
        this.permissionString = permissionString;
        this.permission = permission;
        this.owner = owner;
        this.group = group;
        this.directory = directory;
        this.link = link;
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

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }
}
