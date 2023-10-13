package muon.screens.sessiontabs.filebrowser;

import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FileBrowser extends JPanel {
    private Navigator navigator;
    private JTextField txtAddress;
    private FolderViewTableModel folderViewTableModel;
    private JTable table;
    private NavigationHistory history;
    private JButton btnBack, btnForward, btnUp, btnHome, btnSearch;
    private JPopupMenu popup;

    public FileBrowser(Navigator navigator) {
        super(new BorderLayout());
        this.navigator = navigator;
        this.history = new NavigationHistory();
        this.popup = new JPopupMenu();
        var c1 = AppTheme.INSTANCE.getBackground();
        var toolbar = Box.createHorizontalBox();
        toolbar.setOpaque(true);
        toolbar.setBackground(c1);

        btnBack = AppUtils.createIconButton(IconCode.RI_ARROW_LEFT_LINE);
        btnBack.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnForward = AppUtils.createIconButton(IconCode.RI_ARROW_RIGHT_LINE);
        btnForward.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnUp = AppUtils.createIconButton(IconCode.RI_ARROW_UP_LINE);
        btnUp.setForeground(AppTheme.INSTANCE.getDarkForeground());
        btnHome = AppUtils.createIconButton(IconCode.RI_HOME_4_LINE);
        btnHome.setForeground(AppTheme.INSTANCE.getDarkForeground());

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
            this.navigator.navigate(this, txtAddress.getText());
        });
        toolbar.add(txtAddress);
        btnSearch = AppUtils.createIconButton(IconCode.RI_SEARCH_LINE);
        btnSearch.setForeground(AppTheme.INSTANCE.getDarkForeground());
        toolbar.add(btnSearch);
        toolbar.add(Box.createRigidArea(new Dimension(3, 30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_MORE_2_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5, 30)));
        toolbar.setBorder(new EmptyBorder(2, 5, 0, 0));

        setBackground(c1);

        folderViewTableModel = new FolderViewTableModel(true);
        table = new JTable(folderViewTableModel);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(c1);
        table.setForeground(AppTheme.INSTANCE.getForeground());
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        var header = table.getTableHeader();
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
        sp.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(sp);
        add(toolbar, BorderLayout.NORTH);
        installMouseListener(table);
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

    public String getPath() {
        return txtAddress.getText();
    }

    public void render(String path, List<FileInfo> result) {
        txtAddress.setText(path);
        folderViewTableModel.clear();
        folderViewTableModel.addAll(result);
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
                        navigator.navigate(FileBrowser.this, fileInfo.getPath());
                        return;
                    }
                }
                if (AppUtils.isPopupTrigger(e)
                        || e.getButton() == MouseEvent.BUTTON3) {
                    navigator.preparePopup(popup, getSelectedFiles(), table, e.getX(), e.getY());
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

}
