package snowflake.components.files.browser.folderview;

import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

public class FolderViewRenderer implements TableCellRenderer {
    private JLabel label;
    private Font plainFont, boldFont;
//	private FileIcon folderIcon, fileIcon;

    public FolderViewRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(3, 5,
                3, 0));
        label.setIconTextGap(5);
        plainFont = new Font(Font.DIALOG, Font.PLAIN, 14);
        boldFont = new Font(Font.DIALOG, Font.BOLD, 14);

//		label.setBorder(new CompoundBorder(
//				new MatteBorder(0, Utility.toPixel(0), Utility.toPixel(0), 0, UIManager.getColor("Panel.background")),
//				new EmptyBorder(0, Utility.toPixel(10), 0, 0)));
//		folderIcon = new FileIcon(UIManager.getIcon("ListView.smallFolder"),
//				true);
//		fileIcon = new FileIcon(UIManager.getIcon("ListView.smallFile"), true);
    }

    public int getPreferredHeight() {
        label.setIcon((Icon) UIManager.get("FileChooser.directoryIcon"));
        int h1 = getPrefHeight();
        label.setText("ABC");
        int h2 = getPrefHeight();
        return Math.max(h1, h2);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        FolderViewTableModel folderViewModel = (FolderViewTableModel) table
                .getModel();
        int r = table.convertRowIndexToModel(row);
        int c = table.convertColumnIndexToModel(column);
        FileInfo ent = folderViewModel.getItemAt(r);
        label.setFont(plainFont);
        switch (c) {
            case 0:
                label.setIcon(ent.getType() == FileType.Directory || ent.getType() == FileType.DirLink ?
                        (Icon) UIManager.get("FileChooser.directoryIcon") :
                        (Icon) UIManager.get("FileChooser.fileIcon"));
                label.setText(ent.getName());
                break;
            case 1:
                label.setIcon(null);
                if (ent.getType() == FileType.Directory
                        || ent.getType() == FileType.DirLink) {
                    label.setText("");
                } else {
                    label.setText(
                            FormatUtils.humanReadableByteCount(ent.getSize(), true));
                }
                break;
            case 2:
                label.setIcon(null);
                label.setText(ent.getType() + "");
                break;
            case 3:
                label.setIcon(null);
                label.setText(FormatUtils.formatDate(ent.getLastModified()));
                break;
            case 4:
                label.setIcon(null);
                label.setText(ent.getPermissionString());
                break;
            case 5:
                label.setIcon(null);
                label.setText(ent.getUser());
                break;
            default:
                break;
        }

        label.setBackground(isSelected ? table.getSelectionBackground()
                : table.getBackground());
        label.setForeground(isSelected ? table.getSelectionForeground()
                : table.getForeground());
        return label;
    }

    public int getPrefHeight() {
        return label.getPreferredSize().height;
    }

}

