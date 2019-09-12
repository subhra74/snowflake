package snowflake.components.files.browser.folderview;

import snowflake.common.FileInfo;

import javax.swing.*;

public interface FolderViewEventListener {
    void addBack(String path);

    void render(String path);

    void openApp(FileInfo file);

    boolean createMenu(JPopupMenu popupMenu, FileInfo[] files);

    void install(JComponent c);

    void reload();
}
