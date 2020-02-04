package snowflake.components.taskmgr.plaformsupport;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.ssh.SshClient;
import snowflake.components.taskmgr.ProcessTableEntry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LinuxPlatformSupport implements PlatformSupport {
    private double cpuUsage, memoryUsage, swapUsage;
    private long totalMemory, usedMemory, totalSwap, usedSwap;
    private long prev_idle, prev_total;
    private List<ProcessTableEntry> processes = new ArrayList<>();

    public void updateMetrics(SshClient client) throws Exception {
        ChannelExec exec = client.getExecChannel();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        exec.setOutputStream(bout);
        exec.setCommand("head -1 /proc/stat;grep -E \"MemTotal|MemFree|Cached|SwapTotal|SwapFree\" /proc/meminfo");
        exec.connect();
        while (exec.isConnected()) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
        int ret = exec.getExitStatus();
        exec.disconnect();
        if (ret != 0) throw new Exception("Error while getting metrics");
        //System.out.println(new String(bout.toByteArray()));
        updateStats(new String(bout.toByteArray()));
    }

    private void updateStats(String str) {
        String lines[] = str.split("\n");
        String cpuStr = lines[0];
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
        this.cpuUsage = (1000 * ((double) diff_total - diff_idle) / diff_total + 5) / 10;
        this.prev_idle = idle;
        this.prev_total = total;
    }

    private void updateMemory(String[] lines) {
        long memTotalK = 0, memFreeK = 0, memCachedK = 0, swapTotalK = 0, swapFreeK = 0, swapCachedK = 0;
        for (int i = 1; i < lines.length; i++) {
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
            this.memoryUsage = ((double) (this.totalMemory - freeMemory - memCachedK * 1024) * 100) / this.totalMemory;
        }

        if (this.totalSwap > 0) {
            this.usedSwap = this.totalSwap - freeSwap - swapCachedK * 1024;
            this.swapUsage = ((double) (this.totalSwap - freeSwap - swapCachedK * 1024) * 100) / this.totalSwap;
        }
    }

    public void updateProcessList(SshClient client) throws Exception {
        String ps = "ps -e -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww --sort pid";
        ChannelExec exec = client.getExecChannel();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        exec.setOutputStream(bout);
        exec.setCommand(ps);
        exec.connect();
        while (exec.isConnected()) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
        int ret = exec.getExitStatus();
        exec.disconnect();
        if (ret != 0) throw new Exception("Error while getting metrics");
        String str = new String(bout.toByteArray(), "utf-8");
        parseProcessList(str);
    }

    private void parseProcessList(String text) {
        List<ProcessTableEntry> list = new ArrayList<>();
        String lines[] = text.split("\n");
        boolean first = true;
        for (String line : lines) {
            if (first) {
                first = false;
                continue;
            }
            String p[] = line.trim().split("\\s+");
            if (p.length < 8) {
                continue;
            }

            ProcessTableEntry ent = new ProcessTableEntry();
            try {
                ent.setPid(Integer.parseInt(p[0].trim()));
            } catch (Exception e) {
            }
            try {
                ent.setCpu(Float.parseFloat(p[1].trim()));
            } catch (Exception e) {
            }
            try {
                ent.setMemory(Float.parseFloat(p[2].trim()));
            } catch (Exception e) {
            }
            ent.setTime(p[3]);
            try {
                ent.setPpid(Integer.parseInt(p[4].trim()));
            } catch (Exception e) {
            }
            ent.setUser(p[5]);
            try {
                ent.setNice(Integer.parseInt(p[6].trim()));
            } catch (Exception e) {
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 7; i < p.length; i++) {
                sb.append(p[i] + " ");
            }
            ent.setArgs(sb.toString().trim());
            list.add(ent);
        }
        this.processes = list;
    }

    @Override
    public double getCpuUsage() {
        return this.cpuUsage;
    }

    @Override
    public double getMemoryUsage() {
        return this.memoryUsage;
    }

    @Override
    public long getTotalMemory() {
        return this.totalMemory;
    }

    @Override
    public long getUsedMemory() {
        return this.usedMemory;
    }

    @Override
    public long getTotalSwap() {
        return this.totalSwap;
    }

    @Override
    public long getUsedSwap() {
        return this.usedSwap;
    }

    public double getSwapUsage() {
        return this.swapUsage;
    }

    public List<ProcessTableEntry> getProcessList() {
        return this.processes;
    }
}
