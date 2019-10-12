package snowflake.components.files.browser.folderview;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.components.files.DndTransferHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
//import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class FolderView extends JPanel {
    //    private DefaultListModel<FileInfo> listModel;
//    private ListView list;
    private FolderViewTableModel folderViewModel;
    private JTable table;
    //private TableRowSorter<FolderViewTableModel> sorter;
    private FolderViewEventListener listener;
    private JPopupMenu popup;
    private boolean showHiddenFiles = false;
    //    private int sortIndex = 2;
//    private boolean sortAsc = false;
    private List<FileInfo> files;

    public FolderView(FolderViewEventListener listener, Color viewBackground) {
        super(new BorderLayout());
        this.listener = listener;
        this.popup = new JPopupMenu();

        showHiddenFiles = App.getGlobalSettings().isShowHiddenFilesByDefault();

//        listModel = new DefaultListModel<>();
//        list = new ListView(listModel);
//        list.setCellRenderer(new FolderViewListCellRenderer());

        folderViewModel = new FolderViewTableModel(false);


        //TableCellTextRenderer r = new TableCellTextRenderer();

        TableCellLabelRenderer r1 = new TableCellLabelRenderer(viewBackground);

        table = new JTable(folderViewModel);
        table.setBackground(viewBackground);
        table.setDefaultRenderer(FileInfo.class, r1);
        table.setDefaultRenderer(Long.class, r1);
        table.setDefaultRenderer(LocalDateTime.class, r1);
        table.setDefaultRenderer(Object.class, r1);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);

        listener.install(this);

        table.setIntercellSpacing(new Dimension(0, 0));
        table.setDragEnabled(true);
        table.setDropMode(DropMode.ON);
        //table.setShowGrid(false);
        //table.setRowHeight(r.getPreferredHeight());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

//        TableRowSorter sorter=new TableRowSorter<>(table.getModel());
//        sorter.setComparator(0, new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                System.out.println("Called sorter");
//                return 0;
//            }
//        });
//
//        table.setRowSorter(sorter);

        //
        //
        table.setAutoCreateRowSorter(true);

//        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
//        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
//        table.getRowSorter().setSortKeys(sortKeys);

        //sorter=new TableRowSorter<>(folderViewModel);


//        sorter.setComparator(0,(a,b)->{
//            System.out.println("called new sorter");
//            return 1;
//        });
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
//
//        sorter.setComparator(1, new Comparator<Long>() {
//            @Override
//            public int compare(Long s1, Long s2) {
//                System.out.println("Sorter 1 called");
//                return s1.compareTo(s2);
//            }
//        });
//
//        sorter.setComparator(3, new Comparator<FileInfo>() {
//
//            @Override
//            public int compare(FileInfo info1, FileInfo info2) {
//                System.out.println("Sorter 3 called");
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


