package muon.screens.sessiontabs.filebrowser;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FolderViewTableModel extends AbstractTableModel {
    private List<FileInfo> files = new ArrayList<>();
    private String[] columns = {"Name", "Size", "Date"};
    private boolean local = false;

    public FolderViewTableModel(boolean local) {
        this.local = local;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    public void clear() {
        files.clear();
        fireTableDataChanged();
    }

    public void addAll(List<FileInfo> list) {
        if (list.size() > 0) {
            int sz = files.size();
            files.addAll(list);
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
    }

    public int getRowCount() {
        return files.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        var ent = files.get(rowIndex);
        return ent;
    }
}
