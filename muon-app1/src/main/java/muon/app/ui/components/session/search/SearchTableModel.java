package muon.app.ui.components.session.search;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class SearchTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 7212506492710233442L;
    private List<SearchResult> list = new ArrayList<>();
    private String[] columns = new String[]{
            "File name",
            "Type",
            "Path"};

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public void clear() {
        list.clear();
        fireTableDataChanged();
    }

    public void add(SearchResult res) {
        int index = list.size();
        list.add(res);
        fireTableRowsInserted(index, index);
    }

    public SearchResult getItemAt(int index) {
        return list.get(index);
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        SearchResult ent = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ent.getName();
            case 1:
                return ent.getType();
            case 2:
                return ent.getPath();
        }
        return "";
    }

}

