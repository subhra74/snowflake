package muon.screens.appwin.tabs.terminal;

import muon.screens.appwin.tabs.filebrowser.AbstractFileBrowserView;
import muon.screens.appwin.tabs.filebrowser.FileBrowserHomePage;
import muon.screens.appwin.tabs.filebrowser.local.LocalFileBrowserView;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.widgets.TabEvent;
import muon.widgets.TabListener;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import java.awt.*;

public class TabbedTerminal extends JPanel {
    private TabbedPanel leftTabs;

    public TabbedTerminal() {
        super(new CardLayout());
        leftTabs = createTab();
        this.add(new TerminalHomePage(
                e -> openTab()), "HOME");
        this.add(leftTabs, "TABS");
    }

    private TabbedPanel createTab() {
        var addTabComponent = AppUtils.createAddTabButton();
        addTabComponent.putClientProperty("button.popup", addTabComponent);
        addTabComponent.addActionListener(e -> {
        });

        var tabbedPanel = new TabbedPanel(
                false,
                false,
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkControlBackground(),
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDisabledForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                IconCode.RI_CLOSE_LINE,
                AppTheme.INSTANCE.getButtonBorderColor(),
                addTabComponent,
                false,
                true
        );

        tabbedPanel.addTabListener(new TabListener() {
            @Override
            public void selectionChanged(TabEvent e) {
            }

            @Override
            public boolean tabClosing(TabEvent e) {
                return true;
            }

            @Override
            public void tabClosed(TabEvent e) {
                var c = e.getTabContent();
//                if (c instanceof AbstractFileBrowserView) {
//                    ((AbstractFileBrowserView) c).dispose();
//                }
                handleTabClosure(tabbedPanel);
            }
        });

        return tabbedPanel;
    }

    private void handleTabClosure(TabbedPanel tabbedPanel) {
        if (tabbedPanel == leftTabs && tabbedPanel.getTabCount() == 0) {
            ((CardLayout) this.getLayout()).show(this, "HOME");
        }
    }

    private void openTab() {
        var terminal = new CustomTerminal();
        leftTabs.addTab("Local", IconCode.RI_HARD_DRIVE_3_LINE,
                terminal);
        ((CardLayout) this.getLayout()).show(this, "TABS");
        terminal.start();
    }
}
