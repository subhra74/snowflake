package snowflake.components.files.editor;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.components.files.FileComponentHolder;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.LayoutUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextEditor extends JPanel {
	private JTabbedPane tabs;
	private FileComponentHolder holder;
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();
	private KeyStroke ksOpen, ksSave, ksSaveAs, ksFind, ksReplace, ksReload,
			ksGotoLine;
	private Set<EditorTab> tabSet = new HashSet<>();
	private boolean savingFile = false;
	private boolean reloading = false;
	private JComboBox<String> cmbSyntax;
	private CardLayout cardLayout;
	private JPanel content;
	private JTextField txtFullFilePath;

	public TextEditor(FileComponentHolder holder) {
		super(new BorderLayout());
		this.holder = holder;
		tabs = new JTabbedPane();

		cardLayout = new CardLayout();

		installKeyboardShortcuts();

		Box toolBox = Box.createHorizontalBox();

		JButton btnOpen = new JButton();
		btnOpen.addActionListener(e -> {
			String text = JOptionPane.showInputDialog(
					"Please enter full path of the file to be opened");
			if (text == null)
				return;
			if (text.trim().length() < 1) {
				JOptionPane.showMessageDialog(null,
						"Please enter full path of the file to be opened");
				return;
			}
			holder.statAsync(text, (a, b) -> {
				if (!b) {
					JOptionPane.showMessageDialog(null, "Unable to open file");
					return;
				}
				SwingUtilities.invokeLater(() -> {
					holder.editRemoteFileInternal(a);
				});
			});
		});
		btnOpen.setFont(App.getFontAwesomeFont());
		btnOpen.setText("\uf115");
		btnOpen.setToolTipText("Open file");
		btnOpen.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnSave = new JButton();
		btnSave.addActionListener(e -> {
			save();
		});
		btnSave.setFont(App.getFontAwesomeFont());
		btnSave.setText("\uf0c7");
		btnSave.setToolTipText("Save");
		btnSave.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnReload = new JButton();
		btnReload.addActionListener(e -> {
			reloadFile();
		});
		btnReload.setFont(App.getFontAwesomeFont());
		btnReload.setText("\uf021");
		btnReload.setToolTipText("Reload");
		btnReload.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnFind = new JButton();
		btnFind.addActionListener(e -> {
			findText();
		});
		btnFind.setFont(App.getFontAwesomeFont());
		btnFind.setText("\uf002");
		btnFind.setToolTipText("Find and replace");
		btnFind.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnCut = new JButton();
		btnCut.addActionListener(e -> {
			cutText();
		});
		btnCut.setFont(App.getFontAwesomeFont());
		btnCut.setText("\uf0c4");
		btnCut.setToolTipText("Cut");
		btnCut.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnCopy = new JButton();
		btnCopy.addActionListener(e -> {
			copyText();
		});
		btnCopy.setFont(App.getFontAwesomeFont());
		btnCopy.setText("\uf0c5");
		btnCopy.setToolTipText("Copy");
		btnCopy.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JButton btnPaste = new JButton();
		btnPaste.addActionListener(e -> {
			pasteText();
		});
		btnPaste.setFont(App.getFontAwesomeFont());
		btnPaste.setText("\uf0ea");
		btnPaste.setToolTipText("Paste");
		btnPaste.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);

		JCheckBox btnWrapText = new JCheckBox("Wrap text");
		btnWrapText.addActionListener(e -> {
			wrapText(btnWrapText.isSelected());
		});

		JButton btnGotoLine = new JButton();
		btnGotoLine.addActionListener(e -> {
			gotoLine();
		});
		btnGotoLine.setFont(App.getFontAwesomeFont());
		btnGotoLine.setText("\uf0cb");
		btnGotoLine.setToolTipText("Goto line");
		btnGotoLine.putClientProperty("Nimbus.Overrides",
				App.toolBarButtonSkin);

		JLabel lblFont = new JLabel("Font size");
		SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(12, 5,
				72, 1);
		JSpinner spFontSize = new JSpinner(spinnerNumberModel);
		spFontSize.setMaximumSize(spFontSize.getPreferredSize());
		spFontSize.addChangeListener(e -> {
			int fontSize = (int) spinnerNumberModel.getValue();
			setFontSize(fontSize);
		});

		txtFullFilePath = GraphicsUtils.createTextField();// new JTextField();
		txtFullFilePath.setEditable(false);
		txtFullFilePath.setBorder(null);

		toolBox.add(Box.createHorizontalStrut(5));
		toolBox.add(btnOpen);
		toolBox.add(btnSave);
		toolBox.add(btnReload);
		toolBox.add(btnFind);
		toolBox.add(btnCut);
		toolBox.add(btnCopy);
		toolBox.add(btnPaste);
		toolBox.add(btnGotoLine);
		toolBox.add(Box.createHorizontalStrut(10));
		toolBox.add(btnWrapText);
		toolBox.add(Box.createHorizontalGlue());
		toolBox.add(lblFont);
		toolBox.add(Box.createHorizontalStrut(5));
		toolBox.add(spFontSize);
		toolBox.add(Box.createHorizontalStrut(10));

		LayoutUtils.makeSameSize(btnSave, btnReload, btnFind, btnCut, btnCopy,
				btnPaste, btnGotoLine);

//        JMenuBar menuBar = new JMenuBar();
//        add(menuBar, BorderLayout.NORTH);
//
//        JMenu menuFile = new JMenu("File");
//        menuBar.add(menuFile);
//
//        JMenuItem mOpen = new JMenuItem("Open");
//        mOpen.setAccelerator(ksOpen);
//        mOpen.setMnemonic(KeyEvent.VK_O);
//        menuFile.add(mOpen);
//
//        JMenuItem mSave = new JMenuItem("Save");
//        mSave.setAccelerator(ksSave);
//        mSave.setMnemonic(KeyEvent.VK_S);
//        menuFile.add(mSave);
//
//        JMenuItem mSaveAs = new JMenuItem("Save As...");
//        mSaveAs.setAccelerator(ksSaveAs);
//        mSaveAs.setMnemonic(KeyEvent.VK_S);
//        menuFile.add(mSaveAs);
//
//        mOpen.addActionListener(e -> {
//            open();
//        });
//
//        mSave.addActionListener(e -> {
//            save();
//        });

//        JToolBar toolBar = new JToolBar();
//        toolBar.setFloatable(false);
//
//        JButton btnOpen = new JButton();
//        btnOpen.setFont(App.getFontAwesomeFont());
//        btnOpen.setText("\uf115");
//        toolBar.add(btnOpen);
//        btnOpen.addActionListener(e -> {
//            open();
//        });
//
//        JButton btnSave = new JButton();
//        btnSave.setFont(App.getFontAwesomeFont());
//        btnSave.setText("\uf0c7");
//        toolBar.add(btnSave);
//        btnSave.addActionListener(e -> {
//            save();
//        });

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(toolBox, BorderLayout.NORTH);
		panel.add(tabs);
		panel.add(txtFullFilePath, BorderLayout.SOUTH);

		content = new JPanel(cardLayout);
		// content.add(toolBar, BorderLayout.NORTH);
		content.add(panel, "Tabs");
		content.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel lblTitle = new JLabel(
				"Please enter full path of the file below to open");
		JTextField txtFilePath = GraphicsUtils.createTextField(30);// new
																	// JTextField(30);

		JButton btnOpenFile = new JButton("Open");
		JLabel lblTitle2 = new JLabel(
				"Alternatively you can select the file from file browser");

		Box textBox = Box.createHorizontalBox();
		textBox.add(txtFilePath);
		textBox.add(Box.createHorizontalStrut(10));
		textBox.add(btnOpenFile);

		ActionListener act = e -> {
			String text = txtFilePath.getText();
			if (text.trim().length() < 1) {
				JOptionPane.showMessageDialog(null,
						"Please enter full path of the file to be opened");
				return;
			}
			holder.statAsync(txtFilePath.getText(), (a, b) -> {
				if (!b) {
					JOptionPane.showMessageDialog(null, "Unable to open file");
					return;
				}
				SwingUtilities.invokeLater(() -> {
					holder.editRemoteFileInternal(a);
				});
			});
		};

		btnOpenFile.addActionListener(act);
		txtFilePath.addActionListener(act);

		Box startPanel = Box.createVerticalBox();
		lblTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
		textBox.setAlignmentX(Box.CENTER_ALIGNMENT);
		lblTitle2.setAlignmentX(Box.CENTER_ALIGNMENT);
		startPanel.add(Box.createVerticalStrut(50));
		startPanel.add(lblTitle);
		startPanel.add(Box.createVerticalStrut(10));
		startPanel.add(textBox);
		startPanel.add(Box.createVerticalStrut(5));
		startPanel.add(lblTitle2);

		JPanel msgPanel = new JPanel();
		msgPanel.add(startPanel);
//        JLabel noTabMsg = new JLabel("No files opened, please open a file from file browser");
//        noTabMsg.setHorizontalAlignment(JLabel.CENTER);
//        msgPanel.add(noTabMsg);
		content.add(msgPanel, "Labels");

		add(content);

		cardLayout.show(content, "Labels");

		tabs.addChangeListener(e -> {
			if (tabs.getTabCount() == 0) {
				txtFilePath.setText("");
				cardLayout.show(content, "Labels");
			}
			int index = tabs.getSelectedIndex();
			if (index >= 0) {
				EditorTab tab = (EditorTab) tabs.getComponentAt(index);
				if (this.cmbSyntax != null)
					toolBox.remove(this.cmbSyntax);
				this.cmbSyntax = tab.getCmbSyntax();
				toolBox.add(this.cmbSyntax);
				btnWrapText.setSelected(tab.getWrapText());
				txtFullFilePath.setText(tab.getInfo().getPath());
				revalidate();
				repaint();
			}
		});
	}

	private void installKeyboardShortcuts() {
		InputMap inpMap = getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap actMap = getActionMap();

		ksOpen = KeyStroke.getKeyStroke(KeyEvent.VK_O,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksOpen, "openKey");
		actMap.put("openKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});

		ksSave = KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksSave, "saveKey");
		actMap.put("saveKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		ksSaveAs = KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		inpMap.put(ksSaveAs, "saveKeyAs");
		actMap.put("ksSaveAs", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAs();
			}
		});

		ksFind = KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksFind, "findKey");
		actMap.put("findKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findText();
			}
		});

		ksReplace = KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksReplace, "ksReplace");
		actMap.put("ksReplace", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replaceText();
			}
		});

		ksReload = KeyStroke.getKeyStroke(KeyEvent.VK_R,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksReload, "reloadKey");
		actMap.put("reloadKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reloadFile();
			}
		});

		ksGotoLine = KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_DOWN_MASK);
		inpMap.put(ksGotoLine, "gotoKey");
		actMap.put("gotoKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gotoLine();
			}
		});

	}

	private void gotoLine() {
		System.out.println("Goto line");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.gotoLine();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void setFontSize(int fontSize) {
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.setFontSize(fontSize);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void pasteText() {
		System.out.println("pasteText");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.pasteText();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void copyText() {
		System.out.println("copyText");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.copyText();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void cutText() {
		System.out.println("cutText");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.cutText();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void reloadFile() {
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		reloadTab(tab.getInfo());
	}

	private void replaceText() {
	}

	private void findText() {
		((EditorTab) tabs.getSelectedComponent()).openFindReplace();
	}

	private void saveAs() {
	}

	private void save() {
		System.out.println("Save");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.saveContentsToLocal();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			saveRemoteFile(tab.getInfo(), tab.getLocalFile());
		}
	}

	private void open() {
		System.out.println("Open");
	}

	private StringBuilder readTempFile(String file) {
		StringBuilder sb = new StringBuilder();
		try (Reader r = new InputStreamReader(new FileInputStream(file))) {
			char[] buf = new char[8192];
			while (true) {
				int x = r.read(buf);
				if (x == -1)
					break;
				sb.append(buf, 0, x);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb;
	}

	private void setTabContent(StringBuilder sb) {
		System.out.println("Setting tab content");
		this.reloading = false;
		((EditorTab) tabs.getSelectedComponent()).setText(sb.toString());
	}

	private void createNewTab(FileInfo fileInfo, StringBuilder sb,
			String tempFile) {
		cardLayout.show(content, "Tabs");
		int index = tabs.getTabCount();
		TabHeader tabHeader = new TabHeader(fileInfo.getName());
		EditorTab tab = new EditorTab(fileInfo, sb.toString(), tempFile, this,
				tabHeader);
		int count = tabs.getTabCount();
		tabHeader.getBtnClose().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = tabs.indexOfTabComponent(tabHeader);
				System.out.println("Closing tab at: " + index);
				closeTab(index);
			}
		});
		tabs.addTab(null, tab);
		tabs.setTabComponentAt(count, tabHeader);
		tabSet.add(tab);
		tabs.setSelectedIndex(index);
	}

	public void showTab(FileInfo fileInfo) {
		System.out.println(fileInfo);
		for (int i = 0; i < tabs.getTabCount(); i++) {
			EditorTab tab = (EditorTab) tabs.getComponentAt(i);
			if (tab.getInfo().getPath().equals(fileInfo.getPath())) {
				tabs.setSelectedIndex(i);
				break;
			}
		}
	}

	public void openRemoteFile(FileInfo fileInfo, String tempFile) {
		System.out.println("Local file: " + tempFile);
		this.executorService.submit(() -> {
			String path = tempFile;
			StringBuilder sb = readTempFile(path);
			SwingUtilities.invokeLater(() -> {
				if (reloading) {
					setTabContent(sb);
				} else {
					createNewTab(fileInfo, sb, tempFile);
				}
			});
		});
	}

	public void closeTab(int index) {
		EditorTab tab = (EditorTab) tabs.getComponentAt(index);
		boolean close = false;
		if (tab.hasUnsavedChanges()) {
			if (JOptionPane.showConfirmDialog(this,
					"Changes will be lost, continue?", "Unsaved changes",
					JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
				close = true;
			}
		} else {
			close = true;
		}
		if (close) {
			tabs.removeTabAt(index);
		}
	}

	public void saveRemoteFile(FileInfo fileInfo, String tempFile) {
		try {
			System.out.println("Saving to remote: " + tempFile + " -> "
					+ fileInfo.getPath());
			holder.saveRemoteFile(tempFile, fileInfo, this.hashCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isSavingFile() {
		return savingFile;
	}

	public void setSavingFile(boolean savingFile) {
		this.savingFile = savingFile;
	}

	public void fileSaved() {
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		tab.setHasChanges(false);
	}

//    public void fileSavedWithError() {
//        JOptionPane.showMessageDialog(this, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
//    }

	public boolean isAlreadyOpened(String file) {
		int c = tabs.getTabCount();
		for (int i = 0; i < c; i++) {
			EditorTab tab = (EditorTab) tabs.getComponentAt(i);
			if (file.equals(tab.getInfo().getPath())) {
				tabs.setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}

	public void reloadTab(FileInfo fileInfo) {
		this.reloading = true;
		holder.reloadRemoteFile(fileInfo);
	}

	private void wrapText(boolean selected) {
		System.out.println("wrapText");
		EditorTab tab = (EditorTab) tabs.getSelectedComponent();
		if (tab != null) {
			try {
				tab.setWrapText(selected);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

//    public void hasUnsavedChanges(EditorTab editorTab) {
//        int index = tabs.getSelectedIndex();
//
//        if (index < 0) {
//            return;
//        }
//
//        for (int i = 0; i < tabs.getComponentCount(); i++) {
//            if (tabs.getComponent(i) == editorTab) {
//                TabHeader header = (TabHeader) tabs.getTabComponentAt(index);
//                header.setTitle(tab.getInfo().getName() + (value ? "*" : ""));
//            }
//        }
//
////        TabHeader header = (TabHeader) tabs.getTabComponentAt(index);
////        EditorTab tab = (EditorTab) tabs.getSelectedComponent();
////        header.setTitle(tab.getInfo().getName() + (value ? "*" : ""));
//    }
}
