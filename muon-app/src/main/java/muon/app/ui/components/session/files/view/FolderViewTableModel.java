package muon.app.ui.components.session.files.view;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import muon.app.common.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class FolderViewTableModel extends AbstractTableModel implements ListModel<FileInfo> {

	private static final long serialVersionUID = 7212506492710233442L;
	private List<FileInfo> files = new ArrayList<>();

	protected EventListenerList listenerList = new EventListenerList();

//	private String[] columns = { "Name", "Size", "Type", "Modified",
//			"Permission", "Owner" };
	private String[] columns = { "Name", "Modified", "Size", "Type", "Permission", "Owner" };

	private boolean local = false;

	public FolderViewTableModel(boolean local) {
		this.local = local;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return FileInfo.class;
//		switch (columnIndex) {
//		case 0:
//		case 1:
//			return FileInfo.class;
//		case 2:
//			return Long.class;
//		default:
//			return Object.class;
//		}
	}

//	@Override
//	public Class<?> getColumnClass(int columnIndex) {
//		switch (columnIndex) {
//		case 0:
//		case 3:
//			return FileInfo.class;
//		case 1:
//			return Long.class;
//		case 4:
//		case 2:
//		default:
//			return Object.class;
//		}
//	}

	public void clear() {
		int rows = files.size();
		files.clear();
//        if (rows > 0) {
//            fireTableRowsDeleted(0, rows-1);
//        }
		fireTableDataChanged();
		fireContentsChanged(this, 0, rows - 1);
	}

	public void addAll(List<FileInfo> list) {
		if (list.size() > 0) {
			int sz = files.size();
			files.addAll(list);
			// fireTableDataChanged();
//			if (sz < 0) {
//				sz = 0;
//			}
			// fireTableRowsInserted(sz - 1, sz + list.size() - 1);
			fireTableDataChanged();
			fireContentsChanged(this, 0, sz - 1);
		}

	}

	public FileInfo getItemAt(int index) {
		return files.get(index);
	}

	public void add(FileInfo ent) {
		int sz = files.size();
		files.add(ent);
		fireTableRowsInserted(sz, sz);
		fireContentsChanged(this, 0, sz - 1);
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

	// private String[] columns = { "Name", "Modified", "Size", "Type",
	// "Permission", "Owner" };
	public Object getValueAt(int rowIndex, int columnIndex) {
		FileInfo ent = files.get(rowIndex);
		return ent;
//		switch (columnIndex) {
//		case 0:
//		case 1:
//			return ent;
//		case 2:
//			return ent.getSize();
//		case 3:
//			return ent.getType().toString();
//		case 4:
//			return ent.getPermission() + "";
//		case 5:
//			return ent.getExtra();
//		}
//		return "";
	}

	@Override
	public int getSize() {
		return files.size();
	}

	@Override
	public FileInfo getElementAt(int index) {
		return files.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		System.out.println("addListDataListener");
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	public ListDataListener[] getListDataListeners() {
		return listenerList.getListeners(ListDataListener.class);
	}

	protected void fireContentsChanged(Object source, int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).contentsChanged(e);
			}
		}
	}

	protected void fireIntervalAdded(Object source, int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalAdded(e);
			}
		}
	}

	protected void fireIntervalRemoved(Object source, int index0, int index1) {
		Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).intervalRemoved(e);
			}
		}
	}

}
