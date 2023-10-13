package muon.screens.appwin.tabs.filebrowser;

import muon.dto.file.FileInfo;
import muon.service.SftpFileSystem;

import java.util.List;

public interface FileBrowserViewParent {
    void beginSftpUpload(List<FileInfo> localFiles, String remoteFolder, SftpFileSystem remoteFs);
}
