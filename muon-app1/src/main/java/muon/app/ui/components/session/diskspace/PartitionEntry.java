package muon.app.ui.components.session.diskspace;

public class PartitionEntry {
    private String fileSystem, mountPoint;
    private long totalSize, used, available;
    private double usedPercent;

    public PartitionEntry(String fileSystem, String mountPoint, long totalSize,
                          long used, long available, double usedPercent) {
        this.fileSystem = fileSystem;
        this.mountPoint = mountPoint;
        this.totalSize = totalSize;
        this.used = used;
        this.available = available;
        this.usedPercent = usedPercent;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public double getUsedPercent() {
        return usedPercent;
    }

    public void setUsedPercent(double usedPercent) {
        this.usedPercent = usedPercent;
    }
}
