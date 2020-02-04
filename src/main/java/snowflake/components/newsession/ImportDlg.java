package snowflake.components.newsession;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

public class ImportDlg extends JDialog {
	private JComboBox<String> items;
	private JList<String> sessionList;
	private DefaultListModel<String> model;

	public ImportDlg(Window w, DefaultMutableTreeNode node) {
		super(w);
		setSize(400, 300);
		setLocationRelativeTo(w);
		setModal(true);
		model = new DefaultListModel<>();
		sessionList = new JList<>(model);
		items = new JComboBox<String>(new String[] { "Putty", "WinSCP" });
		items.addActionListener(e -> {
			int index = items.getSelectedIndex();
			switch (index) {
			case 0:
				importFromPutty();
				break;
			case 1:
				importFromWinScp();
				break;
			}

		});
		add(new JScrollPane(sessionList));
		Box b1 = Box.createHorizontalBox();
		b1.setBorder(new EmptyBorder(5, 5, 5, 5));
		b1.add(new JLabel("Import from"));
		b1.add(items);
		add(b1, BorderLayout.NORTH);

		Box b2 = Box.createHorizontalBox();

		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(e -> {
			int index = items.getSelectedIndex();
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

		importFromPutty();
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
		for (int i = 0; i < model.size(); i++) {
			list.add(model.get(i));
		}

		PuttyImporter.importSessions(node, list);
//		SessionFolder folder = SessionStore.load().getFolder();
//		folder.getItems().addAll(sessions);
//		SessionStore.store(folder);
	}

	private void importSessionsFromWinScp(DefaultMutableTreeNode node) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < model.size(); i++) {
			list.add(model.get(i));
		}

		WinScpImporter.importSessions(node, list);
//		SessionFolder folder = SessionStore.load().getFolder();
//		folder.getItems().addAll(sessions);
//		SessionStore.store(folder);
	}
}
