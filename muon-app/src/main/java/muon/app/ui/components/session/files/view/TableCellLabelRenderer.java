package muon.app.ui.components.session.files.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import muon.app.App;
import muon.app.common.FileInfo;
import muon.app.common.FileType;
import util.FormatUtils;

public class TableCellLabelRenderer implements TableCellRenderer {
	private JPanel panel;
	private JLabel textLabel;
	private JLabel iconLabel;
	private JLabel label;
	private int height;
	private Color foreground;

	public TableCellLabelRenderer() {
		foreground = App.SKIN.getInfoTextForeground();
		panel = new JPanel(new BorderLayout(10, 5));
		panel.setBorder(new EmptyBorder(5, 10, 5, 5));
		textLabel = new JLabel();
		textLabel.setForeground(foreground);
		textLabel.setText("AAA");
		textLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

		iconLabel = new JLabel();
		iconLabel.setFont(App.SKIN.getIconFont().deriveFont(Font.PLAIN, 20.f));
		iconLabel.setText("\uf016");
		iconLabel.setForeground(foreground);
		// iconLabel.setForeground(new Color(92, 167, 25));

		Dimension d1 = iconLabel.getPreferredSize();
		iconLabel.setText("\uf07b");
		Dimension d2 = iconLabel.getPreferredSize();

		height = Math.max(d1.height, d2.height) + 10;

		iconLabel.setHorizontalAlignment(JLabel.CENTER);
		panel.add(textLabel);
		panel.add(iconLabel, BorderLayout.WEST);

		panel.doLayout();

		System.out.println(panel.getPreferredSize());

		label = new JLabel();
		label.setForeground(foreground);
		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		label.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
		label.setOpaque(true);
	}

	public int getHeight() {
		return height;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		FolderViewTableModel folderViewModel = (FolderViewTableModel) table
				.getModel();
		int r = table.convertRowIndexToModel(row);
		int c = table.convertColumnIndexToModel(column);
		FileInfo ent = folderViewModel.getItemAt(r);

		panel.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());

		textLabel.setForeground(
				isSelected ? table.getSelectionForeground() : foreground);
		iconLabel.setForeground(
				isSelected ? table.getSelectionForeground() : foreground);
		iconLabel.setText(getIconForType(ent));
		textLabel.setText(ent.getName());

		label.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		label.setForeground(
				isSelected ? table.getSelectionForeground() : foreground);

		switch (c) {
		case 0:
			label.setText("");
			break;
		case 1:
			if (ent.getType() == FileType.Directory
					|| ent.getType() == FileType.DirLink) {
				label.setText("");
			} else {
				label.setText(FormatUtils.humanReadableByteCount(ent.getSize(),
						true));
			}
			break;
		case 2:
			label.setText(ent.getType() + "");
			break;
		case 3:
			label.setText(FormatUtils.formatDate(ent.getLastModified()));
			break;
		case 4:
			label.setText(ent.getPermissionString());
			break;
		case 5:
			label.setText(ent.getUser());
			break;
		default:
			break;
		}

		if (column == 0) {
			return panel;
		} else {
			return label;
		}

	}

	public String getIconForType(FileInfo ent) {
		if (ent.getType() == FileType.Directory
				|| ent.getType() == FileType.DirLink) {
			return "\uf07b";
		}
		String name = ent.getName().toLowerCase(Locale.ENGLISH);
		if (name.endsWith(".zip") || name.endsWith(".tar")
				|| name.endsWith(".tgz") || name.endsWith(".gz")
				|| name.endsWith(".bz2") || name.endsWith(".tbz2")
				|| name.endsWith(".tbz") || name.endsWith(".txz")
				|| name.endsWith(".xz")) {
			return "\uf1c6";
		} else if (name.endsWith(".mp3") || name.endsWith(".aac")
				|| name.endsWith(".mp2") || name.endsWith(".wav")
				|| name.endsWith(".flac") || name.endsWith(".mpa")
				|| name.endsWith(".m4a")) {
			return "\uf1c7";
		} else if (name.endsWith(".c") || name.endsWith(".js")
				|| name.endsWith(".cpp") || name.endsWith(".java")
				|| name.endsWith(".cs") || name.endsWith(".py")
				|| name.endsWith(".pl") || name.endsWith(".rb")
				|| name.endsWith(".sql") || name.endsWith(".go")
				|| name.endsWith(".ksh") || name.endsWith(".css")
				|| name.endsWith(".scss") || name.endsWith(".html")
				|| name.endsWith(".htm") || name.endsWith(".ts")) {
			return "\uf1c9";
		} else if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
			return "\uf1c3";
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")
				|| name.endsWith(".png") || name.endsWith(".ico")
				|| name.endsWith(".gif") || name.endsWith(".svg")) {
			return "\uf1c5";
		} else if (name.endsWith(".mp4") || name.endsWith(".mkv")
				|| name.endsWith(".m4v") || name.endsWith(".avi")) {
			return "\uf1c8";
		} else if (name.endsWith(".pdf")) {
			return "\uf1c1";
		} else if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
			return "\uf1c4";
		} else if (name.endsWith(".doc") || name.endsWith(".docx")) {
			return "\uf1c2";
		}
		return "\uf016";
	}
}
