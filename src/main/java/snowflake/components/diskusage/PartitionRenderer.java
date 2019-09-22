package snowflake.components.diskusage;

import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PartitionRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setOpaque(true);
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        if (value instanceof Long) {
            setText(FormatUtils.humanReadableByteCount((long) value, true));
        } else {
            setText(value.toString());
        }
        return this;
    }
}
