package muon.screens.appwin.tabs.filebrowser;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import muon.dto.file.FileInfo;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;

public class FolderViewTableCellRenderer implements TableCellRenderer {
    private JPanel p1;
    private JLabel iconLbl;
    private JLabel label1, label2;
    private Color background, selectionBackground, iconForeground, selectionForeground, foreground;
    private ImageIcon folderIcon, fileIcon;

    public FolderViewTableCellRenderer(Color background,
                                       Color selectionBackground,
                                       Color iconForeground,
                                       Color selectionForeground,
                                       Color foreground) {
        p1 = new JPanel(new BorderLayout());

        this.background = background;
        this.selectionBackground = selectionBackground;
        this.iconForeground = iconForeground;
        this.selectionForeground = selectionForeground;
        this.foreground = foreground;

        FlatSVGIcon.ColorFilter filter = new FlatSVGIcon.ColorFilter();
        filter.add(Color.BLACK, AppTheme.INSTANCE.getListIconColor());
        try {
            var icon1 = new FlatSVGIcon(getClass().getResourceAsStream("/icons/folder-fill.svg"));
            icon1.setColorFilter(filter);
            folderIcon = icon1.derive(24, 24);

            var icon2 = new FlatSVGIcon(getClass().getResourceAsStream("/icons/file-2-fill.svg"));
            icon2.setColorFilter(filter);
            fileIcon = icon2.derive(24, 24);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        iconLbl = createIconLabel();
        label1 = createTextLabel();
        label1.setBorder(new EmptyBorder(5, 7, 5, 10));
        label2 = createTextLabel();
        label2.setBorder(new EmptyBorder(5, 10, 5, 10));
        label2.setOpaque(true);

        p1.add(iconLbl, BorderLayout.WEST);
        p1.add(label1);
    }

    private JLabel createIconLabel() {
        var iconLbl = new JLabel();
        iconLbl.setVerticalAlignment(JLabel.CENTER);
        iconLbl.setVerticalTextPosition(JLabel.CENTER);
        iconLbl.setBorder(new EmptyBorder(4, 10, 4, 0));
        iconLbl.setFont(IconFont.getSharedInstance().getIconFont(24.0f));
        iconLbl.setForeground(iconForeground);
        return iconLbl;
    }

    private JLabel createTextLabel() {
        var label = new JLabel();
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        return label;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        var fileInfo = (FileInfo) value;
        var col = table.convertColumnIndexToModel(column);
        if (col == 0) {
            iconLbl.setIcon(fileInfo.isDirectory()
                    ? folderIcon
                    : fileIcon);
            label1.setText(fileInfo.getName());
            p1.setBackground(isSelected ? selectionBackground : background);
            label1.setForeground(isSelected ? selectionForeground : foreground);
            return p1;
        } else {
            var str = "";
            switch (col) {
                case 1:
                    str = fileInfo.isDirectory() ? "" :
                            AppUtils.formatSize(fileInfo.getSize(), false);
                    break;
                case 3:
                    str = fileInfo.getFileType().toString();
                    break;
                case 2:
                    str = AppUtils.formatDate(fileInfo.getModificationDate());
                    break;
                case 4:
                    str = fileInfo.getPermissionString();
                    break;
                case 5:
                    str = fileInfo.getOwner();
                    break;
            }
            label2.setText(str);
            label2.setBackground(isSelected ? selectionBackground : background);
            label2.setForeground(isSelected ? selectionForeground : foreground);
            return label2;
        }
    }

    public int calculateRowHeight() {
        iconLbl.setIcon(folderIcon);
        label1.setText("some text");
        return p1.getPreferredSize().height;
    }
}
