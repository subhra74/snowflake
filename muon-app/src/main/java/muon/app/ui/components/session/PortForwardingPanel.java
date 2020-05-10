package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.PortForwardingRule.PortForwardingType;
import util.FontAwesomeContants;

public class PortForwardingPanel extends JPanel {
	private SessionInfo info;
	private PFTableModel model;
	private JTable table;

	public PortForwardingPanel() {
		super(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 0, 0, 10));
		model = new PFTableModel();
		table = new JTable(model);

		JLabel lblTitle = new JLabel("Port forwarding rules");

		JScrollPane scrollPane = new SkinnedScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		Box b1 = Box.createVerticalBox();
		JButton btnAdd = new JButton(FontAwesomeContants.FA_PLUS);
		btnAdd.setFont(App.SKIN.getIconFont());
		JButton btnDel = new JButton(FontAwesomeContants.FA_MINUS);
		btnDel.setFont(App.SKIN.getIconFont());
		JButton btnEdit = new JButton(FontAwesomeContants.FA_PENCIL);
		btnEdit.setFont(App.SKIN.getIconFont());

		btnAdd.addActionListener(e -> {
			PortForwardingRule ent = addOrEditEntry(null);
			if (ent != null) {
				model.addRule(ent);
				updatePFRules();
			}
		});

		btnEdit.addActionListener(e -> {
			int index = table.getSelectedRow();
			if (index != -1) {
				PortForwardingRule ent = model.get(index);
				if (addOrEditEntry(ent) != null) {
					model.refreshTable();
					updatePFRules();
				}
			}
		});

		btnDel.addActionListener(e -> {
			int index = table.getSelectedRow();
			if (index != -1) {
				model.remove(index);
				updatePFRules();
			}
		});

		b1.add(btnAdd);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnEdit);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnDel);
		b1.add(Box.createVerticalStrut(10));

		this.add(lblTitle, BorderLayout.NORTH);
		this.add(scrollPane);
		this.add(b1, BorderLayout.EAST);
	}

	private void updatePFRules() {
		this.info.setPortForwardingRules(model.getRules());
	}

	public void setInfo(SessionInfo info) {
		this.info = info;
		model.setRules(this.info.getPortForwardingRules());
	}

	private static class PFTableModel extends AbstractTableModel {

		private String[] columns = { "Type", "Host", "Source Port", "Target Port", "Bind Host" };
		private List<PortForwardingRule> list = new ArrayList<>();

		@Override
		public int getRowCount() {
			return list.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PortForwardingRule pf = list.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return pf.getType();
			case 1:
				return pf.getHost();
			case 2:
				return pf.getSourcePort();
			case 3:
				return pf.getTargetPort();
			case 4:
				return pf.getBindHost();
			}
			return "";
		}

		private void setRules(List<PortForwardingRule> rules) {
			list.clear();
			if (rules != null) {
				for (PortForwardingRule r : rules) {
					list.add(r);
				}
			}
			fireTableDataChanged();
		}

		private List<PortForwardingRule> getRules() {
			return list;
		}

		private void addRule(PortForwardingRule r) {
			this.list.add(r);
			fireTableDataChanged();
		}

		private void refreshTable() {
			fireTableDataChanged();
		}

		private void remove(int index) {
			list.remove(index);
			fireTableDataChanged();
		}

		private PortForwardingRule get(int index) {
			return list.get(index);
		}
	}

	private PortForwardingRule addOrEditEntry(PortForwardingRule r) {
		JComboBox<String> cmbPFType = new JComboBox<String>(new String[] { "Local", "Remote" });

		JTextField txtHost = new SkinnedTextField(30);

		JSpinner spSourcePort = new JSpinner(new SpinnerNumberModel(0, 0, SessionInfoPanel.DEFAULT_MAX_PORT, 1));
		JSpinner spTargetPort = new JSpinner(new SpinnerNumberModel(0, 0, SessionInfoPanel.DEFAULT_MAX_PORT, 1));

		JTextField txtBindAddress = new SkinnedTextField(30);
		txtBindAddress.setText("127.0.0.1");

		if (r != null) {
			txtHost.setText(r.getHost());
			spSourcePort.setValue((Integer) r.getSourcePort());
			spTargetPort.setValue((Integer) r.getTargetPort());
			txtBindAddress.setText(r.getBindHost());
			cmbPFType.setSelectedIndex(r.getType() == PortForwardingType.Local ? 0 : 1);
		}

		while (JOptionPane.showOptionDialog(this,
				new Object[] { "Port forwarding type", cmbPFType, "Host", txtHost, "Source Port", spSourcePort,
						"Target Port", spTargetPort, "Bind Address", txtBindAddress },
				"Port forwarding rule", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.OK_OPTION) {

			String host = txtHost.getText();
			int port1 = (Integer) spSourcePort.getValue();
			int port2 = (Integer) spTargetPort.getValue();
			String bindAddress = txtBindAddress.getText();

			if (host.length() < 1 || bindAddress.length() < 1 || port1 <= 0 || port2 <= 0) {
				JOptionPane.showMessageDialog(this, "Invalid input: all fields mandatory");
				continue;
			}

			if (r == null) {
				r = new PortForwardingRule();
			}
			r.setType(cmbPFType.getSelectedIndex() == 0 ? PortForwardingType.Local : PortForwardingType.Remote);
			r.setHost(host);
			r.setBindHost(bindAddress);
			r.setSourcePort(port1);
			r.setTargetPort(port2);
			return r;
		}
		return null;
	}
}
