package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

public class ImportDlg extends JDialog {
	// private JComboBox<String> items;
	private JList<String> sessionList;
	private DefaultListModel<String> model;

	public ImportDlg(Window w, int index, DefaultMutableTreeNode node) {
		super(w);
		setSize(400, 300);
		setLocationRelativeTo(w);
		setModal(true);
		model = new DefaultListModel<>();
		sessionList = new JList<>(model);
		sessionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		switch (index) {
		case 0:
			importFromPutty();
			break;
		case 1:
			importFromWinScp();
			break;
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(new JScrollPane(sessionList));
		add(panel);

		Box b2 = Box.createHorizontalBox();
		b2.setBorder(new EmptyBorder(0, 5, 5, 5));

		JButton btnSelect = new JButton("Select all");
		btnSelect.addActionListener(e -> {
			int arr[] = new int[model.size()];
			for (int i = 0; i < model.size(); i++) {
				arr[i] = i;
			}
			sessionList.setSelectedIndices(arr);
		});

		JButton btnUnSelect = new JButton("Un-select all");
		btnUnSelect.addActionListener(e -> {
			int arr[] = new int[0];
			sessionList.setSelectedIndices(arr);
		});

		b2.add(btnSelect);
		b2.add(Box.createRigidArea(new Dimension(5, 5)));
		b2.add(btnUnSelect);

		b2.add(Box.createHorizontalGlue());

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(e -> {

			switch (index) {
			case 0:
				importSessionsFromPutty(node);
				break;
			case 1:
				importSessionsFromWinScp(node);
				break;
			}

			dispose();
		});

		b2.add(btnImport);

		add(b2, BorderLayout.SOUTH);

		//importFromPutty();
	}

	private void importFromPutty() {
		model.clear();
		model.addAll(PuttyImporter.getKeyNames().keySet());
	}

	private void importFromWinScp() {
		model.clear();
		model.addAll(WinScpImporter.getKeyNames().keySet());
	}

	private void importSessionsFromPutty(DefaultMutableTreeNode node) {
		List<String> list = new ArrayList<String>();
		int arr[] = sessionList.getSelectedIndices();
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				list.add(model.get(arr[i]));
			}
		}

		PuttyImporter.importSessions(node, list);
//		SessionFolder folder = SessionStore.load().getFolder();
//		folder.getItems().addAll(sessions);
//		SessionStore.store(folder);
	}

	private void importSessionsFromWinScp(DefaultMutableTreeNode node) {
		List<String> list = new ArrayList<String>();

		int arr[] = sessionList.getSelectedIndices();
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				list.add(model.get(arr[i]));
			}
		}

		WinScpImporter.importSessions(node, list);
//		SessionFolder folder = SessionStore.load().getFolder();
//		folder.getItems().addAll(sessions);
//		SessionStore.store(folder);
	}
}
