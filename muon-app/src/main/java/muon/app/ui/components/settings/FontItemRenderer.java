/**
 * 
 */
package muon.app.ui.components.settings;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import muon.app.App;

/**
 * @author subhro
 *
 */
public class FontItemRenderer extends JLabel
		implements ListCellRenderer<String> {

	/**
	 * 
	 */
	public FontItemRenderer() {
		setOpaque(true);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus) {
		Font font = new Font(value, Font.PLAIN, 14);
		setFont(font);
		setText(value);
		setBackground(isSelected ? App.SKIN.getAddressBarSelectionBackground()
				: App.SKIN.getSelectedTabColor());
		setForeground(isSelected ? App.SKIN.getDefaultSelectionForeground()
				: App.SKIN.getDefaultForeground());
		return this;
	}

}
