package snowflake.components.sysinfo.platforms;

import snowflake.components.sysinfo.ServiceEntry;
import snowflake.components.sysinfo.SocketEntry;

import java.util.List;

public class SystemInfo {
    private String systemOverview;
    private List<SocketEntry> sockets;
    private List<ServiceEntry> services;

    public String getSystemOverview() {
        return systemOverview;
    }

    public void setSystemOverview(String systemOverview) {
        this.systemOverview = systemOverview;
    }

    public List<SocketEntry> getSockets() {
        return sockets;
    }

    public void setSockets(List<SocketEntry> sockets) {
        this.sockets = sockets;
    }

    public List<ServiceEntry> getServices() {
        return services;
    }

    public void setServices(List<ServiceEntry> services) {
        this.services = services;
    }
}
