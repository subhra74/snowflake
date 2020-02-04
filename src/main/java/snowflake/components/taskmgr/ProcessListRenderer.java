package snowflake.components.taskmgr;

import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ProcessListRenderer extends JLabel implements TableCellRenderer {

    public ProcessListRenderer() {
        setText("HHH");
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        if (column == 3) {
            double mem = ((Float) value) * 1024;
            setText(FormatUtils.humanReadableByteCount((long) mem, true));
        } else {
            setText(value.toString());
        }
        return this;
    }
}
