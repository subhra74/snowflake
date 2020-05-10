/**
 * 
 */
package muon.app.ui.components.settings;

import com.jediterm.terminal.TextStyle;

/**
 * @author subhro
 *
 */
public interface TerminalTheme {
	String getName();

	TextStyle getDefaultStyle();

	TextStyle getSelectionColor();

	TextStyle getFoundPatternColor();

	TextStyle getHyperlinkColor();

	String toString();
}
