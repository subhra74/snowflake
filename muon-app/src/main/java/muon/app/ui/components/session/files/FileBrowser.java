package muon.app.ui.components.session.files;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import muon.app.common.FileInfo;
import muon.app.common.FileSystem;
import muon.app.ssh.RemoteSessionInstance;
import muon.app.ssh.SshFileSystem;
import muon.app.ui.components.ClosableTabbedPanel;
import muon.app.ui.components.SkinnedSplitPane;
import muon.app.ui.components.session.Page;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.files.AbstractFileBrowserView.PanelOrientation;
import muon.app.ui.components.session.files.local.LocalFileBrowserView;
import muon.app.ui.components.session.files.ssh.SshFileBrowserView;
import muon.app.ui.components.session.files.transfer.FileTransfer;
import muon.app.ui.components.session.files.transfer.FileTransferProgress;
import muon.app.ui.components.session.files.transfer.FileTransfer.ConflictAction;
import util.FontAwesomeContants;

public class FileBrowser extends Page {
	private FileTransfer ongoingFileTransfer;
	private JSplitPane horizontalSplitter;
	private ClosableTabbedPanel leftTabs, rightTabs;
	private SessionContentPanel holder;
	private SessionInfo info;
	private Map<String, List<FileInfo>> sshDirCache = new HashMap<>();
	private int activeSessionId;
	private AtomicBoolean init = new AtomicBoolean(false);
	private JPopupMenu popup;
	private boolean leftPopup = false;

