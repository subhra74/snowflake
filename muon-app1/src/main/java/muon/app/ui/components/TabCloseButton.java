/**
 * 
 */
package muon.app.ui.components;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import muon.app.App;
import util.FontAwesomeContants;

/**
 * @author subhro
 *
 */
public class TabCloseButton extends JComponent {
	/**
	 * 
	 */
	private boolean hovering;
	private boolean selected;
	private Font font;

	public TabCloseButton() {
		setPreferredSize(new Dimension(20, 20));
		setMinimumSize(new Dimension(20, 20));
		setMaximumSize(new Dimension(20, 20));
		font = App.SKIN.getIconFont().deriveFont(14.0f);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovering = true;
				repaint(0);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
				repaint(0);
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		boolean drawButton = selected || hovering;
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (drawButton) {
			g2.setColor(getForeground());
			int size = Math.min(getHeight(), Math.min(getWidth(), 16));
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;

			g2.setFont(font);
			int acc = g2.getFontMetrics().getAscent();
			int w = g2.getFontMetrics()
					.stringWidth(FontAwesomeContants.FA_WINDOW_CLOSE);
			g2.drawString(FontAwesomeContants.FA_WINDOW_CLOSE, x, y + acc);

//			g2.setColor(getForeground());
//			g2.fillRoundRect(x, y, size, size, 4, 4);
//			g2.setColor(getBackground());
//			g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
//					BasicStroke.JOIN_ROUND));
//			int x1 = x + 5, y1 = y + 5, x2 = x + size - 5, y2 = y + size - 5;
//			g2.drawLine(x1, y1, x2, y2);
//			g2.drawLine(x1, y2, x2, y1);
		}
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		this.repaint(0);
	}

	/**
	 * @param hovering the hovering to set
	 */
	public void setHovering(boolean hovering) {
		this.hovering = hovering;
		this.repaint(0);
	}

}
