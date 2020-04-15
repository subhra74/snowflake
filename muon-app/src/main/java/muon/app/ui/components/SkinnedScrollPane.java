/**
 * 
 */
package muon.app.ui.components;

import java.awt.Component;
import java.awt.ScrollPane;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * @author subhro
 *
 */
public class SkinnedScrollPane extends JScrollPane {

	/**
	 * 
	 */
	public SkinnedScrollPane() {
	}

	public SkinnedScrollPane(Component c) {
		super(c);
		this.setBackground(c.getBackground());
		this.getViewport().setBackground(c.getBackground());
		// System.out.println("bgcolor: " + this.getBackground());

		if (horizontalScrollBar != null) {
			horizontalScrollBar.putClientProperty("ScrollBar.background",
					c.getBackground());
		}

		if (verticalScrollBar != null) {
			verticalScrollBar.putClientProperty("ScrollBar.background",
					c.getBackground());
		}

		JLabel lbl = new JLabel();
		lbl.setOpaque(true);
		lbl.setBackground(c.getBackground());

		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, lbl);

//		horizScroll = new JScrollBar(JScrollBar.HORIZONTAL);
//		vertScroll = new JScrollBar(JScrollBar.VERTICAL);
//		horizScroll.putClientProperty("ScrollBar.background",
//				c.getBackground());
//		vertScroll.putClientProperty("ScrollBar.background", c.getBackground());
	}

//	@Override
//	public JScrollBar createHorizontalScrollBar() {
//		return horizScroll;
////		System.out.println("called horiz " + this.getBackground());
////		JScrollBar scrollbar = new ScrollBar(JScrollBar.HORIZONTAL);
////		scrollbar.putClientProperty("ScrollBar.background",
////				this.getBackground());
//////		CustomScrollBarUI scrollBarUI = new CustomScrollBarUI();
//////		scrollBarUI.setTrackColor(this.getBackground());
//////		scrollbar.setUI(scrollBarUI);
////		return scrollbar;
//	}
//
//	@Override
//	public JScrollBar createVerticalScrollBar() {
//		return vertScroll;
////		System.out.println("called vert");
////		JScrollBar scrollbar = super.createVerticalScrollBar();
////		scrollbar.putClientProperty("ScrollBar.background",
////				this.getBackground());
//////		CustomScrollBarUI scrollBarUI = new CustomScrollBarUI();
//////		scrollBarUI.setTrackColor(this.getBackground());
//////		scrollbar.setUI(scrollBarUI);
////		return scrollbar;
//	}
}
