package muon.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import muon.app.ui.components.session.SavedSessionTree;
import muon.app.ui.components.session.SessionFolder;
import muon.app.ui.components.session.SessionInfo;

public final class PasswordStore {
	private static KeyStore KEY_STORE;
	private static PasswordStore INSTANCE;

	private final AtomicBoolean unlocked = new AtomicBoolean(false);
	private KeyStore.PasswordProtection protParam;

	private PasswordStore() throws KeyStoreException {
		KEY_STORE = KeyStore.getInstance("PKCS12");
	}

	public static final synchronized PasswordStore getSharedInstance() throws Exception {
		if (INSTANCE == null) {
			INSTANCE = new PasswordStore();
		}
		return INSTANCE;
	}

	public final boolean isUnlocked() {
		return unlocked.get();
	}

	public final synchronized void unlockStore(char[] password) throws Exception {
		protParam = new KeyStore.PasswordProtection(password);
		File filePasswordStore = new File(App.CONFIG_DIR, "passwords.pfx");
		if (!filePasswordStore.exists()) {
			KEY_STORE.load(null, protParam.getPassword());
			unlocked.set(true);
			return;
		}
		try (InputStream in = new FileInputStream(filePasswordStore)) {
			KEY_STORE.load(in, protParam.getPassword());
			unlocked.set(true);
		}
	}

	public final synchronized char[] getSavedPassword(String alias) throws Exception {
		if (!unlocked.get()) {
			throw new IllegalAccessException("Password store is locked");
		}

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
		KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) KEY_STORE.getEntry(alias, protParam);

		PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(ske.getSecretKey(), PBEKeySpec.class);

		return keySpec.getPassword();
	}

	public final synchronized void savePassword(String alias, char[] password) throws Exception {
		if (!unlocked.get()) {
			throw new IllegalAccessException("Password store is locked");
		}

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBE");
		SecretKey generatedSecret = secretKeyFactory.generateSecret(new PBEKeySpec(password));
		KEY_STORE.setEntry(alias, new SecretKeyEntry(generatedSecret), protParam);
	}

	public final synchronized void saveKeyStore()
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		try (OutputStream out = new FileOutputStream(new File(App.CONFIG_DIR, "passwords.pfx"))) {
			KEY_STORE.store(out, protParam.getPassword());
		}
	}

	private final boolean unlockStore() {
		if (this.isUnlocked()) {
			return true;
		}

		if (App.getGlobalSettings().isUsingMasterPassword()) {
			if (!unlockUsingMasterPassword()) {
				return false;
			}
			return true;
		} else {
			try {
				unlockStore(new char[0]);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

	}

	public final synchronized void populatePassword(SavedSessionTree savedSessionTree) {
		if (!unlockStore()) {
			return;
		}
		if (savedSessionTree != null) {
			populatePassword(savedSessionTree.getFolder());
		}

	}

	private void populatePassword(SessionFolder folder) {
		for (SessionInfo info : folder.getItems()) {
			try {
				char[] password = this.getSavedPassword(info.getId());
				info.setPassword(new String(password));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		for (SessionFolder f : folder.getFolders()) {
			populatePassword(f);
		}
	}

	public void savePasswords(SavedSessionTree savedSessionTree) {
		if (!this.isUnlocked()) {
			if (App.getGlobalSettings().isUsingMasterPassword()) {
				if (!unlockUsingMasterPassword()) {
					return;
				}
			} else {
				try {
					unlockStore(new char[0]);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
		savePassword(savedSessionTree.getFolder());
		try {
			saveKeyStore();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void savePassword(SessionFolder folder) {
		for (SessionInfo info : folder.getItems()) {
			String password = info.getPassword();
			if (password != null && password.length() > 0) {
				try {
					savePassword(info.getId(), password.toCharArray());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for (SessionFolder f : folder.getFolders()) {
			savePassword(f);
		}
	}

	private boolean unlockUsingMasterPassword() {
		while (true) {
			try {
				JPasswordField txtPass = new JPasswordField(30);
				if (JOptionPane.showOptionDialog(App.getAppWindow(), new Object[] { "Master password", txtPass },
						"Master password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
						null) == JOptionPane.OK_OPTION) {
					this.unlockStore(txtPass.getPassword());
					return true;
				}
			} catch (IOException e) {
				if (e.getCause() instanceof UnrecoverableKeyException) {
					if (JOptionPane.showConfirmDialog(App.getAppWindow(),
							"Incorrect password.\nTry again?") != JOptionPane.YES_OPTION) {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (JOptionPane.showConfirmDialog(App.getAppWindow(),
					"Error loading saved passwords\nTry again?") != JOptionPane.YES_OPTION) {
				break;
			}
		}
		return false;
	}

	public boolean changeStorePassword(char[] newPassword) throws Exception {
		if (!unlockStore()) {
			return false;
		}

		Enumeration<String> aliases = KEY_STORE.aliases();
		Map<String, char[]> passMap = new HashMap<String, char[]>();

		while (aliases.hasMoreElements()) {
			String alias = aliases.nextElement();
			passMap.put(alias, getSavedPassword(alias));
			KEY_STORE.deleteEntry(alias);
		}

		protParam = new KeyStore.PasswordProtection(newPassword);
		for (String alias : passMap.keySet()) {
			savePassword(alias, passMap.get(alias));
		}
		saveKeyStore();
		return true;
	}
}
