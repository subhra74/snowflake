package snowflake.common;
import snowflake.utils.TimeUtils;

import java.io.Serializable;
import java.time.*;
import java.util.regex.*;

public class FileInfo implements Serializable {
    private String name;
    private String path;
    private long size;
    private FileType type;
    private LocalDateTime lastModified;
    private LocalDateTime created;
    private int permission;
    private String protocol;
    private String permissionString;
    private String extra;
    private String user;

    private static final Pattern USER_REGEX = Pattern.compile("^[^\\s]+\\s+[^\\s]+\\s+([^\\s]+)\\s+([^\\s]+)");

    public FileInfo(String name, String path, long size, FileType type, long lastModified, int permission,
                    String protocol, String permissionString, long created, String extra) {
        super();
        this.name = name;
        this.path = path;
        this.size = size;
        this.type = type;
        this.lastModified = TimeUtils.toDateTime(lastModified);
        this.permission = permission;
        this.protocol = protocol;
        this.permissionString = permissionString;
        this.created = TimeUtils.toDateTime(created);
        this.extra = extra;
        if (this.extra != null && this.extra.length() > 0) {
            this.user = getUserName();
        }
    }

    private String getUserName() {
        try {
            if (this.extra != null && this.extra.length() > 0) {
                Matcher matcher = USER_REGEX.matcher(this.extra);
                if (matcher.find()) {
                    String user = matcher.group(1);
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getPath() {
        return path;
    }

    public void setName(String name) {
        this.path = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = TimeUtils.toDateTime(lastModified);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;

//		"FileInfo [name=" + name + ", path=" + path + ", size=" + size
//				+ ", type=" + type + ", lastModified=" + lastModified + "]";
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public void setPermissionString(String permissionString) {
        this.permissionString = permissionString;
    }

    /**
     * @return the created
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
