///**
// *
// */
//package snowflake.components.keymanager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.swing.table.AbstractTableModel;
//
//import nixexplorer.TextHolder;
//
///**
// * @author subhro
// *
// */
//public class HostTableModel extends AbstractTableModel {
//
//	private String[] columns = { TextHolder.getString("keygen.colHost"), TextHolder.getString("keygen.colUser"),
//			TextHolder.getString("keygen.colStatus") };
//
//	private List<HostEntry> list = new ArrayList<>();
//
//	@Override
//	public Class<?> getColumnClass(int columnIndex) {
//		return Object.class;
//	}
//
//	@Override
//	public String getColumnName(int column) {
//		return columns[column];
//	}
//
//	@Override
//	public int getRowCount() {
//		return list.size();
//	}
//
//	@Override
//	public int getColumnCount() {
//		return columns.length;
//	}
//
//	@Override
//	public Object getValueAt(int rowIndex, int columnIndex) {
//		HostEntry ent = list.get(rowIndex);
//		switch (columnIndex) {
//		case 0:
//			return ent.getHost();
//		case 1:
//			return ent.getUser();
//		case 2:
//			return ent.getStatus();
//		}
//		return "";
//	}
//
//	public void add(HostEntry e) {
//		list.add(e);
//		fireTableDataChanged();
//	}
//
//	public void remove(int r) {
//		list.remove(r);
//		fireTableDataChanged();
//	}
//
//	public HostEntry get(int index) {
//		return list.get(index);
//	}
//
//	public void remove(HostEntry ent) {
//		list.remove(ent);
//		fireTableDataChanged();
//	}
//
//	public void updateTable() {
//		fireTableDataChanged();
//	}
//
//	public int size() {
//		return list.size();
//	}
//
//	public List<HostEntry> list() {
//		return list;
//	}
//
//}
