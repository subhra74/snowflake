package muon.screens.appwin.tabs.filebrowser;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import muon.constants.Orientation;
import muon.dto.file.FileInfo;
import muon.screens.appwin.AppWin;
import muon.screens.appwin.tabs.filebrowser.local.LocalFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.sftp.SftpFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.transfer.foreground.ForegroundTransferProgressPanel;
import muon.screens.appwin.tabs.filebrowser.transfer.foreground.TransferDialog;
import muon.screens.sessionmgr.SessionManager;
import muon.service.SftpSession;
import muon.service.SftpUploadTask;
import muon.styles.AppTheme;
import muon.styles.FontIcon;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.widgets.SplitPanel;
import muon.widgets.TabEvent;
import muon.widgets.TabListener;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.function.IntConsumer;

public class DualPaneFileBrowser extends JPanel implements FileBrowserViewParent {
    private SplitPanel splitPane;
    private JTabbedPane leftTabs;
    private JTabbedPane rightTabs;
    private JPanel leftTabHolder, rightTabHolder;
    private AppWin appWin;
    private ForegroundTransferProgressPanel transferPanel;

    public DualPaneFileBrowser(AppWin appWin) {
        super(new CardLayout());
        this.appWin = appWin;
        transferPanel = new ForegroundTransferProgressPanel(b -> {
            ((CardLayout) this.getLayout()).show(this, "FilePanel");
        });
        splitPane = new SplitPanel(Orientation.Horizontal);

        leftTabHolder = new JPanel(new CardLayout());
        rightTabHolder = new JPanel(new CardLayout());

        leftTabs = createTab(
                createPopupMenu(
                        e -> createLocalTab(leftTabs),
                        e -> createRemoteTab(leftTabs)
                ));

        rightTabs = createTab(
                createPopupMenu(
                        e -> createLocalTab(rightTabs),
                        e -> createRemoteTab(rightTabs)
                ));

        leftTabHolder.add(new FileBrowserHomePage(
                e -> createLocalTab(leftTabs),
                e -> createRemoteTab(leftTabs)), "HOME");
        leftTabHolder.add(leftTabs, "TABS");

        rightTabHolder.add(new FileBrowserHomePage(
                e -> createLocalTab(rightTabs),
                e -> createRemoteTab(rightTabs)), "HOME");
        rightTabHolder.add(rightTabs, "TABS");

        splitPane.setLeftComponent(leftTabHolder);
        splitPane.setRightComponent(rightTabHolder);
        add(splitPane, "FilePanel");
        add(transferPanel, "TransferPanel");

        transferPanel.showConfirm();
    }

    private JPopupMenu createPopupMenu(ActionListener actLocal, ActionListener actRemote) {
        var popupMenu = new JPopupMenu();
        var remoteMenu = new JMenuItem("Remote Files");
        var localMenu = new JMenuItem("Local Files");

        popupMenu.add(remoteMenu);
        popupMenu.add(localMenu);

        localMenu.addActionListener(actLocal);
        remoteMenu.addActionListener(actRemote);
        return popupMenu;
    }

    private void createLocalTab(JTabbedPane tabbedPanel) {
        var localFileBrowser = new LocalFileBrowserView(this);
        FlatSVGIcon icon = null;
        try {
            icon = new FlatSVGIcon(getClass().getResourceAsStream("/icons/folder-fill.svg"));
            //icon.derive(24,24)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        icon.setColorFilter(FlatSVGIcon.ColorFilter.getInstance()
//                .add( Color.black, null, Color.white )
//                .add( Color.white, null, Color.black ));
        tabbedPanel.addTab("Local", //new FontIcon(IconCode.RI_HARD_DRIVE_3_LINE,16,16,16,AppTheme.INSTANCE.getSelectionColor())
                AppUtils.createSVGIcon("laptop_black_24dp.svg", 18, Color.GRAY),
                //icon.derive(16,16),
                localFileBrowser);
//        tabbedPanel.addTab("Local", IconCode.RI_HARD_DRIVE_3_LINE,
//                localFileBrowser);
        showTab(tabbedPanel);
        localFileBrowser.init();
    }

    private void createRemoteTab(JTabbedPane tabbedPanel) {
        var window = SwingUtilities.windowForComponent(this);
        var sessionInfo = SessionManager.showDialog(window);
        if (sessionInfo != null) {
            var sftpFileBrowser = new SftpFileBrowserView(sessionInfo, this);
            tabbedPanel.addTab(sessionInfo.getName(),
                    AppUtils.createSVGIcon("language_black_24dp.svg", 18, Color.GRAY),
                    sftpFileBrowser);
//            tabbedPanel.addTab(sessionInfo.getName(), IconCode.RI_FLASH_LINE,
//                    sftpFileBrowser);
            showTab(tabbedPanel);
            sftpFileBrowser.init();
        }
    }

    public void init() {
        createLocalTab(leftTabs);
    }

    private void showTab(JTabbedPane tabbedPanel) {
        if (this.leftTabs == tabbedPanel) {
            ((CardLayout) leftTabHolder.getLayout()).show(leftTabHolder, "TABS");
        }
        if (this.rightTabs == tabbedPanel) {
            ((CardLayout) rightTabHolder.getLayout()).show(rightTabHolder, "TABS");
        }
    }

    private JTabbedPane createTab(JPopupMenu popupMenu) {
        var tab = new JTabbedPane();
        tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tab.putClientProperty("JTabbedPane.tabClosable", true);
        //tab.putClientProperty("JTabbedPane.showContentSeparator", false);
        tab.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) tabIndex -> {
            // close tab here
            var c = tab.getComponentAt(tabIndex);
            tab.removeTabAt(tabIndex);
            if (c instanceof AbstractFileBrowserView) {
                ((AbstractFileBrowserView) c).dispose();
            }
            handleTabClosure(tab);
        });

