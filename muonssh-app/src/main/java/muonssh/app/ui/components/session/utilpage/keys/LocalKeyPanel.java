package muonssh.app.ui.components.session.utilpage.keys;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import muonssh.app.App;
import muonssh.app.ui.components.SkinnedTextArea;
import muonssh.app.ui.components.SkinnedTextField;
import muonssh.app.ui.components.session.SessionInfo;

import java.awt.*;
import java.util.function.Consumer;

public class LocalKeyPanel extends JPanel {
	private final SessionInfo info;
	private final JTextField txtKeyFile;
	private final JButton btnGenNewKey;
	private final JButton btnRefresh;
	private final JTextArea txtPubKey;
	private final Consumer<?> callback1;
	private final Consumer<?> callback2;

	public LocalKeyPanel(SessionInfo info, Consumer<?> callback1,
			Consumer<?> callback2) {
		super(new BorderLayout());
		this.info = info;
		this.callback1 = callback1;
		this.callback2 = callback2;
		JLabel lblTitle = new JLabel(App.bundle.getString("public_key_file"));
		txtKeyFile = new SkinnedTextField(20);// new JTextField(20);
		txtKeyFile.setBackground(App.SKIN.getDefaultBackground());
		txtKeyFile.setBorder(null);
		txtKeyFile.setEditable(false);
		Box hbox = Box.createHorizontalBox();
		hbox.setBorder(new EmptyBorder(10, 10, 10, 10));
		hbox.add(lblTitle);
		hbox.add(Box.createHorizontalStrut(10));
		hbox.add(Box.createHorizontalGlue());
		hbox.add(txtKeyFile);
		add(hbox, BorderLayout.NORTH);

		txtPubKey = new SkinnedTextArea();
		txtPubKey.setLineWrap(true);
		JScrollPane jScrollPane = new JScrollPane(txtPubKey);
		add(jScrollPane);

		btnGenNewKey = new JButton(App.bundle.getString("generate_new_key"));
		btnRefresh = new JButton(App.bundle.getString("refresh"));

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

		add(hbox1, BorderLayout.SOUTH);
	}

	public void setKeyData(SshKeyHolder holder) {
		this.txtKeyFile.setText(holder.getLocalPubKeyFile());
		this.txtPubKey.setText(holder.getLocalPublicKey());
		this.txtPubKey.setEditable(false);
	}
}
