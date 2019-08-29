package snowflake.components.files;

import snowflake.common.FileSystem;
import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;


public class DndTransferHandler extends TransferHandler implements Transferable {
    private FolderView folderView;
    private SessionInfo info;
    private AbstractFileBrowserView fileBrowserView;
    private DndTransferData transferData;

    private final DataFlavor flavor = new DataFlavor(DndTransferData.class,
            "data-file");

    public DndTransferHandler(FolderView folderView, SessionInfo info, AbstractFileBrowserView fileBrowserView) {
        this.folderView = folderView;
        this.info = info;
        this.fileBrowserView = fileBrowserView;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        DndTransferData data = new DndTransferData(info == null ? 0 : info.hashCode(), folderView.getSelectedFiles(),
                this.fileBrowserView.getCurrentDirectory(),
                this.fileBrowserView.hashCode());
        System.out.println("Exporting drag " + data + " hashcode: " + data.hashCode());
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
            if (support.isDataFlavorSupported(flavor)) {
                return (support
                        .getTransferable().getTransferData(flavor) instanceof DndTransferData);
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

            } else if (f.equals(flavor)) {
                try {
                    DndTransferData transferData = (DndTransferData) t.getTransferData(flavor);
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
        return new DataFlavor[]{flavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this.transferData;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return this;
    }
}

