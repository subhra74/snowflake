/**
 * 
 */
package muon.app.ssh;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import muon.app.App;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.HopEntry;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.SessionInfo.JumpType;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.DirectConnection;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.Transport;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.method.AuthKeyboardInteractive;
import net.schmizz.sshj.userauth.method.AuthNone;

/**
 * @author subhro
 *
 */
public class SshClient2 implements Closeable {
	private AtomicBoolean closed = new AtomicBoolean(false);
	private SessionInfo info;
	private SSHClient sshj;
	private PasswordFinderDialog passwordFinder;
	private InputBlocker inputBlocker;
	private CachedCredentialProvider cachedCredentialProvider;
	private SshClient2 previousHop;
	private ServerSocket ss;

	//30000 ms
	private static final int CONNECTION_TIMEOUT = 30000;
	/**
	 * O
	 * 
	 * @param info2
	 */
	public SshClient2(SessionInfo info, InputBlocker inputBlocker, CachedCredentialProvider cachedCredentialProvider) {
		this.info = info;
		this.inputBlocker = inputBlocker;
		this.cachedCredentialProvider = cachedCredentialProvider;
		passwordFinder = new PasswordFinderDialog(cachedCredentialProvider);
	}

	private void setupProxyAndSocketFactory() {
		String proxyHost = info.getProxyHost();
		int proxyType = info.getProxyType();
		String proxyUser = info.getProxyUser();
		String proxyPass = info.getProxyPassword();
		int proxyPort = info.getProxyPort();

		Proxy.Type proxyType1 = Proxy.Type.DIRECT;

		if (proxyType == 1) {
			proxyType1 = Proxy.Type.HTTP;
		} else if (proxyType > 1) {
			proxyType1 = Proxy.Type.SOCKS;
		}

		sshj.setSocketFactory(new CustomSocketFactory(proxyHost, proxyPort, proxyUser, proxyPass, proxyType1));
	}

	private void getAuthMethods(AtomicBoolean authenticated, List<String> allowedMethods)
			throws OperationCancelledException {
		System.out.println("Trying to get allowed authentication methods...");
		try {
			String user = promptUser();
			if (user == null || user.length() < 1) {
				throw new OperationCancelledException();
			}
			sshj.auth(user, new AuthNone());
			authenticated.set(true); // Surprise! no authentication!!!
		} catch (OperationCancelledException e) {
			throw e;
		} catch (Exception e) {
			for (String method : sshj.getUserAuth().getAllowedMethods()) {
				allowedMethods.add(method);
			}
			System.out.println("List of allowed authentications: " + allowedMethods);
		}
	}

	private void authPublicKey() throws Exception {
		KeyProvider provider = null;
		if (info.getPrivateKeyFile() != null && info.getPrivateKeyFile().length() > 0) {
			File keyFile = new File(info.getPrivateKeyFile());
			if (keyFile.exists()) {
				provider = sshj.loadKeys(info.getPrivateKeyFile(), passwordFinder);
				System.out.println("Key provider: " + provider);
				System.out.println("Key type: " + provider.getType());
			}
		}

		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}

		if (provider == null) {
			throw new Exception("No suitable key providers");
		}

