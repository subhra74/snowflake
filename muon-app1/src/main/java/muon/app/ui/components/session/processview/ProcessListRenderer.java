package muon.app.ui.components.session.processview;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import util.FormatUtils;

public class ProcessListRenderer extends JLabel implements TableCellRenderer {

	public ProcessListRenderer() {
		setText("HHH");
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		setForeground(isSelected ? table.getSelectionForeground()
				: table.getForeground());
		if (column == 3) {
			double mem = ((Float) value) * 1024;
			setText(FormatUtils.humanReadableByteCount((long) mem, true));
		} else {
			setText(value.toString());
		}
		return this;
	}
}
