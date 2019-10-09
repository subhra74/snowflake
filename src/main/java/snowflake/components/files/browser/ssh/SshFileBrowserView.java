package snowflake.components.files.browser.ssh;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.local.files.LocalFileSystem;
import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshModalUserInteraction;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.common.AddressBar;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.DndTransferHandler;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.FileBrowser;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SshFileBrowserView extends AbstractFileBrowserView {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SshMenuHandler menuHandler;
    private FileBrowser fileBrowser;
    private JPopupMenu addressPopup;
    private DndTransferHandler transferHandler;
    private JComboBox<String> cmbOptions = new JComboBox<>(new String[]{"Transfer normally", "Transfer in background"});

    public SshFileBrowserView(FileBrowser fileBrowser,
                              JRootPane rootPane, FileComponentHolder holder, String initialPath, PanelOrientation orientation) {
        super(rootPane, holder, orientation, fileBrowser, new Color(250, 250, 250));// new Color(240, 255, 240));
        this.fileBrowser = fileBrowser;
        this.menuHandler = new SshMenuHandler(fileBrowser, this, holder);
        this.menuHandler.initMenuHandler(this.folderView);
        this.transferHandler = new DndTransferHandler(this.folderView, holder.getInfo(), this);
        this.folderView.setTransferHandler(transferHandler);
        this.folderView.setFolderViewTransferHandler(transferHandler);
        this.addressPopup = menuHandler.createAddressPopup();
        if (initialPath == null) {
            this.path = holder.getInfo().getRemoteFolder();
            if (this.path != null && this.path.trim().length() < 1) {
                this.path = null;
            }
            System.out.println("Path: " + path);
        } else {
            this.path = initialPath;
        }

        this.render(path, App.getGlobalSettings().isDirectoryCache());
    }


    private void openDefaultAction() {
    }

    private void openNewTab() {

    }

    public void createAddressBar() {
        addressBar = new AddressBar('/', new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPath = e.getActionCommand();
                addressPopup.setName(selectedPath);
                MouseEvent me = (MouseEvent) e.getSource();
                addressPopup.show(me.getComponent(), me.getX(), me.getY());
                System.out.println("clicked");
            }
        });
        if (App.getGlobalSettings().isShowPathBar()) {
            addressBar.switchToPathBar();
        } else {
            addressBar.switchToText();
        }
    }

    @Override
    public String toString() {
        return holder.getInfo().getName() +
                (this.path == null || this.path.length() < 1 ? "" : " [" + this.path + "]");
    }

