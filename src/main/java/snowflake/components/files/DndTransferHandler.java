package snowflake.components.files;

import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;

public class DndTransferHandler extends TransferHandler
		implements Transferable {
	private FolderView folderView;
	private SessionInfo info;
	private AbstractFileBrowserView fileBrowserView;
	private DndTransferData transferData;
	private DndTransferData.DndSourceType sourceType;

	public static final DataFlavor DATA_FLAVOR = new DataFlavor(
			DndTransferData.class, "data-file");

	public DndTransferHandler(FolderView folderView, SessionInfo info,
			AbstractFileBrowserView fileBrowserView,
			DndTransferData.DndSourceType sourceType) {
		this.folderView = folderView;
		this.info = info;
		this.fileBrowserView = fileBrowserView;
		this.sourceType = sourceType;
	}

	@Override
	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
		DndTransferData data = new DndTransferData(
				info == null ? 0 : info.hashCode(),
				folderView.getSelectedFiles(),
				this.fileBrowserView.getCurrentDirectory(),
				this.fileBrowserView.hashCode(), sourceType);
		System.out.println(
				"Exporting drag " + data + " hashcode: " + data.hashCode());
		this.transferData = data;
		super.exportAsDrag(comp, e, action);
	}

	@Override
	public boolean canImport(TransferSupport support) {
		for (DataFlavor f : support.getDataFlavors()) {
			if (f.isFlavorJavaFileListType()) {
				return true;
			}
		}

		try {
			if (support.isDataFlavorSupported(DATA_FLAVOR)) {
				return (support.getTransferable().getTransferData(
						DATA_FLAVOR) instanceof DndTransferData);
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
		System.out.println("Export complete");
	}

	@Override
	public boolean importData(TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}
		Transferable t = info.getTransferable();
		for (DataFlavor f : t.getTransferDataFlavors()) {
			if (f.isFlavorJavaFileListType()) {

			} else if (f.equals(DATA_FLAVOR)) {
				try {
					DndTransferData transferData = (DndTransferData) t
							.getTransferData(DATA_FLAVOR);
					this.fileBrowserView.handleDrop(transferData);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DATA_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DATA_FLAVOR.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return this.transferData;
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
