package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.TabbedPanel;
import muon.app.ui.components.session.SessionInfo.JumpType;

public class SessionInfoPanel extends JPanel {

	private static final long serialVersionUID = 6679029920589652547L;

	private JTextField inpHostName;
	private JSpinner inpPort;
	private JTextField inpUserName;
	private JPasswordField inpPassword;
	private JTextField inpLocalFolder;
	private JTextField inpRemoteFolder;
	private JTextField inpKeyFile;
	private JButton inpLocalBrowse;
	private JButton inpKeyBrowse;
	private JLabel lblHost, lblPort, lblUser, lblPass, lblLocalFolder, lblRemoteFolder, lblKeyFile, lblProxyType,
			lblProxyHost, lblProxyPort, lblProxyUser, lblProxyPass;
	private SpinnerNumberModel portModel, proxyPortModel;
	private JComboBox<String> cmbProxy;
	private JTextField inpProxyHostName;
	private JSpinner inpProxyPort;
	private JTextField inpProxyUserName;
	private JPasswordField inpProxyPassword;
	private JCheckBox chkUseJumpHosts;
	private JRadioButton radMultiHopTunnel, radMultiHopPortForwarding;
	private JumpHostPanel panJumpHost;
	private PortForwardingPanel panPF;
	private TabbedPanel tabs;

	public static final int DEFAULT_MAX_PORT = 65535;

	private SessionInfo info;

	public SessionInfoPanel() {
		createUI();
	}

	public void hideFields() {
		for (Component c : this.getComponents()) {
			c.setVisible(false);
		}
	}

	public void showFields() {
		for (Component c : this.getComponents()) {
			c.setVisible(true);
		}
	}

	public boolean validateFields() {
		if (inpHostName.getText().length() < 1) {
			showError("Host name can not be left blank");
			return false;
		}
		if (inpUserName.getText().length() < 1) {
			showError("User name can not be left blank");
			return false;
		}
		return true;
	}

	public void setSessionInfo(SessionInfo info) {
		this.info = info;
		setHost(info.getHost());
		setPort(info.getPort());
		setLocalFolder(info.getLocalFolder());
		setRemoteFolder(info.getRemoteFolder());
		setUser(info.getUser());
		setPassword(info.getPassword() == null ? new char[0] : info.getPassword().toCharArray());
		setKeyFile(info.getPrivateKeyFile());
		setProxyType(info.getProxyType());
		setProxyHost(info.getProxyHost());
		setProxyPort(info.getProxyPort());
		setProxyUser(info.getProxyUser());

		setProxyPassword(info.getProxyPassword() == null ? new char[0] : info.getProxyPassword().toCharArray());

		setJumpHostDetails(info.isUseJumpHosts(), info.getJumpType(), info.getJumpHosts());
		panPF.setInfo(info);
	}

	private void setHost(String host) {
		inpHostName.setText(host);
	}

	private void setPort(int port) {
		portModel.setValue(port);
	}

	private void setUser(String user) {
		inpUserName.setText(user);
	}

	private void setPassword(char[] pass) {
		inpPassword.setText(new String(pass));
	}

	private void setProxyType(int type) {
		cmbProxy.setSelectedIndex(type);
	}

	private void setProxyHost(String host) {
		inpProxyHostName.setText(host);
	}

	private void setProxyPort(int port) {
		proxyPortModel.setValue(port);
	}

	private void setProxyUser(String user) {
		inpProxyUserName.setText(user);
	}

	private void setProxyPassword(char[] pass) {
		inpProxyPassword.setText(new String(pass));
	}

	private void setLocalFolder(String folder) {
		inpLocalFolder.setText(folder);
	}

	private void setRemoteFolder(String folder) {
		inpRemoteFolder.setText(folder);
	}

	private void setKeyFile(String keyFile) {
		inpKeyFile.setText(keyFile);
	}

	private void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void setJumpHostDetails(boolean useJumpHosts, JumpType jumpType, List<HopEntry> jumpHosts) {
		this.chkUseJumpHosts.setSelected(useJumpHosts);
		if (jumpType == JumpType.TcpForwarding) {
			radMultiHopTunnel.setSelected(true);
		} else {
			radMultiHopPortForwarding.setSelected(true);
		}
		panJumpHost.setInfo(info);
	}

