package muonssh.app.ui.components.session.files.ssh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileSystem;
import muonssh.app.common.local.LocalFileSystem;
import muonssh.app.ssh.OperationCancelledException;
import muonssh.app.ssh.RemoteSessionInstance;
import muonssh.app.ssh.SshFileSystem;
import muonssh.app.ui.components.session.files.AbstractFileBrowserView;
import muonssh.app.ui.components.session.files.FileBrowser;
import muonssh.app.ui.components.session.files.view.AddressBar;
import muonssh.app.ui.components.session.files.view.DndTransferData;
import muonssh.app.ui.components.session.files.view.DndTransferHandler;
import util.Constants;
import util.PathUtils;

public class SshFileBrowserView extends AbstractFileBrowserView {
	private final SshMenuHandler menuHandler;
	private final JPopupMenu addressPopup;
	private final DndTransferHandler transferHandler;
//	private JComboBox<String> cmbOptions = new JComboBox<>(
//			new String[] { "Transfer normally", "Transfer in background" });

	public SshFileBrowserView(FileBrowser fileBrowser, String initialPath, PanelOrientation orientation) {
		super(orientation, fileBrowser);
		this.menuHandler = new SshMenuHandler(fileBrowser, this);
		this.menuHandler.initMenuHandler(this.folderView);
		this.transferHandler = new DndTransferHandler(this.folderView, this.fileBrowser.getInfo(), this,
				DndTransferData.DndSourceType.SSH, this.fileBrowser);
		this.folderView.setTransferHandler(transferHandler);
		this.folderView.setFolderViewTransferHandler(transferHandler);
		this.addressPopup = menuHandler.createAddressPopup();
		if (initialPath == null) {
			this.path = this.fileBrowser.getInfo().getRemoteFolder();
			if (this.path != null && this.path.trim().length() < 1) {
				this.path = null;
			}
			System.out.println("Path: " + path);
		} else {
			this.path = initialPath;
		}

		this.render(path, App.getGlobalSettings().isDirectoryCache());
	}

	private void openDefaultAction() {
	}

	private void openNewTab() {

	}

	public void createAddressBar() {
		addressBar = new AddressBar('/', new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedPath = e.getActionCommand();
				addressPopup.setName(selectedPath);
				MouseEvent me = (MouseEvent) e.getSource();
				addressPopup.show(me.getComponent(), me.getX(), me.getY());
				System.out.println("clicked");
			}
		});
		if (App.getGlobalSettings().isShowPathBar()) {
			addressBar.switchToPathBar();
		} else {
			addressBar.switchToText();
		}
	}

	@Override
	public String toString() {
		return this.fileBrowser.getInfo().getName()
				+ (this.path == null || this.path.length() < 1 ? "" : " [" + this.path + "]");
	}

