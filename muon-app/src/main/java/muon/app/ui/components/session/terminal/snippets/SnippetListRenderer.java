package muon.app.ui.components.session.terminal.snippets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import muon.app.App;

public class SnippetListRenderer extends JPanel
		implements ListCellRenderer<SnippetItem> {
	private JLabel lblName;
	private JLabel lblCommand;

	public SnippetListRenderer() {
		super(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(5, 10, 5, 10));
		lblName = new JLabel();
		lblName.setFont(lblName.getFont().deriveFont(Font.PLAIN, 14.0f));
		lblCommand = new JLabel();
		add(lblName);
		add(lblCommand, BorderLayout.SOUTH);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends SnippetItem> list, SnippetItem value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setBackground(isSelected ? new Color(3, 155, 229)
				: list.getBackground());
		lblName.setForeground(
				isSelected ? App.SKIN.getDefaultSelectionForeground()
						: App.SKIN.getDefaultForeground());
		lblCommand.setForeground(App.SKIN.getInfoTextForeground());
		lblName.setText(value.getName());
		lblCommand.setText(value.getCommand());
		return this;
	}
}
