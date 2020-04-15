/**
 *
 */
package muon.app.ui.components.session.utilpage.services;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import muon.app.App;
import muon.app.ssh.RemoteSessionInstance;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.utilpage.UtilPageItemView;
import util.SudoUtils;

/**
 * @author subhro
 *
 */
public class ServicePanel extends UtilPageItemView {
	private ServiceTableModel model = new ServiceTableModel();
	private JTable table;
	private static final Pattern SERVICE_PATTERN = Pattern
			.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+([\\S]+.*)");
	private static final Pattern UNIT_PATTERN = Pattern
			.compile("(\\S+)\\s+([\\S]+.*)");
	private JButton btnStart, btnStop, btnRestart, btnReload, btnEnable,
			btnDisable, btnRefresh;
	private JTextField txtFilter;
	private JCheckBox chkRunAsSuperUser;
	private static final String SEP = UUID.randomUUID().toString();
	private JButton btnFilter;
	private List<ServiceEntry> list;

	public static final String SYSTEMD_COMMAND = "systemctl list-unit-files -t service -a "
			+ "--plain --no-pager --no-legend --full; echo " + SEP
			+ "; systemctl list-units -t service -a --plain --no-pager --no-legend --full";

	/**
	 *
	 */
	public ServicePanel(SessionContentPanel holder) {
		super(holder);
	}

	public void setElevationActionListener(ActionListener a) {
		chkRunAsSuperUser.addActionListener(a);
	}

//    public void setRefreshActionListener(ActionListener a) {
//        btnRefresh.addActionListener(a);
//    }

	public void setStartServiceActionListener(ActionListener a) {
		btnStart.addActionListener(a);
	}

	public void setStopServiceActionListener(ActionListener a) {
		btnStop.addActionListener(a);
	}

	public void setRestartServiceActionListener(ActionListener a) {
		btnRestart.addActionListener(a);
	}

	public void setReloadServiceActionListener(ActionListener a) {
		btnReload.addActionListener(a);
	}

	public void setEnableServiceActionListener(ActionListener a) {
		btnEnable.addActionListener(a);
	}

	public void setDisableServiceActionListener(ActionListener a) {
		btnDisable.addActionListener(a);
	}

	private void filter() {
		String text = txtFilter.getText();
		model.clear();
		if (text.length() > 0) {
			List<ServiceEntry> filteredList = new ArrayList<>();
			for (ServiceEntry entry : list) {
				if (entry.getName().contains(text)
						|| entry.getDesc().contains(text)
						|| entry.getUnitStatus().contains(text)) {
					filteredList.add(entry);
				}
			}
			model.addEntries(filteredList);
		} else {
			model.addEntries(list);
		}
	}

	private String getSelectedService() {
		int r = table.getSelectedRow();
		if (r < 0) {
			return null;
		}
		return (String) model.getValueAt(table.convertRowIndexToModel(r), 0);
	}

