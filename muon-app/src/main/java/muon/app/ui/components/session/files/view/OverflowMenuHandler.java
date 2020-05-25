package muon.app.ui.components.session.files.view;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import muon.app.App;
import muon.app.ui.components.session.BookmarkManager;
import muon.app.ui.components.session.files.AbstractFileBrowserView;
import muon.app.ui.components.session.files.FileBrowser;
import muon.app.ui.components.session.files.local.LocalFileBrowserView;
import util.PathUtils;

public class OverflowMenuHandler {
	private JRadioButtonMenuItem mSortName, mSortSize, mSortModified, mSortAsc, mSortDesc;

	private FolderView folderView;
	private JCheckBoxMenuItem mShowHiddenFiles;

	private AtomicBoolean sortingChanging = new AtomicBoolean(false);
	private KeyStroke ksHideShow;
	private AbstractAction aHideShow;
	private JPopupMenu popup;
	// private FileComponentHolder holder;
	private AbstractFileBrowserView fileBrowserView;
	private JMenu favouriteLocations;
	private JPopupMenu mSortMenu;
	private FileBrowser fileBrowser;

	public OverflowMenuHandler(AbstractFileBrowserView fileBrowserView, FileBrowser fileBrowser) {
		// this.holder = holder;
		this.fileBrowserView = fileBrowserView;
		this.fileBrowser = fileBrowser;
		ksHideShow = KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);

		mShowHiddenFiles = new JCheckBoxMenuItem("Show hidden files");
		mShowHiddenFiles.setSelected(App.getGlobalSettings().isShowHiddenFilesByDefault());

		aHideShow = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mShowHiddenFiles.setSelected(!mShowHiddenFiles.isSelected());
				hideOptAction();
			}
		};

		mShowHiddenFiles.addActionListener(e -> {
			hideOptAction();
		});
		mShowHiddenFiles.setAccelerator(ksHideShow);

		ButtonGroup bg1 = new ButtonGroup();

		mSortName = createSortMenuItem("Name", 0, bg1);

		mSortSize = createSortMenuItem("Size", 1, bg1);

		mSortModified = createSortMenuItem("Modification date", 2, bg1);

		ButtonGroup bg2 = new ButtonGroup();

		mSortAsc = createSortMenuItem("Sort ascending", 0, bg2);

		mSortDesc = createSortMenuItem("Sort descending", 1, bg2);

		this.favouriteLocations = new JMenu("Bookmarks");

		popup = new JPopupMenu();
		mSortMenu = new JPopupMenu();

		mSortMenu.add(mSortName);
		mSortMenu.add(mSortSize);
		mSortMenu.add(mSortModified);
		mSortMenu.addSeparator();
		mSortMenu.add(mSortAsc);
		mSortMenu.add(mSortDesc);

		popup.add(mShowHiddenFiles);
		popup.add(favouriteLocations);

		loadFavourites();
	}

	public void loadFavourites() {
    	this.favouriteLocations.removeAll();
    	String id=fileBrowserView instanceof LocalFileBrowserView?null: fileBrowser.getInfo().getId();
    	for(String path:BookmarkManager.getBookmarks(id)) {
          JMenuItem item = new JMenuItem(PathUtils.getFileName(path));
          item.setName(path);
          this.favouriteLocations.add(item);
          item.addActionListener(e -> {
              fileBrowserView.render(item.getName());
          });
    	}
//    	throw new Exception("should not call this");
//        this.favouriteLocations.removeAll();
//        for (String fav : holder.getFavouriteLocations(fileBrowserView)) {
//            JMenuItem item = new JMenuItem(PathUtils.getFileName(fav));
//            item.setName(fav);
//            this.favouriteLocations.add(item);
//            item.addActionListener(e -> {
//                fileBrowserView.render(item.getName());
//            });
//        }
    }

	private void hideOptAction() {
		folderView.setShowHiddenFiles(mShowHiddenFiles.isSelected());
	}

	private JRadioButtonMenuItem createSortMenuItem(String text, Integer index, ButtonGroup bg) {
		JRadioButtonMenuItem mSortItem = new JRadioButtonMenuItem(text);
		mSortItem.putClientProperty("sort.index", index);
		mSortItem.addActionListener(e -> {
			sortMenuClicked(mSortItem);
		});
		bg.add(mSortItem);
		return mSortItem;
	}

	private void sortMenuClicked(JRadioButtonMenuItem mSortItem) {
//        if (mSortItem == mSortAsc) {
//            folderView.sortView(folderView.getSortIndex(), true);
//        } else if (mSortItem == mSortDesc) {
//            folderView.sortView(folderView.getSortIndex(), false);
//        } else {
//            int index = (int) mSortItem.getClientProperty("sort.index");
//            folderView.sortView(index, folderView.isSortAsc());
//        }
	}

	public JPopupMenu getOverflowMenu() {
		return popup;
	}

	public void setFolderView(FolderView folderView) {
//        mSortAsc.setSelected(folderView.isSortAsc());
//        mSortDesc.setSelected(!folderView.isSortAsc());
//        int sortIndex = folderView.getSortIndex();
//        switch (sortIndex) {
//            case 0:
//                mSortName.setSelected(true);
//                break;
//            case 1:
//                mSortSize.setSelected(true);
//                break;
//            case 2:
//                mSortModified.setSelected(true);
//                break;
//        }
		InputMap map = folderView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = folderView.getActionMap();
		this.folderView = folderView;
		map.put(ksHideShow, "ksHideShow");
		act.put("ksHideShow", aHideShow);
	}

	public JPopupMenu getSortMenu() {
		return this.mSortMenu;
	}

}
