package muon.screens.appwin.tabs.filebrowser.local;

import muon.screens.appwin.tabs.filebrowser.AbstractFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.DndTransferData;
import muon.screens.appwin.tabs.filebrowser.DndTransferHandler;
import muon.screens.appwin.tabs.filebrowser.FileBrowserViewParent;
import muon.service.FileSystem;
import muon.service.LocalFileSystem;

import javax.swing.*;

public class LocalFileBrowserView extends AbstractFileBrowserView {
    private LocalFileSystem localFileSystem;

    public LocalFileBrowserView(FileBrowserViewParent parent) {
        super(parent);
        localFileSystem = new LocalFileSystem();
        setDnDTransferHandler(new DndTransferHandler(null, this, DndTransferData.DndSourceType.LOCAL));
    }

    @Override
    public FileSystem getFileSystem() {
        return this.localFileSystem;
    }

    public void init() {
        super.navigate(null);
    }

    protected boolean handleDrop(DndTransferData transferData) {
        System.out.println(transferData);
        return true;
    }
}
