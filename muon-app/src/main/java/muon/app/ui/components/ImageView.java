/**
 * 
 */
package muon.app.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * @author subhro
 *
 */
public class ImageView extends JComponent {
	private BufferedImage image;

	/**
	 * 
	 */
	public ImageView() {
//		try {
//			image = ImageIO.read(
//					new File("C:\\Users\\subhro\\Documents\\Capture.PNG"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

	public void setImage(String imgFile) {
		try {
			image = ImageIO.read(new File(imgFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (image != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		}
	}
}
