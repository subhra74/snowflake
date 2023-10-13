package muon.screens.sessionmgr;

import muon.constants.Orientation;
import muon.dto.session.NamedItem;
import muon.dto.session.SessionInfo;
import muon.widgets.SplitPanel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Objects;

public class SessionManagerPanel extends JPanel {

    int diffX, diffY;
    private SessionEditor sessionEditor;
    private boolean cancelled = true;

    public SessionManagerPanel() {
        super(new BorderLayout());

        var splitPane = new SplitPanel(Orientation.Horizontal);

        sessionEditor = new SessionEditor(e -> {
            this.cancelled = false;
            var win = SwingUtilities.windowForComponent(this);
            win.dispose();
        }, e -> {
            this.cancelled = true;
            var win = SwingUtilities.windowForComponent(this);
            win.dispose();
        });

        splitPane.setLeftComponent(new SessionTreePanel(e -> {
            handleTreeSelection(e);
        }));

        splitPane.setRightComponent(sessionEditor);
        splitPane.setDividerLocation(250);

        add(splitPane);
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public SessionInfo getSelectedSession() {
        if (!cancelled && this.sessionEditor.getSelection() instanceof SessionInfo info) {
            return info;
        }
        return null;
    }

    private void handleTreeSelection(TreeSelectionEvent e) {
        if (Objects.isNull(e.getNewLeadSelectionPath())) {
            return;
        }
        var node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
        var nodeInfo = (NamedItem) node.getUserObject();
        this.sessionEditor.setValue(nodeInfo);
        revalidate();
        repaint();
    }

}
