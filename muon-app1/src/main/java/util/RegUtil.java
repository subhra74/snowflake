/**
 * 
 */
package util;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * @author subhro
 *
 */
public class RegUtil {
	public static String regGetStr(WinReg.HKEY hkey, String key,
			String value) {
		try {
			return Advapi32Util.registryGetStringValue(hkey, key, value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	public static int regGetInt(WinReg.HKEY hkey, String key, String value) {
		try {
			return Advapi32Util.registryGetIntValue(hkey, key, value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return 0;
	}
}
