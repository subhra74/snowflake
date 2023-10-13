package muon.screens.sessiontabs.filebrowser;

import muon.dto.file.FileInfo;

import java.util.List;

public interface FileSystemView {
    List<FileInfo> list(FileInfo path);

    FileInfo home();
}
