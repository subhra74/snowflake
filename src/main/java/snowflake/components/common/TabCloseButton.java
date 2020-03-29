/**
 * 
 */
package snowflake.components.common;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

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

	public TabCloseButton() {
		setPreferredSize(new Dimension(24, 24));
		setMinimumSize(new Dimension(24, 24));
		setMaximumSize(new Dimension(24, 24));
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
		if (drawButton) {
			int size = Math.min(getHeight(), Math.min(getWidth(), 16));
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g2.setColor(getForeground());
			g2.fillRoundRect(x, y, size, size, 4, 4);
			g2.setColor(getBackground());
			g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			int x1 = x + 5, y1 = y + 5, x2 = x + size - 5, y2 = y + size - 5;
			g2.drawLine(x1, y1, x2, y2);
			g2.drawLine(x1, y2, x2, y1);
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
