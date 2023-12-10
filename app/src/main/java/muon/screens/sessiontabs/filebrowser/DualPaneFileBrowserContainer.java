package muon.screens.sessiontabs.filebrowser;

import muon.constants.Orientation;
import muon.dto.file.FileInfo;
import muon.dto.file.FileType;
import muon.dto.session.SessionInfo;
import muon.screens.sessiontabs.SshClientInstance;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.widgets.SplitPanel;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DualPaneFileBrowserContainer extends JPanel implements Navigator {
    private SplitPanel splitPane;
    private SessionInfo sessionInfo;
    private SshClientInstance clientInstance;
    private TabbedPanel leftTabs;
    private TabbedPanel rightTabs;
    private SftpClientInstance sftpClientInstance;
    private CardLayout cardLayout;

    public DualPaneFileBrowserContainer(SshClientInstance initialInstance,
                                        SessionInfo sessionInfo) {
        super(null);
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        this.sessionInfo = sessionInfo;
        this.clientInstance = initialInstance;
        this.sftpClientInstance = new SftpClientInstance(initialInstance);

        splitPane = new SplitPanel(Orientation.Horizontal);

        createLeftTab();
        createRightTab();

        add("content", splitPane);

        showContent();
    }

    private void createLeftTab() {
        var addTabComponent = AppUtils.createAddTabButton();
        leftTabs = new TabbedPanel(
                true,
                true,
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getBackground(),
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                IconCode.RI_CLOSE_LINE,
                AppTheme.INSTANCE.getButtonBorderColor(),
                addTabComponent,
                true,
                false,
                false
        );
        splitPane.setLeftComponent(leftTabs);
    }

    private void createRightTab() {
        var addTabComponent = AppUtils.createAddTabButton();
        rightTabs = new TabbedPanel(
                true,
                true,
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getBackground(),
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                IconCode.RI_CLOSE_LINE,
                AppTheme.INSTANCE.getButtonBorderColor(),
                addTabComponent,
                true,
                false,
                false
        );
        splitPane.setRightComponent(rightTabs);
    }

    private void updateTitle(String title, Component component) {
        var tabItem = leftTabs.getTabForComponent(component);
        if (Objects.nonNull(tabItem)) {
            tabItem.setTabTitle(title);
            return;
        }
        tabItem = rightTabs.getTabForComponent(component);
        if (Objects.nonNull(tabItem)) {
            tabItem.setTabTitle(title);
        }
    }

    public void startSession() {
        AppUtils.runAsync(() -> {
            try {
                if (Objects.isNull(clientInstance)) {
                    clientInstance = new SshClientInstance(
                            sessionInfo);
                }
                //clientInstance.setUiCallback(connectionProgressPanel);
                clientInstance.connect();
                SwingUtilities.invokeLater(() -> {
                    showContent();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
//                SwingUtilities.invokeLater(() -> {
//                    connectionProgressPanel.showErrorRetryPanel();
//                });
            }
        });
    }

    private void showContent() {
        cardLayout.show(this, "prg");
        splitPane.disableUi();
        AppUtils.runAsync(() -> {
            try {
                var localPath = System.getProperty("user.home");
                var localFilePath = Paths.get(localPath);
                var localNamePath = localFilePath.getFileName();
                var localName = Objects.nonNull(localNamePath) ? localNamePath.toString() : "/";
                var localResult = listLocal(localFilePath);

                sftpClientInstance.connect();
                var remotePath = sftpClientInstance.getInitialPath();
                var remoteFilePath = Paths.get(remotePath);
                var remoteNamePath = remoteFilePath.getFileName();
                var remoteName = Objects.nonNull(remoteNamePath) ? remoteNamePath.toString() : "/";

                var remoteResult = sftpClientInstance.ls(remotePath);

                SwingUtilities.invokeLater(() -> {
                    var localFileBrowser = new FileBrowser(this);
                    leftTabs.addTab("", IconCode.RI_INSTANCE_LINE,
                            localFileBrowser);
                    localFileBrowser.render(localPath, localResult);
                    updateTitle(localName, localFileBrowser);

                    var remoteFileBrowser = new FileBrowser(this);
                    rightTabs.addTab("", IconCode.RI_INSTANCE_LINE,
                            remoteFileBrowser);
                    remoteFileBrowser.render(remotePath, remoteResult);
                    updateTitle(remoteName, remoteFileBrowser);
                });
            } catch (Exception ex) {
                //connectionProgressPanel.showErrorRetryPanel();
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    splitPane.enableUi();
                });
            }
        });
    }

//    private ConnectionProgressPanel createConnectionPanel(SessionInfo info) {
//        return new ConnectionProgressPanel(info, e -> {
//
//        });
//    }

    public void navigate(FileBrowser fileBrowser, String path) {
        System.out.println("123loadPasswords");
        splitPane.disableUi();
        AppUtils.runAsync(() -> {
            try {
                var filePath = Paths.get(path);
                var namePath = filePath.getFileName();
                var name = Objects.nonNull(namePath) ? namePath.toString() : "/";
                var result = listLocal(filePath);
                SwingUtilities.invokeLater(() -> {
                    fileBrowser.render(path, result);
                    updateTitle(name, fileBrowser);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    splitPane.enableUi();
                });
            }
        });
    }

    @Override
    public void preparePopup(JPopupMenu popupMenu, List<FileInfo> files, Component source, int x, int y) {
        System.out.println("Show popup");
    }

    private String getOwnerName(Path path) {
        FileOwnerAttributeView attributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        try {
            return attributeView.getOwner().getName();
        } catch (IOException e) {
            return null;
        }
    }

    private LocalDateTime getModificationTime(Path path) {
        try {
            return LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            return LocalDateTime.now();
        }
    }

//    private List<FileInfo> listRemote(String path) throws Exception {
//        sftpClientInstance.ls(path)
//    }

    private List<FileInfo> listLocal(Path path) {
        List<Path> list;
        try {
            list = Files.list(path).toList();
        } catch (Exception ex) {
            ex.printStackTrace();
            list = Collections.emptyList();
        }
        var result = new ArrayList<FileInfo>(list.size());
        list.stream().filter(f -> Files.isDirectory(f)).forEach(d -> {
            result.add(new FileInfo(
                    d.toAbsolutePath().toString(),
                    d.getFileName().toString(),
                    0,
                    getModificationTime(d),
                    FileType.Directory,
                    getOwnerName(d)
            ));
        });

        list.stream().filter(f -> !Files.isDirectory(f)).forEach(f -> {
            result.add(new FileInfo(
                    f.toAbsolutePath().toString(),
                    f.getFileName().toString(),
                    0,
                    getModificationTime(f),
                    FileType.File,
                    getOwnerName(f)
            ));
        });
        return result;
    }

}
