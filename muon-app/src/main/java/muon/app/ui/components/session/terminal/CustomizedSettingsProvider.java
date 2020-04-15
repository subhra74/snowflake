/**
 * 
 */
package muon.app.ui.components.session.terminal;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import muon.app.App;
import muon.app.Settings;

/**
 * @author subhro
 *
 */
public class CustomizedSettingsProvider extends DefaultSettingsProvider {
	private ColorPalette palette;

	/**
	 * 
	 */
	public CustomizedSettingsProvider() {

		Color[] colors = new Color[16];
		int[] colorArr = App.getGlobalSettings().getPalleteColors();
		for (int i = 0; i < 16; i++) {
			colors[i] = new Color(colorArr[i]);
		}
		palette = new ColorPalette() {

			@Override
			public Color[] getIndexColors() {
				return colors;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
	 * getTerminalColorPalette()
	 */
	@Override
	public ColorPalette getTerminalColorPalette() {
		return palette;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
	 * useAntialiasing()
	 */
	@Override
	public boolean useAntialiasing() {
		return true;
	}

	@Override
	public TextStyle getDefaultStyle() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultColorFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultColorBg()));
		// return terminalTheme.getDefaultStyle();
	}

	@Override
	public TextStyle getFoundPatternColor() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultFoundFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultFoundBg()));
		// return terminalTheme.getFoundPatternColor();
	}

	@Override
	public TextStyle getSelectionColor() {
		return new TextStyle(
				getTerminalColor(
						App.getGlobalSettings().getDefaultSelectionFg()),
				getTerminalColor(
						App.getGlobalSettings().getDefaultSelectionBg()));
		//
	}

	@Override
	public TextStyle getHyperlinkColor() {
		return new TextStyle(
				getTerminalColor(App.getGlobalSettings().getDefaultHrefFg()),
				getTerminalColor(App.getGlobalSettings().getDefaultHrefBg()));
		// return
		// terminalTheme.getHyperlinkColor();
	}

	@Override
	public boolean emulateX11CopyPaste() {
		return App.getGlobalSettings().isPuttyLikeCopyPaste();
	}

	@Override
	public boolean enableMouseReporting() {
		return true;
	}

//	@Override
//	public Font getTerminalFont() {
//		return UIManager.getFont("Terminal.font");
//	}

	@Override
	public boolean pasteOnMiddleMouseClick() {
		return App.getGlobalSettings().isPuttyLikeCopyPaste();
	}

	@Override
	public boolean copyOnSelect() {
		return App.getGlobalSettings().isPuttyLikeCopyPaste();
	}

	@Override
	public Font getTerminalFont() {
		return new Font(App.getGlobalSettings().getTerminalFontName(),
				Font.PLAIN, App.getGlobalSettings().getTerminalFontSize());
	}

	@Override
	public float getTerminalFontSize() {
		return App.getGlobalSettings().getTerminalFontSize();
	}

	@Override
	public boolean audibleBell() {
		return App.getGlobalSettings().isTerminalBell();
	}

	public final TerminalColor getTerminalColor(int rgb) {
		return TerminalColor.awt(new Color(rgb));
	}

	@Override
	public KeyStroke[] getCopyKeyStrokes() {
		return new KeyStroke[] { getKeyStroke(Settings.COPY_KEY) };
	}

	@Override
	public KeyStroke[] getPasteKeyStrokes() {
		return new KeyStroke[] { getKeyStroke(Settings.PASTE_KEY) };
	}

	@Override
	public KeyStroke[] getClearBufferKeyStrokes() {
		return new KeyStroke[] { getKeyStroke(Settings.CLEAR_BUFFER) };
	}

	@Override
	public KeyStroke[] getFindKeyStrokes() {
		return new KeyStroke[] { getKeyStroke(Settings.FIND_KEY) };
	}

	private KeyStroke getKeyStroke(String key) {
		return KeyStroke.getKeyStroke(
				App.getGlobalSettings().getKeyCodeMap().get(key),
				App.getGlobalSettings().getKeyModifierMap().get(key));
	}

}
