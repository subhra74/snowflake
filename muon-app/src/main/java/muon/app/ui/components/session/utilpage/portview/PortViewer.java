/**
 * 
 */
package muon.app.ui.components.session.utilpage.portview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.utilpage.UtilPageItemView;
import util.SudoUtils;

/**
 * @author subhro
 *
 */
public class PortViewer extends UtilPageItemView {
	private static final String SEPARATOR = UUID.randomUUID().toString();
	private SocketTableModel model = new SocketTableModel();
	private JTable table;

	private JButton btnRefresh;
	private JTextField txtFilter;
	private JCheckBox chkRunAsSuperUser;
	private JButton btnFilter;
	private List<SocketEntry> list;

	public static final String LSOF_COMMAND = "sh -c \"export PATH=$PATH:/usr/sbin; echo;echo "
			+ SEPARATOR + ";lsof -b -n -i tcp -P -s tcp:LISTEN -F cn 2>&1\"";

	/**
	 * 
	 */
	public PortViewer(SessionContentPanel holder) {
		super(holder);
		setBorder(new EmptyBorder(10, 10, 10, 10));

	}

	private void filter() {
		String text = txtFilter.getText();
		model.clear();
		if (text.length() > 0) {
			List<SocketEntry> filteredList = new ArrayList<>();
			for (SocketEntry entry : list) {
				if (entry.getApp().contains(text)
						|| (entry.getPort() + "").contains(text)
						|| entry.getHost().contains(text)
						|| (entry.getPid() + "").contains(text)) {
					filteredList.add(entry);
				}
			}
			model.addEntries(filteredList);
		} else {
			model.addEntries(list);
		}
		model.fireTableDataChanged();
	}

	public boolean getUseSuperUser() {
		return chkRunAsSuperUser.isSelected();
	}

	public List<SocketEntry> parseSocketList(String text) {
		System.err.println("text: " + text);
		List<SocketEntry> list = new ArrayList<>();
		SocketEntry ent = null;
		boolean start = false;
		for (String line1 : text.split("\n")) {
			String line = line1.trim();
			System.out.println("LINE=" + line);
			if (!start) {
				if (line.trim().equals(SEPARATOR)) {
					start = true;
				}
				continue;
			}
			char ch = line.charAt(0);
			if (ch == 'p') {
				if (ent != null) {
					list.add(ent);
				}
				ent = new SocketEntry();
				ent.setPid(Integer.parseInt(line.substring(1)));
			}
			if (ch == 'c') {
				ent.setApp(line.substring(1));
			}
			if (ch == 'n') {
				String hostStr = line.substring(1);
				int index = hostStr.lastIndexOf(":");
				if (index != -1) {
					int port = Integer.parseInt(hostStr.substring(index + 1));
					String host = hostStr.substring(0, index);
					if (ent.getHost() != null) {
						// if listening on multiple interfaces, ports
						SocketEntry ent1 = new SocketEntry();
						ent1.setPort(port);
						ent1.setHost(host);
						ent1.setApp(ent.getApp());
						ent1.setPid(ent.getPid());
						list.add(ent1);
					} else {
						ent.setPort(port);
						ent.setHost(host);
					}
				}
			}
		}
		if (ent != null) {
			list.add(ent);
		}
		return list;
	}

	public void setSocketData(List<SocketEntry> list) {
		this.list = list;
		filter();
	}

	@Override
	protected void createUI() {
		table = new JTable(model);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setFillsViewportHeight(true);

		JLabel lbl1 = new JLabel("Search");
		txtFilter = new SkinnedTextField(30);// new JTextField(30);
		btnFilter = new JButton("Search");

		Box b1 = Box.createHorizontalBox();
		b1.add(lbl1);
		b1.add(Box.createHorizontalStrut(5));
		b1.add(txtFilter);
		b1.add(Box.createHorizontalStrut(5));
		b1.add(btnFilter);

		add(b1, BorderLayout.NORTH);

		btnFilter.addActionListener(e -> {
			filter();
		});
		table.setAutoCreateRowSorter(true);
		add(new SkinnedScrollPane(table));

		Box box = Box.createHorizontalBox();
		box.setBorder(new EmptyBorder(10, 0, 0, 0));
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(e -> {
			getListingSockets();
		});

		chkRunAsSuperUser = new JCheckBox(
				"Perform actions as super user (sudo)");
		box.add(chkRunAsSuperUser);

		box.add(Box.createHorizontalGlue());
		box.add(btnRefresh);
		box.add(Box.createHorizontalStrut(5));

		add(box, BorderLayout.SOUTH);

		getListingSockets();
	}

	@Override
	protected void onComponentVisible() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onComponentHide() {
		// TODO Auto-generated method stub

	}

	private void getListingSockets() {
		String cmd = LSOF_COMMAND;
		AtomicBoolean stopFlag = new AtomicBoolean(false);
		holder.disableUi(stopFlag);

		boolean elevated = this.getUseSuperUser();
		if (cmd != null) {
			holder.EXECUTOR.submit(() -> {
				try {
					StringBuilder output = new StringBuilder();
					if (elevated) {
						try {
							if (SudoUtils.runSudoWithOutput(cmd,
									holder.getRemoteSessionInstance(), output,
									new StringBuilder()) == 0) {
								java.util.List<SocketEntry> list = this
										.parseSocketList(output.toString());
								SwingUtilities.invokeAndWait(() -> {
									setSocketData(list);
								});
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (!holder.isSessionClosed()) {
							JOptionPane.showMessageDialog(null,
									"Operation failed");
						}
					} else {
						System.out.println("Command was: " + cmd);
						try {
							if (holder.getRemoteSessionInstance().exec(cmd,
									stopFlag, output) == 0) {
								System.out.println(
										"Command was: " + cmd + " " + output);
								java.util.List<SocketEntry> list = this
										.parseSocketList(output.toString());
								SwingUtilities.invokeAndWait(() -> {
									setSocketData(list);
								});
								return;
							}
							System.out.println("Error: " + output);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (!holder.isSessionClosed()) {
							JOptionPane.showMessageDialog(null,
									"Operation failed");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		}
	}
}
