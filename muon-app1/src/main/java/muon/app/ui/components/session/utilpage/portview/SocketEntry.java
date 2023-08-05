package muon.app.ui.components.session.utilpage.portview;

public class SocketEntry {
    private String app;
    private int pid;
    private int port;
    private String host;

    public SocketEntry() {
    }

    public SocketEntry(String app, int pid, int port, String host) {
        this.app = app;
        this.pid = pid;
        this.port = port;
        this.host = host;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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
}
