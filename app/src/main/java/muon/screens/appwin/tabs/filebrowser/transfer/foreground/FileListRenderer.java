package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.dto.file.FileInfo;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FileListRenderer<F> extends JLabel implements ListCellRenderer<FileInfo> {
    private Icon icoFile, icoFolder;

    public FileListRenderer() {
        icoFile = AppUtils.createSVGIcon("file-2-fill.svg", 16, AppTheme.INSTANCE.getListIconColor());
        icoFolder = AppUtils.createSVGIcon("folder-fill.svg", 16, AppTheme.INSTANCE.getListIconColor());
        setIcon(icoFolder);
        setText("Sample text");
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FileInfo> list, FileInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        setIcon(value.isDirectory() ? icoFolder : icoFile);
        setText(value.getName());
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        return this;
    }
}
