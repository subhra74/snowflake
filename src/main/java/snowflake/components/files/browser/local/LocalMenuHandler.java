package snowflake.components.files.browser.local;

import snowflake.common.FileInfo;
import snowflake.common.FileType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocalMenuHandler {
    private JMenuItem mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy, mPaste, mCut, mAddToFav;

    public void initMenuHandler(JComponent folderView) {
        InputMap map = folderView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap act = folderView.getActionMap();
        this.initMenuItems();
    }

    private void initMenuItems() {
        mOpen = new JMenuItem("Open in new tab");
        mOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //openNewTab();
            }
        });

        mRename = new JMenuItem("Rename");
        mRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //rename(folderView.getSelectedFiles()[0]);
            }
        });

        mDelete = new JMenuItem("Delete");
        mDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //delete(folderView.getSelectedFiles());
            }
        });

        mNewFile = new JMenuItem("New file");
        mNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//				newFile();
            }
        });

        mNewFolder = new JMenuItem("New folder");
        mNewFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //newFolder(folderView.getCurrentPath());
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
                //addToFavourites();
            }
        });
    }

    public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles) {
        createMenuContext(popup, selectedFiles);
    }

    private void createMenuContext(JPopupMenu popup, FileInfo[] files) {
        popup.removeAll();
        int selectionCount = files.length;
        createBuitinItems1(selectionCount, popup,files);
        createBuitinItems2(selectionCount, popup);
    }

    private void createBuitinItems1(int selectionCount, JPopupMenu popup,FileInfo[] selectedFiles) {
        if (selectedFiles[0].getType() == FileType.Directory
                || selectedFiles[0].getType() == FileType.DirLink) {
            popup.add(mOpen);
        }

        if (selectionCount == 1) {
            popup.add(mRename);
        }

        if (selectionCount > 0) {
            popup.add(mCopy);
            popup.add(mCut);
        }
    }

    private void createBuitinItems2(int selectionCount, JPopupMenu popup) {
        popup.add(mNewFolder);
        popup.add(mNewFile);
        // check only if folder is selected
        popup.add(mAddToFav);
    }
}
