/**
 * 
 */
package muonssh.app.ssh;

import muonssh.app.App;

import javax.swing.*;

/**
 * @author subhro
 *
 */
public class GraphicalInputBlocker extends JDialog implements InputBlocker {
	private final JFrame window;

	//Todo devlinx9 fix this.
	private final JLabel connectingLabel = new JLabel(App.bundle.getString("connecting"));

	/**
	 * 
	 */
	public GraphicalInputBlocker(JFrame window) {
		super(window);
		this.window = window;
		setModal(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(200, 100);
	}

	@Override
	public void blockInput() {
		SwingUtilities.invokeLater(() -> {
			System.out.println("Making visible...");
			this.setLocationRelativeTo(window);
			//this.setUndecorated(true);
			this.add(connectingLabel);
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
