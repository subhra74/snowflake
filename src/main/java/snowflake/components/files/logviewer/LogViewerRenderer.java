package snowflake.components.files.logviewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class LogViewerRenderer extends JLabel implements TableCellRenderer {
    public LogViewerRenderer() {
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        setOpaque(true);
        setText("the quick brown fox jumped over the lazy dog. THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG.");
        setBorder(new EmptyBorder(3, 3, 3, 3));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null)
            setText(value + "");
        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
        return this;
    }
}
