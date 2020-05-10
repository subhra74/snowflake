package muon.app.ui.components.session.utilpage.keys;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import muon.app.App;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextArea;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.SessionInfo;

import java.awt.*;
import java.util.function.Consumer;

public class RemoteKeyPanel extends JPanel {
	private SessionInfo info;
	private JTextField txtKeyFile;
	private JButton btnGenNewKey, btnRefresh, btnAdd, btnRemove, btnEdit;
	private JTextArea txtPubKey;
	private Consumer<?> callback1, callback2;
	private Consumer<String> callback3;
	private DefaultListModel<String> model;
	private JList<String> jList;

	public RemoteKeyPanel(SessionInfo info, Consumer<?> callback1,
			Consumer<?> callback2, Consumer<String> callback3) {
		super(new BorderLayout());
		this.info = info;
		this.info = info;
		this.callback1 = callback1;
		// this.callback2 = callback2;
		this.callback2 = callback3;
		JLabel lblTitle = new JLabel("Public key file:");
		txtKeyFile = new SkinnedTextField(20);// new JTextField(20);
		txtKeyFile.setBorder(null);
		txtKeyFile.setBackground(App.SKIN.getDefaultBackground());
		txtKeyFile.setEditable(false);
		Box hbox = Box.createHorizontalBox();
		hbox.setBorder(new EmptyBorder(10, 10, 10, 10));
		hbox.add(lblTitle);
		hbox.add(Box.createHorizontalStrut(10));
		hbox.add(Box.createHorizontalGlue());
		hbox.add(txtKeyFile);

		txtPubKey = new SkinnedTextArea();
		txtPubKey.setLineWrap(true);
		JScrollPane jScrollPane = new SkinnedScrollPane(txtPubKey);

		btnGenNewKey = new JButton("Generate new key");
		btnRefresh = new JButton("Refresh");

		btnGenNewKey.addActionListener(e -> {
			callback1.accept(null);
		});

		btnRefresh.addActionListener(e -> {
			callback2.accept(null);
		});

		Box hbox1 = Box.createHorizontalBox();
		hbox1.add(Box.createHorizontalGlue());
		hbox1.add(btnGenNewKey);
		hbox1.add(Box.createHorizontalStrut(10));
		hbox1.add(btnRefresh);
		hbox1.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel hostKeyPanel = new JPanel(new BorderLayout());
		hostKeyPanel.add(hbox, BorderLayout.NORTH);
		hostKeyPanel.add(jScrollPane);
		hostKeyPanel.add(hbox1, BorderLayout.SOUTH);

		model = new DefaultListModel<>();
		jList = new JList<>(model);
		jList.setBackground(App.SKIN.getTextFieldBackground());

		btnAdd = new JButton("Add");
		btnEdit = new JButton("Edit");
		btnRemove = new JButton("Remove");

		btnAdd.addActionListener(e -> {
			String text = JOptionPane.showInputDialog(null, "New entry");
			if (text != null && text.length() > 0) {
				model.addElement(text);
				callback3.accept(getAuthorizedKeys());
			}
		});

		btnEdit.addActionListener(e -> {
			int index = jList.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, "No entry is selected");
				return;
			}
			String str = model.get(index);
			String text = JOptionPane.showInputDialog(null, "New entry", str);
			if (text != null && text.length() > 0) {
				model.set(index, text);
				callback3.accept(getAuthorizedKeys());
			}
		});

		btnRemove.addActionListener(e -> {
			int index = jList.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, "No entry is selected");
				return;
			}
			model.remove(index);
			callback3.accept(getAuthorizedKeys());
		});

		Box boxBottom = Box.createHorizontalBox();
		boxBottom.add(Box.createHorizontalGlue());
		boxBottom.add(btnAdd);
		boxBottom.add(Box.createHorizontalStrut(10));
		boxBottom.add(btnEdit);
		boxBottom.add(Box.createHorizontalStrut(10));
		boxBottom.add(btnRemove);
		boxBottom.setBorder(new EmptyBorder(10, 10, 10, 10));

		Box hbox2 = Box.createHorizontalBox();
		hbox2.setBorder(new EmptyBorder(10, 10, 10, 10));
		hbox2.add(new JLabel("Authorized keys"));
		hbox2.add(Box.createHorizontalStrut(10));

		JPanel authorizedKeysPanel = new JPanel(new BorderLayout());
		authorizedKeysPanel.add(hbox2, BorderLayout.NORTH);
		JScrollPane jScrollPane1 = new SkinnedScrollPane(jList);
		authorizedKeysPanel.add(jScrollPane1);
		authorizedKeysPanel.add(boxBottom, BorderLayout.SOUTH);

		add(hostKeyPanel, BorderLayout.NORTH);
		add(authorizedKeysPanel);
	}

	public void setKeyData(SshKeyHolder holder) {
		this.txtKeyFile.setText(holder.getRemotePubKeyFile());
		this.txtPubKey.setText(holder.getRemotePublicKey());
		this.txtPubKey.setEditable(false);
		this.model.clear();
		if (holder.getRemoteAuthorizedKeys() != null) {
			for (String line : holder.getRemoteAuthorizedKeys().split("\n")) {
				if (line.trim().length() > 0) {
					model.addElement(line);
				}
			}
		}
	}

	private String getAuthorizedKeys() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < model.size(); i++) {
			String item = model.get(i);
			sb.append(item);
			sb.append("\n");
		}
		return sb.toString();
	}
}
