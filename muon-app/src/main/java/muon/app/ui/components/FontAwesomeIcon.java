package muon.app.ui.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

import muon.app.App;

public class FontAwesomeIcon implements Icon {
	private int width, height;
	private String FA_ICON;

	public FontAwesomeIcon(String FA_ICON, int width, int height) {
		this.FA_ICON = FA_ICON;
		this.width = width;
		this.height = height;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(App.SKIN.getIconFont().deriveFont((float)height));
		int ypos = g2.getFontMetrics().getAscent(); // + height / 2;
		g2.drawString(FA_ICON, x, ypos);
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

}
