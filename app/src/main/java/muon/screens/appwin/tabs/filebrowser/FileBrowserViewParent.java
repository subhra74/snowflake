package muon.screens.appwin.tabs.filebrowser;

import muon.dto.file.FileInfo;
import muon.service.SftpSession;

import java.util.List;

public interface FileBrowserViewParent {
    void beginSftpUpload(
            String localFolder,
            List<FileInfo> localFiles,
            String remoteFolder,
            List<FileInfo> remoteFiles,
            SftpSession sftp);
}
