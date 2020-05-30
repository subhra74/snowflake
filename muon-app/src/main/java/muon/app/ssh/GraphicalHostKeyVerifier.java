/**
 * 
 */
package muon.app.ssh;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;

import javax.swing.JOptionPane;

import net.schmizz.sshj.common.KeyType;
import net.schmizz.sshj.common.SecurityUtils;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;

/**
 * @author subhro
 *
 */
public class GraphicalHostKeyVerifier extends OpenSSHKnownHosts {
	/**
	 * @throws IOException
	 * 
	 */
	public GraphicalHostKeyVerifier(File knownHostFile) throws IOException {
		super(knownHostFile);
	}

	@Override
	protected boolean hostKeyUnverifiableAction(String hostname, PublicKey key) {
		final KeyType type = KeyType.fromKey(key);

		int resp = JOptionPane.showConfirmDialog(null,
				String.format(
						"The authenticity of host '%s' can't be established.\n"
								+ "%s key fingerprint is %s.\nAre you sure you want to continue connecting (yes/no)?",
						hostname, type, SecurityUtils.getFingerprint(key)));

		if (resp == JOptionPane.YES_OPTION) {
			try {
				this.entries.add(new HostEntry(null, hostname, KeyType.fromKey(key), key));
				write();
			} catch (Exception e) {
				e.printStackTrace();
				//throw new RuntimeException(e);
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean hostKeyChangedAction(String hostname, PublicKey key) {
		final KeyType type = KeyType.fromKey(key);
		final String fp = SecurityUtils.getFingerprint(key);
		final String path = getFile().getAbsolutePath();
		String msg = String.format("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"
				+ "@    WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!     @\n"
				+ "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n"
				+ "IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!\n"
				+ "Someone could be eavesdropping on you right now (man-in-the-middle attack)!\n"
				+ "It is also possible that the host key has just been changed.\n"
				+ "The fingerprint for the %s key sent by the remote host is\n" + "%s.\n"
				+ "Do you still want to connect to this server?", type, fp, path);
		return JOptionPane.showConfirmDialog(null, msg) == JOptionPane.YES_OPTION;
	}

	@Override
	public boolean verify(String hostname, int port, PublicKey key) {
		try {
			if (!super.verify(hostname, port, key)) {
				return this.hostKeyUnverifiableAction(hostname, key);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return this.hostKeyUnverifiableAction(hostname, key);
		}
	}
}
