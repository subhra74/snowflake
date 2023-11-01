package muon.screens.sessionmgr;

import muon.AppContext;
import muon.dto.session.NamedItem;
import muon.dto.session.SessionFolder;
import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.styles.FlatTreeRenderer;
import muon.widgets.AutoScrollingJTree;
import muon.util.IconCode;
import muon.util.IconFont;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Objects;
import java.util.UUID;

public class SessionTreePanel extends JPanel {
    private DefaultTreeModel treeModel;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private String lastSelectedId;

    public SessionTreePanel(TreeSelectionListener selectionListener) {
        super(new BorderLayout());
        setBackground(AppTheme.INSTANCE.getBackground());
        createUI(selectionListener);
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public void loadTree() {
        this.lastSelectedId = AppContext.sessionTree.getLastSelectionId();
        rootNode = getNode(AppContext.sessionTree.getFolder());
        rootNode.setAllowsChildren(true);
        treeModel.setRoot(rootNode);
        try {
            if (this.lastSelectedId != null) {
                selectNode(lastSelectedId, rootNode);
            } else {
                DefaultMutableTreeNode n = null;
                n = findFirstInfoNode(rootNode);
                if (n == null) {
                    SessionInfo sessionInfo = new SessionInfo();
                    sessionInfo.setName("New host");
                    sessionInfo.setId(UUID.randomUUID().toString());
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(sessionInfo);
                    childNode.setUserObject(sessionInfo);
                    childNode.setAllowsChildren(false);
                    treeModel.insertNodeInto(childNode, rootNode, rootNode.getChildCount());
                    n = childNode;
                    tree.scrollPathToVisible(new TreePath(n.getPath()));
                }

                TreePath path = new TreePath(n.getPath());
                tree.setSelectionPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        treeModel.nodeChanged(rootNode);
    }

    private DefaultMutableTreeNode findFirstInfoNode(DefaultMutableTreeNode node) {
        if (!node.getAllowsChildren()) {
            return node;
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = findFirstInfoNode((DefaultMutableTreeNode) node.getChildAt(i));
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }

    private boolean selectNode(String id, DefaultMutableTreeNode node) {
        if (id.equals((((NamedItem) node.getUserObject()).getId()))) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            if (selectNode(id, child)) {
                return true;
            }
        }
        return false;
    }

    private void createUI(TreeSelectionListener selectionListener) {
        var treeScroll1 = new JScrollPane(createSessionTree(selectionListener));
        treeScroll1.setBorder(new EmptyBorder(0, 0, 0, 0));

        var tabbedPanel = new TabbedPanel(
                true,
                false,
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkControlBackground(),
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDisabledForeground(),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                AppTheme.INSTANCE.getForeground(),
                AppTheme.INSTANCE.getTitleForeground(),
                null,
                AppTheme.INSTANCE.getButtonBorderColor(),
                null,
                true,
                true,
                true
        );

        var hostTreePanel = new JPanel(new BorderLayout());
        hostTreePanel.add(createTreeTools(), BorderLayout.NORTH);
        hostTreePanel.add(treeScroll1);

        tabbedPanel.addTab("Hosts", IconCode.RI_DATABASE_2_LINE,
                hostTreePanel);
        tabbedPanel.addTab("Recent", IconCode.RI_HISTORY_LINE,
                new JPanel());

        add(tabbedPanel);

    }

    private JTree createSessionTree(TreeSelectionListener selectionListener) {
        rootNode = new DefaultMutableTreeNode("Sessions", true);
        rootNode.add(new DefaultMutableTreeNode("New session", true));

        treeModel = new DefaultTreeModel(null, true);
        tree = new AutoScrollingJTree(treeModel);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    var row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    if (row >= 0) {
                        var bounds = tree.getRowBounds(row);
                        if (bounds.contains(e.getX(), e.getY())) {
                            if (e.getX() < bounds.x + 40) {
                                if (tree.isExpanded(row)) {
                                    tree.collapseRow(row);
                                } else {
                                    tree.expandRow(row);
                                }
                            }
                        }
                    }
                }
            }
        });
        var renderer = new FlatTreeRenderer();
        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(false);
        tree.setRowHeight(renderer.getPreferredHeight());
        tree.setRootVisible(true);
        tree.setDragEnabled(true);
        tree.setEditable(false);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.getSelectionModel().addTreeSelectionListener(selectionListener);
        tree.getSelectionModel().addTreeSelectionListener(e -> {
            if (Objects.isNull(e.getNewLeadSelectionPath())) {
                return;
            }
            var node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            var nodeInfo = (NamedItem) node.getUserObject();
            if (Objects.nonNull(nodeInfo)) {
                this.lastSelectedId = nodeInfo.getId();
                System.out.println("Tree selection: " + nodeInfo.getId() + " : " + nodeInfo.getName());
            }
        });
        return tree;
    }

    private Component createTreeTools() {
        var btnAddHost = createButton(IconCode.RI_FILE_ADD_LINE);
        var btnAddFolder = createButton(IconCode.RI_FOLDER_ADD_LINE);
        var btnDelete = createButton(IconCode.RI_DELETE_BIN_LINE);
        var btnClone = createButton(IconCode.RI_FILE_COPY_2_LINE);
        var btnImport = createButton(IconCode.RI_INSTALL_LINE);
        var btnExport = createButton(IconCode.RI_UNINSTALL_LINE);
        var txtSearch = new JTextField();

        btnDelete.setForeground(AppTheme.INSTANCE.getDisabledForeground());
        btnClone.setForeground(AppTheme.INSTANCE.getDisabledForeground());

        var hbox1 = Box.createHorizontalBox();
        hbox1.add(btnAddHost);
        hbox1.add(btnAddFolder);
        hbox1.add(btnDelete);
        hbox1.add(btnClone);
        hbox1.add(btnImport);
        hbox1.add(btnExport);
        hbox1.add(Box.createHorizontalGlue());

        var hbox2 = Box.createHorizontalBox();
        hbox2.add(Box.createRigidArea(new Dimension(2,2)));
        hbox2.add(txtSearch);
        hbox2.add(Box.createRigidArea(new Dimension(2,2)));

        var vbox = Box.createVerticalBox();
        vbox.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );
        vbox.add(hbox1);
        vbox.add(Box.createRigidArea(new Dimension(10, 5)));
        vbox.add(hbox2);
        return vbox;
    }

    private JButton createButton(IconCode iconCode) {
        var button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(AppTheme.INSTANCE.getDarkForeground());
        button.setBorder(new EmptyBorder(2, 5, 2, 5));
        button.setFont(IconFont.getSharedInstance().getIconFont(18));
        button.setText(iconCode.getValue());
        return button;
    }

    public SessionFolder getRootFolder() {
        return convertModelFromTree(rootNode);
    }

    private SessionFolder convertModelFromTree(DefaultMutableTreeNode node) {
        SessionFolder folder = (SessionFolder) node.getUserObject();
        SessionFolder copy = new SessionFolder();
        copy.setId(folder.getId());
        copy.setName(folder.getName());
        Enumeration<TreeNode> children = node.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) children.nextElement();
            if (c.getUserObject() instanceof SessionInfo) {
                copy.getItems().add((SessionInfo) c.getUserObject());
            } else {
                copy.getFolders().add(convertModelFromTree(c));
            }
        }
        return copy;
    }

    public synchronized static DefaultMutableTreeNode getNode(SessionFolder folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        for (SessionInfo info : folder.getItems()) {
            DefaultMutableTreeNode c = new DefaultMutableTreeNode(info);
            c.setAllowsChildren(false);
            node.add(c);
        }

        for (SessionFolder folderItem : folder.getFolders()) {
            node.add(getNode(folderItem));
        }
        return node;
    }

    public String getLastSelectedId() {
        return lastSelectedId;
    }

    public void setLastSelectedId(String lastSelectedId) {
        this.lastSelectedId = lastSelectedId;
    }
}
