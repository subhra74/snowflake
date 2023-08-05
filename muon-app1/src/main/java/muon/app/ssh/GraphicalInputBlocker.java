/**
 * 
 */
package muon.app.ssh;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author subhro
 *
 */
public class GraphicalInputBlocker extends JDialog implements InputBlocker {
	private JFrame window;

	/**
	 * 
	 */
	public GraphicalInputBlocker(JFrame window) {
		super(window);
		this.window = window;
		setModal(true);
		setSize(400, 300);
	}

	@Override
	public void blockInput() {
		SwingUtilities.invokeLater(() -> {
			System.out.println("Making visible...");
			this.setLocationRelativeTo(window);
			this.setVisible(true);
		});
	}

	@Override
	public void unblockInput() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(false);
		});
	}

}
