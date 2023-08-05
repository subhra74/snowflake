/**
 * 
 */
package muon.app.ui.components.settings;

import java.awt.Color;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;

/**
 * @author subhro
 *
 */
public class DarkTerminalTheme implements TerminalTheme {
	private String name;
	public static final int DEF_FG = 0xc8c8c8, DEF_BG = 0x282c34,
			SEL_FG = 0xe6e6e6, SEL_BG = 0x039be5, FIND_FG = 0xe6e6e6,
			FIND_BG = 0x4d525e, HREF_FG = 0xf0f0f0, HREF_BG = 0x282c34;

	/**
	 * 
	 */
	public static final TerminalColor getTerminalColor(int rgb) {
		return TerminalColor.awt(new Color(rgb));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TextStyle getDefaultStyle() {
		return new TextStyle(getTerminalColor(DEF_FG),
				getTerminalColor(DEF_BG));
	}

	@Override
	public TextStyle getSelectionColor() {
		return new TextStyle(getTerminalColor(SEL_FG),
				getTerminalColor(SEL_BG));
	}

	@Override
	public TextStyle getFoundPatternColor() {
		return new TextStyle(getTerminalColor(FIND_FG),
				getTerminalColor(FIND_BG));
	}

	@Override
	public TextStyle getHyperlinkColor() {
		return new TextStyle(getTerminalColor(HREF_FG),
				getTerminalColor(HREF_BG));
	}

	@Override
	public String toString() {
		return "Dark";
	}
}
