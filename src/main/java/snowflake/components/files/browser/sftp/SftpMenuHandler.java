package snowflake.components.files.browser.sftp;

import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SftpMenuHandler {
    private JMenuItem mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy, mPaste, mCut, mAddToFav;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private FileBrowser fileBrowser;
    private FolderView folderView;
    private FileComponentHolder holder;
    private SftpFileBrowserView fileBrowserView;
    private SftpFileOperations fileOperations;
    private SshFileSystem fs;

    public SftpMenuHandler(FileBrowser fileBrowser,
                           SftpFileBrowserView fileBrowserView,
                           FileComponentHolder holder,
                           SshFileSystem fs) {
        this.fileBrowser = fileBrowser;
        this.holder = holder;
        this.fileOperations = new SftpFileOperations();
        this.fileBrowserView = fileBrowserView;
        this.fs = fs;
    }

    public void initMenuHandler(FolderView folderView) {
        this.folderView = folderView;
        InputMap map = folderView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap act = folderView.getActionMap();
        this.initMenuItems();
    }

    private void initMenuItems() {
        mOpen = new JMenuItem("Open in new tab");
        mOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNewTab();
            }
        });

        mRename = new JMenuItem("Rename");
        mRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rename(folderView.getSelectedFiles()[0], fileBrowserView.getCurrentDirectory());
            }
        });

        mDelete = new JMenuItem("Delete");
        mDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete(folderView.getSelectedFiles());
            }
        });

        mNewFile = new JMenuItem("New file");
        mNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });

        mNewFolder = new JMenuItem("New folder");
        mNewFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFolder(fileBrowserView.getCurrentDirectory());
            }
        });

        mCopy = new JMenuItem("Copy");
        mCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //copyToClipboard(false);
            }
        });

        mPaste = new JMenuItem("Paste");
        mPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (AppClipboard.getContent() instanceof TransferFileInfo) {
//                    TransferFileInfo info = (TransferFileInfo) AppClipboard.getContent();
//                    localFolderView.pasteItem(info, folderView);
//                    if (info.getAction() == Action.CUT) {
//                        AppClipboard.setContent(null);
//                    }
//                }
            }
        });

        mCut = new JMenuItem("Cut");
        mCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //copyToClipboard(true);
            }
        });

        mAddToFav = new JMenuItem("Bookmark");
        mAddToFav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToFavourites();
            }
        });
    }


    public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles) {
        createMenuContext(popup, selectedFiles);
    }

    private void createMenuContext(JPopupMenu popup, FileInfo[] files) {
        popup.removeAll();
        int selectionCount = files.length;
        createBuitinItems1(selectionCount, popup, files);
        createBuitinItems2(selectionCount, popup);
    }

    private void createBuitinItems1(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
        if (selectedFiles.length == 1 && (selectedFiles[0].getType() == FileType.Directory
                || selectedFiles[0].getType() == FileType.DirLink)) {
            popup.add(mOpen);
        }

        if (selectionCount == 1) {
            popup.add(mRename);
        }

//        if (selectionCount > 0) {
//            popup.add(mDelete);
//        }
    }

    private void createBuitinItems2(int selectionCount, JPopupMenu popup) {
        popup.add(mNewFolder);
        popup.add(mNewFile);
        // check only if folder is selected
        popup.add(mAddToFav);
    }

    private void openNewTab() {
        FileInfo files[] = folderView.getSelectedFiles();
        if (files.length == 1) {
            FileInfo file = files[0];
            if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
                fileBrowser.openLocalFileBrowserView(file.getPath(), this.fileBrowserView.getOrientation());
            }
        }
    }

    private void rename(FileInfo info, String baseFolder) {
        String text = JOptionPane
                .showInputDialog("Please enter new name", info.getName());
        if (text != null && text.length() > 0) {
            renameAsync(info.getPath(), PathUtils.combineUnix(PathUtils.getParent(info.getPath()), text), baseFolder);
        }
    }

    private void renameAsync(String oldName, String newName, String baseFolder) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            if (fileOperations.rename(fs, oldName, newName)) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    private void delete(FileInfo[] selectedFiles) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            for (FileInfo f : selectedFiles) {
                try {
                    fs.delete(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fileBrowser.enableUi();
        });
    }

    private void newFile() {
        executor.submit(() -> {
            fileBrowser.disableUi();
            String baseFolder = fileBrowserView.getCurrentDirectory();
            if (fileOperations.newFile(fs, baseFolder)) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    private void newFolder(String currentDirectory) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            String baseFolder = currentDirectory;
            if (fileOperations.newFolder(fs, baseFolder)) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    private void addToFavourites() {
        FileInfo arr[] = folderView.getSelectedFiles();
        if (arr.length == 1) {
            holder.addFavouriteLocation(fileBrowserView, arr[0].getPath());
            this.fileBrowserView.getOverflowMenuHandler().loadFavourites();
        } else if (arr.length == 0) {
            holder.addFavouriteLocation(fileBrowserView, fileBrowserView.getCurrentDirectory());
            this.fileBrowserView.getOverflowMenuHandler().loadFavourites();
        }
    }

    public JPopupMenu createAddressPopup() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mOpenInNewTab = new JMenuItem("Open in new tab");
        JMenuItem mCopyPath = new JMenuItem("Copy path");
        JMenuItem mOpenInTerminal = new JMenuItem("Open in terminal");
        JMenuItem mBookmark = new JMenuItem("Bookmark");
        popupMenu.add(mOpenInNewTab);
        popupMenu.add(mCopyPath);
        popupMenu.add(mOpenInTerminal);
        popupMenu.add(mBookmark);

        mOpenInNewTab.addActionListener(e -> {
            String path = popupMenu.getName();
            fileBrowser.openLocalFileBrowserView(path, this.fileBrowserView.getOrientation());
        });

        mOpenInTerminal.addActionListener(e -> {

        });

        mCopyPath.addActionListener(e -> {
            String path = popupMenu.getName();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(path), null);
        });

        mBookmark.addActionListener(e -> {
            String path = popupMenu.getName();
            holder.addFavouriteLocation(fileBrowserView, path);
            this.fileBrowserView.getOverflowMenuHandler().loadFavourites();
        });
        return popupMenu;
    }
}
