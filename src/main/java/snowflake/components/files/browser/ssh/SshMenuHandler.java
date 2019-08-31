package snowflake.components.files.browser.ssh;

import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SshMenuHandler {
    private AbstractAction aOpenInTab, aOpen, aRename, aDelete, aNewFile, aNewFolder, aCopy, aPaste, aCut, aAddToFav,
            aChangePerm, aSendFiles, aUpload, aDownload, aCreateLink, aCopyPath;

    private KeyStroke ksOpenInTab, ksOpen, ksRename, ksDelete, ksNewFile, ksNewFolder, ksCopy, ksPaste, ksCut,
            ksAddToFav, ksChangePerm, ksSendFiles, ksUpload, ksDownload, ksCreateLink, ksCopyPath;

    private JMenuItem mOpenInTab, mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy, mPaste, mCut, mAddToFav,
            mChangePerm, mSendFiles, mUpload, mOpenWithDefApp, mOpenWthInternalEdit, mOpenWithCustom, mOpenWithLogView,
            mDownload, mCreateLink, mCopyPath;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private JMenu mOpenWith;

    private FileBrowser fileBrowser;
    private FolderView folderView;
    private SftpFileOperations fileOperations;
    private SftpFileBrowserView fileBrowserView;
    private FileComponentHolder holder;

    public SshMenuHandler(FileBrowser fileBrowser, SftpFileBrowserView fileBrowserView, FileComponentHolder holder) {
        this.fileBrowser = fileBrowser;
        this.holder = holder;
        this.fileOperations = new SftpFileOperations();
        this.fileBrowserView = fileBrowserView;
    }

    public void initMenuHandler(FolderView folderView) {
        this.folderView = folderView;
        InputMap map = folderView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap act = folderView.getActionMap();
        this.initMenuItems(map, act);
    }

    private void initMenuItems(InputMap map, ActionMap act) {
        ksOpenInTab = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK);
        mOpenInTab = new JMenuItem("Open in new tab");
        mOpenInTab.setAccelerator(ksOpenInTab);
        aOpenInTab = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNewTab();
            }
        };
        mOpenInTab.addActionListener(aOpenInTab);
        map.put(ksOpenInTab, "ksOpenInTab");
        act.put("ksOpenInTab", aOpenInTab);

        aOpen = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Open called");
