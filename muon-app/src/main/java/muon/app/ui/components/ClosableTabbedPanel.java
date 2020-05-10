package muon.app.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;
import util.FontAwesomeContants;

public class ClosableTabbedPanel extends JPanel {
	private Color unselectedBg = App.SKIN.getSelectedTabColor(),
			selectedBg = App.SKIN.getDefaultBackground();// App.SKIN.getDefaultBorderColor();//
															// App.SKIN.getAddressBarSelectionBackground();
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private JPanel tabHolder;
	private JPanel buttonsBox;
//	private JPopupMenu popup;
//	private Border selectedTabBorder = new MatteBorder(2, 0, 0, 0,
//			App.SKIN.getDefaultSelectionBackground());
//	private Border unselectedTabBorder = new MatteBorder(2, 0, 0, 0,
//			App.SKIN.getDefaultBackground());

	/**
	 * Create a tabbed pane with closable tabs
	 * @param newTabCallback Called whenever new tab button is clicked
	 */
	public ClosableTabbedPanel(final Consumer<JButton> newTabCallback) {
		super(new BorderLayout(0, 0), true);
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
		tabHolder = new JPanel(new GridLayout(1, 0, 0, 0));
		// tabHolder.setBackground(unselectedBg);
		tabHolder.setOpaque(true);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);
		cardPanel.setOpaque(false);

		JPanel tabTop = new JPanel(new BorderLayout(3, 3));
		tabTop.setOpaque(true);
		// tabTop.setBackground(unselectedBg);
		tabTop.add(tabHolder);

		// tabTop.add(Box.createHorizontalGlue());
//		tabTop.setBorder(
//				new MatteBorder(0, 0, 1, 0, App.SKIN.getDefaultBorderColor()));

		JButton btn = new JButton();
		btn.setToolTipText("New tab");
		btn.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		btn.setText(FontAwesomeContants.FA_PLUS_SQUARE);
		// btn.setFont(App.SKIN.getDefaultFont().deriveFont(20.0f));
		btn.putClientProperty("Nimbus.Overrides",
				App.SKIN.createTabButtonSkin());
		btn.setForeground(App.SKIN.getInfoTextForeground());
//		popup.setInvoker(btn);
		btn.addActionListener(e -> {
			System.out.println("Callback called");
			newTabCallback.accept(btn);
			// popup.show(btn, 0, btn.getHeight());
		});
		buttonsBox = new JPanel(new GridLayout(1, 0));
		buttonsBox.setOpaque(true);
		buttonsBox.setBackground(App.SKIN.getDefaultBackground());
		buttonsBox.setBorder(new EmptyBorder(0, 0, 0, 0));
		buttonsBox.add(btn);
		tabTop.add(buttonsBox, BorderLayout.EAST);

