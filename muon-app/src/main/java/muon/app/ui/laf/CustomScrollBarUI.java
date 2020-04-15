package muon.app.ui.laf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import muon.app.App;

public class CustomScrollBarUI extends BasicScrollBarUI {
	private AtomicBoolean hot = new AtomicBoolean(false);

	public static ComponentUI createUI(JComponent c) {
		return new CustomScrollBarUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(UIManager.getColor("scrollbar"));
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hot.set(true);
				c.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hot.set(false);
				c.repaint();
			}
		});
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		Color color = (Color) c.getClientProperty("ScrollBar.background");
		Color cx = color == null ? c.getBackground() : color;
		g.setColor(cx);
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width,
				trackBounds.height);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (hot.get()) {
			Color color = (Color) c.getClientProperty("ScrollBar.hotColor");
			if (color == null) {
				g.setColor(UIManager.getColor("scrollbar-hot"));
			} else {
				g.setColor(color);
			}
		} else {
			g.setColor(c.getForeground());
		}
		g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width,
				thumbBounds.height);
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		JButton btn = new JButton();
		btn.setMaximumSize(new Dimension(0, 0));
		btn.setPreferredSize(new Dimension(0, 0));
		return btn;
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		JButton btn = new JButton();
		btn.setMaximumSize(new Dimension(0, 0));
		btn.setPreferredSize(new Dimension(0, 0));
		return btn;
	}

	/**
	 * @return the trackColor
	 */
	public Color getTrackColor() {
		return trackColor;
	}

	/**
	 * @param trackColor the trackColor to set
	 */
	public void setTrackColor(Color trackColor) {
		this.trackColor = trackColor;
	}
}
