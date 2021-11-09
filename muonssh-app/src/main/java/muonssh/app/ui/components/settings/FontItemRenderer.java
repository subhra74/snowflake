/**
 * 
 */
package muonssh.app.ui.components.settings;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import muonssh.app.App;
import util.FontUtils;

/**
 * @author subhro
 *
 */
public class FontItemRenderer extends JLabel implements ListCellRenderer<String> {

	/**
	 * 
	 */
	public FontItemRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		System.out.println("Creating font in renderer: " + value);
		Font font = FontUtils.loadTerminalFont(value).deriveFont(Font.PLAIN, 14);
		setFont(font);
		setText(FontUtils.TERMINAL_FONTS.get(value));
		setBackground(isSelected ? App.SKIN.getAddressBarSelectionBackground() : App.SKIN.getSelectedTabColor());
		setForeground(isSelected ? App.SKIN.getDefaultSelectionForeground() : App.SKIN.getDefaultForeground());
		return this;
	}

}
