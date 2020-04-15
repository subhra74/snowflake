/**
 * 
 */
package util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import muon.app.ui.laf.AppSkin;

/**
 * @author subhro
 *
 */
public class FontUtils {
	public static Font loadFont(String path) {
		try (InputStream is = AppSkin.class.getResourceAsStream(path)) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			return font.deriveFont(Font.PLAIN, 12.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
