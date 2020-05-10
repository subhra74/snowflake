package muon.app.ui.components.session.processview;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProcessTableModel extends AbstractTableModel {
    private String[] columns = {"Command", "PID", "CPU", "Memory", "Time", "PPID", "User", "Nice"};
    private List<ProcessTableEntry> processList = new ArrayList<>();


    public ProcessTableEntry get(int index) {
        return processList.get(index);
    }

    public List<ProcessTableEntry> getProcessList() {
        return processList;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public int getRowCount() {
        return processList.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProcessTableEntry ent = processList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ent.getArgs();
            case 1:
                return ent.getPid();
            case 2:
                return ent.getCpu();
            case 3:
                return ent.getMemory();
            case 4:
                return ent.getTime();
            case 5:
                return ent.getPpid();
            case 6:
                return ent.getUser();
            case 7:
                return ent.getNice();
        }
        return "-";
    }

    public void setProcessList(List<ProcessTableEntry> list) {
        this.processList = list;
        this.fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 4:
            case 6:
                return Object.class;
            case 1:
            case 5:
            case 7:
                return Integer.class;
            case 2:
            case 3:
                return Float.class;
        }
        return Object.class;
    }
}
