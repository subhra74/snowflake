/**
 * 
 */
package muon.app.ui.components.settings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;

/**
 * @author subhro
 *
 */
public class ColorSelectorButton extends JLabel {
	/**
	 * 
	 */
	private Color color;

	public ColorSelectorButton() {
		setBorder(new CompoundBorder(
				new LineBorder(App.SKIN.getDefaultBorderColor()),
				new CompoundBorder(
						new MatteBorder(5, 5, 5, 5,
								App.SKIN.getSelectedTabColor()),
						new LineBorder(App.SKIN.getDefaultBorderColor()))));
		setOpaque(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Color color = JColorChooser.showDialog(null, "Select color",
						getColor());
				if (color != null) {
					setColor(color);
				}
			}
		});
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50, 30);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.setBackground(color);
		this.color = color;
	}

}
