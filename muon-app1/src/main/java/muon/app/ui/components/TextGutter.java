/**
 * 
 */
package muon.app.ui.components;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTextArea;

/**
 * @author subhro
 *
 */
public class TextGutter extends JComponent {
	private JTextArea textArea;
	private int digitCount;
	private long lineStart;

	/**
	 * 
	 */
	public TextGutter(JTextArea textArea, int digitCount) {
		this.textArea = textArea;
		this.digitCount = digitCount;
		this.lineStart = 1;
	}

	public TextGutter(JTextArea textArea) {
		this(textArea, 3);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = textArea.getPreferredSize();
		FontMetrics fm = getFontMetrics(getFont());
		int w = fm.charWidth('w');
		return new Dimension(digitCount * w + 20, d.height);
	}

	@Override
	protected void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());

		FontMetrics fm = g.getFontMetrics();
		int asc = fm.getAscent();

		try {
			for (int i = 0; i < textArea.getLineCount(); i++) {
				String lineNum = (lineStart + i) + "";
				int startIndex = textArea.getLineStartOffset(i);
				double y = textArea.modelToView2D(startIndex).getY();
				int x = getWidth() / 2 - fm.stringWidth(lineNum) / 2;
				g.drawString(lineNum, x, (int) (y + asc));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the digitCount
	 */
	public int getDigitCount() {
		return digitCount;
	}

	/**
	 * @param digitCount the digitCount to set
	 */
	public void setDigitCount(int digitCount) {
		this.digitCount = digitCount;
		revalidate();
		repaint(0);
	}

	/**
	 * @return the lineStart
	 */
	public long getLineStart() {
		return lineStart;
	}

	/**
	 * @param lineStart the lineStart to set
	 */
	public void setLineStart(long lineStart) {
		this.lineStart = lineStart;
		revalidate();
		repaint(0);
	}
}
