package muon.app.ui.components.session.utilpage.services;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ServiceTableCellRenderer extends JLabel implements TableCellRenderer {
    public ServiceTableCellRenderer() {
        setText("HHH");
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value == null ? "" : value.toString());
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        return this;
    }
}
