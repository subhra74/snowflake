package snowflake.components.files.browser.sftp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.common.AddressBar;
import snowflake.components.files.DndTransferData;
import snowflake.components.files.DndTransferHandler;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;

public class SftpFileBrowserView extends AbstractFileBrowserView {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private FileBrowser fileBrowser;
    private DndTransferHandler transferHandler;
    private JPopupMenu addressPopup;
    private SftpMenuHandler menuHandler;

    private SshFileSystem fs;
    private SessionInfo foreignInfo;
    private JComboBox<String> cmbOptions = new JComboBox<>(new String[]{"Transfer normally", "Transfer in background"});

    public SftpFileBrowserView(FileBrowser fileBrowser,
                               JRootPane rootPane, FileComponentHolder holder,
                               String initialPath, PanelOrientation orientation, SessionInfo foreignInfo) {
        super(rootPane, holder, orientation, fileBrowser, new Color(250, 250, 250));//new Color(240, 240, 255));
        this.fileBrowser = fileBrowser;
        this.foreignInfo = foreignInfo;
        this.fs = null;//new SshFileSystem(new SshUserInteraction(foreignInfo, rootPane));
        this.menuHandler = new SftpMenuHandler(fileBrowser, this, holder, fs);
        this.menuHandler.initMenuHandler(this.folderView);
        this.transferHandler = new DndTransferHandler(this.folderView, null,
                this, DndTransferData.DndSourceType.SFTP);
        this.folderView.setTransferHandler(transferHandler);
        this.folderView.setFolderViewTransferHandler(transferHandler);
        this.addressPopup = menuHandler.createAddressPopup();
        if (initialPath != null) {
            this.path = initialPath;
        }

        render(this.path);
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
        return this.foreignInfo.getName() + " [" + this.path + "]";
    }

    public String getHostText() {
        return this.foreignInfo.getName();
    }

    public String getPathText() {
        return (this.path == null || this.path.length() < 1 ? "" : this.path);
    }

    @Override
    public void render(String path, boolean useCache) {
        this.render(path);
    }

    @Override
    public void render(String path) {
        this.path = path;
        executor.submit(() -> {
            fileBrowser.disableUi();
            try {
                if (this.path == null) {
                    this.path = fs.getHome();
                }
                List<FileInfo> list = fs.list(this.path);
                SwingUtilities.invokeLater(() -> {
                    addressBar.setText(this.path);
                    folderView.setItems(list);
                });
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Operation failed");
                e.printStackTrace();
            }
            fileBrowser.enableUi();
        });
    }

    @Override
    public void openApp(FileInfo file) {

    }

    @Override
    public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
        menuHandler.createMenu(popup, files);
        return true;
    }

    protected void up() {
        String s = PathUtils.getParent(path);// new File(path).getParent();
        if (s != null) {
            addBack(path);
            render(s);
        }
    }

    protected void home() {
        addBack(path);
        render(null);
    }

    @Override
    public void install(JComponent c) {

    }

    public boolean handleDrop(DndTransferData transferData) {
        try {
            System.out.println("### " + transferData.getSource() + " " + this.hashCode());
            if (transferData.getSource() == this.hashCode()) {
                return false;
            }
            if (App.getGlobalSettings().isConfirmBeforeMoveOrCopy()
                    && JOptionPane.showConfirmDialog(null,
                    "Copy files?") != JOptionPane.YES_OPTION) {
                return false;
            }
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
            if (sessionHashCode == 0) return true;
            SessionInfo info = holder.getInfo();
            if (info != null && info.hashCode() == sessionHashCode) {
                if (backgroundTransfer) {
                    FileSystem sourceFs = null;//new SshFileSystem(new SshModalUserInteraction(holder.getInfo()));
                    FileSystem targetFs = null;//new SshFileSystem(new SshModalUserInteraction(this.foreignInfo));
                    holder.newFileTransfer(sourceFs, targetFs, transferData.getFiles(), transferData.getCurrentDirectory(),
                            this.path, this.hashCode(), -1, true);
                    return true;
                }
                FileSystem sourceFs = holder.getSshFileSystem();
                if (sourceFs == null) {
                    return false;
                }
                FileSystem targetFs = this.fs;
                holder.newFileTransfer(sourceFs, targetFs, transferData.getFiles(),
                        transferData.getCurrentDirectory(), this.path, this.hashCode(),
                        -1, false);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void close() {
        executor.submit(() -> {
            try {
                this.fs.close();
                System.out.println("Closed sftp foreign fs");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public FileSystem getFileSystem() throws Exception {
        return this.fs;
    }
}
