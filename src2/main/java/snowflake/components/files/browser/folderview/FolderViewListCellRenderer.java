package snowflake.components.files.browser.folderview;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileType;
import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class FolderViewListCellRenderer implements ListCellRenderer<FileInfo> {
    JPanel panel = new JPanel(new BorderLayout(10, 5));
    JLabel lblTitle, lx1, lx2;
    Color lightText = Color.GRAY, lightTextSelected = new Color(230, 230, 230);
    JLabel lblIcon;

    public FolderViewListCellRenderer() {
         lblIcon = new JLabel();
        lblIcon.setBorder(new EmptyBorder(5, 5, 5, 5));
        lblIcon.setText("\uf1c6");
        lblIcon.setFont(App.getFontAwesomeFont().deriveFont(Font.PLAIN, 25.f));
        lblIcon.setHorizontalAlignment(JLabel.CENTER);
        lblIcon.setVerticalAlignment(JLabel.CENTER);
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setBackground(new Color(92, 167, 25));
        lblIcon.setOpaque(true);
//        lblIcon.setMinimumSize(new Dimension(40, 40));
//        lblIcon.setPreferredSize(new Dimension(40, 40));

        lblTitle = new JLabel();
        lblTitle.setForeground(Color.DARK_GRAY);
        lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);

        Box b32 = Box.createHorizontalBox();
        lx1 = new JLabel();
        lx1.setForeground(Color.GRAY);
        lx2 = new JLabel();
        lx2.setForeground(Color.GRAY);

        b32.add(lx1);
        b32.add(Box.createHorizontalGlue());
        b32.add(lx2);
        b32.setAlignmentX(Box.LEFT_ALIGNMENT);

        panel.add(lblIcon, BorderLayout.WEST);
        panel.setBackground(new Color(3, 155, 229));
        Box b43 = Box.createVerticalBox();
        b43.add(Box.createVerticalGlue());
        b43.add(lblTitle);
        b43.add(b32);
        b43.add(Box.createVerticalGlue());

        panel.add(b43);

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }


    @Override
    public Component getListCellRendererComponent(JList<? extends FileInfo> list, FileInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        panel.setOpaque(isSelected);
        lblTitle.setText(value.getName());
        if (value.getType() == FileType.Directory
                || value.getType() == FileType.DirLink) {
            lblIcon.setText("\uf07b");
            lx1.setText(FormatUtils.formatDate(value.getLastModified()));
        } else {
            lblIcon.setText("\uf016");
            lx1.setText(FormatUtils.humanReadableByteCount(value.getSize(), true)
                    + " - " + FormatUtils.formatDate(value.getLastModified()));
        }

        lx2.setText(value.getUser()
                + " - " + value.getPermissionString());

        if (isSelected) {
            lblTitle.setForeground(Color.WHITE);
            lx1.setForeground(lightTextSelected);
            lx2.setForeground(lightTextSelected);
        } else {
            lblTitle.setForeground(Color.DARK_GRAY);
            lx1.setForeground(lightText);
            lx2.setForeground(lightText);
        }

        return panel;
    }
}
