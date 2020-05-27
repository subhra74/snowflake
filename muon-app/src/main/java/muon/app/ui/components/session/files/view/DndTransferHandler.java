package muon.app.ui.components.session.files.view;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import muon.app.App;
import muon.app.common.FileInfo;
import muon.app.common.FileType;
import muon.app.common.local.LocalFileSystem;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.files.AbstractFileBrowserView;
import muon.app.ui.components.session.files.FileBrowser;
import util.Win32DragHandler;

public class DndTransferHandler extends TransferHandler implements Transferable {
	private FolderView folderView;
	private SessionInfo info;
	private AbstractFileBrowserView fileBrowserView;
	private DndTransferData transferData;
	private DndTransferData.DndSourceType sourceType;
	private Win32DragHandler win32DragHandler;
	private File tempDir;
	private FileBrowser fileBrowser;

	public static final DataFlavor DATA_FLAVOR_DATA_FILE = new DataFlavor(DndTransferData.class, "data-file");

	public static final DataFlavor DATA_FLAVOR_FILE_LIST = DataFlavor.javaFileListFlavor;

	public DndTransferHandler(FolderView folderView, SessionInfo info, AbstractFileBrowserView fileBrowserView,
			DndTransferData.DndSourceType sourceType, FileBrowser fileBrowser) {
		this.folderView = folderView;
		this.fileBrowser = fileBrowser;
		this.info = info;
		this.fileBrowserView = fileBrowserView;
		this.sourceType = sourceType;
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		if (info != null) {
			if (App.IS_WINDOWS) {
				try {
					this.tempDir = Files.createTempDirectory(App.APP_INSTANCE_ID).toFile();
					System.out.println("New monitor");
					this.win32DragHandler = new Win32DragHandler();
					this.win32DragHandler.listenForDrop(tempDir.getName(), file -> {
						System.err.println("Dropped on " + file.getParent());
						this.fileBrowser.handleLocalDrop(transferData, info, new LocalFileSystem(), file.getParent());
						// file.delete();
					});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		DndTransferData data = new DndTransferData(info == null ? 0 : info.hashCode(), folderView.getSelectedFiles(),
				this.fileBrowserView.getCurrentDirectory(), this.fileBrowserView.hashCode(), sourceType);
		System.out.println("Exporting drag " + data + " hashcode: " + data.hashCode());
		this.transferData = data;
		super.exportAsDrag(comp, e, action);
	}

	@Override
	public boolean canImport(TransferSupport support) {

		System.out.println("Data flavors: " + support.getDataFlavors().length);
		boolean isDataFile = false, isJavaFileList = false;
		for (DataFlavor f : support.getDataFlavors()) {
			System.out.println("Data flavor: " + f);
			if (f.isFlavorJavaFileListType()) {
				isJavaFileList = this.info != null;
			}
			if (DATA_FLAVOR_DATA_FILE.equals(f)) {
				isDataFile = true;
			}
		}

		try {
			System.out.println("Dropped java file list: " + isJavaFileList);
			if (isDataFile) {
				if (support.isDataFlavorSupported(DATA_FLAVOR_DATA_FILE)) {
					return (support.getTransferable()
							.getTransferData(DATA_FLAVOR_DATA_FILE) instanceof DndTransferData);
				}
			} else if (isJavaFileList) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("drop not supported");
		return false;

	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		System.out.println("Export complete: " + action + " " + Arrays.asList(data.getTransferDataFlavors()));
		if (this.win32DragHandler != null) {
			this.win32DragHandler.dispose();
		}
	}

	/**
	 * When importing always DATA_FLAVOR_DATA_FILE will be preferred over file list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		boolean isDataFile = false, isJavaFileList = false;
		for (DataFlavor f : info.getDataFlavors()) {
			System.out.println("Data flavor: " + f);
			if (f.isFlavorJavaFileListType()) {
				isJavaFileList = this.info != null;
			}
			if (DATA_FLAVOR_DATA_FILE.equals(f)) {
				isDataFile = true;
			}
		}

		Transferable t = info.getTransferable();

		if (isDataFile) {
			try {
				DndTransferData transferData = (DndTransferData) t.getTransferData(DATA_FLAVOR_DATA_FILE);
				return this.fileBrowserView.handleDrop(transferData);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (isJavaFileList) {
			try {
				List<File> fileList = ((List<File>) t.getTransferData(DataFlavor.javaFileListFlavor));
				if (fileList != null) {
					FileInfo infoArr[] = new FileInfo[fileList.size()];
					int c = 0;
					for (File file : fileList) {

						if (file.getName().startsWith(App.APP_INSTANCE_ID)) {
							System.out.println("Internal fake folder dropped");
							return false;
						}

						Path p = file.toPath();
						BasicFileAttributes attrs = null;
						try {
							attrs = Files.readAttributes(p, BasicFileAttributes.class);
						} catch (IOException e) {
							e.printStackTrace();
						}
						FileInfo finfo = new FileInfo(file.getName(), file.getAbsolutePath(), file.length(),
								file.isDirectory() ? FileType.Directory : FileType.File, file.lastModified(), -1,
								LocalFileSystem.PROTO_LOCAL_FILE, "",
								attrs != null ? attrs.creationTime().toMillis() : file.lastModified(), "",
								file.isHidden());
						infoArr[c++] = finfo;
					}

					DndTransferData data = new DndTransferData(0, infoArr, this.fileBrowserView.getCurrentDirectory(),
							this.fileBrowserView.hashCode(), DndTransferData.DndSourceType.LOCAL);
					System.out.println("Exporting drag " + data + " hashcode: " + data.hashCode());
					return this.fileBrowserView.handleDrop(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DATA_FLAVOR_DATA_FILE, DATA_FLAVOR_FILE_LIST };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (this.info != null) {
			return DATA_FLAVOR_DATA_FILE.equals(flavor) || DATA_FLAVOR_FILE_LIST.equals(flavor);
		} else {
			return DATA_FLAVOR_DATA_FILE.equals(flavor);
		}
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DATA_FLAVOR_DATA_FILE.equals(flavor)) {
			return this.transferData;
		}

		if (DATA_FLAVOR_FILE_LIST.equals(flavor)) {
			if (App.IS_WINDOWS && tempDir != null) {
				return Arrays.asList(tempDir);
			}
		}
		return null;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		return this;
	}

//    private DndTransferData createTransferDataFromFiles(List<File>files){
//        FileInfo[] selectedFiles = folderView.getSelectedFiles();
//        DndTransferData transferData = new DndTransferData(0, selectedFiles,
//                fileBrowserView.getCurrentDirectory(), fileBrowserView.hashCode(), DndTransferData.DndSourceType.SSH);
//        transferData.setTransferAction(cut ? DndTransferData.TransferAction.Cut : DndTransferData.TransferAction.Copy);
//    }
}
