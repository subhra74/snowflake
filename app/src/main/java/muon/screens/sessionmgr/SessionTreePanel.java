package muon.screens.sessionmgr;

import muon.io.SessionStore;
import muon.dto.session.NamedItem;
import muon.dto.session.SavedSessionTree;
import muon.dto.session.SessionFolder;
import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.styles.FlatTreeRenderer;
import muon.widgets.AutoScrollingJTree;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.UUID;

public class SessionTreePanel extends JPanel {
    private DefaultTreeModel treeModel;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    private String lastSelected;

    public SessionTreePanel(TreeSelectionListener selectionListener) {
        super(new BorderLayout());
        setBackground(AppTheme.INSTANCE.getBackground());
        createUI(selectionListener);
        loadTree(SessionStore.load());
    }

    private void loadTree(SavedSessionTree stree) {
        this.lastSelected = stree.getLastSelection();
        rootNode = getNode(stree.getFolder());
        rootNode.setAllowsChildren(true);
        treeModel.setRoot(rootNode);
        try {
            if (this.lastSelected != null) {
                selectNode(lastSelected, rootNode);
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
                    TreePath path = new TreePath(n.getPath());
                    tree.setSelectionPath(path);
                }
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
        var treeScroll = new JScrollPane(createSessionTree(selectionListener));
        treeScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(treeScroll);
        add(createTreeTools(), BorderLayout.NORTH);
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
        return tree;
    }

    private Component createTreeTools() {
        var btnAdd = createButton(IconCode.RI_ADD_LINE);
        var btnMore = createButton(IconCode.RI_MORE_2_LINE);
        var txtSearch = new JTextField();

        var hbox1 = Box.createHorizontalBox();
        hbox1.setBorder(
                new EmptyBorder(10, 5, 10, 5)
        );
        hbox1.add(btnAdd);
        hbox1.add(Box.createRigidArea(new Dimension(5, 0)));
        hbox1.add(txtSearch);
        hbox1.add(Box.createRigidArea(new Dimension(5, 0)));
        hbox1.add(btnMore);
        return hbox1;
    }

    private JButton createButton(IconCode iconCode) {
        var button = new JButton();
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(2, 5, 2, 5));
        button.setFont(IconFont.getSharedInstance().getIconFont(18));
        button.setText(iconCode.getValue());
        return button;
    }

    public static synchronized SessionFolder convertModelFromTree(DefaultMutableTreeNode node) {
        SessionFolder folder = new SessionFolder();
        folder.setName(node.getUserObject() + "");
        Enumeration<TreeNode> childrens = node.children();
        while (childrens.hasMoreElements()) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) childrens.nextElement();
            if (c.getUserObject() instanceof SessionInfo) {
                folder.getItems().add((SessionInfo) c.getUserObject());
            } else {
                folder.getFolders().add(convertModelFromTree(c));
            }
        }
        return folder;
    }

    public synchronized static DefaultMutableTreeNode getNode(SessionFolder folder) {
        NamedItem item = new NamedItem();
        item.setName(folder.getName());
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
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
}
