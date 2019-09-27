package snowflake.components.sysinfo.platforms;

import snowflake.components.sysinfo.ServiceEntry;

import java.util.List;

public class SystemInfo {
    private String systemOverview;
    private List<SocketInfo> sockets;
    private List<ServiceEntry> services;

    public String getSystemOverview() {
        return systemOverview;
    }

    public void setSystemOverview(String systemOverview) {
        this.systemOverview = systemOverview;
    }

    public List<SocketInfo> getSockets() {
        return sockets;
    }

    public void setSockets(List<SocketInfo> sockets) {
        this.sockets = sockets;
    }

    public List<ServiceEntry> getServices() {
        return services;
    }

    public void setServices(List<ServiceEntry> services) {
        this.services = services;
    }

    static class SocketInfo {
        private String process;
        private int port;
        private String host;
        private String tcpVersion;
        private int pid;
        private String command;

        public SocketInfo(String process, int port, String host, String tcpVersion, int pid, String command) {
            this.process = process;
            this.port = port;
            this.host = host;
            this.tcpVersion = tcpVersion;
            this.pid = pid;
            this.command = command;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getTcpVersion() {
            return tcpVersion;
        }

        public void setTcpVersion(String tcpVersion) {
            this.tcpVersion = tcpVersion;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }
    }
}
