package muon.screens.sessiontabs;

import muon.screens.sessionmgr.SessionManager;
import muon.styles.AppTheme;
import muon.util.*;
import muon.widgets.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainContainer extends JPanel {
    private TabbedPanel mainTab;
    private HomePanel homePanel;
    private List<SessionInstancePanel> sessionInstancePanels = new ArrayList<>();

    public MainContainer() {
        super(new BorderLayout());
        mainTab = createMainTab();
        homePanel = createHomePanel();
        add(homePanel);
    }

    private TabbedPanel createMainTab() {
        var addTabComponent = AppUtils.createAddTabButton(
//                e -> {
//            createNewSession();
//        }
        );
        mainTab = new TabbedPanel(
                false,
                false,
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkControlBackground(),
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                IconCode.RI_CLOSE_LINE,
                AppTheme.INSTANCE.getButtonBorderColor(),
                addTabComponent,
                false,
                true,
                false
        );
        return mainTab;
    }

    private HomePanel createHomePanel() {
        homePanel = new HomePanel(null, e -> {
            createNewSession();
        });
        return homePanel;
    }

    private void createNewSession() {
        var window = SwingUtilities.windowForComponent(this);
        var sessionInfo = SessionManager.showDialog(window);
        if (sessionInfo != null) {
            AppUtils.runAsync(() -> {
                var initialInstance = new SshClientInstance(
                        sessionInfo);
                if (initialInstance.connect()) {
                    if (this.mainTab.getTabCount() == 0) {
                        this.removeAll();
                        this.add(this.mainTab);
                        this.revalidate();
                        this.repaint();
                    }
                    System.out.println("Session");
                    var sip = new SessionInstancePanel(sessionInfo, initialInstance);
                    this.sessionInstancePanels.add(sip);
                    mainTab.addTab("user@hostname", IconCode.RI_INSTANCE_LINE, sip);
                    sip.init();
                }
            });
        }
    }
}
