package muon.app.ui.components.session.utilpage.keys;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;

import muon.app.common.InputTransferChannel;
import muon.app.common.OutputTransferChannel;
import muon.app.ssh.RemoteSessionInstance;
import muon.app.ssh.SshFileSystem;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import net.schmizz.sshj.sftp.Response;
import net.schmizz.sshj.sftp.SFTPException;
import util.PathUtils;

public class SshKeyManager {
	public static SshKeyHolder getKeyDetails(SessionContentPanel content) throws Exception {
		SshKeyHolder holder = new SshKeyHolder();
		loadLocalKey(getPubKeyPath(content.getInfo()), holder);
		loadRemoteKeys(holder, content.getRemoteSessionInstance().getSshFs());
		return holder;
	}

	private static void loadLocalKey(String pubKeyPath, SshKeyHolder holder) {
		try {
			Path defaultPath = pubKeyPath == null
					? Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa.pub").toAbsolutePath()
					: Paths.get(pubKeyPath);
			byte[] bytes = Files.readAllBytes(defaultPath);
			holder.setLocalPublicKey(new String(bytes, "utf-8"));
			holder.setLocalPubKeyFile(defaultPath.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//    private static void loadRemoteKeys(SshKeyHolder holder, SessionInfo info) throws Exception {
//        try (SshFileSystem fileSystem = new SshFileSystem(new SshModalUserInteraction(info))) {
//            fileSystem.connect();
//            loadRemoteKeys(holder, fileSystem);
//        }
//    }

	private static void loadRemoteKeys(SshKeyHolder holder, SshFileSystem fileSystem) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String path = fileSystem.getHome() + "/.ssh/id_rsa.pub";
		try {
			InputTransferChannel itc = fileSystem.inputTransferChannel();
			try (InputStream in = itc.getInputStream(path)) {
				byte[] bytes = in.readAllBytes();
				out.write(bytes);
			}

			// fileSystem.getSftp().get(path, out);
			holder.setRemotePubKeyFile(path);
			holder.setRemotePublicKey(new String(out.toByteArray(), "utf-8"));
		} catch (SFTPException e) {
			if (e.getStatusCode() != Response.StatusCode.NO_SUCH_FILE
					&& e.getStatusCode() != Response.StatusCode.NO_SUCH_PATH) {
				throw e;
			}
		}
		out = new ByteArrayOutputStream();
		path = fileSystem.getHome() + "/.ssh/authorized_keys";
		try {
			InputTransferChannel itc = fileSystem.inputTransferChannel();
			try (InputStream in = itc.getInputStream(path)) {
				byte[] bytes = in.readAllBytes();
				out.write(bytes);
			}

			// fileSystem.getSftp().get(path, out);
			holder.setRemoteAuthorizedKeys(new String(out.toByteArray(), "utf-8"));
		} catch (SFTPException e) {
			if (e.getStatusCode() != Response.StatusCode.NO_SUCH_FILE
					&& e.getStatusCode() != Response.StatusCode.NO_SUCH_PATH) {
				throw e;
			}
		}
	}

	public static void generateKeys(SshKeyHolder holder, RemoteSessionInstance instance, boolean local)
			throws Exception {
		if (holder.getLocalPublicKey() != null) {
			if (JOptionPane.showConfirmDialog(null,
					"WARNING: This will overwrite the existing SSH key"
							+ "\n\nIf the key was being used to connect to other servers," + "\nconnection will fail."
							+ "\nYou have to reconfigure all the servers"
							+ "\nto use the new key\nDo you still want to continue?",
					"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
				return;
			}
		}

		JCheckBox chkGenPassPhrase = new JCheckBox("Use passphrase to protect private key (Optional)");
		JPasswordField txtPassPhrase = new JPasswordField(30);
		txtPassPhrase.setEditable(false);
		chkGenPassPhrase.addActionListener(e -> {
			txtPassPhrase.setEditable(chkGenPassPhrase.isSelected());
		});

		String passPhrase = new String(txtPassPhrase.getPassword());

		if (JOptionPane.showOptionDialog(null, new Object[] { chkGenPassPhrase, "Passphrase", txtPassPhrase },
				"Passphrase", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.YES_OPTION) {
			if (local) {
				generateLocalKeys(holder, passPhrase);
			} else {
				generateRemoteKeys(instance, holder, passPhrase);
			}
		}
	}

	public static void generateLocalKeys(SshKeyHolder holder, String passPhrase) throws Exception {
		Path sshDir = Paths.get(System.getProperty("user.home"), ".ssh");
		Path pubKeyPath = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa.pub").toAbsolutePath();
		Path keyPath = Paths.get(System.getProperty("user.home"), ".ssh", "id_rsa").toAbsolutePath();
		JSch jsch = new JSch();
		KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
		Files.createDirectories(sshDir);
		if (passPhrase.length() > 0) {
			kpair.writePrivateKey(keyPath.toString(), passPhrase.getBytes("utf-8"));
		} else {
			kpair.writePrivateKey(keyPath.toString());
		}
		kpair.writePublicKey(pubKeyPath.toString(), System.getProperty("user.name") + "@localcomputer");
		kpair.dispose();
		loadLocalKey(pubKeyPath.toString(), holder);
	}

	public static void generateRemoteKeys(RemoteSessionInstance instance, SshKeyHolder holder, String passPhrase)
			throws Exception {
		String path1 = "$HOME/.ssh/id_rsa";
		String path = path1 + ".pub";

		String cmd = "ssh-keygen -q -N \"" + passPhrase + "\" -f \"" + path1 + "\"";

		try {
			instance.getSshFs().deleteFile(path1);
		} catch (SFTPException e) {
			if (e.getStatusCode() != Response.StatusCode.NO_SUCH_FILE
					&& e.getStatusCode() != Response.StatusCode.NO_SUCH_PATH) {
				throw new Exception(e);
			}
		}

		try {
			instance.getSshFs().deleteFile(path);
		} catch (SFTPException e) {
			if (e.getStatusCode() != Response.StatusCode.NO_SUCH_FILE
					&& e.getStatusCode() != Response.StatusCode.NO_SUCH_PATH) {
				throw new Exception(e);
			}
		}

		StringBuilder output = new StringBuilder();
		if (instance.exec(cmd, new AtomicBoolean(false), output) != 0) {
			throw new Exception();
		}
		loadRemoteKeys(holder, instance.getSshFs());
	}

	private static String getPubKeyPath(SessionInfo info) {
		if (info.getPrivateKeyFile() != null && info.getPrivateKeyFile().length() > 0) {
			String path = PathUtils.combine(PathUtils.getParent(info.getPrivateKeyFile()),
					PathUtils.getFileName(info.getPrivateKeyFile()) + ".pub", File.separator);
			if (new File(path).exists()) {
				return path;
			}
		}
		return null;
	}

	public static void saveAuthorizedKeysFile(String authorizedKeys, SshFileSystem fileSystem) throws Exception {
		boolean found = false;
		try {
			fileSystem.getInfo(PathUtils.combineUnix(fileSystem.getHome(), ".ssh"));
			found = true;
		} catch (Exception e) {
		}
		if (!found) {
			fileSystem.mkdir(PathUtils.combineUnix(fileSystem.getHome(), ".ssh"));
		}
		OutputTransferChannel otc = fileSystem.outputTransferChannel();
		try (OutputStream out = otc
				.getOutputStream(PathUtils.combineUnix(fileSystem.getHome(), "/.ssh/authorized_keys"))) {
			out.write(authorizedKeys.getBytes("utf-8"));
		}
	}
}
