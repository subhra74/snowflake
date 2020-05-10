package muon.app.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.TimeUtils;

public class FileInfo implements Serializable, Comparable<FileInfo> {
	private static final Pattern USER_REGEX = Pattern
			.compile("^[^\\s]+\\s+[^\\s]+\\s+([^\\s]+)\\s+([^\\s]+)");
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
	private boolean hidden;

	public FileInfo(String name, String path, long size, FileType type,
			long lastModified, int permission, String protocol,
			String permissionString, long created, String extra,
			boolean hidden) {
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
		this.hidden = hidden;
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

	public void setName(String name) {
		this.path = name;
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

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public int compareTo(FileInfo o) {
		if (getType() == FileType.Directory || getType() == FileType.DirLink) {
			if (o.getType() == FileType.Directory
					|| o.getType() == FileType.DirLink) {
				return getName().compareToIgnoreCase(o.getName());
			} else {
				return 1;
			}
		} else {
			if (o.getType() == FileType.Directory
					|| o.getType() == FileType.DirLink) {
				return -1;
			} else {
				return getName().compareToIgnoreCase(o.getName());
			}
		}
//        if (o != null && o.getName() != null) {
//            return getName().compareToIgnoreCase(o.getName());
//        }
//        return 1;
	}
}
