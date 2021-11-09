package muonssh.app.ui.components.session.files.view;

import javax.swing.*;

import muonssh.app.common.FileInfo;

public interface FolderViewEventListener {
	void addBack(String path);

	void render(String path);

	void render(String path, boolean useCache);

	void openApp(FileInfo file);

	boolean createMenu(JPopupMenu popupMenu, FileInfo[] files);

	void install(JComponent c);

	void reload();
}
