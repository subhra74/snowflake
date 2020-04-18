/**
 * 
 */
package util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.ShellAPI;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.win32.StdCallLibrary;

import muon.app.App;
import muon.app.ui.components.settings.EditorEntry;

/**
 * @author subhro
 *
 */
public class PlatformUtils {
	public static void openWithDefaultApp(File file, boolean openWith) throws IOException {
		String os = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH);
		if (os.contains("mac")) {
			openMac(file);
		} else if (os.contains("linux")) {
			openLinux(file);
		} else if (os.contains("windows")) {
			openWin(file, openWith);
		} else {
			throw new IOException("Unsupported OS: '" + System.getProperty("os.name", "") + "'");
		}
//		if (App.IS_MAC) {
//
//		} else if (System.getProperty("os.name").contains("windows"))
//
//			if (Desktop.isDesktopSupported()) {
//				try {
//					Desktop.getDesktop().open(file);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
	}

	public static void openWithApp(File f, String app) throws Exception {
		new ProcessBuilder(app, f.getAbsolutePath()).start();
	}

	public static void openWin(File f, boolean openWith) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException();
		}

		if (openWith) {
			try {
				System.out.println("Opening with rulldll");
				ProcessBuilder builder = new ProcessBuilder();
				builder.command(Arrays.asList("rundll32", "shell32.dll,OpenAs_RunDLL", f.getAbsolutePath()));
				builder.start();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		try {
			Shell32 INSTANCE = (Shell32) Native.load("shell32", Shell32.class);
			WinDef.HWND h = null;
			WString file = new WString(f.getAbsolutePath());
			INSTANCE.ShellExecuteW(h, new WString("open"), file, null, null, 1);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				ProcessBuilder builder = new ProcessBuilder();
				builder.command(Arrays.asList("rundll32", "url.dll,FileProtocolHandler", f.getAbsolutePath()));
				builder.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public static void openLinux(final File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("xdg-open", f.getAbsolutePath());
			pb.start();// .waitFor();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void openMac(final File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException();
		}
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("open", f.getAbsolutePath());
			if (pb.start().waitFor() != 0) {
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * @param folder folder to open in explorer
	 * @param file   if any file needs to be selected in folder, mentioned in
	 *               previous argument
	 * @throws FileNotFoundException
	 */
	public static void openFolderInExplorer(String folder, String file) throws FileNotFoundException {
		if (file == null) {
			openFolder2(folder);
			return;
		}
		try {
			File f = new File(folder, file);
			if (!f.exists()) {
				throw new FileNotFoundException();
			}
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(Arrays.asList("explorer", "/select,", f.getAbsolutePath()));
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void openFolder2(String folder) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(Arrays.asList("explorer", folder));
			builder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface Shell32 extends ShellAPI, StdCallLibrary {
		WinDef.HINSTANCE ShellExecuteW(WinDef.HWND hwnd, WString lpOperation, WString lpFile, WString lpParameters,
				WString lpDirectory, int nShowCmd);
	}

	public static List<EditorEntry> getKnownEditors() {
		List<EditorEntry> list = new ArrayList<EditorEntry>();
		if (App.IS_WINDOWS) {
			try {
				String vscode = detectVSCode(false);
				if (vscode != null) {
					EditorEntry ent = new EditorEntry("Visual Studio Code", vscode);
					list.add(ent);
				}
			} catch (Exception e) {
				e.printStackTrace();
				String vscode = detectVSCode(true);
				if (vscode != null) {
					EditorEntry ent = new EditorEntry("Visual Studio Code", vscode);
					list.add(ent);
				}
			}

			try {
				String npp = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
						"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Notepad++", "DisplayIcon");
				EditorEntry ent = new EditorEntry("Notepad++", npp);
				list.add(ent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String atom = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
						"Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\atom", "InstallLocation");
				EditorEntry ent = new EditorEntry("Atom", atom + "\\atom.exe");
				list.add(ent);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					String atom = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
							"Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\atom", "InstallLocation");
					EditorEntry ent = new EditorEntry("Atom", atom + "\\atom.exe");
					list.add(ent);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}
		return list;
	}

	private static String detectVSCode(boolean hklm) {
		HKEY hkey = hklm ? WinReg.HKEY_LOCAL_MACHINE : WinReg.HKEY_CURRENT_USER;
		String[] keys = Advapi32Util.registryGetKeys(hkey, "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall");
		for (String key : keys) {
			Map<String, Object> values = Advapi32Util.registryGetValues(hkey,
					"Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key);
			if (values.containsKey("DisplayName")) {
				String text = (values.get("DisplayName") + "").toLowerCase(Locale.ENGLISH);
				if (text.contains("visual studio code")) {
					return values.get("DisplayIcon") + "";
				}
			}
		}
		return null;
	}

}