//                openDefaultAction();
            }
        };
        ksOpen = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        mOpen = new JMenuItem("Open");
        mOpen.addActionListener(aOpen);
        map.put(ksOpen, "mOpen");
        act.put("mOpen", aOpen);
        mOpen.setAccelerator(ksOpen);

        mOpenWithDefApp = new JMenuItem("Default application");
        mOpenWithDefApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDefaultApp();
            }
        });

        mOpenWthInternalEdit = new JMenuItem("Internal editor");
        mOpenWthInternalEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWithInternalEditor();
            }
        });

        mOpenWithCustom = new JMenuItem("Default editor");
        mOpenWithCustom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDefaultEditor();
            }
        });

        mOpenWithLogView = new JMenuItem("Log viewer");
        mOpenWithLogView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLogViewer();
            }
        });

        mOpenWith = new JMenu("Open with");
        mOpenWith.add(mOpenWithDefApp);
        mOpenWith.add(mOpenWithCustom);
        mOpenWith.add(mOpenWthInternalEdit);
        mOpenWith.add(mOpenWithLogView);

        aRename = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rename(folderView.getSelectedFiles()[0], fileBrowserView.getCurrentDirectory());
            }
        };
        ksRename = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
        mRename = new JMenuItem("Rename");
        mRename.addActionListener(aRename);
        map.put(ksRename, "mRename");
        act.put("mRename", aRename);
        mRename.setAccelerator(ksRename);

        ksDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        aDelete = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null,
                        "Selected files will be deleted permanently, continue?", "Confirm delete",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    delete(folderView.getSelectedFiles(), fileBrowserView.getCurrentDirectory());
                }
            }
        };
        mDelete = new JMenuItem("Delete");
        mDelete.addActionListener(aDelete);
        map.put(ksDelete, "ksDelete");
        act.put("ksDelete", aDelete);
        mDelete.setAccelerator(ksDelete);

        ksNewFile = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        aNewFile = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile(fileBrowserView.getCurrentDirectory(), folderView.getFiles());
            }
        };
        mNewFile = new JMenuItem("New file");
        mNewFile.addActionListener(aNewFile);
        map.put(ksNewFile, "ksNewFile");
        act.put("ksNewFile", aNewFile);
        mNewFile.setAccelerator(ksNewFile);

        ksNewFolder = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
        aNewFolder = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFolder(fileBrowserView.getCurrentDirectory(), folderView.getFiles());
            }
        };
        mNewFolder = new JMenuItem("New folder");
        mNewFolder.addActionListener(aNewFolder);
        mNewFolder.setAccelerator(ksNewFolder);
        map.put(ksNewFolder, "ksNewFolder");
        act.put("ksNewFolder", aNewFolder);

        ksCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        aCopy = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //copyToClipboard(false);
            }
        };
        mCopy = new JMenuItem("Copy");
        mCopy.addActionListener(aCopy);
        map.put(ksCopy, "ksCopy");
        act.put("ksCopy", aCopy);
        mCopy.setAccelerator(ksCopy);

        ksCopyPath = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        aCopyPath = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //copyPathToClipboard();
            }
        };
        mCopyPath = new JMenuItem("Copy path");
        mCopyPath.addActionListener(aCopyPath);
        map.put(ksCopyPath, "ksCopyPath");
        act.put("ksCopyPath", aCopyPath);
        mCopyPath.setAccelerator(ksCopyPath);

        ksPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
        aPaste = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if (AppClipboard.getContent() instanceof TransferFileInfo) {
//                    TransferFileInfo info = (TransferFileInfo) AppClipboard.getContent();
//                    remoteFolderView.pasteItem(info, folderView);
//                    if (info.getAction() == Action.CUT) {
//                        AppClipboard.setContent(null);
//                    }
//                }
            }
        };
        mPaste = new JMenuItem("Paste");
        mPaste.addActionListener(aPaste);
        map.put(ksPaste, "ksPaste");
        act.put("ksPaste", aPaste);
        mPaste.setAccelerator(ksPaste);

        ksCut = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
        aCut = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //copyToClipboard(true);
            }
        };
        mCut = new JMenuItem("Cut");
        mCut.addActionListener(aCut);
        map.put(ksCut, "ksCut");
        act.put("ksCut", aCut);
        mCut.setAccelerator(ksCut);

        ksAddToFav = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
        aAddToFav = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //addToFavourites();
            }
        };
        mAddToFav = new JMenuItem("Bookmark");
        mAddToFav.addActionListener(aAddToFav);
        map.put(ksAddToFav, "ksAddToFav");
        act.put("ksAddToFav", aAddToFav);
        mAddToFav.setAccelerator(ksAddToFav);

        ksChangePerm = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);
        aChangePerm = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //changePermission(folderView.getSelectedFiles(), folderView.getCurrentPath());
            }
        };
        mChangePerm = new JMenuItem("Properties");
        mChangePerm.addActionListener(aChangePerm);
        map.put(ksChangePerm, "ksChangePerm");
        act.put("ksChangePerm", aChangePerm);
        mChangePerm.setAccelerator(ksChangePerm);

        ksCreateLink = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
        aCreateLink = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createLink(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles());
            }
        };
        mCreateLink = new JMenuItem("Create link");
        mCreateLink.addActionListener(aCreateLink);
        map.put(ksCreateLink, "ksCreateLink");
        act.put("ksCreateLink", aCreateLink);
        mCreateLink.setAccelerator(ksCreateLink);
    }

    private void openLogViewer() {
        holder.openWithLogViewer(folderView.getSelectedFiles()[0]);
    }

    private void openWithInternalEditor() {
        FileInfo file = folderView.getSelectedFiles()[0];
        holder.editRemoteFileInternal(file);
    }

    public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
        popup.removeAll();
        int selectionCount = files.length;
        int count = 0;
        count += createBuitinItems1(selectionCount, popup, files);
        count += createBuitinItems2(selectionCount, popup, files);
        return count > 0;
    }

    private int createBuitinItems1(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
        int count = 0;
        if (selectionCount == 1) {
            if (selectedFiles[0].getType() == FileType.Directory
                    || selectedFiles[0].getType() == FileType.DirLink) {
                popup.add(mOpenInTab);
                count++;
            }

            if ((selectedFiles[0].getType() == FileType.File
                    || selectedFiles[0].getType() == FileType.FileLink)) {
                popup.add(mOpen);
                count++;
                popup.add(mOpenWith);
                count++;
            }
        }

        if (selectionCount > 0) {
            popup.add(mCut);
            popup.add(mCopy);
            popup.add(mCopyPath);
            count += 3;
        }

//        if (AppClipboard.getContent() instanceof TransferFileInfo) {
//            popup.add(mPaste);
//        }

        if (selectionCount == 1) {
            popup.add(mRename);
            count++;
        }

        return count;
    }

    private int createBuitinItems2(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
        int count = 0;
        if (selectionCount > 0) {
            popup.add(mDelete);
            //popup.add(mSendFiles);
            //count += 2;
            count++;
        }

        if (selectionCount < 1) {
            popup.add(mNewFolder);
            popup.add(mNewFile);
            count += 2;
        }

        // check only if folder is selected
        boolean allFolder = true;
        for (FileInfo f : selectedFiles) {
            if (f.getType() != FileType.Directory && f.getType() != FileType.DirLink) {
                allFolder = false;
                break;
            }
        }

        if (selectionCount >= 1 && allFolder) {
            popup.add(mAddToFav);
            count++;
        }

//        if (selectionCount == 0) {
//            popup.add(mUpload);
//            count++;
//        }
//
//        if (selectionCount > 0) {
//            popup.add(mDownload);
//            count++;
//        }

        if (selectionCount <= 1) {
            popup.add(mCreateLink);
            count++;
        }

        if (selectionCount >= 1) {
            popup.add(mChangePerm);
            count++;
        }
        return count;
    }

    public void openNewTab() {
        FileInfo files[] = folderView.getSelectedFiles();
        if (files.length == 1) {
            FileInfo file = files[0];
            if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
                fileBrowser.openSftpFileBrowserView(file.getPath());
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
            if (fileOperations.rename(oldName, newName, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    private void delete(FileInfo[] targetList, String baseFolder) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            if (fileOperations.delete(targetList, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    public void newFile(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            if (fileOperations.newFile(files, fileBrowserView.getFileSystem(), baseFolder, fileBrowserView.getSshClient())) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    public void newFolder(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            if (fileOperations.newFolder(files, baseFolder, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    public void createLink(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            if (fileOperations.createLink(files, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                fileBrowserView.render(baseFolder);
            } else {
                fileBrowser.enableUi();
            }
        });
    }

    public void openDefaultApp() {
        FileInfo fileInfo = folderView.getSelectedFiles()[0];
        holder.openWithDefaultApp(fileInfo);
    }

    public void openDefaultEditor() {
        FileInfo fileInfo = folderView.getSelectedFiles()[0];
        holder.openWithDefaultEditor(fileInfo);
    }
}
