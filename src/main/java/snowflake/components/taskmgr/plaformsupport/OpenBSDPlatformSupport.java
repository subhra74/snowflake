package snowflake.components.taskmgr.plaformsupport;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.ssh.SshClient;
import snowflake.components.taskmgr.ProcessTableEntry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenBSDPlatformSupport implements PlatformSupport {
    private double cpuUsage, memoryUsage, swapUsage;
    //    private long totalMemory, usedMemory, totalSwap, usedSwap;
    private List<ProcessTableEntry> processes = new ArrayList<>();
    private static final String DELIMITER = UUID.randomUUID().toString();
    private static final Pattern PSTAT_PATTERN = Pattern.compile("Total\\s+(\\d+)\\s+(\\d+)\\s+\\d+\\s+\\d{1,3}%");

    public void updateMetrics(SshClient client) throws Exception {
        ChannelExec exec = client.getExecChannel();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        exec.setOutputStream(bout);
        exec.setCommand("PATH=$PATH:/usr/sbin; vmstat 1 2; echo " + DELIMITER + "; pstat -s -k");
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

        String arr[] = new String(bout.toByteArray()).split(DELIMITER);

        parseVmStatOutput(arr[0].trim());
        parsePstatOutput(arr[1].trim());
        //System.out.println(new String(bout.toByteArray()));
        //updateStats(new String(bout.toByteArray()));
    }

//    private void updateStats(String str) {
//        String lines[] = str.split("\n");
//        String cpuStr = lines[0];
//        updateCpu(cpuStr);
//        updateMemory(lines);
//    }

    public void parseVmStatOutput(String text) {
        System.out.println("VMSTAT---\n" + text);
        long activeMemory = -1, freeMemory = -1;
        long userTime = -1, systemTime = -1, idleTime = -1;
        String[] lines = text.split("\n");
        if (lines.length < 2) return;
        int colFreeMem = -1, colActiveMem = -1, colUserTime = -1, colSystemTime = -1, colIdleTime = -1;
        String lastLine = null;
        for (String line1 : lines) {
            String line = line1.trim();
            String columns[] = line.split("\\s+");
            for (int i = 0; i < columns.length; i++) {
                String col = columns[i].trim();
                if ("avm".equals(col)) {
                    colActiveMem = i;
                }
                if ("fre".equals(col) || "free".equals(col)) {
                    colFreeMem = i;
                }
                if ("us".equals(col)) {
                    colUserTime = i;
                }
                if ("sy".equals(col)) {
                    colSystemTime = i;
                }
                if ("id".equals(col)) {
                    colIdleTime = i;
                }
                if (colFreeMem != -1 && colActiveMem != -1
                        && colUserTime != -1 && colSystemTime != -1 && colIdleTime != -1) break;
            }
            lastLine = line;
        }
        if (lastLine != null && colFreeMem != -1 && colActiveMem != -1
                && colUserTime != -1 && colSystemTime != -1 && colIdleTime != -1) {
            String columns[] = lastLine.split("\\s+");

            activeMemory = Long.parseLong(columns[colActiveMem]
                    .replace("M", (1024 * 1024) + "")
                    .replace("K", 1024 + ""));
            freeMemory = Long.parseLong(columns[colFreeMem]
                    .replace("M", (1024 * 1024) + "")
                    .replace("K", 1024 + ""));
            long totalMemory = activeMemory + freeMemory;

            long usedMemory = activeMemory;

            idleTime = Long.parseLong(columns[colIdleTime]);
            systemTime = Long.parseLong(columns[colSystemTime]);
            userTime = Long.parseLong(columns[colUserTime]);

            long idle = idleTime;
            long total = idleTime + systemTime + userTime;

            this.cpuUsage = (1000 * ((double) total - idle) / total) / 10;

            if (totalMemory > 0) {
                memoryUsage = ((double) (usedMemory) * 100) / totalMemory;
            }


//            System.out.println("Active memory: " + activeMemory);
//            System.out.println("Free memory: " + freeMemory);
//            System.out.println("total memory: " + totalMemory);
//            if (totalMemory > 0) {
//                System.out.println("Memory usage: " + ((activeMemory * 100) / totalMemory));
//            }
        }
        System.out.println("VMSTAT---\n\n");
    }

    private void parsePstatOutput(String text) {
        text = text.trim();
        String[] arr = text.split("\n");
        if (arr.length < 1) return;
        String line = arr[arr.length - 1];
        Matcher matcher = PSTAT_PATTERN.matcher(line);
        long totalSwap, usedSwap;
        if (matcher.find()) {
            totalSwap = Long.parseLong(matcher.group(1).trim()) * 1024;
            usedSwap = Long.parseLong(matcher.group(2).trim()) * 1024;

            if (totalSwap > 0) {
                this.swapUsage = ((double) (usedSwap) * 100) / totalSwap;
            }
        }
    }

//    private void updateCpu(String line) {
//        String cols[] = line.split("\\s+");
//        long idle = Long.parseLong(cols[4]);
//        long total = 0;
//        for (int i = 1; i < cols.length; i++) {
//            total += Long.parseLong(cols[i]);
//        }
//        long diff_idle = idle - prev_idle;
//        long diff_total = total - prev_total;
//        this.cpuUsage = (1000 * ((double) diff_total - diff_idle) / diff_total + 5) / 10;
//        this.prev_idle = idle;
//        this.prev_total = total;
//    }
//
//    private void updateMemory(String[] lines) {
//        long memTotalK = 0, memFreeK = 0, memCachedK = 0, swapTotalK = 0, swapFreeK = 0, swapCachedK = 0;
//        for (int i = 1; i < lines.length; i++) {
//            String[] arr = lines[i].split("\\s+");
//            if (arr.length >= 2) {
//                if (arr[0].trim().equals("MemTotal:")) {
//                    memTotalK = Long.parseLong(arr[1].trim());
//                }
//                if (arr[0].trim().equals("Cached:")) {
//                    memFreeK = Long.parseLong(arr[1].trim());
//                }
//                if (arr[0].trim().equals("MemFree:")) {
//                    memCachedK = Long.parseLong(arr[1].trim());
//                }
//                if (arr[0].trim().equals("SwapTotal:")) {
//                    swapTotalK = Long.parseLong(arr[1].trim());
//                }
//                if (arr[0].trim().equals("SwapFree:")) {
//                    swapFreeK = Long.parseLong(arr[1].trim());
//                }
//            }
//        }
//
//        this.totalMemory = memTotalK * 1024;
//        this.totalSwap = swapTotalK * 1024;
//        long freeMemory = memFreeK * 1024;
//        long freeSwap = swapFreeK * 1024;
//
//        if (this.totalMemory > 0) {
//            this.usedMemory = this.totalMemory - freeMemory - memCachedK * 1024;
//            this.memoryUsage = ((double) (this.totalMemory - freeMemory - memCachedK * 1024) * 100) / this.totalMemory;
//        }
//
//        if (this.totalSwap > 0) {
//            this.usedSwap = this.totalSwap - freeSwap - swapCachedK * 1024;
//            this.swapUsage = ((double) (this.totalSwap - freeSwap - swapCachedK * 1024) * 100) / this.totalSwap;
//        }
//    }

    public void updateProcessList(SshClient client) throws Exception {
        String ps = "ps -a -x -o pid=pid -o pcpu -o rss -o etime -o ppid -o user -o nice -o args -ww";
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
        System.out.println(new String(bout.toByteArray(), "utf-8"));
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
        list.sort((a, b) -> a.getPid() > b.getPid() ? 1 : (a.getPid() < b.getPid() ? -1 : 0));
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
        return 0L;
    }

    @Override
    public long getUsedMemory() {
        return 0L;
    }

    @Override
    public long getTotalSwap() {
        return 0L;
    }

    @Override
    public long getUsedSwap() {
        return 0L;
    }

    public double getSwapUsage() {
        return this.swapUsage;
    }

    public List<ProcessTableEntry> getProcessList() {
        return this.processes;
    }
}