//    private void connect() throws Exception {
//        synchronized (fileSystemMap) {
//            fs = fileSystemMap.get(source.getInfo());
//            if (fs == null || !fs.isConnected()) {
//                if (fs == null) {
//                    fs = new SshFileSystem(source);
//                }
//                try {
//                    fs.connect();
//                    fileSystemMap.put(source.getInfo(), fs);
//                    fileViewMap.put(fs, 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                fileViewMap.put(fs, fileViewMap.get(fs) + 1);
//            }
//
//            String home = source.getInfo().getRemoteFolder();
//            if (home == null) {
//                home = fs.getHome();
//            }
//            this.path = home;
//            final String finalHome = home;
//            SwingUtilities.invokeLater(() -> {
//                addressBar.setText(finalHome);
//            });
//        }
//    }

    private void renderDirectory(final String path, final boolean fromCache) throws Exception {
        List<FileInfo> list = null;
        if (fromCache) {
            list = holder.getDirectoryCache().get(path);
        }
        if (list == null) {
            list = holder.getSshFileSystem().list(path);
            if (fromCache && list != null) {
                holder.getDirectoryCache().put(path, list);
            }
        }
        if (list != null) {
            final List<FileInfo> list2 = list;
            System.out.println("New file list: " + list2);
            SwingUtilities.invokeLater(() -> {
                addressBar.setText(path);
                folderView.setItems(list2);
            });
        }
    }

    @Override
    public void render(String path, boolean useCache) {
        System.out.println("Rendering: " + path);
        this.path = path;
        executor.submit(() -> {
            this.fileBrowser.disableUi();
            while (!holder.isCloseRequested().get()) {
                System.out.println("Listing files now ...");
                try {
                    if (path == null) {
                        this.path = holder.getSshFileSystem().getHome();
                        //holder.getSshFileSystem().statFs();
                    }
                    renderDirectory(this.path, useCache);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (holder.isCloseRequested().get()) {
                        return;
                    }
                    System.out.println("Exception caught in sftp file browser");
                    e.printStackTrace();
                    if (JOptionPane.showConfirmDialog(null,
                            "Unable to connect to server " + holder.getInfo().getName() + " at " + holder.getInfo().getHost() +
                                    "\nDo you want to retry?") == JOptionPane.YES_OPTION) {
                        continue;
                    }
                    break;
                }
            }
            this.fileBrowser.enableUi();
        });
    }

    @Override
    public void render(String path) {
        this.render(path, false);
    }

    @Override
    public void openApp(FileInfo file) {

    }

    protected void up() {
        if (path != null) {
            String parent = PathUtils.getParent(path);
            addBack(path);
            render(parent, App.getGlobalSettings().isDirectoryCache());
        }
    }

    protected void home() {
        addBack(path);
        render(null, App.getGlobalSettings().isDirectoryCache());
    }

    @Override
    public void install(JComponent c) {

    }

    @Override
    public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
        if (this.path == null) {
            return false;
        }
        return menuHandler.createMenu(popup, files);
    }

    public boolean handleDrop(DndTransferData transferData) {
        if (App.getGlobalSettings().isConfirmBeforeMoveOrCopy() && JOptionPane.showConfirmDialog(null, "Move/copy files?") != JOptionPane.YES_OPTION) {
            return false;
        }
        try {
            if (JOptionPane.showOptionDialog(holder,
                    new Object[]{"Please select a transfer mode", cmbOptions},
                    "Transfer options",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null) != JOptionPane.OK_OPTION) {
                return false;
            }
            boolean backgroundTransfer = cmbOptions.getSelectedIndex() == 1;
            System.out.println("Dropped: " + transferData);
            int sessionHashCode = transferData.getInfo();
            FileSystem sourceFs = null;
            if (sessionHashCode == 0) {
                sourceFs = new LocalFileSystem();
            } else if (sessionHashCode == holder.getInfo().hashCode()) {
                sourceFs = holder.getSshFileSystem();
            }
            if (sourceFs instanceof LocalFileSystem) {
                if (backgroundTransfer) {
                    FileSystem targetFs = new SshFileSystem(new SshModalUserInteraction(holder.getInfo()));
                    holder.newFileTransfer(sourceFs, targetFs, transferData.getFiles(), transferData.getCurrentDirectory(),
                            this.path, this.hashCode(), -1, true);
                    return true;
                }
                FileSystem targetFs = holder.getSshFileSystem();
                holder.newFileTransfer(sourceFs, targetFs, transferData.getFiles(), transferData.getCurrentDirectory(),
                        this.path, this.hashCode(), -1, false);
            } else if (sourceFs instanceof SshFileSystem) {
                System.out.println("SshFs is of same instance: " + (sourceFs == holder.getSshFileSystem()));
                if ((sourceFs == holder.getSshFileSystem())) {
                    if (transferData.getFiles().length > 0) {
                        FileInfo fileInfo = transferData.getFiles()[0];
                        String parent = PathUtils.getParent(fileInfo.getPath());
                        System.out.println("Parent: " + parent + " == " + this.getCurrentDirectory());
                        if (!parent.endsWith("/")) {
                            parent += "/";
                        }
                        String pwd = this.getCurrentDirectory();
                        if (!pwd.endsWith("/")) {
                            pwd += "/";
                        }
                        if (parent.equals(pwd)) {
                            JOptionPane.showMessageDialog(null, "Cant move files like this!");
                            return false;
                        }
                    }
                }

                if (transferData.getTransferAction() == DndTransferData.TransferAction.Copy) {
                    menuHandler.copy(Arrays.asList(transferData.getFiles()), getCurrentDirectory());
                } else {
                    menuHandler.move(Arrays.asList(transferData.getFiles()), getCurrentDirectory());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public FileSystem getFileSystem() throws Exception {
        return this.holder.getSshFileSystem();
    }

    public SshClient getSshClient() throws Exception {
        return ((SshFileSystem) this.holder.getSshFileSystem()).getWrapper();
    }

    @Override
    public TransferHandler getTransferHandler() {
        return transferHandler;
    }

    public String getHostText() {
        return holder.getInfo().getName();
    }

    public String getPathText() {
        return (this.path == null || this.path.length() < 1 ? "" : this.path);
    }

}