		sshj.authPublickey(promptUser(), provider);
	}

	private void authPassoword() throws Exception {
		String user = getUser();
		char[] password = getPassword();
		if (user == null || user.length() < 1) {
			password = null;
		}
		// keep on trying with password
		while (!closed.get()) {
			if (password == null || password.length < 1) {
				JTextField txtUser = new SkinnedTextField(30);
				JPasswordField txtPassword = new JPasswordField(30);
				JCheckBox chkUseCache = new JCheckBox("Remember credential for this session");
				txtUser.setText(user);
				int ret = JOptionPane.showOptionDialog(null,
						new Object[] { "User", txtUser, "Password", txtPassword, chkUseCache }, "Authentication",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if (ret == JOptionPane.OK_OPTION) {
					user = txtUser.getText();
					password = txtPassword.getPassword();
					if (chkUseCache.isSelected()) {
						cachedCredentialProvider.setCachedUser(user);
						cachedCredentialProvider.cachePassword(password);
					}
				} else {
					throw new OperationCancelledException();
				}
			}
			try {
				sshj.authPassword(user, password); // provide
													// password
													// updater
													// PasswordUpdateProvider
													// net.schmizz.sshj.userauth.password.PasswordUpdateProvider
				return;
			} catch (Exception e) {
				e.printStackTrace();
				password = null;
			}
		}
	}

	public void connect() throws IOException, OperationCancelledException {
		Deque<HopEntry> hopStack = new ArrayDeque<HopEntry>();
		for (HopEntry e : this.info.getJumpHosts()) {
			hopStack.add(e);
		}
		this.connect(hopStack);
	}

	private void connect(Deque<HopEntry> hopStack) throws IOException, OperationCancelledException {
		this.inputBlocker.blockInput();
		try {
			sshj = new SSHClient();
			sshj.setConnectTimeout(CONNECTION_TIMEOUT);
			sshj.setTimeout(CONNECTION_TIMEOUT);
			if (hopStack.isEmpty()) {
				this.setupProxyAndSocketFactory();
				this.sshj.addHostKeyVerifier(App.HOST_KEY_VERIFIER);
				sshj.connect(info.getHost(), info.getPort());
			} else {
				try {
					System.out.println("Tunneling through...");
					tunnelThrough(hopStack);
					System.out.println("adding host key verifier");
					this.sshj.addHostKeyVerifier(App.HOST_KEY_VERIFIER);
					System.out.println("Host key verifier added");
					if (this.info.getJumpType() == JumpType.TcpForwarding) {
						System.out.println("tcp forwarding...");
						this.connectViaTcpForwarding();
					} else {
						System.out.println("port forwarding...");
						this.connectViaPortForwarding();
					}
				} catch (Exception e) {
					e.printStackTrace();
					disconnect();
					throw new IOException(e);
				}
			}

			// sshj.setRemoteCharset(remoteCharset);

			if (closed.get()) {
				disconnect();
				throw new OperationCancelledException();
			}

			// Connection established, now find out supported authentication
			// methods
			AtomicBoolean authenticated = new AtomicBoolean(false);
			List<String> allowedMethods = new ArrayList<>();

			this.getAuthMethods(authenticated, allowedMethods);

			if (authenticated.get()) {
				return;
			}

			if (closed.get()) {
				disconnect();
				throw new OperationCancelledException();
			}

			// loop over servers preferred authentication methods in the same
			// order sent by server
			for (String authMethod : allowedMethods) {
				if (closed.get()) {
					disconnect();
					throw new OperationCancelledException();
				}

				System.out.println("Trying auth method: " + authMethod);

				switch (authMethod) {
				case "publickey":
					try {
						this.authPublicKey();
						authenticated.set(true);
					} catch (OperationCancelledException e) {
						disconnect();
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case "keyboard-interactive":
					try {
						sshj.auth(promptUser(), new AuthKeyboardInteractive(new InteractiveResponseProvider()));
						authenticated.set(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case "password":
					try {
						this.authPassoword();
						authenticated.set(true);
					} catch (OperationCancelledException e) {
						disconnect();
						throw e;
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}

				if (authenticated.get()) {
					return;
				}
			}

//			if (authenticated.get()) {
//				return;
//			}

			throw new IOException("Authentication failed");

//			System.out.println("allowedMethods4: " + allowedMethods
//					+ " closed: " + closed.get() + " authenticated: "
//					+ authenticated.get());
//
//			// either key is not set or authentication with key is failed
//
//			// if the user has already provided password then use them,
//			// otherwise try to figure out which auth methods are supported by
//			// server
//
//			try {
//				if (isPasswordSet()) {
//					sshj.authPassword(info.getUser(), info.getPassword());
//					authenticated.set(true);
//				} else if (allowedMethods.size() < 1) {
//					// try to figure out what is supported
//					sshj.auth(info.getUser(), new AuthNone());
//					authenticated.set(true);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				allowedMethods = new ArrayList<>();
//				for (String method : sshj.getUserAuth().getAllowedMethods()) {
//					allowedMethods.add(method);
//				}
//
//				System.out.println("Allowed methods: " + allowedMethods);
//			}
//
//			System.out.println("allowedMethods5: " + allowedMethods
//					+ " closed: " + closed.get() + " authenticated: "
//					+ authenticated.get());
//
//			if (closed.get()) {
//				disconnect();
//				throw new OperationCancelledException();
//			}
//
//			System.out.println("allowedMethods6: " + allowedMethods
//					+ " closed: " + closed.get() + " authenticated: "
//					+ authenticated.get());
//
//			if (authenticated.get()) {
//				return;
//			}
//
//			System.out
//					.println("allowedMethods-checkpoint 2: " + allowedMethods);
//
//			// still not authenticated, either user did not provide password
//			// or first password attempt failed
//			// at this point allowedMethods must be populated
//			if (allowedMethods.contains("password")) {
//				// keep on trying with password
//				while (!closed.get()) {
//					JTextField txtUser = new JTextField(30);
//					JPasswordField txtPassword = new JPasswordField(30);
//
//					String user = info.getUser();
//					String password = info.getPassword();
//
//					if (password == null || password.length() < 1) {
//						txtUser.setText(user);
//						int ret = JOptionPane.showOptionDialog(null,
//								new Object[] { "User", txtUser, "Password",
//										txtPassword },
//								"Authentication", JOptionPane.OK_CANCEL_OPTION,
//								JOptionPane.PLAIN_MESSAGE, null, null, null);
//						if (ret == JOptionPane.OK_OPTION) {
//							user = txtUser.getText();
//							password = new String(txtPassword.getPassword());
//						} else {
//							disconnect();
//							throw new OperationCancelledException();
//						}
//					}
//					try {
//						sshj.authPassword(user, password); // provide password
//															// updater
//															// PasswordUpdateProvider
//															// net.schmizz.sshj.userauth.password.PasswordUpdateProvider
//						authenticated.set(true);
//						break;
//					} catch (Exception e) {
//						e.printStackTrace();
//						allowedMethods = new ArrayList<>();
//						for (String method : sshj.getUserAuth()
//								.getAllowedMethods()) {
//							allowedMethods.add(method);
//						}
//						System.out.println(allowedMethods);
//					}
//				}
//			} else if (allowedMethods.contains("keyboard-interactive")) {
//				sshj.auth(info.getUser(), new AuthKeyboardInteractive(
//						new InteractiveResponseProvider()));
//				authenticated.set(true);
//			}
//
//			if (closed.get()) {
//				disconnect();
//				throw new OperationCancelledException();
//			}
//
//			if (!authenticated.get()) {
//				throw new IOException("Authentication failed");
//			}
		} catch (Exception e) {
			if (this.sshj != null) {
				this.sshj.close();
			}
			throw e;
		}

		finally {
			this.inputBlocker.unblockInput();
		}
	}

	private boolean isPasswordSet() {
		return info.getPassword() != null && info.getPassword().length() > 0;
	}

	private String getUser() {
		String user = cachedCredentialProvider.getCachedUser();
		if (user == null || user.length() < 1) {
			user = this.info.getUser();
		}
		return user;
	}

	private char[] getPassword() {
		char[] password = cachedCredentialProvider.getCachedPassword();
		if (password == null && (this.info.getPassword() != null && this.info.getPassword().length() > 0)) {
			password = this.info.getPassword().toCharArray();
		}
		return password;
	}

	private String promptUser() {
		String user = getUser();
		if (user == null || user.length() < 1) {
			JTextField txtUser = new SkinnedTextField(30);
			JCheckBox chkCacheUser = new JCheckBox("Remember user name for this session");
			int ret = JOptionPane.showOptionDialog(null, new Object[] { "User name", txtUser, chkCacheUser }, "User",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (ret == JOptionPane.OK_OPTION) {
				user = txtUser.getText();
				if (chkCacheUser.isSelected()) {
					cachedCredentialProvider.setCachedUser(user);
				}
			}
		}
		return user;
	}

	public Session openSession() throws Exception {
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		Session session = sshj.startSession();
		if (closed.get()) {
			disconnect();
			throw new IOException("Closed by user");
		}
		return session;
	}

	public boolean isConnected() {
		return sshj != null && sshj.isConnected();
	}

	@Override
	public void close() throws IOException {
		try {
			System.out.println("Wrapper closing for: " + info);
			disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		if (closed.get()) {
			System.out.println("Already closed: " + info);
			return;
		}
		closed.set(true);
		try {
			if (sshj != null)
				sshj.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (previousHop != null)
				previousHop.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (this.ss != null) {
				this.ss.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ResourceManager.unregister(info.getContainterId(), this);
	}

	@Override
	public String toString() {
		return info.getName();
	}

	public SessionInfo getSource() {
		return info;
	}

	public SSHClient getSession() {
		return sshj;
	}

	public SFTPClient createSftpClient() throws IOException {
		return sshj.newSFTPClient();
	}

	/**
	 * @return the info
	 */
	public SessionInfo getInfo() {
		return info;
	}

//	private Proxy getProxy() {
//		String proxyHost = info.getProxyHost();
//		int proxyType = info.getProxyType();
//
//		String proxyUser = info.getProxyUser();
//		
//		Proxy proxy=null;
//
//		if (proxyType != 0 && proxyHost != null
//				&& proxyHost.trim().length() > 0) {
//			switch (proxyType) {
//			case 1: {
//				proxy=new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, info.getProxyPort()));
//				if (proxyUser != null && proxyUser.trim().length() > 0) {
//					//proxy.
//					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
//				}
//				break;
//			}
//			case 2: {
//				ProxySOCKS4 proxy = new ProxySOCKS4(proxyHost,
//						info.getProxyPort());
//				if (proxyUser != null && proxyUser.trim().length() > 0) {
//					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
//				}
//				break;
//			}
//			case 3: {
//				ProxySOCKS5 proxy = new ProxySOCKS5(proxyHost,
//						info.getProxyPort());
//				if (proxyUser != null && proxyUser.trim().length() > 0) {
//					proxy.setUserPasswd(proxyUser, info.getProxyPassword());
//				}
//				break;
//			}
//			}
//		}
//	}

	// recursively
	private void tunnelThrough(Deque<HopEntry> hopStack) throws Exception {
		HopEntry ent = hopStack.poll();
		SessionInfo hopInfo = new SessionInfo();
		hopInfo.setHost(ent.getHost());
		hopInfo.setPort(ent.getPort());
		hopInfo.setUser(ent.getUser());
		hopInfo.setPassword(ent.getPassword());
		hopInfo.setPrivateKeyFile(ent.getKeypath());
		previousHop = new SshClient2(hopInfo, inputBlocker, cachedCredentialProvider);
		previousHop.connect(hopStack);
	}

	private DirectConnection newDirectConnection(String host, int port) throws Exception {
		return sshj.newDirectConnection(host, port);
	}

	private void connectViaTcpForwarding() throws Exception {
		this.sshj.connectVia(this.previousHop.newDirectConnection(info.getHost(), info.getPort()), info.getHost(),
				info.getPort());
	}

	private void connectViaPortForwarding() throws Exception {
		ss = new ServerSocket();
		ss.setReuseAddress(true);
		ss.bind(new InetSocketAddress("127.0.0.1", 0));
		int port = ss.getLocalPort();
		new Thread(() -> {
			try {
				this.previousHop
						.newLocalPortForwarder(
								new Parameters("127.0.0.1", port, this.info.getHost(), this.info.getPort()), ss)
						.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		while (true) {
			if (ss.isBound()) {
				break;
			}
			Thread.sleep(100);
		}
		this.sshj.connect("127.0.0.1", port);
	}

	public LocalPortForwarder newLocalPortForwarder(Parameters parameters, ServerSocket serverSocket) {
		return this.sshj.newLocalPortForwarder(parameters, serverSocket);
	}

	@SuppressWarnings("deprecation")
	public RemotePortForwarder getRemotePortForwarder() {
		this.sshj.getTransport().setHeartbeatInterval(30);
		return this.sshj.getRemotePortForwarder();
	}

	public Transport getTransport() {
		return this.sshj.getTransport();
	}
}
