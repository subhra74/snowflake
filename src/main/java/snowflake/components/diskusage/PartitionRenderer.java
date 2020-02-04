package snowflake.components.diskusage;

import snowflake.utils.FormatUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PartitionRenderer extends JLabel implements TableCellRenderer {

    public PartitionRenderer() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(true);
        setText("/dev");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? UIManager.getColor("nimbusSelectionBackground") : Color.WHITE);
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        if (value instanceof Long) {
            setText(FormatUtils.humanReadableByteCount((long) value, true));
        } else {
            setText(value.toString());
        }
        return this;
    }
}
