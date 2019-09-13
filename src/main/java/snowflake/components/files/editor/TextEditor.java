package snowflake.components.files.editor;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.components.files.FileComponentHolder;

import javax.swing.*;
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
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private KeyStroke ksOpen, ksSave, ksSaveAs, ksFind, ksReplace, ksReload, ksGotoLine;
    private Set<EditorTab> tabSet = new HashSet<>();
    private boolean savingFile = false;
    private boolean reloading = false;
    private JComboBox<String> cmbSyntax;

    public TextEditor(
            FileComponentHolder holder) {
        super(new BorderLayout());
        this.holder = holder;
        tabs = new JTabbedPane();

        installKeyboardShortcuts();

        Box toolBox = Box.createHorizontalBox();

        JButton btnSave = new JButton();
        btnSave.addActionListener(e -> {
            save();
        });
        btnSave.setFont(App.getFontAwesomeFont());
        btnSave.setText("\uf0c7");
        btnSave.setToolTipText("Save");

        JButton btnReload = new JButton();
        btnReload.addActionListener(e -> {
            reloadFile();
        });
        btnReload.setFont(App.getFontAwesomeFont());
        btnReload.setText("\uf021");
        btnReload.setToolTipText("Reload");

        JButton btnFind = new JButton();
        btnFind.addActionListener(e -> {
            findText();
        });
        btnFind.setFont(App.getFontAwesomeFont());
        btnFind.setText("\uf002");
        btnFind.setToolTipText("Find and replace");

        JButton btnCut = new JButton();
        btnCut.addActionListener(e -> {
            //cutText();
        });
        btnCut.setFont(App.getFontAwesomeFont());
        btnCut.setText("\uf002");
        btnCut.setToolTipText("Cut");

        JButton btnCopy = new JButton();
        btnCopy.addActionListener(e -> {
            //cutText();
        });
        btnCopy.setFont(App.getFontAwesomeFont());
        btnCopy.setText("\uf002");
        btnCopy.setToolTipText("Copy");

        JButton btnPaste = new JButton();
        btnPaste.addActionListener(e -> {
            //cutText();
        });
        btnPaste.setFont(App.getFontAwesomeFont());
        btnPaste.setText("\uf002");
        btnPaste.setToolTipText("Paste");

        JButton btnWrapText = new JButton();
        btnWrapText.addActionListener(e -> {
            //cutText();
        });
        btnWrapText.setFont(App.getFontAwesomeFont());
        btnWrapText.setText("\uf002");
        btnWrapText.setToolTipText("Wrap text");

        JButton btnGotoLine = new JButton();
        btnGotoLine.addActionListener(e -> {
            //cutText();
        });
        btnGotoLine.setFont(App.getFontAwesomeFont());
        btnGotoLine.setText("\uf002");
        btnGotoLine.setToolTipText("Goto line");

        toolBox.add(btnSave);
        toolBox.add(btnReload);
        toolBox.add(btnFind);
        toolBox.add(btnCut);
        toolBox.add(btnCopy);
        toolBox.add(btnPaste);
        toolBox.add(btnWrapText);
        toolBox.add(btnGotoLine);

        add(toolBox, BorderLayout.NORTH);

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

        JPanel content = new JPanel(new BorderLayout());
        //content.add(toolBar, BorderLayout.NORTH);
        content.add(tabs);

        add(content);

        tabs.addChangeListener(e -> {
            int index = tabs.getSelectedIndex();
            if (index >= 0) {
                EditorTab tab = (EditorTab) tabs.getComponentAt(index);
                if (this.cmbSyntax != null) toolBox.remove(this.cmbSyntax);
                this.cmbSyntax = tab.getCmbSyntax();
                toolBox.add(this.cmbSyntax);
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
                if (x == -1) break;
                sb.append(buf, 0, x);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    private void setTabContent(StringBuilder sb) {
        this.reloading = false;
        ((EditorTab) tabs.getSelectedComponent()).setText(sb.toString());
    }

    private void createNewTab(FileInfo fileInfo, StringBuilder sb, String tempFile) {
        int index = tabs.getTabCount();
        EditorTab tab = new EditorTab(fileInfo, sb.toString(), tempFile);
        JPanel pan = new JPanel(new BorderLayout());
        pan.add(new JLabel(fileInfo.getName()));
        JLabel btnClose = new JLabel();
        btnClose.setFont(App.getFontAwesomeFont());
        btnClose.setText("\uf2d3");
        int count = tabs.getTabCount();
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabs.indexOfTabComponent(pan);
                System.out.println("Closing tab at: " + index);
                closeTab(index);
            }
        });
        pan.add(btnClose, BorderLayout.EAST);
        tabs.addTab(null, tab);
        tabs.setTabComponentAt(count, pan);
        tabSet.add(tab);
        tabs.setSelectedIndex(index);
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
            if (JOptionPane.showConfirmDialog(this, "Changes will be lost, continue?", "Unsaved changes", JOptionPane.YES_OPTION) == JOptionPane.YES_OPTION) {
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
}
