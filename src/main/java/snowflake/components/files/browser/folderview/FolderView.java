package snowflake.components.files.browser.folderview;

import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.components.common.CustomScrollBarUI;
import snowflake.components.files.DndTransferHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class FolderView extends JPanel {
    private DefaultListModel<FileInfo> listModel;
    private ListView list;
    //private FolderViewTableModel folderViewModel;
    //private JTable table;
    //private TableRowSorter<FolderViewTableModel> sorter;
    private FolderViewEventListener listener;
    private JPopupMenu popup;

    public FolderView(FolderViewEventListener listener) {
        super(new BorderLayout());
        this.listener = listener;
        this.popup = new JPopupMenu();

        listModel = new DefaultListModel<>();
        list = new ListView(listModel);
        list.setCellRenderer(new FolderViewListCellRenderer());

//        folderViewModel = new FolderViewTableModel(false);
//        FolderViewRenderer r = new FolderViewRenderer();
//        table = new JTable(folderViewModel);
//        table.setDefaultRenderer(Object.class, r);
//        table.setDefaultRenderer(Long.class, r);
//        table.setDefaultRenderer(Date.class, r);
//        table.setFillsViewportHeight(true);

        listener.install(this);

//        table.setIntercellSpacing(new Dimension(0, 0));
//        table.setBorder(null);
//        table.setDragEnabled(true);
//        table.setDropMode(DropMode.ON);
        //table.setShowGrid(false);
//        table.setRowHeight(r.getPreferredHeight());
//        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

//        sorter = new TableRowSorter<>(folderViewModel);
//        sorter.setComparator(0, new Comparator<Object>() {
//            @Override
//            public int compare(Object s1, Object s2) {
//                FileInfo info1 = (FileInfo) s1;
//                FileInfo info2 = (FileInfo) s2;
//                if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
//                    if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                        return info1.getName().compareToIgnoreCase(info2.getName());
//                    } else {
//                        return 1;
//                    }
//                } else {
//                    if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                        return -1;
//                    } else {
//                        return info1.getName().compareToIgnoreCase(info2.getName());
//                    }
//                }
//            }
//        });

//        sorter.setComparator(1, new Comparator<Long>() {
//            @Override
//            public int compare(Long s1, Long s2) {
//                return s1.compareTo(s2);
//            }
//        });
//
//        sorter.setComparator(3, new Comparator<FileInfo>() {
//
//            @Override
//            public int compare(FileInfo info1, FileInfo info2) {
//                if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
//                    if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                        return info1.getLastModified().compareTo(info2.getLastModified());
//                    } else {
//                        return 1;
//                    }
//                } else {
//                    if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                        return -1;
//                    } else {
//                        return info1.getLastModified().compareTo(info2.getLastModified());
//                    }
//                }
//            }
//
//        });
//
//        table.setRowSorter(sorter);
//
////		ArrayList<RowSorter.SortKey> list = new ArrayList<>();
////		list.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
////		sorter.setSortKeys(list);
////
////		sorter.sort();
//
//        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
//                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
//        table.getActionMap().put("Enter", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                FileInfo[] files = getSelectedFiles();
//                if (files.length > 0) {
//                    if (files[0].getType() == FileType.Directory || files[0].getType() == FileType.DirLink) {
//                        String str = files[0].getPath();
//                        listener.render(str);
//                    }
//                }
//            }
//        });
//
//        table.addKeyListener(new FolderViewKeyHandler(table, folderViewModel));

//        table.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                System.out.println("Mouse click on table");
//                if (table.getSelectionModel().getValueIsAdjusting()) {
//                    System.out.println("Value adjusting");
//                    selectRow(e);
//                    return;
//                }
//                if (e.getClickCount() == 2) {
//                    Point p = e.getPoint();
//                    int r = table.rowAtPoint(p);
//                    int x = table.getSelectedRow();
//                    if (x == -1) {
//                        return;
//                    }
//                    if (r == table.getSelectedRow()) {
//                        FileInfo fileInfo = folderViewModel.getItemAt(getRow(r));
//                        if (fileInfo.getType() == FileType.Directory || fileInfo.getType() == FileType.DirLink) {
//                            listener.addBack(fileInfo.getPath());
//                            listener.render(fileInfo.getPath());
//                        } else {
//                            listener.openApp(fileInfo);
//                        }
//                    }
//                } else if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
//                    selectRow(e);
//                    System.out.println("called");
//                    listener.createMenu(popup, getSelectedFiles());
//                    popup.pack();
//                    popup.show(table, e.getX(), e.getY());
//                }
//            }
//        });

        list.setVisibleRowCount(-1);
        list.setDragEnabled(true);

        list.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        list.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                FileInfo[] files = getSelectedFiles();
                if (files.length > 0) {
                    if (files[0].getType() == FileType.Directory || files[0].getType() == FileType.DirLink) {
                        String str = files[0].getPath();
                        System.out.println("Rendering: " + str);
                        listener.render(str);
                    }
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse click on list");
                if (list.getSelectionModel().getValueIsAdjusting()) {
                    System.out.println("List Value adjusting");
                    return;
                }

                if (e.getClickCount() == 2) {
                    System.out.println("Double click");
                    Point p = e.getPoint();
                    int r = list.locationToIndex(p);// folderTable.rowAtPoint(p);
                    int x = list.getSelectedIndex();// folderTable.getSelectedRow();
                    if (x == -1) {
                        System.out.println("List no row selected");
                        return;
                    }
                    if (r == list.getSelectedIndex()) {
                        FileInfo fileInfo = listModel.getElementAt(r);
                        if (fileInfo.getType() == FileType.Directory || fileInfo.getType() == FileType.DirLink) {
                            listener.addBack(fileInfo.getPath());
                            listener.render(fileInfo.getPath());
                        } else {
                            listener.openApp(fileInfo);
                        }
                    }
                } else if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("popup called");
                    listener.createMenu(popup, getSelectedFiles());
                    popup.pack();
                    popup.show(list, e.getX(), e.getY());
                }
            }
        });

