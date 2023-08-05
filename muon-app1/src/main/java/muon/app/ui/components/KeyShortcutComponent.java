/**
 * 
 */
package muon.app.ui.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import muon.app.App;

/**
 * @author subhro
 *
 */
public class KeyShortcutComponent extends JComponent {

	/**
	 * 
	 */
	private int keyCode = Integer.MIN_VALUE;
	private int modifier;
	private static final String WAITING_STRING = "Please press the key combination";
	private boolean waitingForKeys = false;

	public KeyShortcutComponent() {
		setFocusable(true);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!waitingForKeys)
					return;
				keyCode = e.getExtendedKeyCode();
				modifier = e.getModifiersEx();
				waitingForKeys = false;
				revalidate();
				repaint(0);

//				System.out.println("Key released");
//				System.out.println(e.getKeyChar() + " "
//						+ KeyEvent.getKeyText(e.getExtendedKeyCode()) + " -- "
//						+ KeyEvent.getModifiersExText(e.getModifiersEx()));
			}

//			@Override
//			public void keyTyped(KeyEvent e) {
//				System.out.println("Key typed...");
//				if (!waitingForKeys
//						|| e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
//					return;
//				}
//				keyCode = e.getKeyCode();
//				System.out.println(e.getKeyChar() + " "
//						+ KeyEvent.getKeyText(e.getExtendedKeyCode()) + " -- "
//						+ KeyEvent.getModifiersExText(e.getModifiersEx()));
//				shiftDown = e.isShiftDown();
//				altDown = e.isAltDown();
//				ctrlDown = e.isControlDown();
//				waitingForKeys = false;
//				repaint(0);
//			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				waitingForKeys = !waitingForKeys;
				requestFocusInWindow();
				revalidate();
				repaint(0);
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(App.SKIN.getSelectedTabColor());

		Insets inset = getInsets();

		g2.fillRoundRect(inset.left, inset.top,
				getWidth() - inset.left - inset.right,
				getHeight() - inset.top - inset.bottom, 5, 5);

		String text = getText();
		g2.setColor(getForeground());
		g2.setFont(getFont());
		int stringWidth = g2.getFontMetrics().stringWidth(text);
		int stringHeight = g2.getFontMetrics().getHeight();
		int x = getWidth() / 2 - stringWidth / 2;
		int y = getHeight() / 2 - stringHeight / 2;
		g2.drawString(text, x, y + g2.getFontMetrics().getAscent());
		g2.dispose();
	}

	private String getText() {
		if (!waitingForKeys) {
			if (keyCode == Integer.MIN_VALUE) {
				return "Not configured";
			}
			String txtModifier = KeyEvent.getModifiersExText(modifier);
			String txtKeyText = KeyEvent.getKeyText(keyCode);
			return txtModifier == null || txtModifier.length() < 1 ? txtKeyText
					: txtModifier + "+" + txtKeyText;
		} else {
			return WAITING_STRING;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		FontMetrics fmt = getFontMetrics(getFont());
		int width = fmt.stringWidth(getText());
		int height = fmt.getHeight();
		Insets insets = getInsets();
		return new Dimension(width + insets.left + insets.right + 6,
				height + insets.top + insets.bottom + 6);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * @return the keyCode
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * @param keyCode the keyCode to set
	 */
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	/**
	 * @return the modifier
	 */
	public int getModifier() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

}
