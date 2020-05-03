package muon.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.ssh.GraphicalInputBlocker;
import muon.app.ssh.InputBlocker;
import muon.app.ui.AppWindow;
import muon.app.ui.components.session.ExternalEditorHandler;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionExportImport;
import muon.app.ui.components.session.files.transfer.BackgroundFileTransfer;
import muon.app.ui.components.settings.SettingsPageName;
import muon.app.ui.laf.AppSkin;
import muon.app.ui.laf.AppSkinDark;
import muon.app.ui.laf.AppSkinLight;
import muon.app.updater.VersionEntry;
import util.PlatformUtils;

/**
 * Hello world!
 *
 */
public class App {
	static {
		System.setProperty("java.net.useSystemProxies", "true");
	}

	public static final VersionEntry VERSION = new VersionEntry("v1.0.5");
	public static final String UPDATE_URL = "https://subhra74.github.io/snowflake/check-update.html?v="
			+ VERSION.getNumericValue();

	public static final String CONFIG_DIR = System.getProperty("user.home") + File.separatorChar + "muon-ssh";
	public static final String SESSION_DB_FILE = "session-store.json";
	public static final String CONFIG_DB_FILE = "settings.json";
	public static final String SNIPPETS_FILE = "snippets.json";
	public static final String PINNED_LOGS = "pinned-logs.json";
	public static final String TRANSFER_HOSTS = "transfer-hosts.json";
	private static Settings settings;
	public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	public static final SnippetManager SNIPPET_MANAGER = new SnippetManager();
	private static InputBlocker inputBlocker;
	private static ExternalEditorHandler externalEditorHandler;
	private static AppWindow mw;
	public static final boolean IS_MAC = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH)
			.startsWith("mac");
	public static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH)
			.contains("windows");
	private static Map<String, List<String>> pinnedLogs = new HashMap<>();

	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");

		Security.setProperty("crypto.policy", "unlimited");

		Security.addProvider(new BouncyCastleProvider());

		boolean firstRun = false;

		File appDir = new File(CONFIG_DIR);
		if (!appDir.exists()) {
			appDir.mkdirs();
			firstRun = true;
		}

		loadSettings();

		if (settings.isManualScaling()) {
			System.setProperty("sun.java2d.uiScale.enabled", "true");
			System.setProperty("sun.java2d.uiScale", String.format("%.2f", settings.getUiScaling()));
		}

		if (firstRun) {
			SessionExportImport.importOnFirstRun();
		}

		if (settings.getEditors().size() == 0) {
			System.out.println("Searching for known editors...");
			settings.setEditors(PlatformUtils.getKnownEditors());
			saveSettings();
			System.out.println("Searching for known editors...done");
		}

		SKIN = settings.isUseGlobalDarkTheme() ? new AppSkinDark() : new AppSkinLight();

		UIManager.setLookAndFeel(SKIN.getLaf());

		try {
			int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
			System.out.println("maxKeySize: " + maxKeySize);
			if (maxKeySize < Integer.MAX_VALUE) {
				JOptionPane.showMessageDialog(null, "Unlimited cryptography is not enabled in JVM");
			}
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		// JediTerm seems to take a long time to load, this might make UI more
		// responsive
		App.EXECUTOR.submit(() -> {
			try {
				Class.forName("com.jediterm.terminal.ui.JediTermWidget");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		});

		mw = new AppWindow();
		inputBlocker = new GraphicalInputBlocker(mw);
		externalEditorHandler = new ExternalEditorHandler(mw);
		SwingUtilities.invokeLater(() -> {
			mw.setVisible(true);
		});
	}

	public synchronized static void loadSettings() {
		File file = new File(CONFIG_DIR, CONFIG_DB_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				settings = objectMapper.readValue(file, new TypeReference<Settings>() {
				});
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		settings = new Settings();
	}

	public synchronized static void saveSettings() {
		File file = new File(CONFIG_DIR, CONFIG_DB_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static Settings getGlobalSettings() {
		return settings;
	}

	public static AppSkin SKIN;// = new AppSkinDark();
	// public static final AppSkin SKIN = new AppSkinLight();

	/**
	 * @return the inputBlocker
	 */
	public static InputBlocker getInputBlocker() {
		return inputBlocker;
	}

	/**
	 * @return the externalEditorHandler
	 */
	public static ExternalEditorHandler getExternalEditorHandler() {
		return externalEditorHandler;
	}

	public static SessionContentPanel getSessionContainer(int activeSessionId) {
		return mw.getSessionListPanel().getSessionContainer(activeSessionId);
	}

	/**
	 * @return the pinnedLogs
	 */
	public static Map<String, List<String>> getPinnedLogs() {
		return pinnedLogs;
	}

	public synchronized static void loadPinnedLogs() {
		File file = new File(CONFIG_DIR, PINNED_LOGS);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				pinnedLogs = objectMapper.readValue(file, new TypeReference<Map<String, List<String>>>() {
				});
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pinnedLogs = new HashMap<String, List<String>>();
	}

	public synchronized static void savePinnedLogs() {
		File file = new File(CONFIG_DIR, PINNED_LOGS);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, pinnedLogs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void addUpload(BackgroundFileTransfer transfer) {
		mw.addUpload(transfer);
	}

	public static synchronized void addDownload(BackgroundFileTransfer transfer) {
		mw.addDownload(transfer);
	}

	public static synchronized void removePendingTransfers(int sessionId) {
		mw.removePendingTransfers(sessionId);
	}

	public static synchronized void openSettings(SettingsPageName page) {
		mw.openSettings(page);
	}

	public static synchronized AppWindow getAppWindow() {
		return mw;
	}

	private static final SecretKey generateKeys() {
		/*
		 * 
		 * SecretKeyFactory factory =
		 * SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); KeySpec spec = new
		 * PBEKeySpec(password, salt, 65536, 256); SecretKey tmp =
		 * factory.generateSecret(spec); SecretKey secret = new
		 * SecretKeySpec(tmp.getEncoded(), "AES");
		 * 
		 */

		KeyGenerator kgen;
		try {
			kgen = KeyGenerator.getInstance("AES");
			SecretKey skey = kgen.generateKey();
			try (OutputStream out = new FileOutputStream(new File(App.CONFIG_DIR, "key.dat"))) {
				byte[] keyb = skey.getEncoded();
				out.write(keyb);
				return skey;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
