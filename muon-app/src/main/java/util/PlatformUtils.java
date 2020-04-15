/**
 * 
 */
package util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * @author subhro
 *
 */
public class PlatformUtils {
	public static void openWithDefaultApp(File file) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
