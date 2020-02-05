package snowflake.components.newsession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.event.*;
import java.io.File;

import snowflake.App;
import snowflake.utils.*;

import java.awt.*;
import java.util.UUID;

public class NewSessionDlg extends JDialog implements ActionListener, TreeSelectionListener, TreeModelListener {

	private static final long serialVersionUID = -1182844921331289546L;

	private DefaultTreeModel treeModel;
	private JTree tree;
	private DefaultMutableTreeNode rootNode;
	private JScrollPane jsp;
	private SessionInfoPanel sessionInfoPanel;
	private JButton btnNewHost, btnDel, btnDup, btnNewFolder, btnExport, btnImport;
	private JButton btnConnect, btnCancel;
	private JTextField txtName;
	private JPanel namePanel;
	private NamedItem selectedInfo;
	// private DefaultMutableTreeNode lastConnected;
	private String lastSelected;
	private JPanel prgPanel;
	private JPanel pdet;
	private SessionInfo info;
	private JLabel lblName;

	public NewSessionDlg(Window wnd) {
		super(wnd);
		createUI();
	}

	private void createUI() {
		setBackground(new Color(245, 245, 245));
		// setIconImage(App.getAppIcon());
		setLayout(new BorderLayout());

		setSize(800, 600);
		setModal(true);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Saving before exit");
				save();
				dispose();
			}
		});

		setTitle("Session manager");

