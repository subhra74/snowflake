package snowflake.components.files.browser.folderview;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableCellLabelRenderer implements TableCellRenderer {
    private JPanel panel;
    private JLabel textLabel;
    private JLabel iconLabel;
    private JLabel label;
    private int height;
    private Color foreground;

    public TableCellLabelRenderer() {
        foreground = new Color(100, 100, 100);
        panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        textLabel = new JLabel();
        textLabel.setForeground(foreground);
        textLabel.setText("AAA");
        textLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        iconLabel = new JLabel();
        iconLabel.setFont(App.getFontAwesomeFont().deriveFont(Font.PLAIN, 25.f));
        iconLabel.setText("\uf016");
        iconLabel.setForeground(foreground);
        //iconLabel.setForeground(new Color(92, 167, 25));

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
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        FolderViewTableModel folderViewModel = (FolderViewTableModel) table
                .getModel();
        int r = table.convertRowIndexToModel(row);
        int c = table.convertColumnIndexToModel(column);
        FileInfo ent = folderViewModel.getItemAt(r);

        panel.setBackground(isSelected ? table.getSelectionBackground()
                : Color.WHITE);

        textLabel.setForeground(isSelected ? table.getSelectionForeground() : foreground);
        iconLabel.setForeground(isSelected ? table.getSelectionForeground() : foreground);
        iconLabel.setText(ent.getType() == FileType.Directory || ent.getType() == FileType.DirLink
                ? "\uf07b" : "\uf016");
        textLabel.setText(ent.getName());

        label.setBackground(isSelected ? table.getSelectionBackground()
                : Color.WHITE);
        label.setForeground(isSelected ? table.getSelectionForeground() : foreground);

        switch (c) {
            case 0:
                label.setText("");
                break;
            case 1:
                if (ent.getType() == FileType.Directory
                        || ent.getType() == FileType.DirLink) {
                    label.setText("");
                } else {
                    label.setText(
                            FormatUtils.humanReadableByteCount(ent.getSize(), true));
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
}
