/**
 * 
 */
package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;

/**
 * @author subhro
 *
 */
public class TabbedPage extends JPanel {
	/**
	 * 
	 */
	private Page page;
	private JLabel lblIcon, lblText;
	private Border selectedBorder = new CompoundBorder(
			new MatteBorder(0, 0, 2, 0,
					App.SKIN.getDefaultSelectionBackground()),
			new EmptyBorder(10, 0, 10, 0));
	private Border normalBorder = new CompoundBorder(
			new MatteBorder(0, 0, 2, 0, App.SKIN.getDefaultBackground()),
			new EmptyBorder(10, 0, 10, 0));

	public TabbedPage(Page page, PageHolder holder) {
		super(new BorderLayout(5, 5));
		this.page = page;
		setBorder(normalBorder);

		lblIcon = new JLabel(page.getIcon());
		lblText = new JLabel(page.getText());

		lblIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				holder.showPage(TabbedPage.this.hashCode() + "");
			}
		});
		lblText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				holder.showPage(TabbedPage.this.hashCode() + "");
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				holder.showPage(TabbedPage.this.hashCode() + "");
			}
		});

		lblIcon.setForeground(App.SKIN.getInfoTextForeground());
		lblText.setForeground(App.SKIN.getInfoTextForeground());

		int prefW = lblText.getPreferredSize().width + 20;

		lblIcon.setHorizontalAlignment(JLabel.CENTER);
		lblText.setHorizontalAlignment(JLabel.CENTER);

		lblIcon.setFont(App.SKIN.getIconFont().deriveFont(24.0f));
		lblText.setFont(App.SKIN.getDefaultFont().deriveFont(12.0f));

		this.add(lblIcon);
		this.add(lblText, BorderLayout.SOUTH);

		this.setPreferredSize(
				new Dimension(prefW, this.getPreferredSize().height));
		this.setMaximumSize(
				new Dimension(prefW, this.getPreferredSize().height));
		this.setMinimumSize(
				new Dimension(prefW, this.getPreferredSize().height));
	}

	public void setSelected(boolean selected) {
		this.setBorder(selected ? selectedBorder : normalBorder);
		this.lblIcon.setForeground(selected ? App.SKIN.getDefaultForeground()
				: App.SKIN.getInfoTextForeground());
		this.lblText.setForeground(selected ? App.SKIN.getDefaultForeground()
				: App.SKIN.getInfoTextForeground());
		this.revalidate();
		this.repaint();
	}

	/**
	 * 
	 */
	public String getText() {
		return lblText.getText();
	}

	/**
	 * @return the page
	 */
	public Page getPage() {
		return page;
	}

	public String getId() {
		return this.hashCode() + "";
	}
}
