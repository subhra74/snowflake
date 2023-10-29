package muon.screens.sessionmgr;

import muon.AppContext;
import muon.constants.Orientation;
import muon.dto.session.NamedItem;
import muon.dto.session.SavedSessionTree;
import muon.dto.session.SessionFolder;
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
    private SessionTreePanel sessionTreePanel;
    private boolean cancelled = true;

    public SessionManagerPanel() {
        super(new BorderLayout());

        var splitPane = new SplitPanel(Orientation.Horizontal);

        sessionTreePanel = new SessionTreePanel(this::handleTreeSelection);

        sessionEditor = new SessionEditor(e -> {
            this.cancelled = false;
            try {
                AppContext.saveSession();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            var win = SwingUtilities.windowForComponent(this);
            win.dispose();
        }, e -> {
            this.cancelled = true;
            try {
                AppContext.saveSession();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            var win = SwingUtilities.windowForComponent(this);
            win.dispose();
        });

        splitPane.setLeftComponent(sessionTreePanel);

        splitPane.setRightComponent(sessionEditor);
        splitPane.setDividerLocation(250);

        add(splitPane);

        sessionTreePanel.loadTree();
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

        if (sessionTreePanel == null) return;

        var model = sessionTreePanel.getTreeModel();

        this.sessionEditor.setValue(nodeInfo, () -> {
            model.nodeChanged(node);
        });
        revalidate();
        repaint();
    }

    public void saveSessionUpdates() {
        try {
            System.out.println("Encrypting and writing...");
            var sessionTree = new SavedSessionTree();
            sessionTree.setFolder(sessionTreePanel.getRootFolder());
            sessionTree.setLastSelectionId(sessionTreePanel.getLastSelectedId());
            AppContext.setSessionTree(sessionTree);
            AppContext.saveSession();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
