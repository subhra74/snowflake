/**
 * 
 */
package util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.Map;

import muonssh.app.ui.laf.AppSkin;

/**
 * @author subhro
 *
 */
public class FontUtils {
	public static final Map<String, String> TERMINAL_FONTS = new CollectionHelper.OrderedDict<String, String>()
			.putItem("DejaVuSansMono", "DejaVu Sans Mono").putItem("FiraCode-Regular", "Fira Code Regular")
			.putItem("Inconsolata-Regular", "Inconsolata Regular").putItem("NotoMono-Regular", "Noto Mono");

	public static Font loadFont(String path) {
		try (InputStream is = AppSkin.class.getResourceAsStream(path)) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			System.out.println("Font loaded: " + font.getFontName() + " of family: " + font.getFamily());
			return font.deriveFont(Font.PLAIN, 12.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Font loadTerminalFont(String name) {
		System.out.println("Loading font: "+name);
		try (InputStream is = AppSkin.class.getResourceAsStream(String.format("/fonts/terminal/%s.ttf", name))) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
			System.out.println("Font loaded: " + font.getFontName() + " of family: " + font.getFamily());
			return font.deriveFont(Font.PLAIN, 12.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
