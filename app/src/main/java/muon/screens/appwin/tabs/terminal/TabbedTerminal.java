package muon.screens.appwin.tabs.terminal;

import muon.App;
import muon.screens.appwin.tabs.filebrowser.AbstractFileBrowserView;
import muon.screens.sessionmgr.SessionManager;
import muon.styles.AppTheme;
import muon.util.*;
import muon.widgets.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.IntConsumer;

public class TabbedTerminal extends JPanel {
    private JTabbedPane leftTabs;

    public TabbedTerminal() {
        super(new CardLayout());
        leftTabs = createTab();
        this.add(new TerminalHomePage(
                e -> openTab()), "HOME");
        this.add(leftTabs, "TABS");
    }

    private JTabbedPane createTab() {
        var addTabComponent = new JButton(AppUtils.createSVGIcon("add-line.svg", 16, Color.GRAY));// AppUtils.createAddTabButton();
        //addTabComponent.setBorder(new EmptyBorder(2, 2, 2, 0));
        addTabComponent.putClientProperty("button.popup", addTabComponent);
        addTabComponent.addActionListener(e -> {
            openTab();
        });

        var moreComponent = new JButton(AppUtils.createSVGIcon("more_vert_black_24dp.svg", 16, Color.GRAY));//AppUtils.createMoreButton();

//        var b1 = Box.createHorizontalBox();
//        b1.add(addTabComponent);
//        b1.add(moreComponent);

        var tab = new JTabbedPane();
        System.out.println("aaaa: " + UIManager.getColor("TabbedPane.background"));
        //tab.putClientProperty("JTabbedPane.background", UIManager.getColor("TextArea.background"));
        //tab.putClientProperty("JTabbedPane.background", UIManager.getColor("TextArea.background"));
        //tab.putClientProperty("JTabbedPane.showContentSeparator", false);
        //tab.setBackground(UIManager.getColor("TextArea.background"));
        tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tab.putClientProperty("JTabbedPane.tabClosable", true);
        tab.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) tabIndex -> {
            // close tab here
            var c = tab.getComponentAt(tabIndex);
            tab.removeTabAt(tabIndex);
            if (c instanceof TerminalContainer) {
                ((TerminalContainer) c).dispose();
            }
            handleTabClosure(tab);
        });

        var toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(null);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(addTabComponent);
        toolbar.add(moreComponent);
        tab.putClientProperty("JTabbedPane.trailingComponent", toolbar);
        return tab;

//        var tabbedPanel = new TabbedPanel(
//                false,
//                false,
//                AppTheme.INSTANCE.getSelectionColor(),
//                AppTheme.INSTANCE.getDarkControlBackground(),
//                AppTheme.INSTANCE.getDarkForeground(),
//                AppTheme.INSTANCE.getDisabledForeground(),
//                AppTheme.INSTANCE.getBackground(),
//                AppTheme.INSTANCE.getDarkControlBackground(),
//                AppTheme.INSTANCE.getForeground(),
//                AppTheme.INSTANCE.getTitleForeground(),
//                IconCode.RI_CLOSE_LINE,
//                AppTheme.INSTANCE.getButtonBorderColor(),
//                b1,
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
//                if (c instanceof TerminalContainer) {
//                    ((TerminalContainer) c).dispose();
//                }
//                handleTabClosure(tabbedPanel);
//            }
//        });
//
//        return tabbedPanel;
    }

    private void handleTabClosure(JTabbedPane tabbedPanel) {
        if (tabbedPanel == leftTabs && tabbedPanel.getTabCount() == 0) {
            ((CardLayout) this.getLayout()).show(this, "HOME");
        }
    }

    private void openTab() {
        var window = SwingUtilities.windowForComponent(this);
        var sessionInfo = SessionManager.showDialog(window);
        if (sessionInfo != null) {
            var terminal = new TerminalContainer(sessionInfo);
            leftTabs.addTab(sessionInfo.getName(), AppUtils.createSVGIcon("terminal-box-fill.svg", 18, Color.GRAY),
                    //IconCode.RI_TERMINAL_BOX_LINE,
                    terminal);
            ((CardLayout) this.getLayout()).show(this, "TABS");
            terminal.beginSession();
        }
    }
}
