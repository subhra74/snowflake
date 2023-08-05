package muon.app.ui.components.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.App;
import muon.app.PasswordStore;

public class SessionExportImport {
	public static synchronized final void exportSessions() {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showSaveDialog(App.getAppWindow()) == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
				for (File f : new File(App.CONFIG_DIR).listFiles()) {
					ZipEntry ent = new ZipEntry(f.getName());
					out.putNextEntry(ent);
					out.write(Files.readAllBytes(f.toPath()));
					out.closeEntry();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized final boolean importSessions() {
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(App.getAppWindow()) != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		File f = jfc.getSelectedFile();
		if (JOptionPane.showConfirmDialog(App.getAppWindow(),
				"Existing data will be replaced.\nContinue?") != JOptionPane.YES_OPTION) {
			return false;
		}
		byte[] b = new byte[8192];
		try (ZipInputStream in = new ZipInputStream(new FileInputStream(f))) {
			ZipEntry ent = in.getNextEntry();
			File file = new File(App.CONFIG_DIR, ent.getName());
			try (OutputStream out = new FileOutputStream(file)) {
				while (true) {
					int x = in.read(b);
					if (x == -1)
						break;
					out.write(b, 0, x);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static synchronized final void importOnFirstRun() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			SavedSessionTree savedSessionTree = objectMapper.readValue(new File(System.getProperty("user.home")
					+ File.separator + "snowflake-ssh" + File.separator + "session-store.json"),
					new TypeReference<SavedSessionTree>() {
					});
			SessionStore.save(savedSessionTree.getFolder(), savedSessionTree.getLastSelection(),
					new File(App.CONFIG_DIR, App.SESSION_DB_FILE));
			Files.copy(Paths.get(System.getProperty("user.home"), "snowflake-ssh", "snippets.json"),
					Paths.get(App.CONFIG_DIR, App.SNIPPETS_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
