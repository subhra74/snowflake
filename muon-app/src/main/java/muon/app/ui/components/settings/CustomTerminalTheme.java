/**
 * 
 */
package muon.app.ui.components.settings;

import java.awt.Color;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;

import muon.app.App;

/**
 * @author subhro
 *
 */
public class CustomTerminalTheme implements TerminalTheme {

	@Override
	public String getName() {
		return "Custom";
	}

	@Override
	public String toString() {
		return getName();
	}

	public static final TerminalColor getTerminalColor(int rgb) {
		return TerminalColor.awt(new Color(rgb));
	}

	@Override
	public TextStyle getDefaultStyle() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultColorFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultColorBg()));
	}

	@Override
	public TextStyle getSelectionColor() {
		return new TextStyle(
				getTerminalColor(
						App.getGlobalSettings().getDefaultSelectionFg()),
				getTerminalColor(
						App.getGlobalSettings().getDefaultSelectionBg()));
	}

	@Override
	public TextStyle getFoundPatternColor() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultFoundFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultFoundFg()));
	}

	@Override
	public TextStyle getHyperlinkColor() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultHrefFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultHrefBg()));
	}
}
