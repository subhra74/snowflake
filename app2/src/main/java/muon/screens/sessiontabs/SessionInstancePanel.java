package muon.screens.sessiontabs;

import muon.constants.Orientation;
import muon.model.SessionInfo;
import muon.screens.sessiontabs.filebrowser.DualPaneFileBrowserContainer;
import muon.screens.sessiontabs.terminal.TabbedTerminalContainer;
import muon.widgets.SplitPanel;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionInstancePanel extends JPanel {
    private SessionInfo sessionInfo;
    private boolean connected = false;
    private SshClientInstance initialInstance;
    private AtomicBoolean started = new AtomicBoolean(false);
    private SplitPanel splitPanel;

    public SessionInstancePanel(SessionInfo sessionInfo, SshClientInstance clientInstance) {
        super(new BorderLayout());
        this.sessionInfo = sessionInfo;
        this.initialInstance = clientInstance;
    }

    public void init() {
        showContent();
    }

    private void showContent() {
        splitPanel = new SplitPanel(Orientation.Vertical);
        splitPanel.setTopComponent(new DualPaneFileBrowserContainer(this.initialInstance, this.sessionInfo));
        splitPanel.setBottomComponent(new TabbedTerminalContainer(this.initialInstance));
        this.removeAll();
        this.add(splitPanel);
        revalidate();
        repaint();
    }
}
