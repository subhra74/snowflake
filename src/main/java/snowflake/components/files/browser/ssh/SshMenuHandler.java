package snowflake.components.files.browser.ssh;

import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.DndTransferHandler;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.files.browser.folderview.FolderView;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshMenuHandler {
    private AbstractAction aOpenInTab, aOpen, aRename, aDelete, aNewFile, aNewFolder, aCopy, aPaste, aCut, aAddToFav,
            aChangePerm, aSendFiles, aUpload, aDownload, aCreateLink, aCopyPath;

    private KeyStroke ksOpenInTab, ksOpen, ksRename, ksDelete, ksNewFile, ksNewFolder, ksCopy, ksPaste, ksCut,
            ksAddToFav, ksChangePerm, ksSendFiles, ksUpload, ksDownload, ksCreateLink, ksCopyPath;

    private JMenuItem mOpenInTab, mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy, mPaste, mCut, mAddToFav,
            mChangePerm, mSendFiles, mUpload, mOpenWithDefApp, mOpenWthInternalEdit, mOpenWithCustom, mOpenWithLogView,
            mDownload, mCreateLink, mCopyPath, mOpenFolderInTerminal, mOpenTerminalHere, mRunScriptInTerminal,
            mRunScriptInBackground;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private JMenu mOpenWith;

    private FileBrowser fileBrowser;
    private FolderView folderView;
    private SftpFileOperations fileOperations;
    private SftpFileBrowserView fileBrowserView;
    private FileComponentHolder holder;

    public SshMenuHandler(FileBrowser fileBrowser, SftpFileBrowserView fileBrowserView,
                          FileComponentHolder holder) {
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
        mOpenWithDefApp.addActionListener(e -> openDefaultApp());

        mOpenWthInternalEdit = new JMenuItem("Internal editor");
        mOpenWthInternalEdit.addActionListener(e -> openWithInternalEditor());

        mOpenWithCustom = new JMenuItem("Default editor");
        mOpenWithCustom.addActionListener(e -> openDefaultEditor());

        mOpenWithLogView = new JMenuItem("Log viewer");
        mOpenWithLogView.addActionListener(e -> openLogViewer());

        mOpenWith = new JMenu("Open with");
        mOpenWith.add(mOpenWithDefApp);
        mOpenWith.add(mOpenWithCustom);
        mOpenWith.add(mOpenWthInternalEdit);
        mOpenWith.add(mOpenWithLogView);

        mRunScriptInTerminal = new JMenuItem("Run in terminal");
        mRunScriptInTerminal.addActionListener(e -> {

        });

        mOpenFolderInTerminal = new JMenuItem("Open folder in terminal");
        mOpenFolderInTerminal.addActionListener(e -> {
            openFolderInTerminal(folderView.getSelectedFiles()[0].getPath());
        });

        mOpenTerminalHere = new JMenuItem("Open terminal here");
        mOpenTerminalHere.addActionListener(e -> {
            openFolderInTerminal(fileBrowserView.getCurrentDirectory());
        });

        mRunScriptInTerminal = new JMenuItem("Run file in terminal");
        mRunScriptInTerminal.addActionListener(e -> {
            openRunInTerminal(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles()[0].getPath());
        });

        mRunScriptInBackground = new JMenuItem("Run file in background");
        mRunScriptInBackground.addActionListener(e -> {
            openRunInBackground(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles()[0].getPath());
        });

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
                copyToClipboard(false);
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
                copyPathToClipboard();
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
                handlePaste();
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
                copyToClipboard(true);
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
                addToFavourites();
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

    private void copyToClipboard(boolean cut) {
        FileInfo[] selectedFiles = folderView.getSelectedFiles();
        DndTransferData transferData = new DndTransferData(holder.getInfo().hashCode(), selectedFiles, fileBrowserView.getCurrentDirectory(), fileBrowserView.hashCode());
        transferData.setTransferAction(cut ? DndTransferData.TransferAction.Cut : DndTransferData.TransferAction.Copy);

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DndTransferHandler.DATA_FLAVOR};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                if (flavor.equals(DndTransferHandler.DATA_FLAVOR)) {
                    return true;
                }
                return false;
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return transferData;
            }
        }, (a, b) -> {
        });
    }

    private void copyPathToClipboard() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (FileInfo f : folderView.getSelectedFiles()) {
            if (!first) {
                sb.append("\n");
            }
            sb.append(f.getPath());
            if (first) {
                first = false;
            }
        }
        if (sb.length() > 0) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        }
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
        count += createBuiltInItems1(selectionCount, popup, files);
        count += createBuiltInItems2(selectionCount, popup, files);
        return count > 0;
    }

    private int createBuiltInItems1(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
        int count = 0;
        if (selectionCount == 1) {
            if (selectedFiles[0].getType() == FileType.Directory
                    || selectedFiles[0].getType() == FileType.DirLink) {
                popup.add(mOpenInTab);
                count++;
                popup.add(mOpenFolderInTerminal);
                count++;
            }

            if ((selectedFiles[0].getType() == FileType.File
                    || selectedFiles[0].getType() == FileType.FileLink)) {
                popup.add(mOpen);
                count++;
                popup.add(mOpenWith);
                count++;
                popup.add(mRunScriptInTerminal);
                popup.add(mRunScriptInBackground);
            }
        }

        if (selectionCount > 0) {
            popup.add(mCut);
            popup.add(mCopy);
            popup.add(mCopyPath);
            count += 3;
        }

        if (hasSupportedContentOnClipboard()) {
            popup.add(mPaste);
        }

        if (selectionCount == 1) {
            popup.add(mRename);
            count++;
        }

        return count;
    }

    private int createBuiltInItems2(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
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
            popup.add(mOpenTerminalHere);
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
                fileBrowser.openSftpFileBrowserView(file.getPath(), this.fileBrowserView.getOrientation());
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
            try {
                if (fileOperations.rename(oldName, newName, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                    fileBrowserView.render(baseFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileBrowser.enableUi();
            }
        });
    }

    private void delete(FileInfo[] targetList, String baseFolder) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.delete(targetList, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                    fileBrowserView.render(baseFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileBrowser.enableUi();
            }

        });
    }

    public void newFile(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.newFile(files, fileBrowserView.getFileSystem(), baseFolder, fileBrowserView.getSshClient())) {
                    fileBrowserView.render(baseFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileBrowser.enableUi();
            }

        });
    }

    public void newFolder(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.newFolder(files, baseFolder, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                    fileBrowserView.render(baseFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fileBrowser.enableUi();
            }

        });
    }

    public void createLink(String baseFolder, FileInfo[] files) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.createLink(files, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
                    fileBrowserView.render(baseFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
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

    private void handlePaste() {
        if (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DndTransferHandler.DATA_FLAVOR)) {
            try {
                DndTransferData transferData = (DndTransferData) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DndTransferHandler.DATA_FLAVOR);
//                for(DataFlavor df:Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors()){
//                    Object obj=Toolkit.getDefaultToolkit().getSystemClipboard().getData(df);
//                    System.out.println(obj);
//                }
                if (transferData != null) {
                    fileBrowserView.handleDrop(transferData);
                }
            } catch (UnsupportedFlavorException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            DataFlavor[] flavors = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
            for (DataFlavor flavor : flavors) {
                if (flavor.isFlavorJavaFileListType()) {

                }
            }
        }
    }

    private boolean hasSupportedContentOnClipboard() {
        boolean ret = (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DndTransferHandler.DATA_FLAVOR) ||
                Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.javaFileListFlavor));
        if (!ret) System.out.println("Nothing on clipboard");
        return ret;
    }

    public void copy(List<FileInfo> files, String targetFolder) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.copyTo(fileBrowserView.getSshClient(), files, targetFolder, fileBrowserView.getFileSystem())) {
                    fileBrowserView.render(targetFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void move(List<FileInfo> files, String targetFolder) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.moveTo(fileBrowserView.getSshClient(), files, targetFolder, fileBrowserView.getFileSystem())) {
                    fileBrowserView.render(targetFolder);
                } else {
                    fileBrowser.enableUi();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            fileBrowser.openSftpFileBrowserView(path, this.fileBrowserView.getOrientation());
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

    private void openFolderInTerminal(String folder) {
        holder.openTerminal("cd \"" + folder + "\"");
    }

    private void openRunInTerminal(String folder, String file) {
        holder.openTerminal("cd \"" + folder + "\"; \"" + file + "\"");
    }

    private void openRunInBackground(String folder, String file) {
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (fileOperations.runScriptInBackground(fileBrowserView.getSshClient(),
                        "cd \"" + folder + "\"; nohup \"" + file + "\" &",
                        new AtomicBoolean())) {
                }
                fileBrowser.enableUi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