	public String getStartServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl start " + cmd;
	}

	public String getStopServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl stop " + cmd;
	}

	public String getRestartServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl restart " + cmd;
	}

	public String getReloadServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl reload " + cmd;
	}

	public String getEnableServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl enable " + cmd;
	}

	public String getDisableServiceCommand() {
		String cmd = getSelectedService();
		if (cmd == null) {
			return null;
		}
		return "systemctl disable " + cmd;
	}

	public void setUseSuperUser(boolean select) {
		chkRunAsSuperUser.setSelected(select);
	}

	public boolean getUseSuperUser() {
		return chkRunAsSuperUser.isSelected();
	}

	private static List<ServiceEntry> parseServiceEntries(StringBuilder data) {
		List<ServiceEntry> list = new ArrayList<>();
		Map<String, String> unitMap = new HashMap<>();
		// Map<String, ServiceEntry> unitMap = new HashMap<>();
		boolean parsingUnit = true;
		for (String s : data.toString().split("\n")) {
			if (parsingUnit && s.equals(SEP)) {
				parsingUnit = false;
				continue;
			}

			if (parsingUnit) {
				parseUnitFile(s, unitMap);
			} else {
				ServiceEntry ent = parseUnit(s, unitMap);
				if (ent != null) {
					list.add(ent);
				}
			}
		}
//        System.out.println(unitMap);
//        System.out.println(list);

		return list;
//        return unitMap.entrySet().stream().map(e -> e.getValue())
//                .collect(Collectors.toList());
	}

	private void setServiceData(List<ServiceEntry> list) {
		this.list = list;
		filter();
	}

	private static void parseUnitFile(String data, Map<String, String> map) {
		Matcher m = UNIT_PATTERN.matcher(data);
		if (m.find() && m.groupCount() == 2) {
			map.put(m.group(1).trim(), m.group(2).trim());
//            ServiceEntry e = new ServiceEntry();
//            e.setName(m.group(1));
//            e.setUnitStatus("");
//            e.setDesc("");
//            e.setUnitFileStatus(m.group(2));
//            return e;
		}
//        return null;
	}

	private static ServiceEntry parseUnit(String data,
			Map<String, String> unitMap) {
		ServiceEntry ent = new ServiceEntry();
		Matcher m = SERVICE_PATTERN.matcher(data);
		if (m.find() && m.groupCount() == 5) {
			String name = m.group(1).trim();
			if (unitMap.get(name) != null) {
				String status = unitMap.get(name);
				ent.setName(name);
				ent.setUnitFileStatus(status);
				ent.setUnitStatus(m.group(3) + "(" + m.group(4) + ")");
				ent.setDesc(m.group(5).trim());
				return ent;
			}

//            ServiceEntry e = unitMap.get(m.group(1));
//            if (e != null) {
//                e.setDesc(m.group(5));
//                e.setUnitStatus(m.group(3) + "(" + m.group(4) + ")");
//            }
		}
		return null;
	}

	@Override
	protected void createUI() {
		setBorder(new EmptyBorder(10, 10, 10, 10));

		ServiceTableCellRenderer r = new ServiceTableCellRenderer();

		table = new JTable(model);
		table.setDefaultRenderer(Object.class, r);
		table.setShowGrid(false);
		table.setRowHeight(r.getPreferredSize().height);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setFillsViewportHeight(true);

		JLabel lbl1 = new JLabel("Search");
		txtFilter = new SkinnedTextField(30);// new JTextField(30);
		txtFilter.addActionListener(e -> {
			filter();
		});
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

		btnStart = new JButton("Start");
		btnStop = new JButton("Stop");
		btnRestart = new JButton("Restart");
		btnReload = new JButton("Reload");
		btnEnable = new JButton("Enable");
		btnDisable = new JButton("Disable");
		btnRefresh = new JButton("Refresh");

		chkRunAsSuperUser = new JCheckBox(
				"Perform actions as super user (sudo)");
		box.add(chkRunAsSuperUser);

		box.add(Box.createHorizontalGlue());
		box.add(btnStart);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnStop);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnRestart);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnReload);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnEnable);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnDisable);
		box.add(Box.createHorizontalStrut(5));
		box.add(btnRefresh);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(10, 0, 0, 0));

		add(box, BorderLayout.SOUTH);

		this.setStartServiceActionListener(e -> {
			performServiceAction(1);
		});
		this.setStopServiceActionListener(e -> {
			performServiceAction(2);
		});
		this.setEnableServiceActionListener(e -> {
			performServiceAction(3);
		});
		this.setDisableServiceActionListener(e -> {
			performServiceAction(4);
		});
		this.setReloadServiceActionListener(e -> {
			performServiceAction(5);
		});
		this.setRestartServiceActionListener(e -> {
			performServiceAction(6);
		});

		btnRefresh.addActionListener(e -> {
			holder.EXECUTOR.submit(() -> {
				AtomicBoolean stopFlag = new AtomicBoolean(false);
				holder.disableUi(stopFlag);
				updateView(stopFlag);
				holder.enableUi();
			});
		});

		holder.EXECUTOR.submit(() -> {
			AtomicBoolean stopFlag = new AtomicBoolean(false);
			holder.disableUi(stopFlag);
			updateView(stopFlag);
			holder.enableUi();
		});
	}

	@Override
	protected void onComponentVisible() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onComponentHide() {
		// TODO Auto-generated method stub

	}

	private void performServiceAction(int option) {
		String cmd1 = null;
		switch (option) {
		case 1:
			cmd1 = this.getStartServiceCommand();
			break;
		case 2:
			cmd1 = this.getStopServiceCommand();
			break;
		case 3:
			cmd1 = this.getEnableServiceCommand();
			break;
		case 4:
			cmd1 = this.getDisableServiceCommand();
			break;
		case 5:
			cmd1 = this.getReloadServiceCommand();
			break;
		case 6:
			cmd1 = this.getRestartServiceCommand();
			break;
		}

		String cmd = cmd1;

		AtomicBoolean stopFlag = new AtomicBoolean(false);

		holder.disableUi(stopFlag);

		boolean elevated = this.getUseSuperUser();
		if (cmd != null) {
			holder.EXECUTOR.submit(() -> {
				try {
					if (elevated) {
						try {
							if (this.runCommandWithSudo(
									holder.getRemoteSessionInstance(), stopFlag,
									cmd)) {
								updateView(stopFlag);
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (!holder.isSessionClosed()) {
							JOptionPane.showMessageDialog(null,
									"Operation failed");
						}
						// JOptionPane.showMessageDialog(null, "Operation
						// failed");
					} else {
						try {
							if (this.runCommand(
									holder.getRemoteSessionInstance(), stopFlag,
									cmd)) {
								updateView(stopFlag);
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (!holder.isSessionClosed()) {
							JOptionPane.showMessageDialog(null,
									"Operation failed");
						} // JOptionPane.showMessageDialog(null, "Operation
							// failed");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					holder.enableUi();
				}
			});
		}
	}

	public boolean runCommandWithSudo(RemoteSessionInstance client,
			AtomicBoolean stopFlag, String command) throws Exception {
		// StringBuilder output = new StringBuilder();
		return SudoUtils.runSudo(command, client) == 0;
	}

	public boolean runCommand(RemoteSessionInstance client,
			AtomicBoolean stopFlag, String command) throws Exception {
		StringBuilder output = new StringBuilder();
		return client.exec(command, new AtomicBoolean(false), output) == 0;
	}

	private void updateView(AtomicBoolean stopFlag) {
		try {
			StringBuilder output = new StringBuilder();
			int ret = holder.getRemoteSessionInstance().exec(SYSTEMD_COMMAND,
					stopFlag, output);
			if (ret == 0) {
				List<ServiceEntry> list = ServicePanel
						.parseServiceEntries(output);
				SwingUtilities.invokeAndWait(() -> {
					setServiceData(list);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
