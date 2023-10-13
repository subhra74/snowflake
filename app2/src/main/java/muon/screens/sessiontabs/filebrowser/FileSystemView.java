package muon.screens.sessiontabs.filebrowser;

import java.util.List;

public interface FileSystemView {
    List<FileInfo> list(FileInfo path);

    FileInfo home();
}