////        sorter = new TableRowSorter<FolderViewTableModel>(folderViewModel);
//        sorter.setRowFilter(new RowFilter<FolderViewTableModel, Integer>() {
//            @Override
//            public boolean include(Entry<? extends FolderViewTableModel, ? extends Integer> entry) {
//                return true;
//            }
//        });
//
//        sorter.setComparator(0, (Object o1, Object o2) -> {
//            FileInfo s1 = (FileInfo) o1;
//            FileInfo s2 = (FileInfo) o2;
//            System.out.println("Name sorter called");
//            return s1.toString().compareTo(s2.toString());
//        });
//
//        sorter.setComparator(0, (FileInfo o1, FileInfo o2) -> {
//            FileInfo s1 = (FileInfo) o1;
//            FileInfo s2 = (FileInfo) o2;
//            System.out.println("Name sorter called");
//            return s1.toString().compareTo(s2.toString());
//        });
//
//        sorter.setComparator(0, (String o1, String o2) -> {
//            System.out.println("Name sorter called");
//            return o1.toString().compareTo(o2.toString());
//        });
//
//        sorter.setComparator(1, (Long s1, Long s2) -> {
//            System.out.println("Size sorter called");
//            return s1.compareTo(s2);
//        });
//
//        sorter.setComparator(2, (Object s1, Object s2) -> {
//            System.out.println("Type sorter called");
//            return (s1 + "").compareTo((s2 + ""));
//        });
//
//        sorter.setComparator(3,
//                new Comparator<Object>() {
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        FileInfo s1 = (FileInfo) o1;
//                        FileInfo s2 = (FileInfo) o2;
//                        System.out.println("Date sorter called");
//                        if (s1.getType() == FileType.Directory || s1.getType() == FileType.DirLink) {
//                            if (s2.getType() == FileType.Directory || s2.getType() == FileType.DirLink) {
//                                return s1.getLastModified().compareTo(s2.getLastModified());
//                            } else {
//                                return 1;
//                            }
//                        } else {
//                            if (s1.getType() == FileType.Directory || s1.getType() == FileType.DirLink) {
//                                return -1;
//                            } else {
//                                return s1.getLastModified().compareTo(s2.getLastModified());
//                            }
//                        }
//                    }
//                });
//
//        table.setRowSorter(sorter);
//
////        sorter.setComparator(3, (FileInfo s1, FileInfo s2) -> {
////            System.out.println("Date sorter called");
////            if (s1.getType() == FileType.Directory || s1.getType() == FileType.DirLink) {
////                if (s2.getType() == FileType.Directory || s2.getType() == FileType.DirLink) {
////                    return s1.getLastModified().compareTo(s2.getLastModified());
////                } else {
////                    return 1;
////                }
////            } else {
////                if (s1.getType() == FileType.Directory || s1.getType() == FileType.DirLink) {
////                    return -1;
////                } else {
////                    return s1.getLastModified().compareTo(s2.getLastModified());
////                }
////            }
////        });
//
//        sorter.setComparator(4, (Object s1, Object s2) -> {
//            System.out.println("Perm sorter called");
//            return (s1 + "").compareTo((s2 + ""));
//        });
//
//        sorter.setComparator(5, (Object s1, Object s2) -> {
//            System.out.println("Extra sorter called");
//            return (s1 + "").compareTo((s2 + ""));
//        });
//
//        table.setRowSorter(sorter);
//
//        ArrayList<RowSorter.SortKey> list = new ArrayList<>();
//        list.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
//        sorter.setSortKeys(list);
//
//        sorter.sort();
//


        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
        table.getActionMap().put("Enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                FileInfo[] files = getSelectedFiles();
                if (files.length > 0) {
                    if (files[0].getType() == FileType.Directory || files[0].getType() == FileType.DirLink) {
                        String str = files[0].getPath();
                        listener.render(str, App.getGlobalSettings().isDirectoryCache());
                    }
                }
            }
        });

        table.addKeyListener(new FolderViewKeyHandler(table, folderViewModel));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse click on table");
                if (table.getSelectionModel().getValueIsAdjusting()) {
                    System.out.println("Value adjusting");
                    selectRow(e);
                    return;
                }
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    int r = table.rowAtPoint(p);
                    int x = table.getSelectedRow();
                    if (x == -1) {
                        return;
                    }
                    if (r == table.getSelectedRow()) {
                        FileInfo fileInfo = folderViewModel.getItemAt(getRow(r));
                        if (fileInfo.getType() == FileType.Directory || fileInfo.getType() == FileType.DirLink) {
                            listener.addBack(fileInfo.getPath());
                            listener.render(fileInfo.getPath(), App.getGlobalSettings().isDirectoryCache());
                        } else {
                            listener.openApp(fileInfo);
                        }
                    }
                } else if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                    selectRow(e);
                    System.out.println("called");
                    listener.createMenu(popup, getSelectedFiles());
                    popup.pack();
                    popup.show(table, e.getX(), e.getY());
                }
            }
        });

