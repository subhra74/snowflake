package muon.screens.appwin.tabs.filebrowser.sftp;

import muon.dto.file.FileInfo;
import muon.dto.session.SessionInfo;
import muon.screens.appwin.tabs.filebrowser.AbstractFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.DndTransferData;
import muon.screens.appwin.tabs.filebrowser.DndTransferHandler;
import muon.screens.appwin.tabs.filebrowser.FileBrowserViewParent;
import muon.service.FileSystem;
import muon.service.LocalFileSystem;
import muon.service.SftpFileSystem;

import java.util.List;

public class SftpFileBrowserView extends AbstractFileBrowserView {
    private SftpFileSystem sftpFileSystem;
    private SessionInfo sessionInfo;
    private FileBrowserViewParent parent;

    public SftpFileBrowserView(SessionInfo sessionInfo, FileBrowserViewParent parent) {
        super(parent);
        this.sessionInfo = sessionInfo;
        this.parent = parent;
        this.sftpFileSystem = new SftpFileSystem(sessionInfo, getInputBlockerPanel());
        setDnDTransferHandler(new DndTransferHandler(sessionInfo,
                this, DndTransferData.DndSourceType.SFTP));
    }

    @Override
    public FileSystem getFileSystem() {
        return this.sftpFileSystem;
    }

    @Override
    public void init() {
        super.navigate();
    }

    protected boolean handleDrop(DndTransferData transferData) {
        System.out.println(transferData);
        try {
            int sessionCode = transferData.getInfo();
            System.out.println("Session code: " + sessionCode);

            FileSystem sourceFs = null;
            if (sessionCode == 0 && transferData.getSourceType() == DndTransferData.DndSourceType.LOCAL) {
                System.out.println("Source fs is local");
                beginUpload(transferData.getFiles());
            } else if (transferData.getSourceType() == DndTransferData.DndSourceType.SFTP
                    && sessionCode == this.sessionInfo.hashCode()) {
                System.out.println("Source fs is remote this");
                sourceFs = this.sftpFileSystem;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private void beginUpload(List<FileInfo> localFiles) {
        var path = getPath();
        parent.beginSftpUpload(localFiles, path, this.sftpFileSystem);
    }
}
