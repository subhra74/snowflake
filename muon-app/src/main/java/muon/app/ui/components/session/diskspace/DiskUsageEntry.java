package muon.app.ui.components.session.diskspace;

import java.util.ArrayList;
import java.util.List;

import util.FormatUtils;

public class DiskUsageEntry {
	private String path, name;
	private long size;
	private double usagePercent;
	// private DiskUsageEntry parent;
	private List<DiskUsageEntry> children;

	private boolean directory;

	public DiskUsageEntry(String name, String path, long size,
			double usagePercent, boolean directory) {
		super();
		this.name = name;
		this.path = path;
		this.size = size;
		this.usagePercent = usagePercent;
		this.children = new ArrayList<>();
		this.directory = directory;
	}

	public synchronized String getPath() {
		return path;
	}

	public synchronized void setPath(String path) {
		this.path = path;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized long getSize() {
		return size;
	}

	public synchronized void setSize(long size) {
		this.size = size;
	}

	public synchronized double getUsagePercent() {
		return usagePercent;
	}

	public synchronized void setUsagePercent(double usagePercent) {
		this.usagePercent = usagePercent;
	}

	@Override
	public String toString() {
		return name + " [" + FormatUtils.humanReadableByteCount(size, true)
				+ "]";
//        return "DiskUsageEntry [path=" + path + ", name=" + name + ", size="
//                + size + ", usagePercent=" + usagePercent + ", children=" + children + "]";
	}

//    public DiskUsageEntry getParent() {
//        return this.parent;
//    }

	public List<DiskUsageEntry> getChildren() {
		return children;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

}
