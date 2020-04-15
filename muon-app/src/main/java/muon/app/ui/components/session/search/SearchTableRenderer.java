package muon.app.ui.components.session.search;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import muon.app.App;

import java.awt.*;

public class SearchTableRenderer implements TableCellRenderer {

	private JLabel label;
	private JPanel panel;
	private SearchTableModel model;
	private JLabel textLabel, iconLabel;

	public SearchTableRenderer(SearchTableModel model) {
		this.model = model;
		this.label = new JLabel();
		this.label.setOpaque(true);
		this.panel = new JPanel(new BorderLayout(5, 5));
		this.textLabel = new JLabel();
		this.iconLabel = new JLabel();
		this.iconLabel
				.setFont(App.SKIN.getIconFont().deriveFont(Font.PLAIN, 20));
		panel.add(iconLabel, BorderLayout.WEST);
		panel.add(textLabel);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		SearchResult ent = this.model.getItemAt(row);
		if (column == 0) {
			iconLabel.setText(ent.getType() == "Folder" ? "\uf114"
					: (ent.getType() == "File" ? "\uf016" : "\uf0c1"));
			textLabel.setText(ent.getName());
		} else {
			label.setText(value.toString());
		}

		label.setForeground(isSelected ? table.getSelectionForeground()
				: table.getForeground());
		textLabel.setForeground(isSelected ? table.getSelectionForeground()
				: table.getForeground());
		iconLabel.setForeground(isSelected ? table.getSelectionForeground()
				: table.getForeground());
		label.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		panel.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		return column == 0 ? panel : label;
	}

}