        FlatSVGIcon.ColorFilter filter = new FlatSVGIcon.ColorFilter();
        filter.add(Color.BLACK, Color.GRAY);
        FlatSVGIcon icon = null;
        try {
            icon = new FlatSVGIcon(getClass().getResourceAsStream("/icons/add-line.svg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        icon.setColorFilter(filter);
        var addTabComponent = new JButton(icon.derive(16, 16));

        addTabComponent.putClientProperty("button.popup", addTabComponent);
        //addTabComponent.putClientProperty("JButton.buttonType", "toolBarButton");
        addTabComponent.addActionListener(e -> {
            popupMenu.pack();
            popupMenu.setInvoker(addTabComponent);
            popupMenu.show(addTabComponent, addTabComponent.getWidth() - popupMenu.getPreferredSize().width, addTabComponent.getHeight());
        });

        var toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(null);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(addTabComponent);
        tab.putClientProperty("JTabbedPane.trailingComponent", toolbar);

        return tab;
//
//        var addTabComponent = AppUtils.createAddTabButton();
//        addTabComponent.putClientProperty("button.popup", addTabComponent);
//        addTabComponent.addActionListener(e -> {
//            popupMenu.pack();
//            popupMenu.setInvoker(addTabComponent);
//            popupMenu.show(addTabComponent, addTabComponent.getWidth() - popupMenu.getPreferredSize().width, addTabComponent.getHeight());
//        });
//
//        var tabbedPanel = new TabbedPanel(
//                false,
//                false,
//                new Color(52, 117, 233),
//                AppTheme.INSTANCE.getDarkControlBackground(),
//                new Color(52, 117, 233),
//                AppTheme.INSTANCE.getDisabledForeground(),
//                AppTheme.INSTANCE.getBackground(),
//                AppTheme.INSTANCE.getDarkControlBackground(),
//                AppTheme.INSTANCE.getForeground(),
//                AppTheme.INSTANCE.getTitleForeground(),
//                IconCode.RI_CLOSE_LINE,
//                AppTheme.INSTANCE.getButtonBorderColor(),
//                addTabComponent,
//                false,
//                true,
//                false
//        );
//
//        tabbedPanel.addTabListener(new TabListener() {
//            @Override
//            public void selectionChanged(TabEvent e) {
//            }
//
//            @Override
//            public boolean tabClosing(TabEvent e) {
//                return true;
//            }
//
//            @Override
//            public void tabClosed(TabEvent e) {
//                var c = e.getTabContent();
//                if (c instanceof AbstractFileBrowserView) {
//                    ((AbstractFileBrowserView) c).dispose();
//                }
//                handleTabClosure(tabbedPanel);
//            }
//        });
//
//        return tabbedPanel;
    }

    private void handleTabClosure(JTabbedPane tabbedPanel) {
        if (tabbedPanel == leftTabs && tabbedPanel.getTabCount() == 0) {
            ((CardLayout) leftTabHolder.getLayout()).show(leftTabHolder, "HOME");
        }
        if (tabbedPanel == rightTabs && tabbedPanel.getTabCount() == 0) {
            ((CardLayout) rightTabHolder.getLayout()).show(rightTabHolder, "HOME");
        }
    }

    public void beginSftpUpload(
            String localFolder,
            List<FileInfo> localFiles,
            String remoteFolder,
            List<FileInfo> remoteFiles,
            SftpSession sftp) {
        var transferDialog = new TransferDialog(
                new SftpUploadTask(sftp, localFolder, localFiles, remoteFolder, remoteFiles), e -> {
        }, SwingUtilities.windowForComponent(this));
        transferDialog.setVisible(true);
    }
}
