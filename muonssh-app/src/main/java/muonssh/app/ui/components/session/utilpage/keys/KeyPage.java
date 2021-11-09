/**
 * 
 */
package muonssh.app.ui.components.session.utilpage.keys;

import javax.swing.SwingUtilities;

import muonssh.app.App;
import muonssh.app.ui.components.TabbedPanel;
import muonssh.app.ui.components.session.SessionContentPanel;
import muonssh.app.ui.components.session.utilpage.UtilPageItemView;

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
		tabs.addTab(App.bundle.getString("server"), remoteKeyPanel);
		tabs.addTab(App.bundle.getString("local_computer"), localKeyPanel);
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
