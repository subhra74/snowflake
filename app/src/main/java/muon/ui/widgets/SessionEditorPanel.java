package muon.ui.widgets;

import muon.ui.styles.AppTheme;
import muon.ui.styles.FlatTreeRenderer;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SessionEditorPanel extends JPanel {
    private JSplitPane splitPane;

    public SessionEditorPanel() {
        super(new BorderLayout());
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setBackground(AppTheme.INSTANCE.getSplitPaneBackground());

        var treeScroll = new JScrollPane(createSessionTree());
        treeScroll.setBorder(new EmptyBorder(0, 0, 0, 0));

        var leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(treeScroll);
        leftPanel.add(createTreeTools(), BorderLayout.NORTH);
        leftPanel.setBackground(AppTheme.INSTANCE.getBackground());

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(createSessionEditorPanel());
        splitPane.setDividerLocation(250);

        add(splitPane);
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

    private Component createTreeTools() {
        var hbox1 = Box.createHorizontalBox();
        hbox1.setBorder(
                new EmptyBorder(10, 5, 10, 5)
        );
        hbox1.add(Box.createRigidArea(new Dimension(5, 0)));
        hbox1.add(createButton(IconCode.RI_ADD_LINE));
        hbox1.add(Box.createRigidArea(new Dimension(5, 0)));
        var txtSearch = new JTextField();
        hbox1.add(txtSearch);
        hbox1.add(Box.createRigidArea(new Dimension(5, 0)));
        hbox1.add(createButton(IconCode.RI_MORE_2_LINE));
        return hbox1;
    }

    private JTree createSessionTree() {
        var root = new DefaultMutableTreeNode("Sessions", true);
        root.add(new DefaultMutableTreeNode("New session", true));
        var treeModel = new DefaultTreeModel(root, true);
        var tree = new JTree(treeModel);
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
        return tree;
    }

    private JPanel createSessionEditorPanel() {
        var panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.INSTANCE.getBackground());

        var nameLbl = new JLabel("Name");
        var txtName = new JTextField();

        var gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 5);
        panel.add(nameLbl, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 5, 10, 10);
        gc.weightx = 1;
        gc.gridwidth = 3;
        panel.add(txtName, gc);

        gc = new GridBagConstraints();
        gc.gridy = 1;
        gc.gridwidth = 4;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.PAGE_START;

        var tabbedPanel = new TabbedPanel(
                false,
                false,
                new Color(52, 117, 233),
                new Color(24, 24, 24),
                new Color(52, 117, 233),
                new Color(100, 100, 100),
                new Color(31, 31, 31),
                new Color(130, 130, 130),
                new Color(180, 180, 180),
                null,
                Color.BLACK
        );

        tabbedPanel.addTab("Basic", null, createBasicPanel());
        tabbedPanel.addTab("Proxy", null, createBasicPanel());
        tabbedPanel.addTab("Jump hosts", null, createBasicPanel());
        tabbedPanel.addTab("Port forwarding", null, createBasicPanel());
        tabbedPanel.setSelectedIndex(0);

        panel.add(tabbedPanel, gc);

        gc = new GridBagConstraints();
        gc.gridy = 2;
        gc.gridx = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        panel.add(new JLabel(), gc);

        gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 0);
        gc.gridy = 2;
        gc.gridx = 2;
        gc.gridwidth = 1;
        gc.weightx = 0;
        panel.add(new JButton("Connect"), gc);

        gc = new GridBagConstraints();
        gc.gridy = 2;
        gc.gridx = 3;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(10, 10, 10, 10);

        panel.add(new JButton("Cancel"), gc);

        return panel;
    }

    private JPanel createBasicPanel() {
        var panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(AppTheme.INSTANCE.getBackground());

        var lblHost = new JLabel("Host name", JLabel.RIGHT);
        var lblPort = new JLabel("Port", JLabel.RIGHT);
        var lblUserName = new JLabel("User name", JLabel.RIGHT);
        var lblPassword = new JLabel("Password", JLabel.RIGHT);
        var lblKeyFile = new JLabel("Key file", JLabel.RIGHT);
        var lblRemoteFolder = new JLabel("Remote folder", JLabel.RIGHT);
        var lblLocalFolder = new JLabel("Local folder", JLabel.RIGHT);
        var lblCombinedMode = new JLabel("Show files and terminal in same tab", JLabel.LEFT);
        var lblStartPage = new JLabel("Start page", JLabel.RIGHT);

        var txtHost = new JTextField();
        var txtPort = new JTextField();
        var txtUser = new JTextField();
        var txtPass = new JPasswordField();
        var txtKeyFile = new JTextField();
        var txtRemoteFolder = new JTextField();
        var txtLocalFolder = new JTextField();
        var swCombinedMode = new SwitchButton();
        var cmbStartPage = new JComboBox<String>(new String[]{"SFTP+Terminal", "SFTP", "Terminal", "Port forwarding"});

        var btnBrowseKey = new JButton("...");
        var btnBrowseFolder = new JButton("...");

        var c = 0;
        var insets = new Insets(5, 5, 5, 5);

        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblHost, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtHost, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblPort, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtPort, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblUserName, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtUser, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblPassword, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtPass, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblKeyFile, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtKeyFile, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        panel.add(btnBrowseKey, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblRemoteFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtRemoteFolder, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblLocalFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(txtLocalFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        panel.add(btnBrowseFolder, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblCombinedMode, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        panel.add(swCombinedMode, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        panel.add(lblStartPage, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        panel.add(cmbStartPage, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = c;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.PAGE_START;
        gc.weightx = 1;
        gc.weighty = 1;
        panel.add(new JLabel(), gc);

        return panel;
    }
}
