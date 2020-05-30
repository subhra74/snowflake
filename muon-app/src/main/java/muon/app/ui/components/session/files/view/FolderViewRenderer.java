//package muon.app.ui.components.session.files.view;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//
//import muon.app.App;
//import muon.app.common.FileInfo;
//import muon.app.common.FileType;
//import util.FormatUtils;
//
//import java.awt.*;
//
//public class FolderViewRenderer implements TableCellRenderer {
//	private JLabel label;
//	private JLabel iconLabel;
//	private Font plainFont, boldFont;
//	private JPanel panel;
//	private JLabel textLabel;
////	private FileIcon folderIcon, fileIcon;
//
//	public FolderViewRenderer() {
//		panel = new JPanel(new BorderLayout(5, 5));
//		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
//
//		label = new JLabel();
//		label.setOpaque(true);
//		label.setIconTextGap(5);
//		plainFont = new Font(Font.DIALOG, Font.PLAIN, 14);
//
//		iconLabel = new JLabel();
//		iconLabel.setFont(App.SKIN.getIconFont().deriveFont(Font.PLAIN, 25.f));
//		iconLabel.setForeground(new Color(92, 167, 25));
//		// iconLabel.setBorder(new EmptyBorder(5,5,5,5));
//		iconLabel.setHorizontalAlignment(JLabel.CENTER);
//		panel.add(label);
//		panel.add(iconLabel, BorderLayout.WEST);
//
//		label.setFont(plainFont);
//
//		textLabel = new JLabel();
//		textLabel.setFont(plainFont);
//
//		iconLabel.setText("\uf114");
//		Dimension d1 = iconLabel.getPreferredSize();
//		iconLabel.setText("\uf016");
//		Dimension d2 = iconLabel.getPreferredSize();
//
//		int iconW = Math.max(d1.width, d2.width);
//		int iconH = Math.max(d1.height, d2.height);
//
//		// Dimension dm = new Dimension(iconW + 10, iconH);
//
////        iconLabel.setPreferredSize(dm);
////        iconLabel.setMinimumSize(dm);
////        iconLabel.setMaximumSize(dm);
//
////		label.setBorder(new CompoundBorder(
////				new MatteBorder(0, Utility.toPixel(0), Utility.toPixel(0), 0, UIManager.getColor("Panel.background")),
////				new EmptyBorder(0, Utility.toPixel(10), 0, 0)));
////		folderIcon = new FileIcon(UIManager.getIcon("ListView.smallFolder"),
////				true);
////		fileIcon = new FileIcon(UIManager.getIcon("ListView.smallFile"), true);
//	}
//
//	public int getPreferredHeight() {
//		iconLabel.setText("\uf114");
//		Dimension d1 = iconLabel.getPreferredSize();
//		iconLabel.setText("\uf016");
//		Dimension d2 = iconLabel.getPreferredSize();
//
//		int iconW = Math.max(d1.width, d2.width);
//		int iconH = Math.max(d1.height, d2.height);
//
//		iconLabel.setPreferredSize(new Dimension(iconW, iconH));
//
//		int h1 = getPrefHeight();
//		label.setText("ABC");
//		int h2 = getPrefHeight();
//		return Math.max(h1, h2);
//	}
//
//	public Component getTableCellRendererComponent(JTable table, Object value,
//			boolean isSelected, boolean hasFocus, int row, int column) {
//		FolderViewTableModel folderViewModel = (FolderViewTableModel) table
//				.getModel();
//		int r = table.convertRowIndexToModel(row);
//		int c = table.convertColumnIndexToModel(column);
//		FileInfo ent = folderViewModel.getItemAt(r);
//
//		panel.setBackground(
//				isSelected ? table.getSelectionBackground() : Color.WHITE);
//		label.setBackground(
//				isSelected ? table.getSelectionBackground() : Color.WHITE);
//		label.setForeground(Color.BLACK);
//
//		switch (c) {
//		case 0:
//			iconLabel.setText(ent.getType() == FileType.Directory
//					|| ent.getType() == FileType.DirLink ? "\uf07b" : "\uf016");
////                label.setIcon(ent.getType() == FileType.Directory || ent.getType() == FileType.DirLink ?
////                        (Icon) UIManager.get("FileChooser.directoryIcon") :
////                        (Icon) UIManager.get("FileChooser.fileIcon"));
//			label.setText(ent.getName());
//			return panel;
//		case 1:
//			iconLabel.setText("");
//			if (ent.getType() == FileType.Directory
//					|| ent.getType() == FileType.DirLink) {
//				label.setText("");
//			} else {
//				label.setText(FormatUtils.humanReadableByteCount(ent.getSize(),
//						true));
//			}
//			return label;
//		case 2:
//			iconLabel.setText("");
//			label.setText(ent.getType() + "");
//			return label;
//		case 3:
//			iconLabel.setText("");
//			label.setText(FormatUtils.formatDate(ent.getLastModified()));
//			return label;
//		case 4:
//			iconLabel.setText("");
//			label.setText(ent.getPermissionString());
//			return label;
//		case 5:
//			iconLabel.setText("");
//			label.setText(ent.getUser());
//			return label;
//		default:
//			break;
//		}
//
//		return label;
////        iconLabel.setForeground(isSelected ? table.getSelectionForeground()
////                : table.getForeground());
//
//	}
//
//	public int getPrefHeight() {
//		return panel.getPreferredSize().height;
//	}
//
//}