//		List<SessionFolder> folders = SessionStore.getSharedInstance()
//				.getFolders();
//		List<SessionInfo> sessions = SessionStore.getSharedInstance()
//				.getSessions();
//		SessionFolder rootFolder = getRoot(folders);
//		SavedSessionTree stree = SessionStore.load();
//		this.lastSelected = stree.getLastSelection();
//		rootNode = SessionStore.getNode(stree.getFolder());// new
//		// DefaultMutableTreeNode(rootFolder);
//		rootNode.setAllowsChildren(true);
		// createTree(rootNode, folders, sessions);

		treeModel = new DefaultTreeModel(null, true);
		treeModel.addTreeModelListener(this);
		tree = new AutoScrollingJTree(treeModel);
		tree.setDragEnabled(true);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.setTransferHandler(new TreeTransferHandler());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.getSelectionModel().addTreeSelectionListener(this);
		// tree.setDragEnabled(true);

		tree.setEditable(false);
		jsp = new JScrollPane(tree);

		// btnNewHost = new JButton(TextHolder.getString("session.newHost"));
		btnNewHost = new JButton("New site");
		btnNewHost.addActionListener(this);
		btnNewHost.putClientProperty("button.name", "btnNewHost");
		// btnNewHost.setFont(Utility.getFont(Constants.SMALL));
		btnNewFolder = new JButton("New folder");// new
													// JButton(TextHolder.getString("session.newFolder"));
		btnNewFolder.addActionListener(this);
		btnNewFolder.putClientProperty("button.name", "btnNewFolder");
		// btnNewFolder.setFont(Utility.getFont(Constants.SMALL));
		btnDel = new JButton("Remove");// new
										// JButton(TextHolder.getString("session.remove"));
		btnDel.addActionListener(this);
		btnDel.putClientProperty("button.name", "btnDel");
		// btnDel.setFont(Utility.getFont(Constants.SMALL));
		btnDup = new JButton("Duplicate");// new
											// JButton(TextHolder.getString("session.duplicate"));
		btnDup.addActionListener(this);
		btnDup.putClientProperty("button.name", "btnDup");
		// btnDup.setFont(Utility.getFont(Constants.SMALL));

		btnConnect = new JButton("Connect");// new
											// JButton(TextHolder.getString("session.connect"));
		btnConnect.addActionListener(this);
		btnConnect.putClientProperty("button.name", "btnConnect");
		// btnConnect.setFont(Utility.getFont(Constants.SMALL));

		btnCancel = new JButton("Cancel");// new
											// JButton(TextHolder.getString("session.cancel"));
		btnCancel.addActionListener(this);
		btnCancel.putClientProperty("button.name", "btnCancel");

		btnExport = new JButton("Export");// new
		// JButton(TextHolder.getString("session.cancel"));
		btnExport.addActionListener(this);
		btnExport.putClientProperty("button.name", "btnExport");

		btnImport = new JButton("Import");// new
		// JButton(TextHolder.getString("session.cancel"));
		btnImport.addActionListener(this);
		btnImport.putClientProperty("button.name", "btnImport");
		// btnCancel.setFont(Utility.getFont(Constants.SMALL));

		normalizeButtonSize();

		Box box1 = Box.createHorizontalBox();
		box1.setBorder(new EmptyBorder(10, 10, 10, 10));
		// box1.add(new JLabel("Warning: Passwords will be stored as plain
		// text"));
		box1.add(Box.createHorizontalGlue());
		box1.add(Box.createHorizontalStrut(10));
		box1.add(btnConnect);
		box1.add(Box.createHorizontalStrut(10));
		box1.add(btnCancel);

		GridLayout gl = new GridLayout(3, 2, 5, 5);
		JPanel btnPane = new JPanel(gl);
		btnPane.setBorder(new EmptyBorder(10, 0, 0, 0));
		btnPane.add(btnNewHost);
		btnPane.add(btnNewFolder);
		btnPane.add(btnDup);
		btnPane.add(btnDel);
		btnPane.add(btnExport);
		btnPane.add(btnImport);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.putClientProperty("Nimbus.Overrides", App.splitPaneSkin);

		JPanel treePane = new JPanel(new BorderLayout());
		treePane.setBorder(new EmptyBorder(10, 10, 10, 0));
		treePane.add(jsp);
		treePane.add(btnPane, BorderLayout.SOUTH);

		add(treePane, BorderLayout.WEST);

		sessionInfoPanel = new SessionInfoPanel();

		namePanel = new JPanel();

		JPanel pp = new JPanel(new BorderLayout());
		pp.add(namePanel, BorderLayout.NORTH);
		pp.add(sessionInfoPanel);

		pdet = new JPanel(new BorderLayout());
		// pdet.setVisible(false);

		JScrollPane scrollPane = new JScrollPane(pp);
		// scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		pdet.add(scrollPane);
		pdet.add(box1, BorderLayout.SOUTH);

		// add(pdet);

		BoxLayout boxLayout = new BoxLayout(namePanel, BoxLayout.PAGE_AXIS);
		namePanel.setLayout(boxLayout);

		namePanel.setBorder(new EmptyBorder(10, 10, 0, 10));

		lblName = new JLabel(TextHolder.getString("session.name"));
		lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblName.setHorizontalAlignment(JLabel.LEADING);
		lblName.setBorder(new EmptyBorder(0, 0, 5, 0));
		// lblName.setFont(Utility.getFont(Constants.SMALL));

		txtName = GraphicsUtils.createTextField(10);// new JTextField(30);
		txtName.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtName.setHorizontalAlignment(JLabel.LEADING);
		// txtName.setFont(Utility.getFont(Constants.SMALL));
		txtName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateName();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateName();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateName();
			}

			private void updateName() {
				selectedInfo.setName(txtName.getText());
				TreePath parentPath = tree.getSelectionPath();
				DefaultMutableTreeNode parentNode = null;

				if (parentPath != null) {
					parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
					if (parentNode != null) {
						treeModel.nodeChanged(parentNode);
					}
				}
			}
		});

		namePanel.add(lblName);
		namePanel.add(txtName);

		prgPanel = new JPanel();

		JLabel lbl = new JLabel("Connecting...");
		prgPanel.add(lbl);

		splitPane.setLeftComponent(treePane);
		splitPane.setRightComponent(pdet);

		add(splitPane);

		lblName.setVisible(false);
		txtName.setVisible(false);
		sessionInfoPanel.setVisible(false);
		btnConnect.setVisible(false);

		loadTree(SessionStore.load());
	}

	private void loadTree(SavedSessionTree stree) {
		this.lastSelected = stree.getLastSelection();
		rootNode = SessionStore.getNode(stree.getFolder());
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
					sessionInfo.setName("New site");
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

	private boolean selectNode(String id, DefaultMutableTreeNode node) {
		if (id.equals((((NamedItem) node.getUserObject()).getId()))) {
			tree.setSelectionPath(new TreePath(node.getPath()));
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

//	private SessionFolder getRoot(List<SessionFolder> folders) {
//		for (SessionFolder f : folders) {
//			if (f.getParentId() == null) {
//				System.out.println("Root node found: " + f.getId());
//				return f;
//			}
//		}
//		SessionFolder folder = new SessionFolder(UUID.randomUUID().toString(),
//				null, TextHolder.getString("sessionTree.defaultText"));
//		return folder;
//	}

//	private void createTree(DefaultMutableTreeNode parentNode,
//			List<SessionFolder> folders, List<SessionInfo> sessions) {
//		Object obj = parentNode.getUserObject();
//		if (obj instanceof SessionInfo) {
//			return;
//		}
//		SessionFolder folder = (SessionFolder) obj;
//		for (SessionFolder f : folders) {
//			if (f.getParentId() == null)
//				continue;// Root node has been extracted already
//			if (f.getParentId().equals(folder.getId())) {
//				if (f.getParentId().equals(folder.getId())) {
//					DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
//					parentNode.add(node);
//					createTree(node, folders, sessions);
//				}
//			}
//		}
//
//		String lastConnected = SessionStore.getSharedInstance()
//				.getLastConnected();
//
//		for (SessionInfo info : sessions) {
//			if (info.getParentId().equals(folder.getId())) {
//				DefaultMutableTreeNode node = new DefaultMutableTreeNode(info);
//				node.setAllowsChildren(false);
//				parentNode.add(node);
//				if (info.getId().equals(lastConnected)) {
//					this.lastConnected = node;
//				}
//			}
//		}
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		TreePath parentPath = tree.getSelectionPath();
		DefaultMutableTreeNode parentNode = null;

		if (parentPath != null) {
			parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
		}

		switch ((String) btn.getClientProperty("button.name")) {
		case "btnNewHost":
			if (parentNode == null) {
				parentNode = rootNode;
			}
			Object obj = parentNode.getUserObject();
			if (obj instanceof SessionInfo) {
				parentNode = (DefaultMutableTreeNode) parentNode.getParent();
				obj = parentNode.getUserObject();
			}
			SessionInfo sessionInfo = new SessionInfo();
			sessionInfo.setName("New site");
			sessionInfo.setId(UUID.randomUUID().toString());
			// sessionInfo.setParentId(((SessionFolder) obj).getId());
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(sessionInfo);
			childNode.setUserObject(sessionInfo);
			childNode.setAllowsChildren(false);
			treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
			tree.scrollPathToVisible(new TreePath(childNode.getPath()));
			TreePath path = new TreePath(childNode.getPath());
			tree.setSelectionPath(path);
			// tree.startEditingAtPath(path);
			break;
		case "btnNewFolder":
			if (parentNode == null) {
				parentNode = rootNode;
			}
			Object objFolder = parentNode.getUserObject();
			if (objFolder instanceof SessionInfo) {
				parentNode = (DefaultMutableTreeNode) parentNode.getParent();
				objFolder = parentNode.getUserObject();
			}
			SessionFolder folder = new SessionFolder();
			folder.setName(TextHolder.getString("sessionTree.defaultFolderText"));
			DefaultMutableTreeNode childNode1 = new DefaultMutableTreeNode(folder);
			treeModel.insertNodeInto(childNode1, parentNode, parentNode.getChildCount());
			tree.scrollPathToVisible(new TreePath(childNode1.getPath()));
			TreePath path2 = new TreePath(childNode1.getPath());
			tree.setSelectionPath(path2);
			// tree.startEditingAtPath(path2);
			break;
		case "btnDel":
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node != null && node.getParent() != null) {
				DefaultMutableTreeNode sibling = node.getNextSibling();
				if (sibling != null) {
					String id = ((NamedItem) sibling.getUserObject()).getId();
					selectNode(id, sibling);
				} else {
					DefaultMutableTreeNode parentNode1 = (DefaultMutableTreeNode) node.getParent();
					tree.setSelectionPath(new TreePath(parentNode1.getPath()));
				}
				treeModel.removeNodeFromParent(node);
			}
			break;
		case "btnDup":
			DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node1 != null && node1.getParent() != null && (node1.getUserObject() instanceof SessionInfo)) {
				SessionInfo info = ((SessionInfo) node1.getUserObject()).copy();
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(info);
				child.setAllowsChildren(false);
				treeModel.insertNodeInto(child, (MutableTreeNode) node1.getParent(), node1.getParent().getChildCount());
				selectNode(info.getId(), child);
			}
			break;
		case "btnConnect":
			connectClicked();
			break;
		case "btnCancel":
			save();
			dispose();
			break;
		case "btnImport":
			if (parentNode == null) {
				parentNode = rootNode;
			}
			if (parentNode.getUserObject() instanceof SessionInfo) {
				parentNode = (DefaultMutableTreeNode) parentNode.getParent();
			}
			JComboBox<String> cmbImports = new JComboBox<>(
					new String[] { "Putty", "WinSCP", "Snowflake session store" });

			if (JOptionPane.showOptionDialog(this, new Object[] { "Import from", cmbImports }, "Import sessions",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.OK_OPTION) {
				if (cmbImports.getSelectedIndex() < 2) {
					new ImportDlg(this, cmbImports.getSelectedIndex(), parentNode).setVisible(true);
					treeModel.nodeStructureChanged(parentNode);
				} else {
					JFileChooser jfc = new JFileChooser();
					if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
						File f = jfc.getSelectedFile();
						SavedSessionTree stree = SessionStore.load(f);
						loadTree(stree);
//						this.lastSelected = stree.getLastSelection();
//						rootNode = SessionStore.getNode(stree.getFolder());// new
//						// DefaultMutableTreeNode(rootFolder);
//						rootNode.setAllowsChildren(true);
//						treeModel.setRoot(rootNode);++
//						treeModel.nodeStructureChanged(rootNode);
					}
				}
			}

			break;
		case "btnExport":
			JFileChooser jfc = new JFileChooser();
			if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				String id = null;
				TreePath path3 = tree.getSelectionPath();
				if (path3 != null) {
					DefaultMutableTreeNode node3 = (DefaultMutableTreeNode) path3.getLastPathComponent();
					NamedItem item = (NamedItem) node3.getUserObject();
					id = item.getId();
				}
				SessionStore.save(SessionStore.convertModelFromTree(rootNode), id, jfc.getSelectedFile());
			}
			break;
		default:
			break;
		}
	}

	private void connectClicked() {
		save();
		this.info = (SessionInfo) selectedInfo;
		if (this.info.getHost() == null || this.info.getHost().length() < 1) {
			JOptionPane.showMessageDialog(this, "No hostname provided");
			this.info = null;
			System.out.println("Returned");
			return;
		} else {
			System.out.println("Returned disposing");
			dispose();
		}
	}

	public SessionInfo newSession() {
		setLocationRelativeTo(null);
		setVisible(true);
		return this.info;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		System.out.println("value changed");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			// Nothing is selected.
			return;

		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof SessionInfo) {
			sessionInfoPanel.setVisible(true);
			SessionInfo info = (SessionInfo) nodeInfo;
			sessionInfoPanel.setSessionInfo(info);
			selectedInfo = info;
			txtName.setVisible(true);
			lblName.setVisible(true);
			txtName.setText(selectedInfo.getName());
			btnConnect.setVisible(true);
		} else if (nodeInfo instanceof NamedItem) {
			selectedInfo = (NamedItem) nodeInfo;
			lblName.setVisible(true);
			txtName.setVisible(true);
			txtName.setText(selectedInfo.getName());
			sessionInfoPanel.setVisible(false);
			btnConnect.setVisible(false);
		}

		revalidate();
		repaint();
	}

	private void save() {
		String id = null;
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			NamedItem item = (NamedItem) node.getUserObject();
			id = item.getId();
		}
		SessionStore.save(SessionStore.convertModelFromTree(rootNode), id);
	}

//	private void walkSessionTree(DefaultMutableTreeNode node,
//			List<SessionFolder> folders, List<SessionInfo> sessions) {
//		if (node.getUserObject() instanceof SessionFolder) {
//			folders.add((SessionFolder) node.getUserObject());
//			for (int i = 0; i < node.getChildCount(); i++) {
//				walkSessionTree((DefaultMutableTreeNode) node.getChildAt(i),
//						folders, sessions);
//			}
//		} else {
//			System.out.println(node.getUserObject());
//			sessions.add((SessionInfo) node.getUserObject());
//		}
//	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
		System.out.println("treeNodesChanged");
		// save();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
		// TODO Auto-generated method stub

	}

	private void normalizeButtonSize() {
		int width = Math.max(btnConnect.getPreferredSize().width, btnCancel.getPreferredSize().width);
		btnConnect.setPreferredSize(new Dimension(width, btnConnect.getPreferredSize().height));
		btnCancel.setPreferredSize(new Dimension(width, btnCancel.getPreferredSize().height));
	}
}
