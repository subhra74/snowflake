package muon.app.ui.components.session.diskspace;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class UsagePercentageRenderer extends JPanel
		implements TableCellRenderer {
	private JProgressBar progressBar;

	public UsagePercentageRenderer() {
		super(new BorderLayout());
		progressBar = new JProgressBar(0, 100);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		add(progressBar);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		setForeground(isSelected ? table.getSelectionForeground()
				: table.getForeground());
		double pct = (Double) value;
		if (pct > 100) {
			pct = 100;
		} else if (pct < 0) {
			pct = 0;
		}
		progressBar.setValue((int) pct);
		return this;
	}
}