//    private void connect() throws Exception {
//        synchronized (fileSystemMap) {
//            fs = fileSystemMap.get(source.getInfo());
//            if (fs == null || !fs.isConnected()) {
//                if (fs == null) {
//                    fs = new SshFileSystem(source);
//                }
//                try {
//                    fs.connect();
//                    fileSystemMap.put(source.getInfo(), fs);
//                    fileViewMap.put(fs, 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                fileViewMap.put(fs, fileViewMap.get(fs) + 1);
//            }
//
//            String home = source.getInfo().getRemoteFolder();
//            if (home == null) {
//                home = fs.getHome();
//            }
//            this.path = home;
//            final String finalHome = home;
//            SwingUtilities.invokeLater(() -> {
//                addressBar.setText(finalHome);
//            });
//        }
//    }

	private String trimPath(String path) {
		if (path.equals("/"))
			return path;
		if (path.endsWith("/")) {
			String trim = path.substring(0, path.length() - 1);
			System.out.println("Trimmed path: " + trim);
			return trim;
		}
		return path;
	}

	private void renderDirectory(final String path, final boolean fromCache) throws Exception {
		List<FileInfo> list = null;
		if (fromCache) {
			list = this.fileBrowser.getSSHDirectoryCache().get(trimPath(path));
		}
		if (list == null) {
			list = this.fileBrowser.getSSHFileSystem().list(path);
			if (list != null) {
				this.fileBrowser.getSSHDirectoryCache().put(trimPath(path), list);
			}
		}
		if (list != null) {
			final List<FileInfo> list2 = list;
			System.out.println("New file list: " + list2);
			SwingUtilities.invokeLater(() -> {
				addressBar.setText(path);
				folderView.setItems(list2);
				tabTitle.getCallback().accept(PathUtils.getFileName(path));
				int tc = list2.size();
				String text = String.format("Total %d remote file(s)", tc);
				fileBrowser.updateRemoteStatus(text);
			});
		}
	}

	@Override
	public void render(String path, boolean useCache) {
		System.out.println("Rendering: " + path + " caching: " + useCache);
		this.path = path;
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			this.fileBrowser.disableUi();
			try {
				while (!fileBrowser.isCloseRequested()) {
					System.out.println("Listing files now ...");
					try {
						if (path == null) {
							SshFileSystem sshfs = this.fileBrowser.getSSHFileSystem();
							this.path = sshfs.getHome();
							// holder.getSshFileSystem().statFs();
						}
						renderDirectory(this.path, useCache);
						break;
					} catch (OperationCancelledException e) {
						e.printStackTrace();

						break;
					} catch (Exception e) {
						e.printStackTrace();
						if (this.fileBrowser.isSessionClosed()) {
							return;
						}
						System.out.println("Exception caught in sftp file browser: " + e.getMessage());
						
						this.fileBrowser.getHolder().reconnect();

						e.printStackTrace();
						if (JOptionPane.showConfirmDialog(null,
								"Unable to connect to server " + this.fileBrowser.getInfo().getName() + " at "
										+ this.fileBrowser.getInfo().getHost()
										+ (e.getMessage() != null ? "\n\nReason: " + e.getMessage() : "\n")
										+ "\n\nDo you want to retry?") == JOptionPane.YES_OPTION) {
							continue;
						}
						break;
					}
				}
			} finally {
				this.fileBrowser.enableUi();
			}
		});
	}

	@Override
	public void render(String path) {
		this.render(path, false);
	}

	@Override
	public void openApp(FileInfo file) {

		FileInfo fileInfo = folderView.getSelectedFiles()[0];
		try {
			App.getExternalEditorHandler().openRemoteFile(fileInfo, fileBrowser.getSSHFileSystem(),
					fileBrowser.getActiveSessionId(), false, null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// holder.openWithDefaultApp(file);
	}

	protected void up() {
		if (path != null) {
			String parent = PathUtils.getParent(path);
			addBack(path);
			render(parent, App.getGlobalSettings().isDirectoryCache());
		}
	}

	protected void home() {
		addBack(path);
		render(null, App.getGlobalSettings().isDirectoryCache());
	}

	@Override
	public void install(JComponent c) {

	}

	@Override
	public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
		if (this.path == null) {
			return false;
		}
		return menuHandler.createMenu(popup, files);
	}

	public boolean handleDrop(DndTransferData transferData) {
		if (App.getGlobalSettings().isConfirmBeforeMoveOrCopy()
				&& JOptionPane.showConfirmDialog(null, "Move/copy files?") != JOptionPane.YES_OPTION) {
			return false;
		}
		try {
			int sessionHashCode = transferData.getInfo();
			System.out.println("Session hash code: " + sessionHashCode);
			FileSystem sourceFs = null;
			if (sessionHashCode == 0 && transferData.getSourceType() == DndTransferData.DndSourceType.LOCAL) {
				System.out.println("Source fs is local");
				sourceFs = new LocalFileSystem();
			} else if (transferData.getSourceType() == DndTransferData.DndSourceType.SSH
					&& sessionHashCode == this.fileBrowser.getInfo().hashCode()) {
				System.out.println("Source fs is remote");
				sourceFs = this.fileBrowser.getSSHFileSystem();
			}
//			else if (transferData.getSourceType() == DndTransferData.DndSourceType.SFTP) {
			// handle server to server drop - sftp
//				System.out.println("Foreign file drop");
//				sourceFs = this.fileBrowser.getFs(transferData.getSource());
//				System.out.println("Foreign sftp fs: " + sourceFs);
//			}

			if (sourceFs instanceof LocalFileSystem) {
				System.out.println("Dropped: " + transferData);
				FileBrowser.ResponseHolder holder = new FileBrowser.ResponseHolder();
				if (!this.fileBrowser.selectTransferModeAndConflictAction(holder)) {
					return false;
				}
				if (holder.transferMode == Constants.TransferMode.BACKGROUND) {
					this.fileBrowser.getHolder().uploadInBackground(transferData.getFiles(), this.path,
							holder.conflictAction);
					return true;
				}
				FileSystem targetFs = this.fileBrowser.getSSHFileSystem();
				this.fileBrowser.newFileTransfer(sourceFs, targetFs, transferData.getFiles(), this.path,
						this.hashCode(), holder.conflictAction, null);
			} else if (sourceFs instanceof SshFileSystem && (sourceFs == this.fileBrowser.getSSHFileSystem())) {
				System.out.println("SshFs is of same instance: " + (sourceFs == this.fileBrowser.getSSHFileSystem()));
				if (transferData.getFiles().length > 0) {
					FileInfo fileInfo = transferData.getFiles()[0];
					String parent = PathUtils.getParent(fileInfo.getPath());
					System.out.println("Parent: " + parent + " == " + this.getCurrentDirectory());
					if (!parent.endsWith("/")) {
						parent += "/";
					}
					String pwd = this.getCurrentDirectory();
					if (!pwd.endsWith("/")) {
						pwd += "/";
					}
					if (parent.equals(pwd)) {
						JOptionPane.showMessageDialog(null, "Source and target directory is same!");
						return false;
					}
				}

				if (transferData.getTransferAction() == DndTransferData.TransferAction.Copy) {
					menuHandler.copy(Arrays.asList(transferData.getFiles()), getCurrentDirectory());
				} else {
					menuHandler.move(Arrays.asList(transferData.getFiles()), getCurrentDirectory());
				}
			} else if (sourceFs instanceof SshFileSystem
					&& (transferData.getSourceType() == DndTransferData.DndSourceType.SFTP)) {
//				System.out.println("Sftp file drop");
//				FileSystem targetFs = holder.getSshFileSystem();
//				holder.newFileTransfer(sourceFs, targetFs,
//						transferData.getFiles(),
//						transferData.getCurrentDirectory(), this.path,
//						this.hashCode(), -1, false);
			}
			System.out.println("12345: " + (sourceFs instanceof SshFileSystem) + " " + transferData.getSourceType());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// throw new RuntimeException("Not implemented");
	}

	public FileSystem getFileSystem() throws Exception {
		return this.fileBrowser.getSSHFileSystem();
	}

	public RemoteSessionInstance getSshClient() throws Exception {
		return this.fileBrowser.getSessionInstance();
	}

	@Override
	public TransferHandler getTransferHandler() {
		return transferHandler;
	}

	public String getHostText() {
		return this.fileBrowser.getInfo().getName();
	}

	public String getPathText() {
		return (this.path == null || this.path.length() < 1 ? "" : this.path);
	}

}
