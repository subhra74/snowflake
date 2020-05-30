//package muon.app.ui.components.session.files.view;
//
//import javax.swing.*;
//import javax.swing.table.TableCellRenderer;
//
//import muon.app.common.FileInfo;
//import muon.app.common.FileType;
//import util.FormatUtils;
//
//import java.awt.*;
//
//public class TableCellTextRenderer extends JLabel implements TableCellRenderer {
//	public TableCellTextRenderer() {
//
//	}
//
//	@Override
//	public Component getTableCellRendererComponent(JTable table, Object value,
//			boolean isSelected, boolean hasFocus, int row, int column) {
//		FolderViewTableModel folderViewModel = (FolderViewTableModel) table
//				.getModel();
//		int r = table.convertRowIndexToModel(row);
//		int c = table.convertColumnIndexToModel(column);
//		FileInfo ent = folderViewModel.getItemAt(r);
//		setBackground(
//				isSelected ? table.getSelectionBackground() : Color.WHITE);
//		setForeground(isSelected ? table.getSelectionForeground()
//				: table.getForeground());
//
//		switch (c) {
//		case 0:
//			setText("");
//			break;
//		case 1:
//			if (ent.getType() == FileType.Directory
//					|| ent.getType() == FileType.DirLink) {
//				setText("");
//			} else {
//				setText(FormatUtils.humanReadableByteCount(ent.getSize(),
//						true));
//			}
//			break;
//		case 2:
//			setText(ent.getType() + "");
//			break;
//		case 3:
//			setText(FormatUtils.formatDate(ent.getLastModified()));
//			break;
//		case 4:
//			setText(ent.getPermissionString());
//			break;
//		case 5:
//			setText(ent.getUser());
//			break;
//		default:
//			break;
//		}
//
//		return this;
//	}
//}
