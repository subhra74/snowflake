package muon.screens.appwin.tabs.filebrowser;

import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.service.InputBlocker;
import muon.util.PathUtils;
import muon.util.StringUtils;
import muon.widgets.InputBlockerPanel;
import muon.screens.sessiontabs.filebrowser.*;
import muon.service.FileSystem;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractFileBrowserView extends JLayeredPane {
    private InputBlockerPanel inputBlockerPanel;
    private JPanel contentPanel;
    private JTextField txtAddress;
    private JTable table;
    private NavigationHistory history;
    private JButton btnBack, btnForward, btnUp, btnHome, btnSearch, btnRefresh;
    private JPopupMenu popup;
    private FileBrowserViewParent parent;
    private AtomicBoolean disposing = new AtomicBoolean(false);
    protected FolderViewTableModel folderViewTableModel;

    public AbstractFileBrowserView(FileBrowserViewParent parent) {
        this.parent = parent;

        this.history = new NavigationHistory();
        this.popup = new JPopupMenu();

        createContentPanel();
        inputBlockerPanel = new InputBlockerPanel(e -> {
            navigate();
        });
        inputBlockerPanel.setVisible(false);

        this.add(contentPanel, Integer.valueOf(1));
        this.add(inputBlockerPanel, Integer.valueOf(2));

        setBackground(AppTheme.INSTANCE.getBackground());
        installMouseListener(table);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPanel.setBounds(0, 0, getWidth(), getHeight());
                inputBlockerPanel.setBounds(0, 0, getWidth(), getHeight());
                revalidate();
                repaint();
            }

//            @Override
//            public void componentShown(ComponentEvent e) {
//                contentPanel.setBounds(0, 0, getWidth(), getHeight());
//                inputBlockerPanel.setBounds(0, 0, getWidth(), getHeight());
//                revalidate();
//                repaint();
//            }
        });
    }

    //public abstract FileSystem getFileSystem();

    public abstract void init();

    public abstract String getHome();

    public abstract FileList ls(String folder) throws FSConnectException, FSAccessException;

    public abstract void cleanup();

    public abstract boolean isConnected();

    public abstract void connect() throws FSConnectException;

    protected InputBlockerPanel getInputBlockerPanel() {
        return inputBlockerPanel;
    }

    private void createContentPanel() {
        var c1 = AppTheme.INSTANCE.getBackground();
        var toolbar = Box.createHorizontalBox();
        toolbar.setOpaque(true);
        toolbar.setBackground(c1);

        btnBack = AppUtils.createIconButton(IconCode.RI_ARROW_LEFT_LINE);
        btnBack.addActionListener(e -> onBack());
        btnBack.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnForward = AppUtils.createIconButton(IconCode.RI_ARROW_RIGHT_LINE);
        btnForward.addActionListener(e -> onNext());
        btnForward.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnUp = AppUtils.createIconButton(IconCode.RI_ARROW_UP_LINE);
        btnUp.addActionListener(e -> onUp());
        btnUp.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnHome = AppUtils.createIconButton(IconCode.RI_HOME_4_LINE);
        btnHome.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnHome.addActionListener(e -> navigate(null));

        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.add(btnBack);
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.add(btnForward);
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.add(btnUp);
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.add(btnHome);
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));

        txtAddress = new JTextField();
        txtAddress.putClientProperty("textField.noBorder", Boolean.TRUE);
        txtAddress.setBackground(c1);
        txtAddress.setForeground(AppTheme.INSTANCE.getForeground());
        txtAddress.setBorder(new EmptyBorder(0, 5, 0, 0));
        txtAddress.setText("");
        txtAddress.addActionListener(e -> {
            navigate(txtAddress.getText());
        });
        toolbar.add(txtAddress);
        btnSearch = AppUtils.createIconButton(IconCode.RI_SEARCH_LINE);
        btnSearch.setForeground(AppTheme.INSTANCE.getDarkForeground());
        toolbar.add(btnSearch);
        toolbar.add(Box.createRigidArea(new Dimension(6, 30)));
        btnRefresh = AppUtils.createIconButton(IconCode.RI_REFRESH_LINE);
        btnRefresh.addActionListener(e -> onRefresh());
        btnRefresh.setForeground(AppTheme.INSTANCE.getDarkForeground());
        toolbar.add(btnRefresh);
        toolbar.add(Box.createRigidArea(new Dimension(3, 30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_MORE_2_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.setBorder(new EmptyBorder(2, 5, 0, 0));

        folderViewTableModel = new FolderViewTableModel(true);
        table = new JTable(folderViewTableModel);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(c1);
        table.setForeground(AppTheme.INSTANCE.getForeground());
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setDragEnabled(true);
        table.setDropMode(DropMode.ON);
        var header = table.getTableHeader();
        header.setBackground(c1);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
        header.setDefaultRenderer((a, b, c, d, e, f) -> {
            var label = new JLabel(b.toString());
            label.setBorder(
                    new CompoundBorder(new EmptyBorder(0, 0, 5, 0),
                            new CompoundBorder(
                                    new MatteBorder(1, f == 0 ? 0 : 1, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()),
                                    new EmptyBorder(5, 10, 5, 10)
                            )));
            label.setOpaque(true);
            label.setBackground(c1);
            label.setForeground(AppTheme.INSTANCE.getDarkForeground());
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            return label;
        });
        table.setRowHeight(30);

        table.setDefaultRenderer(Object.class,
                new FolderViewTableCellRenderer(
                        c1,
                        AppTheme.INSTANCE.getListSelectionColor(),
                        AppTheme.INSTANCE.getListIconColor(),
                        AppTheme.INSTANCE.getSelectionForeground(),
                        AppTheme.INSTANCE.getForeground()));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setComponentPopupMenu(this.popup);
        JScrollPane sp = new JScrollPane(table);
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, createCorner(c1));
        sp.setBackground(c1);
        sp.getViewport().setBackground(c1);
        sp.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setBorder(new EmptyBorder(0, 0, 5, 0));

        resizeColumnWidth(table);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(c1);
        contentPanel.add(sp);
        contentPanel.add(toolbar, BorderLayout.NORTH);
    }

    private JLabel createCorner(Color c) {
        var label = new JLabel();
        label.setBorder(
                new CompoundBorder(new EmptyBorder(0, 0, 5, 0),
                        new CompoundBorder(
                                new MatteBorder(1, 0, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()),
                                new EmptyBorder(5, 10, 5, 10)
                        )));
        label.setOpaque(true);
        label.setBackground(c);
        return label;
    }

    private void installMouseListener(JTable table) {
        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectionModel().getValueIsAdjusting()) {
                    return;
                }
                Point p = e.getPoint();
                int r = table.rowAtPoint(p);
                if (r == -1) {
                    table.clearSelection();
                }
                if (e.getClickCount() == 2) {
                    if (r == table.getSelectedRow()) {
                        FileInfo fileInfo = folderViewTableModel
                                .getItemAt(getRow(r));
                        navigate(fileInfo.getPath());
                        return;
                    }
                }
                if (AppUtils.isPopupTrigger(e)
                        || e.getButton() == MouseEvent.BUTTON3) {
                    //navigator.preparePopup(popup, getSelectedFiles(), table, e.getX(), e.getY());
                    popup.setInvoker(table);
                    popup.show(table, e.getX(), e.getY());
                }
            }
        });
    }

    public List<FileInfo> getSelectedFiles() {
        int indexes[] = table.getSelectedRows();
        var list = new ArrayList<FileInfo>(indexes.length);
        for (int index : indexes) {
            FileInfo info = folderViewTableModel
                    .getItemAt(table.convertRowIndexToModel(index));
            list.add(info);
        }
        return list;
    }

    private int getRow(int r) {
        if (r == -1) {
            return -1;
        }
        return table.convertRowIndexToModel(r);
    }

    private void addBack(String path) {
        history.addBack(path);
        updateNavButtons();
    }

    private void addNext(String path) {
        history.addForward(path);
        updateNavButtons();
    }

    private void updateNavButtons() {
        btnBack.setEnabled(history.hasPrevElement());
        btnForward.setEnabled(history.hasNextElement());
    }

    public String getPath() {
        return txtAddress.getText();
    }

    private void render(String path, java.util.List<FileInfo> result) {
        txtAddress.setText(path);
        folderViewTableModel.clear();
        folderViewTableModel.addAll(result);
    }

    protected void navigate() {
        navigate(txtAddress.getText());
    }

    protected void navigate(final String path) {
        System.out.println("Navigating to: " + path);
        inputBlockerPanel.blockInput();
        AppUtils.runAsync(() -> {
            try {
                if(!isConnected()){
                    connect();
                }
                var folder = StringUtils.isEmpty(path) ? getHome() : path;
                var fileList = ls(folder);
                SwingUtilities.invokeAndWait(() -> {
                    txtAddress.setText(fileList.getCurrentPath());
                    folderViewTableModel.clear();
                    folderViewTableModel.addAll(fileList.getFolders());
                    folderViewTableModel.addAll(fileList.getFiles());
                    inputBlockerPanel.unblockInput();
                    updateToolbarState();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                if (ex instanceof FSAccessException) {
                    inputBlockerPanel.showError();
                } else {
                    inputBlockerPanel.showRetryOption();
                }
            }
        });
    }

    public void dispose() {
        AppUtils.runAsync(() -> {
            try {
                disposing.set(true);
                System.out.println("File browser disposing...");
                this.cleanup();
                this.inputBlockerPanel.close();
                System.out.println("File browser Disposed");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void onBack() {
        String item = history.prevElement();
        addNext(getPath());
        navigate(item);
    }

    private void onNext() {
        String item = history.nextElement();
        addBack(getPath());
        navigate(item);
    }

    private void onUp() {
        var parent = PathUtils.getParentDir(getPath());
        if (Objects.nonNull(parent)) {
            addBack(getPath());
            navigate(parent);
        }
    }

    private void onRefresh() {
        navigate(getPath());
    }

    private void updateToolbarState() {
        btnForward.setEnabled(history.hasNextElement());
        btnBack.setEnabled(history.hasPrevElement());
        btnUp.setEnabled(Objects.nonNull(PathUtils.getParentDir(getPath())));
    }

    public final void resizeColumnWidth(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn col = columnModel.getColumn(column);
            if (column == 0) {
                col.setPreferredWidth(200);
            } else {
                col.setPreferredWidth(100);
            }
        }
    }

    protected abstract boolean handleDrop(DndTransferData transferData);

    protected void setDnDTransferHandler(TransferHandler transferHandler) {
        table.setTransferHandler(transferHandler);
    }
}

