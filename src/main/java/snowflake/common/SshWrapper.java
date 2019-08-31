package snowflake.common;

import java.io.*;

import com.jcraft.jsch.*;
import snowflake.App;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.newsession.*;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshWrapper implements Closeable {
    private JSch jsch;
    private Session session;
    private SessionInfo info;
    private JRootPane rootPane;
    private AtomicBoolean closed = new AtomicBoolean(false);

    public SshWrapper(SessionInfo info, JRootPane rootPane) {
        System.out.println("New wrapper session");
        this.info = info;
        this.rootPane = rootPane;
    }

    public boolean isConnected() {
        if (session == null)
            return false;
        return session.isConnected();
    }

    public int connectWithReturn() {
        try {
            connect();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                disconnect();
            } catch (Exception e2) {
            }
            return 1;
        }
    }

    @Override
    public String toString() {
        return info.getName();
    }

    public void connect() throws Exception {
        // ResourceManager.register(info.getContainterId(), this);
        jsch = new JSch();
        try {
            jsch.setKnownHosts(new File(App.getConfig("app.dir"), "known_hosts").getAbsolutePath());
        } catch (Exception e) {

        }

        // JSch.setLogger(new JSCHLogger());
//		JSch.setConfig("PreferredAuthentications",
//				"password,keyboard-interactive");
        JSch.setConfig("MaxAuthTries", "5");

        if (info.getPrivateKeyFile() != null && info.getPrivateKeyFile().length() > 0) {
            jsch.addIdentity(info.getPrivateKeyFile());
        }

        String user = info.getUser();

        if (info.getUser() == null || info.getUser().length() < 1) {
            throw new Exception("User name is not present");
        }

        session = jsch.getSession(info.getUser(), info.getHost(), info.getPort());

        session.setUserInfo(new SshUserInteraction(info, rootPane));

        session.setPassword(info.getPassword());
        // session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

        if(closed.get()){
            return;
        }

        //session.setTimeout(AppContext.INSTANCE.getConfig().getConnectionTimeout() * 1000);
        session.connect();

        if(closed.get()){
            disconnect();
            return;
        }

        System.out.println("Client version: " + session.getClientVersion());
        System.out.println("Server host: " + session.getHost());
        System.out.println("Server version: " + session.getServerVersion());
        System.out.println("Hostkey: " + session.getHostKey().getFingerPrint(jsch));
    }

    public void disconnect() {
        closed.set(true);
        try {
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ResourceManager.unregister(info.getContainterId(), this);
    }

    public ChannelSftp getSftpChannel() throws Exception {
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        return sftp;
    }

    public ChannelShell getShellChannel() throws Exception {
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        ChannelShell shell = (ChannelShell) session.openChannel("shell");
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        return shell;
    }

    public ChannelExec getExecChannel() throws Exception {
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        if(closed.get()){
            disconnect();
            throw new IOException("Closed by user");
        }
        return exec;
    }

    public SessionInfo getInfo() {
        return info;
    }

    public void setInfo(SessionInfo info) {
        this.info = info;
    }


    class JSCHLogger implements com.jcraft.jsch.Logger {
        @Override
        public boolean isEnabled(int level) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public void log(int level, String message) {
            // TODO Auto-generated method stub
            System.out.println(message);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            System.out.println("Wrapper closing");
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }
}

