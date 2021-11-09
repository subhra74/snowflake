package muonssh.app.ui.components.settings;

import muonssh.app.App;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EditorTableModel extends AbstractTableModel {
	private final List<EditorEntry> list = new ArrayList<>();
	private final String[] cols = { App.bundle.getString("editor_name"), App.bundle.getString("path_executable")};

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public String getColumnName(int column) {
		return column == 0 ? cols[0] : cols[1];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		EditorEntry ent = list.get(rowIndex);
		return columnIndex == 0 ? ent.getName() : ent.getPath();
	}

	public void addEntry(EditorEntry ent) {
		int r = this.list.size();
		this.list.add(ent);
		this.fireTableRowsInserted(r, r);
	}

	public void deleteEntry(int r) {
		this.list.remove(r);
		this.fireTableRowsDeleted(r, r);
	}

	public void addEntries(List<EditorEntry> e) {
		this.list.addAll(e);
		this.fireTableDataChanged();
	}

	public void clear() {
		this.list.clear();
		this.fireTableDataChanged();
	}

	public List<EditorEntry> getEntries() {
		return this.list;
	}

}
