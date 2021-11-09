package muonssh.app.ui.components.session.diskspace;

import muonssh.app.App;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PartitionTableModel extends AbstractTableModel {
    private final String[] columns = {App.bundle.getString("filesystem"), App.bundle.getString("total_size"), App.bundle.getString("used"), App.bundle.getString("available"), App.bundle.getString("percentage_use"), App.bundle.getString("mount_point")};

    private final List<PartitionEntry> list = new ArrayList<>();

    public PartitionEntry get(int index) {
        return list.get(index);
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PartitionEntry ent = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ent.getFileSystem();
            case 1:
                return ent.getTotalSize();
            case 2:
                return ent.getUsed();
            case 3:
                return ent.getAvailable();
            case 4:
                return ent.getUsedPercent();
            case 5:
                return ent.getMountPoint();
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public void clear() {
        this.list.clear();
        fireTableDataChanged();
    }

    public void add(PartitionEntry ent) {
        list.add(ent);
        fireTableDataChanged();
    }

    public void add(List<PartitionEntry> ents) {
        list.addAll(ents);
        fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 2:
            case 3:
                return Long.class;
            case 4:
                return Double.class;
            default:
                return String.class;
        }
    }
}
