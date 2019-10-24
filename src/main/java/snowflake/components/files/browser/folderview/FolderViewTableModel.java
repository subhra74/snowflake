package snowflake.components.files.browser.folderview;

import snowflake.common.FileInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FolderViewTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 7212506492710233442L;
    private List<FileInfo> files = new ArrayList<>();

    private String[] columns = {"Name",
            "Size", "Type",
            "Modified", "Permission",
            "Owner"};

    private boolean local = false;

    public FolderViewTableModel(boolean local) {
        this.local = local;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 3:
                return FileInfo.class;
            case 1:
                return Long.class;
            case 4:
            case 2:
            default:
                return Object.class;
        }
    }

    public void clear() {
//        int rows = files.size();
        files.clear();
//        if (rows > 0) {
//            fireTableRowsDeleted(0, rows-1);
//        }
        fireTableDataChanged();
    }

    public void addAll(List<FileInfo> list) {
        if (list.size() > 0) {
            int sz = files.size();
            files.addAll(list);
            // fireTableDataChanged();
//			if (sz < 0) {
//				sz = 0;
//			}
            //fireTableRowsInserted(sz - 1, sz + list.size() - 1);
            fireTableDataChanged();
        }
    }

    public FileInfo getItemAt(int index) {
        return files.get(index);
    }

    public void add(FileInfo ent) {
        int sz = files.size();
        files.add(ent);
        fireTableRowsInserted(sz, sz);
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
//		switch (column) {
//		case 0:
//			return "Name";
//		case 1:
//			return "Size";
//		case 2:
//			return "Type";
//		case 3:
//			return "Modified";
//		case 4:
//			return "Permission";
//		}
//		return "";
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return local ? 4 : columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        FileInfo ent = files.get(rowIndex);
        switch (columnIndex) {
            case 0:
            case 3:
                return ent;
            case 1:
                return ent.getSize();
            case 2:
                return ent.getType().toString();
            case 4:
                // System.out.println(ent.getPermission() + "");
                return ent.getPermission() + "";
            case 5:
                return ent.getExtra();
        }
        return "";
    }

}
