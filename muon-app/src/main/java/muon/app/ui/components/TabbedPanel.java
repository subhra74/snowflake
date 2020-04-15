package muon.app.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;
import util.FontAwesomeContants;

public class TabbedPanel extends JPanel {
	private Color unselectedBg = App.SKIN.getSelectedTabColor(),
			selectedBg = App.SKIN.getDefaultBackground();// App.SKIN.getDefaultBorderColor();//
															// App.SKIN.getAddressBarSelectionBackground();
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private Box tabHolder;
//	private JPopupMenu popup;
	private Border selectedTabBorder = new CompoundBorder(
			new MatteBorder(2, 0, 0, 0,
					App.SKIN.getDefaultSelectionBackground()),
			new EmptyBorder(10, 15, 10, 15));
	private Border unselectedTabBorder = new CompoundBorder(
			new MatteBorder(2, 0, 0, 0, App.SKIN.getSelectedTabColor()),
			new EmptyBorder(10, 15, 10, 15));

	public TabbedPanel() {
		super(new BorderLayout(), true);
		setOpaque(true);

//		JMenuItem localMenuItem = new JMenuItem("Local file browser");
//		JMenuItem remoteMenuItem = new JMenuItem("Remote file browser");
//
//		popup = new JPopupMenu();
//		popup.add(remoteMenuItem);
//		popup.add(localMenuItem);
//		popup.pack();
//
//		localMenuItem.addActionListener(e -> {
//			callback.accept(NewTabType.LocalTab);
//		});
//
//		remoteMenuItem.addActionListener(e -> {
//			callback.accept(NewTabType.RemoteTab);
//		});

//		tabHolder = new JPanel();
//		BoxLayout boxLayout = new BoxLayout(tabHolder, BoxLayout.LINE_AXIS);

		// tabHolder.setLayout(boxLayout);
		tabHolder = Box.createHorizontalBox();
		tabHolder.setBackground(unselectedBg);
		tabHolder.setOpaque(true);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setOpaque(false);

		JPanel tabTop = new JPanel(new BorderLayout());
		tabTop.setOpaque(true);
		tabTop.setBackground(unselectedBg);
		tabTop.add(tabHolder);
		add(tabTop, BorderLayout.NORTH);
		add(cardPanel);
	}

	public void addTab(String tabTitle, Component body) {
		int index = tabHolder.getComponentCount();
		cardPanel.add(body, body.hashCode() + "");

		JLabel tabLabel = new JLabel(tabTitle);
		tabLabel.setOpaque(true);
		tabLabel.setBorder(unselectedTabBorder);
		tabLabel.setName(body.hashCode() + "");
		tabLabel.putClientProperty("component", body);
		tabHolder.add(tabLabel);

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (int i = 0; i < tabHolder.getComponentCount(); i++) {
					JComponent c = (JComponent) tabHolder.getComponent(i);
					if (c == tabLabel) {
						setSelectedIndex(i);
						break;
					}
				}
			}
		};

		tabLabel.addMouseListener(mouseAdapter);
		setSelectedIndex(index);
	}

	public int getSelectedIndex() {
		for (int i = 0; i < tabHolder.getComponentCount(); i++) {
			JComponent c = (JComponent) tabHolder.getComponent(i);
			if (c.getClientProperty("selected") == Boolean.TRUE) {
				return i;
			}
		}
		return -1;
	}

	public void setSelectedIndex(int n) {
		JComponent c = (JComponent) tabHolder.getComponent(n);
		String id = c.getName();
		cardLayout.show(cardPanel, id);
		for (int i = 0; i < tabHolder.getComponentCount(); i++) {
			JComponent cc = (JComponent) tabHolder.getComponent(i);
			cc.putClientProperty("selected", Boolean.FALSE);
			unselectTabTitle(cc);
		}

		c.putClientProperty("selected", Boolean.TRUE);
		selectTabTitle(c);
	}

	private void selectTabTitle(JComponent c) {
		c.setBorder(selectedTabBorder);
		c.setBackground(selectedBg);
		c.revalidate();
		c.repaint();
	}

	private void unselectTabTitle(JComponent c) {
		c.setBorder(unselectedTabBorder);
		c.setBackground(unselectedBg);
		c.revalidate();
		c.repaint();
	}

	@Deprecated
	@Override
	public Component add(Component comp) {
		// TODO Auto-generated method stub
		return super.add(comp);
	}

//	private void removeTabAt(int index, String name, TabTitleComponent title) {
//		tabHolder.remove(title);
//		cardPanel.remove(title.component);
//		if (index > 0) {
//			setSelectedIndex(index - 1);
//		} else if (cardPanel.getComponentCount() > index) {
//			setSelectedIndex(index);
//		}
//		tabHolder.revalidate();
//		tabHolder.repaint();
//	}

//	public Component getSelectedContent() {
//		for (int i = 0; i < tabHolder.getComponentCount(); i++) {
//			if (tabHolder.getComponent(i) instanceof TabTitleComponent) {
//				TabTitleComponent c = (TabTitleComponent) tabHolder
//						.getComponent(i);
//				if (c.selected) {
//					return c.component;
//				}
//			}
//		}
//		return null;
//	}

}