	public FileBrowser(SessionInfo info, SessionContentPanel holder, JRootPane rootPane, int activeSessionId) {
		this.activeSessionId = activeSessionId;
		this.info = info;
		this.holder = holder;

		JMenuItem localMenuItem = new JMenuItem("Local file browser");
		JMenuItem remoteMenuItem = new JMenuItem("Remote file browser");

		popup = new JPopupMenu();
		popup.add(remoteMenuItem);
		popup.add(localMenuItem);
		popup.pack();

		localMenuItem.addActionListener(e -> {
			if (leftPopup) {
				openLocalFileBrowserView(null, PanelOrientation.Left);
			} else {
				openSshFileBrowserView(null, PanelOrientation.Right);
			}
		});

		remoteMenuItem.addActionListener(e -> {
			if (leftPopup) {
				openLocalFileBrowserView(null, PanelOrientation.Left);
			} else {
				openSshFileBrowserView(null, PanelOrientation.Right);
			}
		});

		this.leftTabs = new ClosableTabbedPanel(c -> {
			popup.setInvoker(c);
			leftPopup = true;
			popup.show(c, 0, c.getHeight());
		});

		this.rightTabs = new ClosableTabbedPanel(c -> {
			popup.setInvoker(c);
			leftPopup = false;
			popup.show(c, 0, c.getHeight());
		});

//		this.leftTabs = new ClosableTabbedPanel(tabType -> {
//			if (tabType == NewTabType.LocalTab) {
//				openLocalFileBrowserView(null, PanelOrientation.Left);
//			} else {
//				openSshFileBrowserView(null, PanelOrientation.Left);
//			}
//		});
//
//		this.rightTabs = new ClosableTabbedPanel(tabType -> {
//			if (tabType == NewTabType.LocalTab) {
//				openLocalFileBrowserView(null, PanelOrientation.Right);
//			} else {
//				openSshFileBrowserView(null, PanelOrientation.Right);
//			}
//		});

		horizontalSplitter = new SkinnedSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitter.setResizeWeight(0.5);

//		JPanel leftPanel = new JPanel(new BorderLayout());
//		JPanel rightPanel = new JPanel(new BorderLayout());
//
//		leftPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 0),
//				new MatteBorder(1, 1, 1, 1, App.SKIN.getDefaultBorderColor())));
//		rightPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 0),
//				new MatteBorder(1, 1, 1, 1, App.SKIN.getDefaultBorderColor())));
//
//		leftPanel.add(this.leftTabs);
//		rightPanel.add(this.rightTabs);

		horizontalSplitter.setLeftComponent(this.leftTabs);
		horizontalSplitter.setRightComponent(this.rightTabs);
		horizontalSplitter.setDividerSize(5);

		this.add(horizontalSplitter);

//		leftDropdown.addActionListener(e -> {
//			System.out.println("Left drop down changed");
//			int index = leftDropdown.getSelectedIndex();
//			if (index != -1) {
//				Object obj = leftList.getElementAt(index);
//				if (obj instanceof String) {
//					if (ignoreEvent) {
//						ignoreEvent = false;
//						return;
//					}
//					openSshFileBrowserView(null,
//							AbstractFileBrowserView.PanelOrientation.Left);
//				} else {
//					leftCard.show(leftPanel, obj.hashCode() + "");
//				}
//			}
//		});
//
////        leftDropdown.addItemListener(e -> {
////            System.out.println("Left drop down changed");
////            int index = leftDropdown.getSelectedIndex();
////            if (index != -1) {
////                Object obj = leftList.getElementAt(index);
////                if (obj instanceof String) {
////                } else {
////                    leftCard.show(leftPanel, obj.hashCode() + "");
////                }
////            }
////        });
//
//		rightDropdown.addActionListener(e -> {
//			int index = rightDropdown.getSelectedIndex();
//			if (index != -1) {
//				Object obj = rightList.getElementAt(index);
//				if (obj instanceof String) {
//					if (ignoreEvent) {
//						ignoreEvent = false;
//						return;
//					}
//					JComboBox<String> cmbList = new JComboBox<>(
//							new String[] { "Local files", "SFTP server" });
//					if (JOptionPane.showOptionDialog(this, new Object[] {
//							"Please select a server to open in this tab",
//							cmbList }, "New tab", JOptionPane.OK_CANCEL_OPTION,
//							JOptionPane.PLAIN_MESSAGE, null, null,
//							null) == JOptionPane.OK_OPTION) {
//						int selectedOption = cmbList.getSelectedIndex();
//						if (selectedOption == 0) {
//							openLocalFileBrowserView(null,
//									AbstractFileBrowserView.PanelOrientation.Right);
//						} else if (selectedOption == 1) {
//							openSftpFileBrowserView(null,
//									AbstractFileBrowserView.PanelOrientation.Right);
//						}
//					}
//				} else {
//					rightCard.show(rightPanel, obj.hashCode() + "");
//				}
//			}
//		});
//
////        rightDropdown.addItemListener(e -> {
////            int index = rightDropdown.getSelectedIndex();
////            if (index != -1) {
////                Object obj = rightList.getElementAt(index);
////                if (obj instanceof String) {
////                } else {
////                    rightCard.show(rightPanel, obj.hashCode() + "");
////                }
////            }
////        });
//
//		JPanel leftPanelHolder = new JPanel(new BorderLayout());
//		JPanel rightPanelHolder = new JPanel(new BorderLayout());
//
//		leftPanelHolder.setBorder(new EmptyBorder(10, 10, 10, 0));
//		rightPanelHolder.setBorder(new EmptyBorder(10, 0, 10, 10));
//
//		leftPanelHolder.add(leftDropdown, BorderLayout.NORTH);
//		rightPanelHolder.add(rightDropdown, BorderLayout.NORTH);
//		leftPanelHolder.add(leftPanel);
//		rightPanelHolder.add(rightPanel);
//
//		SshFileBrowserView fv1 = new SshFileBrowserView(this, rootPane, holder,
//				null, AbstractFileBrowserView.PanelOrientation.Left);
//		leftList.addElement(fv1);
//		leftDropdown.setSelectedIndex(1);
//		leftPanel.add(fv1, fv1.hashCode() + "");
//		leftCard.show(leftPanel, fv1.hashCode() + "");
//
//		LocalFileBrowserView fv2 = new LocalFileBrowserView(this, rootPane,
//				holder, null, AbstractFileBrowserView.PanelOrientation.Right);
//		rightList.addElement(fv2);
//		rightDropdown.setSelectedIndex(1);
//		rightPanel.add(fv2, fv2.hashCode() + "");
//		rightCard.show(rightPanel, fv2.hashCode() + "");
	}

//	public void close() {
//		this.closeRequested.set(true);
//	}
//
//	public boolean isCloseRequested() {
//		return closeRequested.get();
//	}
//
	public void disableUi() {
		holder.disableUi();
	}

