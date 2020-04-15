package muon.app.ui.components.session.utilpage.portview;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SocketTableModel extends AbstractTableModel {
    private String columns[] = {"Process", "PID", "Host", "Port"};
    private List<SocketEntry> list = new ArrayList<>();

    public void addEntry(SocketEntry e) {
        list.add(e);
        fireTableDataChanged();
    }

    public void addEntries(List<SocketEntry> entries) {
        if (entries != null) {
            list.addAll(entries);
            fireTableDataChanged();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
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
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SocketEntry e = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return e.getApp();
            case 1:
                return e.getPid();
            case 2:
                return e.getHost();
            case 3:
                return e.getPort();
            default:
                return "";
        }
    }

    public void clear() {
        list.clear();
    }
}