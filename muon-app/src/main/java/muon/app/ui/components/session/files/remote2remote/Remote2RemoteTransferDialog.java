package muon.app.ui.components.session.files.remote2remote;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.App;
import muon.app.common.FileInfo;
import muon.app.common.FileType;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.NewSessionDlg;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;

public class Remote2RemoteTransferDialog extends JDialog {
	private DefaultListModel<RemoteServerEntry> remoteHostModel;
	private JList<RemoteServerEntry> remoteHostList;
	private SessionContentPanel session;
	private FileInfo[] selectedFiles;
	private String currentDirectory;

	public Remote2RemoteTransferDialog(JFrame frame, SessionContentPanel session, FileInfo[] selectedFiles,
			String currentDirectory) {
		super(frame);
		this.session = session;
		this.selectedFiles = selectedFiles;
		this.currentDirectory = currentDirectory;
		setSize(640, 480);
		setModal(true);

		remoteHostModel = new DefaultListModel<RemoteServerEntry>();
		remoteHostModel.addAll(load());
		remoteHostList = new JList<RemoteServerEntry>(remoteHostModel);
		remoteHostList.setCellRenderer(new RemoteHostRenderer());
		this.add(new SkinnedScrollPane(remoteHostList));

		Box bottom = Box.createHorizontalBox();
		JButton btnAddKnown = new JButton("Add from session manager");
		JButton btnAdd = new JButton("Add");
		JButton btnRemove = new JButton("Delete");
		JButton btnEdit = new JButton("Edit");
		JButton btnSend = new JButton("Send");

		btnAddKnown.addActionListener(e -> {
			SessionInfo info = new NewSessionDlg(this).newSession();
			if (info != null) {
				RemoteServerEntry ent = getEntryDetails(info.getHost(), info.getUser(), info.getRemoteFolder(),
						info.getPort());
				if (ent != null) {
					remoteHostModel.insertElementAt(ent, 0);
					save();
					remoteHostList.setSelectedIndex(0);
				}
			}
		});

		btnSend.addActionListener(e -> {
			int index = remoteHostList.getSelectedIndex();
			if (index != -1) {
				RemoteServerEntry ent = remoteHostModel.get(index);
				this.dispose();
				this.session.openTerminal(generateCommand(ent));
			}
		});

		bottom.add(btnRemove);
		bottom.add(Box.createHorizontalStrut(5));
		bottom.add(btnEdit);
		bottom.add(Box.createHorizontalStrut(5));
		bottom.add(btnAdd);
		bottom.add(Box.createHorizontalStrut(5));
		bottom.add(btnAddKnown);
		bottom.add(Box.createHorizontalGlue());
		bottom.add(btnSend);
		bottom.setBorder(new EmptyBorder(10, 10, 10, 10));

		this.add(bottom, BorderLayout.SOUTH);
	}

	private RemoteServerEntry getEntryDetails(String host, String user, String path, int port) {
		JTextField txtHost = new SkinnedTextField(30);
		JTextField txtUser = new SkinnedTextField(30);
		JTextField txtPath = new SkinnedTextField(30);
		JSpinner spPort = new JSpinner(new SpinnerNumberModel(port, 1, 65535, 1));

		if (host != null) {
			txtHost.setText(host);
		}
		if (user != null) {
			txtUser.setText(user);
		}
		if (path != null) {
			txtPath.setText(path);
		}
		if (port > 0) {
			spPort.setValue(port);
		}

		while (JOptionPane.showOptionDialog(this,
				new Object[] { "Host", txtHost, "User", txtUser, "Copy to ( target directory)", txtPath, "Port",
						spPort },
				"Remote host details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.OK_OPTION) {
			host = txtHost.getText();
			user = txtUser.getText();
			path = txtPath.getText();
			port = (Integer) spPort.getValue();
			if (host.length() < 1 || user.length() < 1 || path.length() < 1 || port <= 0) {
				JOptionPane.showMessageDialog(this, "Invalid input: all fields mandatory");
				continue;
			}
			return new RemoteServerEntry(host, port, user, path);
		}
		return null;
	}

	private String generateCommand(RemoteServerEntry e) {
		return createSftpFileList(e);
//		System.out.println(createSftpFileList(e));
//		StringBuilder files = new StringBuilder();
//		for (FileInfo finfo : selectedFiles) {
//			files.append("'" + finfo.getPath() + "' ");
//		}
//		String command = String.format("cd '%s' && tar -cvf - %s | ssh %s@%s <<EOF\ncd '%s' && tar -xvf -",
//				this.currentDirectory, files.toString(), e.getUser(), e.getHost(), e.getPath());
//		return command;
	}

	private String createSftpFileList(RemoteServerEntry e) {
		StringBuilder sb = new StringBuilder();
		sb.append("sftp " + e.getUser() + "@" + e.getHost() + "<<EOF\n");
		sb.append("lcd \"" + this.currentDirectory + "\"\n");
		sb.append("cd \"" + e.getPath() + "\"\n");

		for (FileInfo finfo : selectedFiles) {
			if (finfo.getType() == FileType.Directory) {
				sb.append("mkdir \"" + finfo.getName() + "\"\n");
				sb.append("put -r \"" + finfo.getName() + "\"\n");
			} else if (finfo.getType() == FileType.File) {
				sb.append("put -P \"" + finfo.getName() + "\"\n");
			}
		}
		sb.append("bye\n");
		sb.append("EOF\n");
		return sb.toString();
	}

	private void save() {
		List<RemoteServerEntry> list = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			list.add(remoteHostModel.get(i));
		}
		File file = new File(App.CONFIG_DIR, App.TRANSFER_HOSTS);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<RemoteServerEntry> load() {
		File file = new File(App.CONFIG_DIR, App.TRANSFER_HOSTS);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				return objectMapper.readValue(file, new TypeReference<List<RemoteServerEntry>>() {
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<>();
	}
}
