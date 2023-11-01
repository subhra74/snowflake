package muon.screens.appwin.tabs.terminal;

import muon.App;
import muon.screens.sessionmgr.SessionManager;
import muon.styles.AppTheme;
import muon.util.*;
import muon.widgets.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        addTabComponent.setBorder(new EmptyBorder(2, 2, 2, 0));
        addTabComponent.putClientProperty("button.popup", addTabComponent);
        addTabComponent.addActionListener(e -> {
            openTab();
        });

        var moreComponent = AppUtils.createMoreButton();

        var b1 = Box.createHorizontalBox();
        b1.add(addTabComponent);
        b1.add(moreComponent);

        var tabbedPanel = new TabbedPanel(
                false,
                false,
                AppTheme.INSTANCE.getSelectionColor(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getDarkForeground(),
                AppTheme.INSTANCE.getDisabledForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                IconCode.RI_CLOSE_LINE,
                AppTheme.INSTANCE.getButtonBorderColor(),
                b1,
                false,
                true,
                false
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
                if (c instanceof TerminalContainer) {
                    ((TerminalContainer) c).dispose();
                }
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
        var window = SwingUtilities.windowForComponent(this);
        var sessionInfo = SessionManager.showDialog(window);
        if (sessionInfo != null) {
            var terminal = new TerminalContainer(sessionInfo);
            leftTabs.addTab(sessionInfo.getName(), IconCode.RI_TERMINAL_BOX_LINE,
                    terminal);
            ((CardLayout) this.getLayout()).show(this, "TABS");
            terminal.beginSession();
        }
    }
}
