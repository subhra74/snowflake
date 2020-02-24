/**
 * 
 */
package snowflake.common.ssh;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;
import snowflake.App;
import snowflake.components.newsession.SessionInfo;

/**
 * @author subhro
 *
 */
public class SshClient2 implements Closeable {
	private AtomicBoolean closed = new AtomicBoolean(false);
	private SessionInfo info;
	private SSHClient sshj;
	private PasswordFinderDialog passwordFinder;

	/**
	 * @param info2
	 */
	public SshClient2(SessionInfo info) {
		this.info = info;
	}

	public void connect() throws Exception {
		sshj = new SSHClient();
		sshj.addHostKeyVerifier((a, b, c) -> {
			System.out.println(a + " " + c.getAlgorithm()+" ");
			return true;
		});
		//sshj.setRemoteCharset(remoteCharset);
		sshj.connect(info.getHost(), info.getPort());
		sshj.authPassword(info.getUser(), info.getPassword());
		if (closed.get()) {
			disconnect();
			return;
		}
		// skip host verification for now, will add it later
		// TODO IMPORTAND add host verification
//		if (info.getPrivateKeyFile() != null
//				&& info.getPrivateKeyFile().length() > 0) {
//			KeyProvider provider = null;
//			File keyFile = new File(info.getPrivateKeyFile());
//			if (keyFile.exists() && info.getPrivateKeyFile().endsWith(".ppk")) {
//				PuTTYKeyFile puttyKeyFile = new PuTTYKeyFile();
//				puttyKeyFile.init(keyFile, passwordFinder);
//				provider = puttyKeyFile;
//			} else if (keyFile.exists()) {
//				OpenSSHKeyFile openSSHKeyFile = new OpenSSHKeyFile();
//				openSSHKeyFile.init(keyFile, passwordFinder);
//				provider = openSSHKeyFile;
//			}
//			List<String> allowedMethods = Arrays.asList("password");
//			try {
//				if (provider != null) {
//					sshj.authPublickey(info.getUser(), provider);
//				}
//			} catch (Exception e) {
//				allowedMethods = new ArrayList<>();
//				for (String method : sshj.getUserAuth().getAllowedMethods()) {
//					allowedMethods.add(method);
//				}
//			}
//
//			if (allowedMethods.contains("password")) {
//				if (info.getPassword() != null
//						&& info.getPassword().length() > 0) {
//					sshj.authPassword(info.getUser(), info.getPassword());
//				}
//			}
//		}
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
