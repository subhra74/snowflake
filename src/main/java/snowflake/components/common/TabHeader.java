package snowflake.components.common;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TabHeader extends JPanel {
	private JLabel lblTitle;
	private TabCloseButton btnClose;

	public TabHeader(String title) {
		super(new BorderLayout(10, 10));

		setBorder(new EmptyBorder(4, 5, 2, 0));
		setOpaque(false);
		lblTitle = new JLabel(title);
		add(lblTitle);
		btnClose = new TabCloseButton();
		btnClose.setSelected(true);
//        btnClose = new JLabel();
//        btnClose.setFont(App.getFontAwesomeFont());
//        btnClose.setText("\uf00d");
		add(btnClose, BorderLayout.EAST);

	}

	public JLabel getLblTitle() {
		return lblTitle;
	}

	public TabCloseButton getBtnClose() {
		return btnClose;
	}

	public void setTitle(String text) {
		lblTitle.setText(text);
	}
}
