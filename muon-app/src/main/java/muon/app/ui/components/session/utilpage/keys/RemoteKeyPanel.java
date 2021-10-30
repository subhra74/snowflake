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

import static muon.app.App.bundle;

public class RemoteKeyPanel extends JPanel {
	private SessionInfo info;
	private final JTextField txtKeyFile;
	private final JButton btnGenNewKey;
	private final JButton btnRefresh;
	private final JButton btnAdd;
	private final JButton btnRemove;
	private final JButton btnEdit;
	private final JTextArea txtPubKey;
	private final Consumer<?> callback1;
	private final Consumer<?> callback2;
	private Consumer<String> callback3;
	private final DefaultListModel<String> model;
	private final JList<String> jList;

	public RemoteKeyPanel(SessionInfo info, Consumer<?> callback1,
			Consumer<?> callback2, Consumer<String> callback3) {
		super(new BorderLayout());
		this.info = info;
		this.info = info;
		this.callback1 = callback1;
		// this.callback2 = callback2;
		this.callback2 = callback3;
		JLabel lblTitle = new JLabel(bundle.getString("public_key_file"));
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

		btnGenNewKey = new JButton(bundle.getString("generate_new_key"));
		btnRefresh = new JButton(bundle.getString("generate"));

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

		btnAdd = new JButton(bundle.getString("add"));
		btnEdit = new JButton(bundle.getString("edit"));
		btnRemove = new JButton();

		btnAdd.addActionListener(e -> {
			String text = JOptionPane.showInputDialog(null, bundle.getString("new_entry"));
			if (text != null && text.length() > 0) {
				model.addElement(text);
				callback3.accept(getAuthorizedKeys());
			}
		});

		btnEdit.addActionListener(e -> {
			int index = jList.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, bundle.getString("no_entry_selected"));
				return;
			}
			String str = model.get(index);
			String text = JOptionPane.showInputDialog(null, bundle.getString("new_entry"), str);
			if (text != null && text.length() > 0) {
				model.set(index, text);
				callback3.accept(getAuthorizedKeys());
			}
		});

		btnRemove.addActionListener(e -> {
			int index = jList.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, bundle.getString("no_entry_selected"));
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
		hbox2.add(new JLabel(bundle.getString("authorized_keys")));
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