//        list.setVisibleRowCount(-1);
//        list.setDragEnabled(true);
//        list.setVisibleRowCount(20);
//
//        list.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
//                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
//        list.getActionMap().put("Enter", new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                FileInfo[] files = getSelectedFiles();
//                if (files.length > 0) {
//                    if (files[0].getType() == FileType.Directory || files[0].getType() == FileType.DirLink) {
//                        String str = files[0].getPath();
//                        System.out.println("Rendering: " + str);
//                        listener.addBack(str);
//                        listener.render(str);
//                    }
//                }
//            }
//        });
//
//        list.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                System.out.println("Mouse click on list");
//                if (list.getSelectionModel().getValueIsAdjusting()) {
//                    System.out.println("List Value adjusting");
//                    return;
//                }
//
//                if (e.getClickCount() == 2) {
//                    System.out.println("Double click");
//                    Point p = e.getPoint();
//                    int r = list.locationToIndex(p);// folderTable.rowAtPoint(p);
//                    int x = list.getSelectedIndex();// folderTable.getSelectedRow();
//                    if (x == -1) {
//                        System.out.println("List no row selected");
//                        return;
//                    }
//                    if (r == list.getSelectedIndex()) {
//                        FileInfo fileInfo = listModel.getElementAt(r);
//                        if (fileInfo.getType() == FileType.Directory || fileInfo.getType() == FileType.DirLink) {
//                            listener.addBack(fileInfo.getPath());
//                            listener.render(fileInfo.getPath());
//                        } else {
//                            listener.openApp(fileInfo);
//                        }
//                    }
//                } else if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
//                    System.out.println("popup called");
//                    listener.createMenu(popup, getSelectedFiles());
//                    popup.pack();
//                    popup.show(list, e.getX(), e.getY());
//                }
//            }
//        });

        resizeColumnWidth(table);

        //table.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(table);
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
        scrollPane.getViewport().setBackground(viewBackground);
        add(scrollPane);

        table.setRowHeight(r1.getHeight());

        resizeColumnWidth(table);

        System.out.println("Row height: " + r1.getHeight());
    }

    private void selectRow(MouseEvent e) {
        int r = table.rowAtPoint(e.getPoint());
        System.out.println("Row at point: " + r);
        if (r == -1) {
            table.clearSelection();
        } else {
            if (table.getSelectedRowCount() > 0) {
                int[] rows = table.getSelectedRows();
                for (int row : rows) {
                    if (r == row) {
                        return;
                    }
                }
            }
            table.setRowSelectionInterval(r, r);
        }
    }

    public FileInfo[] getSelectedFiles() {
        int indexes[] = table.getSelectedRows();
        FileInfo fs[] = new FileInfo[indexes.length];
        int i = 0;
        for (int index : indexes) {
            FileInfo info = folderViewModel.getItemAt(table.convertRowIndexToModel(index));
            fs[i++] = info;
        }
        return fs;

//        List<FileInfo> lst = list.getSelectedValuesList();
//        FileInfo fs[] = new FileInfo[lst.size()];
//        int i = 0;
//        for (FileInfo f : lst) {
//            fs[i++] = f;
//        }
//        return fs;
    }

    public FileInfo[] getFiles() {
        if (this.files == null) {
            return new FileInfo[0];
        } else {
            FileInfo fs[] = new FileInfo[files.size()];
            for (int i = 0; i < files.size(); i++) {
                fs[i] = files.get(i);
            }
            return fs;
        }
    }

    private int getRow(int r) {
        if (r == -1) {
            return -1;
        }
        return table.convertRowIndexToModel(r);
    }

    public void setItems(List<FileInfo> list) {
        this.files = list;
        applyHiddenFilter();
        //this.resizeColumnWidth(table);
//        if (showHiddenFiles) {
//            sortAndAddItems(list);
//        } else {
//            List<FileInfo> list2 = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                FileInfo info = list.get(i);
//                if (!info.isHidden()) {
//                    list2.add(info);
//                }
//            }
//            sortAndAddItems(list2);
//        }
    }

    public final void resizeColumnWidth(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            // System.out.println("running..");
            TableColumn col = columnModel.getColumn(column);
//			col.getHeaderRenderer().getTableCellRendererComponent(table, col.getHeaderValue(),
//					false, false, 0, 0).getpre;
            if (column == 0) {
                col.setPreferredWidth(300);
            } else if (column == 3) {
                col.setPreferredWidth(150);
            } else {
                col.setPreferredWidth(100);
            }
        }
    }

    public void setFolderViewTransferHandler(DndTransferHandler transferHandler) {
//        this.list.setTransferHandler(transferHandler);
        this.table.setTransferHandler(transferHandler);
    }

