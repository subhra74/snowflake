/**
 * 
 */
package muon.app.ui.components.session.utilpage.keys;

import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import muon.app.App;
import muon.app.ui.components.TabbedPanel;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.utilpage.UtilPageItemView;

/**
 * @author subhro
 *
 */
public class KeyPage extends UtilPageItemView {
	private RemoteKeyPanel remoteKeyPanel;
	private LocalKeyPanel localKeyPanel;
	private TabbedPanel tabs;
	private SshKeyHolder keyHolder;

	/**
	 * 
	 */
	public KeyPage(SessionContentPanel content) {
		super(content);
	}

	private void setKeyData(SshKeyHolder holder) {
		System.out.println("Holder: " + holder);
		this.localKeyPanel.setKeyData(holder);
		this.remoteKeyPanel.setKeyData(holder);
	}

	@Override
	protected void createUI() {
		keyHolder = new SshKeyHolder();
		tabs = new TabbedPanel();
		remoteKeyPanel = new RemoteKeyPanel(holder.getInfo(), a -> {
			holder.disableUi();
			holder.EXECUTOR.submit(() -> {
				try {
					SshKeyManager.generateKeys(keyHolder,
							holder.getRemoteSessionInstance(), false);
					SwingUtilities.invokeLater(() -> {
						setKeyData(keyHolder);
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		}, a -> {
			holder.disableUi();
			holder.EXECUTOR.submit(() -> {
				try {
					keyHolder = SshKeyManager.getKeyDetails(holder);
					SwingUtilities.invokeLater(() -> {
						setKeyData(keyHolder);
					});

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		}, a -> {
			holder.disableUi();
			holder.EXECUTOR.submit(() -> {
				try {
					SshKeyManager.saveAuthorizedKeysFile(a,
							holder.getRemoteSessionInstance().getSshFs());
					keyHolder = SshKeyManager.getKeyDetails(holder);
					SwingUtilities.invokeLater(() -> {
						setKeyData(keyHolder);
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		});
		localKeyPanel = new LocalKeyPanel(holder.getInfo(), a -> {
			holder.disableUi();
			holder.EXECUTOR.submit(() -> {
				try {
					SshKeyManager.generateKeys(keyHolder,
							holder.getRemoteSessionInstance(), true);
					SwingUtilities.invokeLater(() -> {
						setKeyData(keyHolder);
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		}, a -> {
			holder.disableUi();
			holder.EXECUTOR.submit(() -> {
				try {
					keyHolder = SshKeyManager.getKeyDetails(holder);
					SwingUtilities.invokeLater(() -> {
						setKeyData(keyHolder);
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		});
		tabs.addTab("Server", remoteKeyPanel);
		tabs.addTab("Local computer", localKeyPanel);
		this.add(tabs);

		holder.EXECUTOR.submit(() -> {
			holder.disableUi();
			try {
				keyHolder = SshKeyManager.getKeyDetails(holder);
				SwingUtilities.invokeLater(() -> {
					setKeyData(keyHolder);
				});
			} catch (Exception err) {
				err.printStackTrace();
			} finally {
				holder.enableUi();
			}
		});
	}

	@Override
	protected void onComponentVisible() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onComponentHide() {
		// TODO Auto-generated method stub

	}
}