//        table.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(list);
//        JScrollBar verticalScroller = new JScrollBar(JScrollBar.VERTICAL);
//        verticalScroller.setUI(new CustomScrollBarUI());
//
//        //verticalScroller.putClientProperty("Nimbus.Overrides", App.scrollBarSkin);
//        scrollPane.setVerticalScrollBar(verticalScroller);
//
//        JScrollBar horizontalScroller = new JScrollBar(JScrollBar.HORIZONTAL);
//        horizontalScroller.setUI(new CustomScrollBarUI());
//        scrollPane.setHorizontalScrollBar(horizontalScroller);

        scrollPane.setBorder(new LineBorder(new Color(240, 240, 240), 1));

        add(scrollPane);

//        resizeColumnWidth(table);
    }

//    private void selectRow(MouseEvent e) {
//        int r = table.rowAtPoint(e.getPoint());
//        System.out.println("Row at point: " + r);
//        if (r == -1) {
//            table.clearSelection();
//        } else {
//            if (table.getSelectedRowCount() > 0) {
//                int[] rows = table.getSelectedRows();
//                for (int row : rows) {
//                    if (r == row) {
//                        return;
//                    }
//                }
//            }
//            table.setRowSelectionInterval(r, r);
//        }
//    }

    public FileInfo[] getSelectedFiles() {
        List<FileInfo> lst = list.getSelectedValuesList();
        FileInfo fs[] = new FileInfo[lst.size()];
        int i = 0;
        for (FileInfo f : lst) {
            fs[i++] = f;
        }
        return fs;
    }

    public FileInfo[] getFiles() {

        FileInfo fs[] = new FileInfo[listModel.getSize()];
        for (int i = 0; i < fs.length; i++) {
            fs[i] = listModel.get(i);
        }
        return fs;
    }

//    private int getRow(int r) {
//        if (r == -1) {
//            return -1;
//        }
//        return table.convertRowIndexToModel(r);
//    }

    public void setItems(List<FileInfo> list) {
        listModel.clear();
        listModel.addAll(list);
    }

//    public final void resizeColumnWidth(JTable table) {
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        final TableColumnModel columnModel = table.getColumnModel();
//        for (int column = 0; column < table.getColumnCount(); column++) {
//            // System.out.println("running..");
//            TableColumn col = columnModel.getColumn(column);
////			col.getHeaderRenderer().getTableCellRendererComponent(table, col.getHeaderValue(),
////					false, false, 0, 0).getpre;
//            if (column == 0) {
//                col.setPreferredWidth(200);
//            } else if (column == 3) {
//                col.setPreferredWidth(150);
//            } else {
//                col.setPreferredWidth(100);
//            }
//        }
//    }

    public void setFolderViewTransferHandler(DndTransferHandler transferHandler) {
        this.list.setTransferHandler(transferHandler);
//        this.table.setTransferHandler(transferHandler);
    }
}
