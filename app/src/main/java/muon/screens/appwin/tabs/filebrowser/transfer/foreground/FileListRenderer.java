package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.dto.file.FileInfo;
import muon.styles.AppTheme;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FileListRenderer<F> implements ListCellRenderer<FileInfo> {
    private JPanel panel1, panel2;
    private JLabel lblIcon, lblText, lblInfo;

    public FileListRenderer() {
        panel1 = new JPanel(new BorderLayout(5, 10));
        panel1.setBorder(new EmptyBorder(0, 0, 5, 0));
        lblIcon = new JLabel();
        lblIcon.setForeground(AppTheme.INSTANCE.getDarkForeground());
        lblIcon.setFont(IconFont.getSharedInstance().getIconFont(32.0f));

        lblText = new JLabel();
        lblText.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblText.setForeground(AppTheme.INSTANCE.getForeground());

        lblInfo = new JLabel();
        lblInfo.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
        lblInfo.setForeground(AppTheme.INSTANCE.getDarkForeground());

        panel2 = new JPanel(new BorderLayout(5, 0));

        panel2.add(lblText, BorderLayout.NORTH);
        panel2.add(lblInfo);

        panel1.add(lblIcon, BorderLayout.WEST);
        panel1.add(panel2);

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FileInfo> list, FileInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        lblIcon.setText(value.isDirectory() ? IconCode.RI_FOLDER_FILL.getValue() : IconCode.RI_FILE_FILL.getValue());
        lblText.setText(value.getName());
        lblInfo.setText(value.isDirectory() ? "Folder" : value.getSize() + "");
        return panel1;
    }
}