//    public void sortView(int index, boolean asc) {
//        this.sortIndex = index;
//        this.sortAsc = asc;
//        System.out.println("Sort click- index: " + index + " asc: " + asc);
//        this.setItems(this.files);
//
////        List<FileInfo> fileInfoList = new ArrayList<>();
////        for (int i = 0; i < listModel.getSize(); i++) {
////            fileInfoList.add(listModel.get(i));
////        }
////        sortAndAddItems(fileInfoList);
//    }

//    private void sortAndAddItems(List<FileInfo> fileInfoList) {
//        switch (this.sortIndex) {
//            case 0:
//                fileInfoList.sort(new Comparator<FileInfo>() {
//                    @Override
//                    public int compare(FileInfo info1, FileInfo info2) {
//                        if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
//                            if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                                return info1.getName().compareToIgnoreCase(info2.getName());
//                            } else {
//                                return 1;
//                            }
//                        } else {
//                            if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                                return -1;
//                            } else {
//                                return info1.getName().compareToIgnoreCase(info2.getName());
//                            }
//                        }
//                    }
//                });
//                break;
//            case 1:
//                fileInfoList.sort(new Comparator<FileInfo>() {
//                    @Override
//                    public int compare(FileInfo o1, FileInfo o2) {
//                        Long s1 = o1.getSize();
//                        Long s2 = o2.getSize();
//                        return s1.compareTo(s2);
//                    }
//                });
//                break;
//            case 2:
//                fileInfoList.sort(new Comparator<FileInfo>() {
//                    @Override
//                    public int compare(FileInfo info1, FileInfo info2) {
//                        if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
//                            if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                                return info1.getLastModified().compareTo(info2.getLastModified());
//                            } else {
//                                return 1;
//                            }
//                        } else {
//                            if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
//                                return -1;
//                            } else {
//                                return info1.getLastModified().compareTo(info2.getLastModified());
//                            }
//                        }
//                    }
//                });
//                break;
//        }
//        if (!this.sortAsc) {
//            Collections.reverse(fileInfoList);
//        }
//        listModel.removeAllElements();
//        listModel.addAll(fileInfoList);
//    }

    public void setShowHiddenFiles(boolean showHiddenFiles) {
        this.showHiddenFiles = showHiddenFiles;
        applyHiddenFilter();
        //this.resizeColumnWidth(table);
    }

    private void applyHiddenFilter() {
        int arr[] = new int[table.getColumnCount()];
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn col = columnModel.getColumn(column);
            arr[column] = col.getPreferredWidth();
        }

        this.folderViewModel.clear();
        if (!this.showHiddenFiles) {
            List<FileInfo> list2 = new ArrayList<>();
            for (FileInfo info : this.files) {
                if (!info.getName().startsWith(".")) {
                    list2.add(info);
                }
            }
            this.folderViewModel.addAll(list2);
        } else {
            this.folderViewModel.addAll(this.files);
        }

        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn col = columnModel.getColumn(column);
            col.setPreferredWidth(arr[column]);
        }

        //table.setRowSorter(sorter);
    }

//    public int getSortIndex() {
//        return sortIndex;
//    }
//
//    public boolean isSortAsc() {
//        return sortAsc;
//    }
}
