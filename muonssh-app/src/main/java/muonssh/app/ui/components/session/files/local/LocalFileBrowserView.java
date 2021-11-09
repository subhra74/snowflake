package muonssh.app.ui.components.session.files.local;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileSystem;
import muonssh.app.common.local.LocalFileSystem;
import muonssh.app.ui.components.session.files.AbstractFileBrowserView;
import muonssh.app.ui.components.session.files.FileBrowser;
import muonssh.app.ui.components.session.files.view.AddressBar;
import muonssh.app.ui.components.session.files.view.DndTransferData;
import muonssh.app.ui.components.session.files.view.DndTransferHandler;
import util.PathUtils;

public class LocalFileBrowserView extends AbstractFileBrowserView {
	private final LocalMenuHandler menuHandler;
	private final DndTransferHandler transferHandler;
	private LocalFileSystem fs;
	private final JPopupMenu addressPopup;

	public LocalFileBrowserView(FileBrowser fileBrowser, String initialPath, PanelOrientation orientation) {
		super(orientation, fileBrowser);
		this.menuHandler = new LocalMenuHandler(fileBrowser, this);
		this.menuHandler.initMenuHandler(this.folderView);
		this.transferHandler = new DndTransferHandler(this.folderView, null, this, DndTransferData.DndSourceType.LOCAL,
				this.fileBrowser);
		this.folderView.setTransferHandler(transferHandler);
		this.folderView.setFolderViewTransferHandler(transferHandler);
		this.addressPopup = menuHandler.createAddressPopup();

		if (this.fileBrowser.getInfo().getLocalFolder() != null && this.fileBrowser.getInfo().getLocalFolder().trim().length() > 1) {
			this.path = fileBrowser.getInfo().getLocalFolder();
		} else if (initialPath != null) {
			this.path = initialPath;
		}

		System.out.println("Path: " + path);
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			try {
				this.fs = new LocalFileSystem();
				//Validate if local path exists, if not set the home path
				if (this.path == null || Files.notExists(Paths.get(this.path)) || !Files.isDirectory(Paths.get(this.path))) {
					System.err.println("The file path doesn't exists " + this.path);
					System.out.println("Setting to " + fs.getHome());

					path = fs.getHome();
				}
				List<FileInfo> list = fs.list(path);
				SwingUtilities.invokeLater(() -> {
					addressBar.setText(path);
					folderView.setItems(list);
					tabTitle.getCallback().accept(PathUtils.getFileName(path));
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void createAddressBar() {
		addressBar = new AddressBar(File.separatorChar, new ActionListener() {
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
		return "Local files [" + this.path + "]";
	}

	public String getHostText() {
		return "Local files";
	}

	public String getPathText() {
		return (this.path == null || this.path.length() < 1 ? "" : this.path);
	}

	@Override
	public void render(String path, boolean useCache) {
		this.render(path);
	}

	@Override
	public void render(String path) {
		this.path = path;
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (this.path == null) {
					this.path = fs.getHome();
				}
				List<FileInfo> list = fs.list(this.path);
				SwingUtilities.invokeLater(() -> {
					addressBar.setText(this.path);
					folderView.setItems(list);
					int tc = list.size();
					String text = String.format("Total %d remote file(s)", tc);
					fileBrowser.updateRemoteStatus(text);
					tabTitle.getCallback().accept(PathUtils.getFileName(this.path));
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileBrowser.enableUi();
		});
	}

	@Override
	public void openApp(FileInfo file) {
		// PlatformAppLauncher.shellLaunch(file.getPath());
	}

	@Override
	public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
		menuHandler.createMenu(popup, files);
		return true;
	}

	protected void up() {
		String s = new File(path).getParent();
		if (s != null) {
			addBack(path);
			render(s);
		}
	}

	protected void home() {
		addBack(path);
		render(null);
	}

	@Override
	public void install(JComponent c) {

	}

	public boolean handleDrop(DndTransferData transferData) {
		System.out.println("### " + transferData.getSource() + " " + this.hashCode());
		if (transferData.getSource() == this.hashCode()) {
			return false;
		}
		return this.fileBrowser.handleLocalDrop(transferData, fileBrowser.getInfo(), this.fs, this.path);
	}

	public FileSystem getFileSystem() throws Exception {
		return new LocalFileSystem();
	}
}
