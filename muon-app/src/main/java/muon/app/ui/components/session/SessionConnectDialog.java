/**
 * 
 */
package muon.app.ui.components.session;

import javax.swing.JDialog;

import muon.app.ui.AppWindow;

/**
 * @author subhro
 *
 */
public class SessionConnectDialog extends JDialog implements GUIBlocker {
	/**
	 * 
	 */
	private AppWindow appWindow;

	public SessionConnectDialog(AppWindow appWindow) {
		super(appWindow);
		this.setSize(400, 300);
		this.setModal(true);
		this.appWindow = appWindow;
	}

	public void connectSession(SessionInfo info) {

	}

	@Override
	public void blockInterface() {
		setLocationRelativeTo(appWindow);
		setVisible(true);
	}

	@Override
	public void unBlockInterface() {
		setVisible(false);
	}
}
