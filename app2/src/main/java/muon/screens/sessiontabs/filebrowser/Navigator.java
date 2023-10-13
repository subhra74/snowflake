package muon.screens.sessiontabs.filebrowser;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public interface Navigator {
    void navigate(FileBrowser fileBrowser, String path);

    void preparePopup(JPopupMenu popupMenu, List<FileInfo> files, Component source, int x, int y);
}
