package snowflake.common.ssh;

import java.io.*;

import com.jcraft.jsch.*;
import snowflake.App;
import snowflake.components.newsession.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class SshClient implements Closeable {
	private JSch jsch;
	private Session session;
	private AbstractUserInteraction source;
	private AtomicBoolean closed = new AtomicBoolean(false);

	public SshClient(AbstractUserInteraction source) {
		System.out.println("New wrapper session");
		this.source = source;
	}

	public boolean isConnected() {
		if (session == null)
			return false;
		return session.isConnected();
	}

//    public int connectWithReturn() {
//        try {
//            connect();
//            return 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                disconnect();
//            } catch (Exception e2) {
//            }
//            return 1;
//        }
//    }

	@Override
	public String toString() {
		return source.getInfo().getName();
	}

	public void connect() throws Exception {
		// ResourceManager.register(info.getContainterId(), this);
		jsch = new JSch();
		try {
			jsch.setKnownHosts(new File(App.getConfig("app.dir"), "known_hosts")
					.getAbsolutePath());
		} catch (Exception e) {

		}

		// JSch.setLogger(new JSCHLogger());
//		JSch.setConfig("PreferredAuthentications",
//				"password,keyboard-interactive");
		JSch.setConfig("MaxAuthTries", "5");

		SessionInfo info = source.getInfo();

		if (info.getPrivateKeyFile() != null
				&& info.getPrivateKeyFile().length() > 0) {
			jsch.addIdentity(info.getPrivateKeyFile());
		}

		String user = info.getUser();

		if (user == null || user.length() < 1) {
			throw new Exception("User name is not present");
		}

		session = jsch.getSession(user, info.getHost(), info.getPort());

		String proxyHost = info.getProxyHost();
		int proxyType = info.getProxyType();

		String proxyUser = info.getProxyUser();

		if (proxyType != 0 && proxyHost != null
				&& proxyHost.trim().length() > 0) {
			switch (proxyType) {
			case 1: {
				ProxyHTTP proxy = new ProxyHTTP(proxyHost, info.getProxyPort());
				if (proxyUser != null && proxyUser.trim().length() > 0) {
					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
				}
				break;
			}
			case 2: {
				ProxySOCKS4 proxy = new ProxySOCKS4(proxyHost,
						info.getProxyPort());
				if (proxyUser != null && proxyUser.trim().length() > 0) {
					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
				}
				break;
			}
			case 3: {
				ProxySOCKS5 proxy = new ProxySOCKS5(proxyHost,
						info.getProxyPort());
				if (proxyUser != null && proxyUser.trim().length() > 0) {
					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
				}
				break;
			}
			}
		}

		session.setUserInfo(source);

		session.setPassword(source.getInfo().getPassword());
		// session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications",
				"publickey,keyboard-interactive,password");

		if (closed.get()) {
			return;
		}

		session.setTimeout(10 * 1000);
		session.connect();

		if (closed.get()) {
			disconnect();
			return;
		}

		System.out.println("Client version: " + session.getClientVersion());
		System.out.println("Server host: " + session.getHost());
		System.out.println("Server version: " + session.getServerVersion());
		System.out.println(
				"Hostkey: " + session.getHostKey().getFingerPrint(jsch));
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
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		return sftp;
	}

	public ChannelShell getShellChannel() throws Exception {
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		ChannelShell shell = (ChannelShell) session.openChannel("shell");
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		return shell;
	}

	public ChannelExec getExecChannel() throws Exception {
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		ChannelExec exec = (ChannelExec) session.openChannel("exec");
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		return exec;
	}

	public SessionInfo getSource() {
		return source.getInfo();
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
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Session getSession() {
		return session;
	}
}
