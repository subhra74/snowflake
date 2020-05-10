package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import util.FontAwesomeContants;

public class JumpHostPanel extends JPanel {
	private DefaultListModel<HopEntry> hopModel = new DefaultListModel<HopEntry>();
	private JList<HopEntry> hopList = new JList<HopEntry>(hopModel);
	private SessionInfo info;

	public JumpHostPanel() {
		super(new BorderLayout(5, 5));
		JLabel lblTitle = new JLabel("Intermediate hops");
		hopList.setBackground(App.SKIN.getTableBackgroundColor());

		JScrollPane scrollPane = new SkinnedScrollPane(hopList);

		Box b1 = Box.createVerticalBox();
		JButton btnAdd = new JButton(FontAwesomeContants.FA_PLUS);
		btnAdd.setFont(App.SKIN.getIconFont());
		JButton btnDel = new JButton(FontAwesomeContants.FA_MINUS);
		btnDel.setFont(App.SKIN.getIconFont());
		JButton btnEdit = new JButton(FontAwesomeContants.FA_PENCIL);
		btnEdit.setFont(App.SKIN.getIconFont());
		JButton btnUp = new JButton(FontAwesomeContants.FA_ARROW_UP);
		btnUp.setFont(App.SKIN.getIconFont());
		JButton btnDown = new JButton(FontAwesomeContants.FA_ARROW_DOWN);
		btnDown.setFont(App.SKIN.getIconFont());

		btnAdd.addActionListener(e -> {
			HopEntry ent = addOrEditEntry(null);
			if (ent != null) {
				hopModel.addElement(ent);
				updateJumpHosts();
			}
		});

		btnEdit.addActionListener(e -> {
			int index = hopList.getSelectedIndex();
			if (index != -1) {
				HopEntry ent = hopModel.get(index);
				HopEntry he = addOrEditEntry(ent);
				if (he != null) {
					hopModel.set(index, he);
					updateJumpHosts();
				}

			}
		});

		btnDel.addActionListener(e -> {
			int index = hopList.getSelectedIndex();
			if (index != -1) {
				hopModel.remove(index);
				updateJumpHosts();
			}
		});

		btnUp.addActionListener(e -> {
			int index = hopList.getSelectedIndex();
			if (index > 0) {
				HopEntry ent = hopModel.remove(index);
				hopModel.add(index - 1, ent);
				updateJumpHosts();
			}
		});

		btnDown.addActionListener(e -> {
			int index = hopList.getSelectedIndex();
			if (index < hopModel.size() - 1) {
				HopEntry ent = hopModel.remove(index);
				hopModel.add(index + 1, ent);
				updateJumpHosts();
			}
		});

		b1.add(btnAdd);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnEdit);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnDel);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnUp);
		b1.add(Box.createVerticalStrut(10));

		b1.add(btnDown);
		b1.add(Box.createVerticalStrut(10));

		this.add(lblTitle, BorderLayout.NORTH);
		this.add(scrollPane);
		this.add(b1, BorderLayout.EAST);
	}

	private void setJumpHosts(List<HopEntry> jumpHosts) {
		hopModel.clear();
		for (HopEntry ent : jumpHosts) {
			hopModel.addElement(ent);
		}
	}

	private List<HopEntry> getJumpHosts() {
		List<HopEntry> list = new ArrayList<HopEntry>();
		for (int i = 0; i < this.hopModel.size(); i++) {
			HopEntry ent = this.hopModel.get(i);
			list.add(ent);
		}
		return list;
	}

	private void updateJumpHosts() {
		this.info.setJumpHosts(getJumpHosts());
	}

	public void setInfo(SessionInfo info) {
		this.info = info;
		setJumpHosts(this.info.getJumpHosts());
	}

	private HopEntry addOrEditEntry(HopEntry e) {
		JTextField txtHost = new SkinnedTextField(30);
		JSpinner spPort = new JSpinner(new SpinnerNumberModel(22, 1, SessionInfoPanel.DEFAULT_MAX_PORT, 1));
		JTextField txtUser = new SkinnedTextField(30);
		JPasswordField txtPassword = new JPasswordField(30);

		Box b1 = Box.createHorizontalBox();

		JTextField txtKeyFile = new SkinnedTextField(30);
		JButton btnBrowse = new JButton("...");

		b1.add(txtKeyFile);
		b1.add(Box.createHorizontalStrut(10));
		b1.add(btnBrowse);

		if (e != null) {
			if (e.getHost() != null)
				txtHost.setText(e.getHost());
			if (e.getUser() != null)
				txtUser.setText(e.getUser());
			if (e.getKeypath() != null)
				txtKeyFile.setText(e.getKeypath());
			spPort.setValue((Integer) e.getPort());
		}

		btnBrowse.addActionListener(ev -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileHidingEnabled(false);
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				txtKeyFile.setText(f.getAbsolutePath());
			}
		});

		while (JOptionPane.showOptionDialog(this,
				new Object[] { "Host", txtHost, "Port", spPort, "User", txtUser, "Password", txtPassword, "Private key",
						txtKeyFile },
				"Hop entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.OK_OPTION) {
			String host = txtHost.getText();
			String user = txtUser.getText();
			String password = txtPassword.getPassword().length > 0 ? new String(txtPassword.getPassword()) : null;
			String path = txtKeyFile.getText();
			int port = (Integer) spPort.getValue();
			if (host.length() < 1 || user.length() < 1 || port <= 0) {
				JOptionPane.showMessageDialog(this, "Invalid input: all fields mandatory");
				continue;
			}

			if (e == null) {
				e = new HopEntry();
				e.setId(UUID.randomUUID().toString());
			}
			e.setHost(host);
			e.setPassword(password);
			e.setUser(user);
			e.setPort(port);
			e.setKeypath(path);
			return e;
		}
		return null;
	}
}
