package muon.app.ui.components.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import util.RegUtil;


public class PuttyImporter {
	private static final String PuttyREGKey = "Software\\SimonTatham\\PuTTY\\Sessions";

	public static Map<String, String> getKeyNames() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] keys = Advapi32Util
					.registryGetKeys(WinReg.HKEY_CURRENT_USER, PuttyREGKey);
			for (String key : keys) {
				String decodedKey = key.replace("%20", " ");
				map.put(key, decodedKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static void importSessions(DefaultMutableTreeNode node,
			List<String> keys) {

		// String[] keys =
		// Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER,
		// PuttyREGKey);
		for (String key : keys) {
			if ("ssh".equals(RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
					PuttyREGKey + "\\" + key, "Protocol"))) {
				String host = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "HostName");
				int port = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "PortNumber");
				String user = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "UserName");
				String keyfile = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "PublicKeyFile");

				String proxyHost = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "ProxyHost");
				int proxyPort = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "ProxyPort");
				String proxyUser = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "ProxyUsername");

				String proxyPass = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "ProxyPassword");

				int proxyType = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						PuttyREGKey + "\\" + key, "ProxyMethod");
				if (proxyType == 1) {
					proxyType = 2;
				} else if (proxyType == 2) {
					proxyType = 3;
				} else if (proxyType == 3) {
					proxyType = 1;
				} else {
					proxyType = 0;
				}
				SessionInfo info = new SessionInfo();
				info.setName(key);
				info.setHost(host);
				info.setPort(port);
				info.setUser(user);
				info.setPrivateKeyFile(keyfile);
				info.setProxyHost(proxyHost);
				info.setProxyPort(proxyPort);
				info.setProxyUser(proxyUser);
				info.setProxyPassword(proxyPass);
				info.setProxyType(proxyType);

				DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(info);
				node1.setAllowsChildren(false);
				node.add(node1);
			}
		}
	}

}
