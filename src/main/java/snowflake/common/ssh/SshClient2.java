/**
 * 
 */
package snowflake.common.ssh;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hierynomus.sshj.userauth.keyprovider.OpenSSHKeyV1KeyFile;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;
import net.schmizz.sshj.userauth.method.AuthKeyboardInteractive;
import net.schmizz.sshj.userauth.method.AuthMethod;
import net.schmizz.sshj.userauth.method.AuthNone;
import net.schmizz.sshj.userauth.method.PasswordResponseProvider;
import snowflake.components.newsession.SessionInfo;

/**
 * @author subhro
 *
 */
public class SshClient2 implements Closeable {
	private AtomicBoolean closed = new AtomicBoolean(false);
	private SessionInfo info;
	private SSHClient sshj;
	private PasswordFinderDialog passwordFinder = new PasswordFinderDialog();

	/**
	 * @param info2
	 */
	public SshClient2(SessionInfo info) {
		this.info = info;
	}

	public void connect() throws IOException, OperationCancelledException {
		sshj = new SSHClient();

		sshj.loadKnownHosts();

		File knownHostFile = new File(System.getProperty("user.home"),
				".ssh" + File.separator + "known_hosts");

		sshj.addHostKeyVerifier(new GraphicalHostKeyVerifier(knownHostFile));
		// sshj.setRemoteCharset(remoteCharset);
		sshj.connect(info.getHost(), info.getPort());
		// sshj.authPassword(info.getUser(), info.getPassword());
		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}
		// skip host verification for now, will add it later
		// TODO IMPORTAND add host verification

		KeyProvider provider = null;
		if (info.getPrivateKeyFile() != null
				&& info.getPrivateKeyFile().length() > 0) {
			File keyFile = new File(info.getPrivateKeyFile());
			if (keyFile.exists()) {
				provider = sshj.loadKeys(info.getPrivateKeyFile(),
						passwordFinder);
				System.out.println("Key provider: " + provider);
				System.out.println(provider.getType());
			}

//			if (keyFile.exists() && info.getPrivateKeyFile().endsWith(".ppk")) {
//				PuTTYKeyFile puttyKeyFile = new PuTTYKeyFile();
//				puttyKeyFile.init(keyFile, passwordFinder);
//				System.out.println(puttyKeyFile.getType());
//				provider = puttyKeyFile;
//			} else if (keyFile.exists()) {
//				OpenSSHKeyV1KeyFile openSSHKeyFile = new OpenSSHKeyV1KeyFile();
//				openSSHKeyFile.init(keyFile, passwordFinder);
//				provider = openSSHKeyFile;
//			}
		}

		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}

		AtomicBoolean authenticated = new AtomicBoolean(false);
		List<String> allowedMethods = new ArrayList<>();

		try {
			if (provider != null) {
				sshj.authPublickey(info.getUser(), provider);
				authenticated.set(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			allowedMethods = new ArrayList<>();
			for (String method : sshj.getUserAuth().getAllowedMethods()) {
				allowedMethods.add(method);
			}
		}

		if (closed.get() || !passwordFinder.shouldRetry(null)) {
			disconnect();
			throw new OperationCancelledException();
		}

		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}

		if (authenticated.get()) {
			return;
		}

		// either key is not set or authentication with key is failed

		// if the user has already provided password then use them,
		// otherwise try to figure out which auth methods are supported by
		// server

		try {
			if (isPasswordSet()) {
				sshj.authPassword(info.getUser(), info.getPassword());
			} else if (allowedMethods.size() < 1) {
				// try to figure out what is supported
				sshj.auth(info.getUser(), new AuthNone());
			}
			authenticated.set(true);
		} catch (Exception e) {
			e.printStackTrace();
			allowedMethods = new ArrayList<>();
			for (String method : sshj.getUserAuth().getAllowedMethods()) {
				allowedMethods.add(method);
			}

			System.out.println("Allowed methods: " + allowedMethods);
		}

		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}

		if (authenticated.get()) {
			return;
		}

		// still not authenticated, either user did not provide password
		// or first password attempt failed
		// at this point allowedMethods must be populated
		if (allowedMethods.contains("password")) {
			// keep on trying with password
			while (!closed.get()) {
				JTextField txtUser = new JTextField(30);
				JPasswordField txtPassword = new JPasswordField(30);

				String user = info.getUser();
				String password = info.getPassword();

				if (password == null || password.length() < 1) {
					txtUser.setText(user);
					int ret = JOptionPane.showOptionDialog(null,
							new Object[] { "User", txtUser, "Password",
									txtPassword },
							"Authentication", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE, null, null, null);
					if (ret == JOptionPane.OK_OPTION) {
						user = txtUser.getText();
						password = new String(txtPassword.getPassword());
					} else {
						disconnect();
						throw new OperationCancelledException();
					}
				}
				try {
					sshj.authPassword(user, password); // provide password
														// updater
														// PasswordUpdateProvider
														// net.schmizz.sshj.userauth.password.PasswordUpdateProvider
					authenticated.set(true);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					allowedMethods = new ArrayList<>();
					for (String method : sshj.getUserAuth()
							.getAllowedMethods()) {
						allowedMethods.add(method);
					}
					System.out.println(allowedMethods);
				}
			}
		} else if (allowedMethods.contains("keyboard-interactive")) {
			sshj.auth(info.getUser(), new AuthKeyboardInteractive(
					new InteractiveResponseProvider()));
			authenticated.set(true);
		}

		if (closed.get()) {
			disconnect();
			throw new OperationCancelledException();
		}

		if (!authenticated.get()) {
			throw new IOException("Authenticatuin failed");
		}
	}

	private boolean isPasswordSet() {
		return info.getPassword() != null && info.getPassword().length() > 0;
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
			sshj.disconnect();
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
}