	public void disableUi(AtomicBoolean stopFlag) {
		holder.disableUi(stopFlag);
	}

	public void enableUi() {
		holder.enableUi();
	}

	public void openSshFileBrowserView(String path, AbstractFileBrowserView.PanelOrientation orientation) {
		SshFileBrowserView tab = new SshFileBrowserView(this, path, orientation);
		if (orientation == PanelOrientation.Left) {
			this.leftTabs.addTab(tab.getTabTitle(), tab);
		} else {
			this.rightTabs.addTab(tab.getTabTitle(), tab);
		}
	}

	public void openLocalFileBrowserView(String path, AbstractFileBrowserView.PanelOrientation orientation) {

		LocalFileBrowserView tab = new LocalFileBrowserView(this, path, orientation);
		if (orientation == PanelOrientation.Left) {
			this.leftTabs.addTab(tab.getTabTitle(), tab);
		} else {
			this.rightTabs.addTab(tab.getTabTitle(), tab);
		}
	}

	public SshFileSystem getSSHFileSystem() {
		return this.getSessionInstance().getSshFs();
	}

	public RemoteSessionInstance getSessionInstance() {
		return this.holder.getRemoteSessionInstance();
	}

	public SessionInfo getInfo() {
		return info;
	}

	public boolean isCloseRequested() {
		return this.holder.isSessionClosed();
	}

	public Map<String, List<FileInfo>> getSSHDirectoryCache() {
		return this.sshDirCache;
	}
//
//	public void openSftpFileBrowserView(String path,
//			AbstractFileBrowserView.PanelOrientation orientation) {
//		SessionInfo info = new NewSessionDlg(
//				SwingUtilities.windowForComponent(this)).newSession();
//		if (info != null) {
//			int c = rightList.getSize();
//			SftpFileBrowserView fv1 = new SftpFileBrowserView(this, rootPane,
//					holder, path, orientation, info);
//			rightList.addElement(fv1);
//			rightPanel.add(fv1, fv1.hashCode() + "");
//			rightDropdown.setSelectedIndex(c);
//			rightCard.show(rightPanel, fv1.hashCode() + "");
//		}
//	}
//
//	public FileSystem getFs(int sessionCode) {
//		try {
//			for (int i = 0; i < rightList.getSize(); i++) {
//				Object obj = rightList.getElementAt(i);
//				if (obj.hashCode() == sessionCode) {
//					return ((AbstractFileBrowserView) obj).getFileSystem();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
////    public void newFileTransfer(FileSystem sourceFs,
////                                FileSystem targetFs,
////                                FileInfo[] files,
////                                String sourceFolder,
////                                String targetFolder,
////                                int dragsource) {
////        holder.newFileTransfer(sourceFs, targetFs, files, sourceFolder, targetFolder, dragsource);
////    }
//
//	public void requestReload(int sourceHashcode) {
//		for (int i = 0; i < this.leftList.getSize(); i++) {
//			Object obj = leftList.getElementAt(i);
//			if (obj.hashCode() == sourceHashcode) {
//				((AbstractFileBrowserView) obj).reload();
//				return;
//			}
//		}
//		for (int i = 0; i < this.rightList.getSize(); i++) {
//			Object obj = rightList.getElementAt(i);
//			if (obj.hashCode() == sourceHashcode) {
//				((AbstractFileBrowserView) obj).reload();
//			}
//		}
//	}
//
//	public void removeFileView(AbstractFileBrowserView fileBrowserView) {
//		if (fileBrowserView instanceof SshFileBrowserView) {
//			removeRemoteFileView(fileBrowserView);
//		} else {
//			removeLocalOrForeignFileView(fileBrowserView);
//		}
//	}
//
//	public void removeRemoteFileView(AbstractFileBrowserView fileBrowserView) {
//		for (int i = 0; i < this.leftList.getSize(); i++) {
//			Object obj = leftList.getElementAt(i);
//			if (obj == fileBrowserView) {
//				ignoreEvent = true;
//				System.out.println("Remove remote");
//				leftPanel.remove((Component) obj);
//				leftList.removeElement(obj);
//				revalidate();
//				repaint();
//				return;
//			}
//		}
//	}
//
//	public void removeLocalOrForeignFileView(
//			AbstractFileBrowserView fileBrowserView) {
//		for (int i = 0; i < this.rightList.getSize(); i++) {
//			Object obj = rightList.getElementAt(i);
//			if (obj == fileBrowserView) {
//				ignoreEvent = true;
//				System.out.println("Remove local or foreign");
//				rightPanel.remove((Component) obj);
//				rightList.removeElement(obj);
//				fileBrowserView.close();
//				revalidate();
//				repaint();
//				return;
//			}
//		}
//	}