		add(tabTop, BorderLayout.NORTH);
		add(cardPanel);
	}

	public void addTab(TabTitle tabTitle, Component body) {
		int index = tabHolder.getComponentCount();
		cardPanel.add(body, body.hashCode() + "");

		TabTitleComponent titleComponent = new TabTitleComponent(
				body instanceof ClosableTabContent);
		tabTitle.setCallback(titleComponent.titleLabel::setText);
		titleComponent.setName(body.hashCode() + "");
		titleComponent.component = body;

//		JPanel tabTitlePanel = new JPanel(new BorderLayout());
//		tabTitlePanel.setBorder(new EmptyBorder(5, 10, 5, 5));
//		tabTitlePanel.setBackground(unselectedBg);
//		tabTitlePanel.setOpaque(true);
//		JLabel titleLabel = new JLabel();
//		Consumer<String> titleCallback = titleLabel::setText;
//		tabTitle.setCallback(titleCallback);
//		titleLabel.setHorizontalAlignment(JLabel.CENTER);
//		tabTitlePanel.add(titleLabel);
//		tabTitlePanel.setName(body.hashCode() + "");
//
//		if (body instanceof ClosableTabContent) {
//			System.out.println("Closable");
//			TabCloseButton tabCloseButton = new TabCloseButton();
//			tabTitlePanel.add(tabCloseButton, BorderLayout.EAST);
//		}

//		Dimension dimMax = new Dimension(tabTitlePanel.getPreferredSize().width,
//				Integer.MAX_VALUE);
//		// System.out.println(tabTitlePanel.getPreferredSize()+"
//		// "+tabTitlePanel.getMaximumSize());
//		tabTitlePanel.setMaximumSize(dimMax);
		tabHolder.add(titleComponent);
		// tabHolder.add(Box.createHorizontalGlue());

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (int i = 0; i < tabHolder.getComponentCount(); i++) {
					JComponent c = (JComponent) tabHolder.getComponent(i);
					if (c == titleComponent) {
						setSelectedIndex(i);
						break;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (titleComponent.tabCloseButton != null)
					titleComponent.tabCloseButton.setHovering(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (titleComponent.tabCloseButton != null)
					titleComponent.tabCloseButton.setHovering(false);
			}
		};

		MouseAdapter mouseAdapter2 = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (body instanceof ClosableTabContent) {
					ClosableTabContent closableTabContent = (ClosableTabContent) body;
					if (closableTabContent.close()) {
						System.out.println("Closing...");
						for (int i = 0; i < tabHolder
								.getComponentCount(); i++) {
							JComponent c = (JComponent) tabHolder
									.getComponent(i);
							if (c == titleComponent) {
								removeTabAt(i, c.getName(), titleComponent);
								break;
							}
						}
					}
				}
			}
		};

		titleComponent.addMouseListener(mouseAdapter);
		titleComponent.titleLabel.addMouseListener(mouseAdapter);
		if (titleComponent.tabCloseButton != null)
			titleComponent.tabCloseButton.addMouseListener(mouseAdapter2);

		setSelectedIndex(index);
	}

	public int getSelectedIndex() {
		for (int i = 0; i < tabHolder.getComponentCount(); i++) {
			if (tabHolder.getComponent(i) instanceof TabTitleComponent) {
				TabTitleComponent c = (TabTitleComponent) tabHolder
						.getComponent(i);
				if (c.selected) {
					return i;
				}
			}
		}
		return -1;
	}

	public void setSelectedIndex(int n) {
		JComponent c = (JComponent) tabHolder.getComponent(n);
		if (c instanceof TabTitleComponent) {
			String id = c.getName();
			cardLayout.show(cardPanel, id);
			for (int i = 0; i < tabHolder.getComponentCount(); i++) {
				JComponent cc = (JComponent) tabHolder.getComponent(i);
				if (cc instanceof TabTitleComponent) {
					((TabTitleComponent) cc).selected = false;
					unselectTabTitle((TabTitleComponent) cc);
				}
			}
			JComponent cc = (JComponent) tabHolder.getComponent(n);
			if (cc instanceof TabTitleComponent) {
				((TabTitleComponent) cc).selected = true;
				selectTabTitle((TabTitleComponent) cc);
			}
		}
	}

	private void selectTabTitle(TabTitleComponent c) {
		// c.setBorder(selectedTabBorder);
		c.setBackground(selectedBg);
		if (c.tabCloseButton != null)
			c.tabCloseButton.setSelected(true);
		c.revalidate();
		c.repaint();
	}

	private void unselectTabTitle(TabTitleComponent c) {
		// c.setBorder(unselectedTabBorder);
		c.setBackground(unselectedBg);
		if (c.tabCloseButton != null)
			c.tabCloseButton.setSelected(false);
		c.revalidate();
		c.repaint();
	}

	private void removeTabAt(int index, String name, TabTitleComponent title) {
		tabHolder.remove(title);
		cardPanel.remove(title.component);
		if (index > 0) {
			setSelectedIndex(index - 1);
		} else if (cardPanel.getComponentCount() > index) {
			setSelectedIndex(index);
		}
		tabHolder.revalidate();
		tabHolder.repaint();
	}

	public Component getSelectedContent() {
		for (int i = 0; i < tabHolder.getComponentCount(); i++) {
			if (tabHolder.getComponent(i) instanceof TabTitleComponent) {
				TabTitleComponent c = (TabTitleComponent) tabHolder
						.getComponent(i);
				if (c.selected) {
					return c.component;
				}
			}
		}
		return null;
	}

	private class TabTitleComponent extends JPanel {
		JLabel titleLabel;
		TabCloseButton tabCloseButton;
		boolean selected;
		Component component;

		/**
		 * 
		 */
		public TabTitleComponent(boolean closable) {
			super(new BorderLayout());
			setBorder(
					new CompoundBorder(new MatteBorder(0, 0, 0, 1, selectedBg),
							new EmptyBorder(5, 10, 5, 5)));
			setBackground(unselectedBg);
			setOpaque(true);
			titleLabel = new JLabel();
			titleLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(titleLabel);

			if (closable) {
				tabCloseButton = new TabCloseButton();
				tabCloseButton.setForeground(App.SKIN.getInfoTextForeground());
				this.add(tabCloseButton, BorderLayout.EAST);
			}
		}
	}

	public static class TabTitle {
		private Consumer<String> callback;

		/**
		 * @return the callback
		 */
		public Consumer<String> getCallback() {
			return callback;
		}

		/**
		 * @param callback the callback to set
		 */
		public void setCallback(Consumer<String> callback) {
			this.callback = callback;
		}
	}

	public enum NewTabType {
		LocalTab, RemoteTab
	}

	/**
	 * @return the buttonsBox
	 */
	public JPanel getButtonsBox() {
		return buttonsBox;
	}

	public Component[] getTabContents() {
		return cardPanel.getComponents();
	}
}
