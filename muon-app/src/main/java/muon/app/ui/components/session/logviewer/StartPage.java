/**
 * 
 */
package muon.app.ui.components.session.logviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import util.CollectionHelper;

/**
 * @author subhro
 *
 */
public class StartPage extends JPanel {
	private DefaultListModel<String> pinnedLogsModel;
	private JList<String> pinnedLogList;
	private boolean hover = false;
	private List<String> finalPinnedLogs;
	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private static final Cursor DEFAULT_CURSOR = new Cursor(
			Cursor.DEFAULT_CURSOR);
	private String sessionId;

	/**
	 * 
	 */
	public StartPage(Consumer<String> callback, String sessionId) {
		super(new BorderLayout());
		this.sessionId = sessionId;
		List<String> pinnedLogs = CollectionHelper
				.arrayList("/var/log/gpu-manager.log", "/var/log/syslog");
		App.loadPinnedLogs();
		if (App.getPinnedLogs().containsKey(sessionId)) {
			pinnedLogs = App.getPinnedLogs().get(sessionId);
		}

		this.finalPinnedLogs = pinnedLogs;

		pinnedLogsModel = new DefaultListModel<>();
		pinnedLogsModel.addAll(finalPinnedLogs);
		pinnedLogList = new JList<>(pinnedLogsModel);
		pinnedLogList.setCellRenderer(new PinnedLogsRenderer());
		pinnedLogList.setBackground(App.SKIN.getSelectedTabColor());
		JScrollPane jsp = new SkinnedScrollPane(pinnedLogList);
		jsp.setBorder(new EmptyBorder(0, 10, 0, 10));
		this.add(jsp);
		JButton btnAddLog = new JButton("Add log");
		JButton btnDelLog = new JButton("Delete");
		btnAddLog.addActionListener(e -> {
			String logPath = promptLogPath();
			if (logPath != null) {
				finalPinnedLogs.add(logPath);
				pinnedLogsModel.addElement(logPath);
				App.getPinnedLogs().put(sessionId, finalPinnedLogs);
				App.savePinnedLogs();
			}
		});
		btnDelLog.addActionListener(e -> {
			int index = pinnedLogList.getSelectedIndex();
			if (index != -1) {
				pinnedLogsModel.remove(index);
			}
		});
		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(Box.createHorizontalGlue());
		bottomBox.add(btnAddLog);
		bottomBox.add(Box.createHorizontalStrut(10));
		bottomBox.add(btnDelLog);
		bottomBox.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(bottomBox, BorderLayout.SOUTH);
		pinnedLogList.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				int index = pinnedLogList.locationToIndex(e.getPoint());
				if (index != -1) {
					Rectangle r = pinnedLogList.getCellBounds(index, index);
					if (r != null && r.contains(e.getPoint())) {
						if (!pinnedLogList.isSelectedIndex(index)) {
							pinnedLogList.setSelectedIndex(index);
						}
						if (hover)
							return;
						hover = true;
						pinnedLogList.setCursor(HAND_CURSOR);
						return;
					}
				}
				if (hover) {
					hover = false;
					pinnedLogList.setCursor(DEFAULT_CURSOR);
				}
			}
		});
		pinnedLogList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (hover) {
					int index = pinnedLogList.getSelectedIndex();
					if (index != -1) {
						callback.accept(pinnedLogsModel.elementAt(index));
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hover = false;
				pinnedLogList.setCursor(DEFAULT_CURSOR);
			}
		});

	}

	private String promptLogPath() {
		JTextField txt = new SkinnedTextField(30);
		if (JOptionPane.showOptionDialog(this,
				new Object[] { "Please provide full path of the log file",
						txt },
				"Input", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.OK_OPTION && txt.getText().length() > 0) {
			return txt.getText();
		}
		return null;
	}

	static class PinnedLogsRenderer extends JLabel
			implements ListCellRenderer<String> {
		/**
		 * 
		 */
		public PinnedLogsRenderer() {
			setOpaque(true);
			setBorder(new CompoundBorder(
					new MatteBorder(0, 0, 2, 0,
							App.SKIN.getDefaultBackground()),
					new EmptyBorder(10, 10, 10, 10)));
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setBackground(isSelected ? App.SKIN.getDefaultSelectionBackground()
					: list.getBackground());
			setForeground(isSelected ? App.SKIN.getDefaultSelectionForeground()
					: list.getForeground());
			setText(value);
			return this;
		}
	}

	public void pinLog(String logPath) {
		pinnedLogsModel.addElement(logPath);
		finalPinnedLogs.add(logPath);
		App.getPinnedLogs().put(sessionId, finalPinnedLogs);
		App.savePinnedLogs();
	}
}
