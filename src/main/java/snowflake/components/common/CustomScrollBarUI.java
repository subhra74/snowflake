package snowflake.components.common;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import snowflake.utils.GraphicsUtils;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomScrollBarUI extends BasicScrollBarUI {
	private AtomicBoolean hot = new AtomicBoolean(false);

	public static ComponentUI createUI(JComponent c) {
		return new CustomScrollBarUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBackground(GraphicsUtils.getThemeColor(c, "control"));
		c.setForeground(GraphicsUtils.getThemeColor(c, "scrollbar"));
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
		g.setColor(c.getBackground());
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width,
				trackBounds.height);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (hot.get()) {
			Color color = (Color) c.getClientProperty("ScrollBar.hotColor");
			if (color == null) {
				g.setColor(GraphicsUtils.getThemeColor("scrollbar-hot"));
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
}