	private void createUI() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 0, 10, 0));
		tabs = new TabbedPanel();
		tabs.addTab("Connection", createConnectionPanel());
		tabs.addTab("Directories", createDirectoryPanel());
		tabs.addTab("Proxy", createProxyPanel());
		tabs.addTab("Jump Hosts", createJumpPanel());
		tabs.addTab("Port Forwarding", createPortForwardingPanel());
		this.add(tabs);
		tabs.setSelectedIndex(0);
	}

	private JPanel createJumpPanel() {
		GridBagLayout gbl1 = new GridBagLayout();
		JPanel panel = new JPanel(gbl1);

		Insets topInset = new Insets(20, 10, 0, 10);
		Insets noInset = new Insets(5, 10, 0, 10);

		chkUseJumpHosts = new JCheckBox("Jump Hosts / Multi hop port forwarding");
		radMultiHopTunnel = new JRadioButton("Use multihop SSH tunnel");
		radMultiHopPortForwarding = new JRadioButton("Use multihop port forwarding");

		chkUseJumpHosts.addActionListener(e -> {
			info.setUseJumpHosts(chkUseJumpHosts.isSelected());
		});

		radMultiHopPortForwarding.addActionListener(e -> updateHopMode());
		radMultiHopTunnel.addActionListener(e -> updateHopMode());

		ButtonGroup bg = new ButtonGroup();
		bg.add(radMultiHopPortForwarding);
		bg.add(radMultiHopTunnel);

		panJumpHost = new JumpHostPanel();

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = topInset;

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 5;
		c.insets = topInset;
		panel.add(chkUseJumpHosts, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 2;
		c.insets = topInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(radMultiHopPortForwarding, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 3;
		c.insets = topInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(radMultiHopTunnel, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 4;
		c.insets = topInset;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		panel.add(panJumpHost, c);

		return panel;
	}

	private JPanel createPortForwardingPanel() {
		GridBagLayout gbl1 = new GridBagLayout();
		JPanel panel = new JPanel(gbl1);

		panPF = new PortForwardingPanel();

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(panPF, c);

		return panel;
	}

	private JPanel createProxyPanel() {
		GridBagLayout gbl1 = new GridBagLayout();
		JPanel panel = new JPanel(gbl1);

		Insets topInset = new Insets(20, 10, 0, 10);
		Insets noInset = new Insets(5, 10, 0, 10);

		// -----------
		lblProxyType = new JLabel("Proxy type");
		lblProxyHost = new JLabel("Proxy host");
		lblProxyHost.setHorizontalAlignment(JLabel.LEADING);
		lblProxyPort = new JLabel("Proxy port");
		lblProxyUser = new JLabel("Proxy user");
		lblProxyPass = new JLabel("Proxy password" + " ( Warning: it will be saved in plain text! )");

		cmbProxy = new JComboBox<>(new String[] { "NONE", "HTTP", "SOCKS" });
		cmbProxy.addActionListener(e -> {
			info.setProxyType(cmbProxy.getSelectedIndex());
		});

		inpProxyHostName = new SkinnedTextField(10);// new
													// JTextField(30);
		inpProxyHostName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateHost();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateHost();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateHost();
			}

			private void updateHost() {
				info.setProxyHost(inpProxyHostName.getText());
			}
		});
		proxyPortModel = new SpinnerNumberModel(8080, 1, DEFAULT_MAX_PORT, 1);
		proxyPortModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				info.setProxyPort((Integer) proxyPortModel.getValue());
			}
		});
		inpProxyPort = new JSpinner(proxyPortModel);
		inpProxyUserName = new SkinnedTextField(10);// new
													// JTextField(30);
		inpProxyUserName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateUser();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateUser();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateUser();
			}

			private void updateUser() {
				info.setProxyUser(inpProxyUserName.getText());
			}
		});

		inpProxyPassword = new JPasswordField(10);
		inpProxyPassword.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			private void updatePassword() {
				info.setProxyPassword(new String(inpProxyPassword.getPassword()));
			}
		});
		// -----------

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = topInset;

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = topInset;
		panel.add(lblProxyType, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 1;
		c.gridy = 2;
		c.insets = noInset;
		c.fill = GridBagConstraints.NONE;
		panel.add(cmbProxy, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = topInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(lblProxyHost, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 4;
		c.insets = noInset;
		panel.add(inpProxyHostName, c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = topInset;
		panel.add(lblProxyPort, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 6;
		c.insets = noInset;
		c.fill = GridBagConstraints.NONE;
		panel.add(inpProxyPort, c);

		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		c.weightx = 1;
		c.insets = topInset;
		panel.add(lblProxyUser, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 8;
		c.insets = noInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(inpProxyUserName, c);

		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		c.weightx = 5;
		c.insets = topInset;
		panel.add(lblProxyPass, c);

		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.gridy = 10;
		c.insets = noInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(inpProxyPassword, c);

		JPanel panel2 = new JPanel(new BorderLayout());
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		panel.add(panel2, c);

		return panel;
	}

	private JPanel createDirectoryPanel() {
		GridBagLayout gbl1 = new GridBagLayout();
		JPanel panel = new JPanel(gbl1);

		Insets topInset = new Insets(20, 10, 0, 10);
		Insets noInset = new Insets(5, 10, 0, 10);

		inpLocalFolder = new SkinnedTextField(10);// new
		// JTextField(30);
		inpLocalFolder.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			private void updateFolder() {
				info.setLocalFolder(inpLocalFolder.getText());
			}
		});

		inpRemoteFolder = new SkinnedTextField(10);// new
		// JTextField(30);
		inpRemoteFolder.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateFolder();
			}

			private void updateFolder() {
				info.setRemoteFolder(inpRemoteFolder.getText());
			}
		});

		inpLocalBrowse = new JButton("Browse");
		inpLocalBrowse.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileHidingEnabled(false);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				inpLocalFolder.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = topInset;

		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 2;
		c.insets = topInset;
		panel.add(lblLocalFolder, c);

		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1;
		c.insets = noInset;
		c.weightx = 1;
		panel.add(inpLocalFolder, c);

		c.gridx = 1;
		c.gridy = 13;
		c.gridwidth = 1;
		c.insets = new Insets(5, 0, 0, 10);
		c.weightx = 0;
		panel.add(inpLocalBrowse, c);

		c.gridx = 0;
		c.gridy = 15;
		c.gridwidth = 2;
		c.insets = topInset;
		panel.add(lblRemoteFolder, c);

		c.gridx = 0;
		c.gridy = 16;
		c.gridwidth = 2;
		c.insets = noInset;
		c.weightx = 1;
		panel.add(inpRemoteFolder, c);

		JPanel panel2 = new JPanel(new BorderLayout());
		c.gridx = 0;
		c.gridy = 20;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		panel.add(panel2, c);

		return panel;
	}

	private JPanel createConnectionPanel() {
		GridBagLayout gbl1 = new GridBagLayout();
		JPanel panel = new JPanel(gbl1);

		Insets topInset = new Insets(20, 10, 0, 10);
		Insets noInset = new Insets(5, 10, 0, 10);

		// setBackground(new Color(245,245,245));
		lblHost = new JLabel("Host");
		lblHost.setHorizontalAlignment(JLabel.LEADING);
		lblPort = new JLabel("Port");
		lblUser = new JLabel("User");
		lblPass = new JLabel("Password" + " ( Warning: it will be saved in plain text! )");
		lblLocalFolder = new JLabel("Local folder");
		lblRemoteFolder = new JLabel("Remote folder");
		lblKeyFile = new JLabel("Private key file");

		inpHostName = new SkinnedTextField(10);// new JTextField(30);
		inpHostName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateHost();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateHost();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateHost();
			}

			private void updateHost() {
				info.setHost(inpHostName.getText());
			}
		});

		portModel = new SpinnerNumberModel(22, 1, DEFAULT_MAX_PORT, 1);
		portModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				info.setPort((Integer) portModel.getValue());
			}
		});
		inpPort = new JSpinner(portModel);
		inpUserName = new SkinnedTextField(10);// new JTextField(30);
		inpUserName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateUser();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateUser();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateUser();
			}

			private void updateUser() {
				info.setUser(inpUserName.getText());
			}
		});

		inpPassword = new JPasswordField(10);
		inpPassword.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updatePassword();
			}

			private void updatePassword() {
				info.setPassword(new String(inpPassword.getPassword()));
			}
		});

		inpKeyFile = new SkinnedTextField(10);// new JTextField(30);
		inpKeyFile.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateKeyFile();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateKeyFile();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateKeyFile();
			}

			private void updateKeyFile() {
				info.setPrivateKeyFile(inpKeyFile.getText());
			}
		});

		inpKeyBrowse = new JButton("Browse");// new
												// JButton(TextHolder.getString("host.browse"));
		inpKeyBrowse.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileHidingEnabled(false);

			jfc.addChoosableFileFilter(new FileNameExtensionFilter("Putty key files (*.ppk)", "ppk"));

			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				String selectedFile = jfc.getSelectedFile().getAbsolutePath();
				if (selectedFile.endsWith(".ppk")) {
					if (!isSupportedPuttyKeyFile(jfc.getSelectedFile())) {
						JOptionPane.showMessageDialog(this,
								"This key format is not supported, please convert it to OpenSSH format");
						return;
					}
				}
				inpKeyFile.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.insets = topInset;
		panel.add(lblHost, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.insets = noInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(inpHostName, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.insets = topInset;
		panel.add(lblPort, c);

		c.gridx = 0;
		c.gridy = 4;
		c.ipady = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = noInset;
		panel.add(inpPort, c);

		c.gridx = 0;
		c.gridy = 5;
		c.insets = topInset;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		panel.add(lblUser, c);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		c.insets = noInset;
		panel.add(inpUserName, c);

		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 2;
		c.insets = topInset;
		panel.add(lblPass, c);

		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 2;
		c.insets = noInset;
		panel.add(inpPassword, c);

		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 2;
		c.insets = topInset;
		panel.add(lblKeyFile, c);

		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		c.insets = noInset;
		c.weightx = 1;
		panel.add(inpKeyFile, c);

		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 10);
		panel.add(inpKeyBrowse, c);

		JPanel panel2 = new JPanel(new BorderLayout());
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 10;
		c.fill = GridBagConstraints.BOTH;
		panel.add(panel2, c);

		return panel;

	}

	private void updateHopMode() {
		if (radMultiHopPortForwarding.isSelected()) {
			info.setJumpType(JumpType.PortForwarding);
		} else {
			info.setJumpType(JumpType.TcpForwarding);
		}
	}

	private boolean isSupportedPuttyKeyFile(File file) {
		try {
			String content = Files.readString(file.toPath());
			if (content.contains("ssh-ed25519")) {
				return false;
			}
			if (content.contains("Encryption:")
					&& (content.contains("Encryption: aes256-cbc") || content.contains("Encryption: none"))) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
