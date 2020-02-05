/**
 * 
 */
package snowflake.components.newsession;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import snowflake.utils.RegUtil;

/**
 * @author subhro
 *
 */
public class WinScpImporter {
	private static final String WinSCPRegKey = "Software\\Martin Prikryl\\WinSCP 2\\Sessions";

	public static Map<String, String> getKeyNames() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			String[] keys = Advapi32Util
					.registryGetKeys(WinReg.HKEY_CURRENT_USER, WinSCPRegKey);
			for (String key : keys) {
				String decodedKey = key.replace("%20", " ");
				map.put(key, decodedKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(map);

		return map;
	}

	public static void importSessions(DefaultMutableTreeNode node,
			List<String> keys) {

		// String[] keys =
		// Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER,
		// WinSCPRegKey);
		for (String key : keys) {
			if (RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
					WinSCPRegKey + "\\" + key, "FSProtocol") == 0) {
				String host = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "HostName");
				int port = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "PortNumber");
				if (port == 0)
					port = 22;
				String user = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "UserName");
				String keyfile = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "PublicKeyFile");

				String proxyHost = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "ProxyHost");
				int proxyPort = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "ProxyPort");
				String proxyUser = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "ProxyUsername");

				String proxyPass = RegUtil.regGetStr(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "ProxyPassword");

				int proxyType = RegUtil.regGetInt(WinReg.HKEY_CURRENT_USER,
						WinSCPRegKey + "\\" + key, "ProxyMethod");
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

				info.setHost(host);
				info.setPort(port);
				info.setUser(user);
				try {
					if (keyfile != null && keyfile.length() > 0) {
						info.setPrivateKeyFile(
								URLDecoder.decode(keyfile, "utf-8"));
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				info.setProxyHost(proxyHost);
				info.setProxyPort(proxyPort);
				info.setProxyUser(proxyUser);
				info.setProxyPassword(proxyPass);
				info.setProxyType(proxyType);

				try {

					String[] arr = URLDecoder.decode(key, "utf-8").split("/");
					info.setName(arr[arr.length - 1]);

					DefaultMutableTreeNode parent = node;

					if (arr.length > 1) {
						for (int i = 0; i < arr.length - 1; i++) {

							DefaultMutableTreeNode parent2 = find(parent,
									arr[i]);
							if (parent2 == null) {
								SessionFolder folder = new SessionFolder();
								folder.setName(arr[i]);
								parent2 = new DefaultMutableTreeNode(folder);
								parent2.setAllowsChildren(true);
								parent.add(parent2);
							}

							parent = parent2;
						}

					}

					DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(
							info);
					node1.setAllowsChildren(false);
					parent.add(node1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static DefaultMutableTreeNode find(DefaultMutableTreeNode node,
			String name) {
		NamedItem item = (NamedItem) node.getUserObject();
		if (item.name.equals(name)) {
			return node;
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			if (child.getAllowsChildren()) {
				DefaultMutableTreeNode fn = find(child, name);
				if (fn != null)
					return fn;
			}
		}
		return null;
	}
}
