/**
 * 
 */
package muon.app.ui.components.session.utilpage.sysload;

import java.util.concurrent.atomic.AtomicBoolean;

import muon.app.ssh.RemoteSessionInstance;

/**
 * @author subhro
 *
 */
public class LinuxMetrics {
	private double cpuUsage, memoryUsage, swapUsage;
	private long totalMemory, usedMemory, totalSwap, usedSwap;
	private long prev_idle, prev_total;
	private String OS;

	public void updateMetrics(RemoteSessionInstance instance) throws Exception {
		StringBuilder out = new StringBuilder(), err = new StringBuilder();
		int ret = instance.exec(
				"uname; head -1 /proc/stat;grep -E \"MemTotal|MemFree|Cached|SwapTotal|SwapFree\" /proc/meminfo",
				new AtomicBoolean(), out, err);
		if (ret != 0)
			throw new Exception("Error while getting metrics");
		// System.out.println(new String(bout.toByteArray()));
		updateStats(out.toString());
	}

	private void updateStats(String str) {
		String lines[] = str.split("\n");
		OS = lines[0];
		String cpuStr = lines[1];
		updateCpu(cpuStr);
		updateMemory(lines);
	}

	private void updateCpu(String line) {
		String cols[] = line.split("\\s+");
		long idle = Long.parseLong(cols[4]);
		long total = 0;
		for (int i = 1; i < cols.length; i++) {
			total += Long.parseLong(cols[i]);
		}
		long diff_idle = idle - prev_idle;
		long diff_total = total - prev_total;
		this.cpuUsage = (1000 * ((double) diff_total - diff_idle) / diff_total
				+ 5) / 10;
		this.prev_idle = idle;
		this.prev_total = total;
	}

	private void updateMemory(String[] lines) {
		long memTotalK = 0, memFreeK = 0, memCachedK = 0, swapTotalK = 0,
				swapFreeK = 0, swapCachedK = 0;
		for (int i = 2; i < lines.length; i++) {
			String[] arr = lines[i].split("\\s+");
			if (arr.length >= 2) {
				if (arr[0].trim().equals("MemTotal:")) {
					memTotalK = Long.parseLong(arr[1].trim());
				}
				if (arr[0].trim().equals("Cached:")) {
					memFreeK = Long.parseLong(arr[1].trim());
				}
				if (arr[0].trim().equals("MemFree:")) {
					memCachedK = Long.parseLong(arr[1].trim());
				}
				if (arr[0].trim().equals("SwapTotal:")) {
					swapTotalK = Long.parseLong(arr[1].trim());
				}
				if (arr[0].trim().equals("SwapFree:")) {
					swapFreeK = Long.parseLong(arr[1].trim());
				}
			}
		}

		this.totalMemory = memTotalK * 1024;
		this.totalSwap = swapTotalK * 1024;
		long freeMemory = memFreeK * 1024;
		long freeSwap = swapFreeK * 1024;

		if (this.totalMemory > 0) {
			this.usedMemory = this.totalMemory - freeMemory - memCachedK * 1024;
			this.memoryUsage = ((double) (this.totalMemory - freeMemory
					- memCachedK * 1024) * 100) / this.totalMemory;
		}

		if (this.totalSwap > 0) {
			this.usedSwap = this.totalSwap - freeSwap - swapCachedK * 1024;
			this.swapUsage = ((double) (this.totalSwap - freeSwap
					- swapCachedK * 1024) * 100) / this.totalSwap;
		}
	}

	/**
	 * @return the cpuUsage
	 */
	public double getCpuUsage() {
		return cpuUsage;
	}

	/**
	 * @return the memoryUsage
	 */
	public double getMemoryUsage() {
		return memoryUsage;
	}

	/**
	 * @return the swapUsage
	 */
	public double getSwapUsage() {
		return swapUsage;
	}

	/**
	 * @return the totalMemory
	 */
	public long getTotalMemory() {
		return totalMemory;
	}

	/**
	 * @return the usedMemory
	 */
	public long getUsedMemory() {
		return usedMemory;
	}

	/**
	 * @return the totalSwap
	 */
	public long getTotalSwap() {
		return totalSwap;
	}

	/**
	 * @return the usedSwap
	 */
	public long getUsedSwap() {
		return usedSwap;
	}

	/**
	 * @return the oS
	 */
	public String getOS() {
		return OS;
	}
}
