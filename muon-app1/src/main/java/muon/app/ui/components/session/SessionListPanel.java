/**
 * 
 */
package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;
import muon.app.ui.AppWindow;
import muon.app.ui.components.SkinnedScrollPane;
import util.FontAwesomeContants;

/**
 * @author subhro
 *
 */
public class SessionListPanel extends JPanel {
	private DefaultListModel<SessionContentPanel> sessionListModel;
	private JList<SessionContentPanel> sessionList;
	private AppWindow window;
	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	/**
	 * 
	 */
	public SessionListPanel(AppWindow window) {
		super(new BorderLayout());
		this.window = window;
		sessionListModel = new DefaultListModel<>();
		sessionList = new JList<>(sessionListModel);
		sessionList.setCursor(DEFAULT_CURSOR);

		SessionListRenderer r = new SessionListRenderer();
		sessionList.setCellRenderer(r);
//		Dimension d=sessionList.getPreferredSize();
//		d.setSize(r.getPreferredCellSize().width, d.height);
//		sessionList.setMaximumSize(new Dimension(r.getPreferredCellSize().width, Short.MAX_VALUE));

		JScrollPane scrollPane = new SkinnedScrollPane(sessionList);
		this.add(scrollPane);
		sessionList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedIndex = sessionList.getSelectedIndex();
				int index = sessionList.locationToIndex(e.getPoint());
				if (index != -1 && selectedIndex == index) {
					Rectangle r = sessionList.getCellBounds(index, index);
					if (r != null && r.contains(e.getPoint())) {
						int x = e.getPoint().x;
						int y = e.getPoint().y;

						if (x > r.x + r.width - 30 && x < r.x + r.width && y > r.y + 10 && y < r.y + r.height - 10) {
							System.out.println("Clicked on: " + index);
							removeSession(index);
						}
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sessionList.setCursor(DEFAULT_CURSOR);
			}
		});

		sessionList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = sessionList.locationToIndex(e.getPoint());
				if (index != -1) {
					Rectangle r = sessionList.getCellBounds(index, index);
					if (r != null && r.contains(e.getPoint())) {
						int x = e.getPoint().x;
						int y = e.getPoint().y;

						if (x > r.x + r.width - 30 && x < r.x + r.width && y > r.y + 10 && y < r.y + r.height - 10) {
							sessionList.setCursor(HAND_CURSOR);
							return;
						}
					}
				}
				sessionList.setCursor(DEFAULT_CURSOR);
			}
		});

		sessionList.addListSelectionListener(e -> {
			System.out.println("called for index: " + sessionList.getSelectedIndex() + " " + e.getFirstIndex() + " "
					+ e.getLastIndex() + e.getValueIsAdjusting());
			// selectSession(sessionList.getSelectedIndex());
			if (!e.getValueIsAdjusting()) {
				int index = sessionList.getSelectedIndex();
				if (index != -1) {
					this.selectSession(index);
				}
			}
		});
	}

	public void createSession(SessionInfo info) {
		SessionContentPanel panel = new SessionContentPanel(info);
		sessionListModel.insertElementAt(panel, 0);

		sessionList.setSelectedIndex(0);
		// selectSession(0);
	}

	public void selectSession(int index) {
		SessionContentPanel sessionContentPanel = sessionListModel.get(index);
		window.showSession(sessionContentPanel);
		window.revalidate();
		window.repaint();
	}

	public void removeSession(int index) {
		if (JOptionPane.showConfirmDialog(window, "Disconnect session?") == JOptionPane.YES_OPTION) {
			SessionContentPanel sessionContentPanel = sessionListModel.get(index);
			sessionContentPanel.close();
			window.removeSession(sessionContentPanel);
			window.revalidate();
			window.repaint();
			sessionListModel.remove(index);
			if (sessionListModel.size() == 0) {
				return;
			}
			if (index == sessionListModel.size()) {
				sessionList.setSelectedIndex(index - 1);
			} else {
				sessionList.setSelectedIndex(index);
			}
		}
	}

	public static final class SessionListRenderer implements ListCellRenderer<SessionContentPanel> {

		private JPanel panel;
		private JLabel lblIcon, lblText, lblHost, lblClose;

		/**
		 * 
		 */
		public SessionListRenderer() {
			lblIcon = new JLabel();
			lblText = new JLabel();
			lblHost = new JLabel();
			lblClose = new JLabel();

			lblIcon.setFont(App.SKIN.getIconFont().deriveFont(24.0f));
			lblText.setFont(App.SKIN.getDefaultFont().deriveFont(14.0f));
			lblHost.setFont(App.SKIN.getDefaultFont().deriveFont(12.0f));
			lblClose.setFont(App.SKIN.getIconFont().deriveFont(16.0f));

			lblText.setText("Sample server");
			lblHost.setText("server host");
			lblIcon.setText(FontAwesomeContants.FA_CUBE);
			lblClose.setText(FontAwesomeContants.FA_EJECT);

			JPanel textHolder = new JPanel(new BorderLayout(5, 0));
			textHolder.setOpaque(false);
			textHolder.add(lblText);
			textHolder.add(lblHost, BorderLayout.SOUTH);

			panel = new JPanel(new BorderLayout(5, 5));
			panel.add(lblIcon, BorderLayout.WEST);
			panel.add(lblClose, BorderLayout.EAST);
			panel.add(textHolder);

			panel.setBorder(new EmptyBorder(10, 10, 10, 10));
			panel.setBackground(App.SKIN.getDefaultBackground());
			panel.setOpaque(true);

			Dimension d = panel.getPreferredSize();
			panel.setPreferredSize(d);
			panel.setMaximumSize(d);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends SessionContentPanel> list,
				SessionContentPanel value, int index, boolean isSelected, boolean cellHasFocus) {

			SessionInfo info = value.getInfo();

			// String strTitle = info.getName().substring(0,
			// Math.min(info.getName().length(), 15));

			lblText.setText(info.getName());
			lblHost.setText(info.getHost());
			lblIcon.setText(FontAwesomeContants.FA_CUBE);
			lblClose.setText(FontAwesomeContants.FA_EJECT);

			if (isSelected) {
				panel.setBackground(App.SKIN.getDefaultSelectionBackground());
				lblText.setForeground(App.SKIN.getDefaultSelectionForeground());
				lblHost.setForeground(App.SKIN.getDefaultSelectionForeground());
				lblIcon.setForeground(App.SKIN.getDefaultSelectionForeground());
			} else {
				panel.setBackground(App.SKIN.getDefaultBackground());
				lblText.setForeground(App.SKIN.getDefaultForeground());
				lblHost.setForeground(App.SKIN.getInfoTextForeground());
				lblIcon.setForeground(App.SKIN.getDefaultForeground());
			}
			return panel;
		}
	}

	public SessionContentPanel getSessionContainer(int activeSessionId) {
		for (int i = 0; i < sessionListModel.size(); i++) {
			SessionContentPanel scp = sessionListModel.get(i);
			if (scp.getActiveSessionId() == activeSessionId)
				return scp;
		}
		return null;
	}
}
