package muon.screens.appwin.tabs.filebrowser.sftp;

import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.dto.session.SessionInfo;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.screens.appwin.tabs.filebrowser.AbstractFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.DndTransferData;
import muon.screens.appwin.tabs.filebrowser.DndTransferHandler;
import muon.screens.appwin.tabs.filebrowser.FileBrowserViewParent;
import muon.service.*;

import java.util.List;

public class SftpFileBrowserView extends AbstractFileBrowserView {
    private SftpSession session;
    private SessionInfo sessionInfo;
    private FileBrowserViewParent parent;

    public SftpFileBrowserView(SessionInfo sessionInfo, FileBrowserViewParent parent) {
        super(parent);
        this.sessionInfo = sessionInfo;
        this.parent = parent;
        this.session = new SftpSession(sessionInfo);
        setDnDTransferHandler(new DndTransferHandler(sessionInfo,
                this, DndTransferData.DndSourceType.SFTP));
    }

    @Override
    public void init() {
        super.navigate();
    }

    @Override
    public boolean isConnected() {
        return this.session.isConnected();
    }

    @Override
    public void connect() throws FSConnectException {
        var passwordUserAuthFactory = new GuiUserAuthFactory(getInputBlockerPanel(), sessionInfo);
        var callback = new SshCallback(getInputBlockerPanel(), sessionInfo);
        this.session.connect(callback, passwordUserAuthFactory);
    }

    @Override
    public String getHome() {
        return this.session.getHomePath();
    }

    @Override
    public FileList ls(String folder) throws FSConnectException, FSAccessException {
        return this.session.list(folder);
    }

    @Override
    public void cleanup() {
        this.session.close();
    }


    protected boolean handleDrop(DndTransferData transferData) {
        try {
            int sessionCode = transferData.getInfo();
            System.out.println("Session code: " + sessionCode);

            //FileSystem sourceFs = null;
            if (sessionCode == 0 && transferData.getSourceType() == DndTransferData.DndSourceType.LOCAL) {
                System.out.println("Source fs is local");
                beginUpload(transferData.getCurrentDirectory(), transferData.getFiles());
            } else if (transferData.getSourceType() == DndTransferData.DndSourceType.SFTP
                    && sessionCode == this.sessionInfo.hashCode()) {
                System.out.println("Source fs is remote this");
                //sourceFs = this.session;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private void beginUpload(String localFolder, List<FileInfo> localFiles) {
        var path = getPath();
        var files = folderViewTableModel.getFiles();
        parent.beginSftpUpload(localFolder, localFiles, path, files, this.session);
    }
}