	/**
	 * @param sourceFs
	 * @param targetFs
	 * @param files
	 * @param sourceFolder
	 * @param targetFolder
	 * @param dragsource
	 * @param defaultOverwriteAction
	 * @param backgroundTransfer
	 */
	public void newFileTransfer(FileSystem sourceFs, FileSystem targetFs, FileInfo[] files, String targetFolder,
			int dragsource, ConflictAction defaultConflictAction) {
		System.out.println("Initiating new file transfer...");
		this.ongoingFileTransfer = new FileTransfer(sourceFs, targetFs, files, targetFolder,
				new FileTransferProgress() {

					@Override
					public void progress(long processedBytes, long totalBytes, long processedCount, long totalCount,
							FileTransfer fileTransfer) {
						SwingUtilities.invokeLater(() -> {
							if (totalBytes == 0) {
								holder.setTransferProgress(0);
							} else {
								holder.setTransferProgress((int) ((processedBytes * 100) / totalBytes));
							}
						});
					}

					@Override
					public void init(long totalSize, long files, FileTransfer fileTransfer) {
					}

					@Override
					public void error(String cause, FileTransfer fileTransfer) {
						SwingUtilities.invokeLater(() -> {
							holder.endFileTransfer();
							if (!holder.isSessionClosed()) {
								JOptionPane.showMessageDialog(null, "Operation failed");
							}
						});
					}

					@Override
					public void done(FileTransfer fileTransfer) {
						System.out.println("Done");
						SwingUtilities.invokeLater(() -> {
							holder.endFileTransfer();
							reloadView();
						});
					}
				}, defaultConflictAction);
		holder.startFileTransferModal(e -> {
			this.ongoingFileTransfer.close();
		});
		holder.EXECUTOR.submit(this.ongoingFileTransfer);
	}

	private void reloadView() {
		Component c = leftTabs.getSelectedContent();
		System.out.println("c1 " + c);
		if (c instanceof AbstractFileBrowserView) {
			((AbstractFileBrowserView) c).reload();
		}
		c = rightTabs.getSelectedContent();
		System.out.println("c2 " + c);
		if (c instanceof AbstractFileBrowserView) {
			((AbstractFileBrowserView) c).reload();
		}
	}

	/**
	 * @return the activeSessionId
	 */
	public int getActiveSessionId() {
		return activeSessionId;
	}

	@Override
	public void onLoad() {
		if (init.get()) {
			return;
		}
		init.set(true);
		SshFileBrowserView left = new SshFileBrowserView(this, null, PanelOrientation.Left);
		this.leftTabs.addTab(left.getTabTitle(), left);

		LocalFileBrowserView right = new LocalFileBrowserView(this, System.getProperty("user.home"),
				PanelOrientation.Right);
		this.rightTabs.addTab(right.getTabTitle(), right);
	}

	@Override
	public String getIcon() {
		return FontAwesomeContants.FA_FOLDER;
		// return FontAwesomeContants.FA_FOLDER_OPEN_O;
	}

	@Override
	public String getText() {
		return "File browser";
	}

	/**
	 * @return the holder
	 */
	public SessionContentPanel getHolder() {
		return holder;
	}

	public void openPath(String path) {
		openSshFileBrowserView(path, PanelOrientation.Left);
	}

	public boolean isSessionClosed() {
		return this.holder.isSessionClosed();
	}
}
