package muonssh.app.ui.components.session.terminal.snippets;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import muonssh.app.App;
import muonssh.app.ui.components.SkinnedTextField;
import util.FontAwesomeContants;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static muonssh.app.App.bundle;

public class SnippetPanel extends JPanel {
	private final DefaultListModel<SnippetItem> listModel = new DefaultListModel<>();
	private final List<SnippetItem> snippetList = new ArrayList<>();
	private final JList<SnippetItem> listView = new JList<>(listModel);
	private final JTextField searchTextField;
	private final JButton btnCopy;
	private final JButton btnInsert;
	private final JButton btnAdd;
	private final JButton btnEdit;
	private final JButton btnDel;

	public SnippetPanel(Consumer<String> callback, Consumer<String> callback2) {
		super(new BorderLayout());
		setBorder(new LineBorder(App.SKIN.getDefaultBorderColor(), 1));
		Box topBox = Box.createHorizontalBox();
		topBox.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 1, 0, App.SKIN.getDefaultBorderColor()),
				new EmptyBorder(10, 10, 10, 10)));
		JLabel lblSearch = new JLabel();
		lblSearch.setFont(App.SKIN.getIconFont());
		lblSearch.setText(FontAwesomeContants.FA_SEARCH);
		topBox.add(lblSearch);
		topBox.add(Box.createHorizontalStrut(10));

		searchTextField = new SkinnedTextField(30);// new
													// JTextField(30);
		searchTextField.getDocument()
				.addDocumentListener(new DocumentListener() {
					@Override
					public void insertUpdate(DocumentEvent e) {
						filter();
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						filter();
					}

					@Override
					public void changedUpdate(DocumentEvent e) {
						filter();
					}
				});
		topBox.add(searchTextField);

		listView.setCellRenderer(new SnippetListRenderer());
		listView.setBackground(App.SKIN.getTableBackgroundColor());

		btnAdd = new JButton(bundle.getString("add"));
		btnEdit = new JButton(bundle.getString("edit"));
		btnDel = new JButton(bundle.getString("delete"));
		btnInsert = new JButton(bundle.getString("insert"));
		btnCopy = new JButton(bundle.getString("copy"));

		btnAdd.addActionListener(e -> {
			JTextField txtName = new SkinnedTextField(30);// new
															// JTextField(30);
			JTextField txtCommand = new SkinnedTextField(30);// new
																// JTextField(30);

			if (JOptionPane.showOptionDialog(null,
					new Object[] { "Snippet name", txtName, "Command",
							txtCommand },
					"New snippet", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.OK_OPTION) {
				if (txtCommand.getText().length() < 1
						|| txtName.getText().length() < 1) {
					JOptionPane.showMessageDialog(null,
							"Please enter name and command");
					return;
				}
				App.SNIPPET_MANAGER.getSnippetItems().add(new SnippetItem(
						txtName.getText(), txtCommand.getText()));
				App.SNIPPET_MANAGER.saveSnippets();
			}
			callback2.accept(null);
		});

		btnEdit.addActionListener(e -> {
			int index = listView.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null,
						"Please select an item to edit");
				return;
			}

			SnippetItem snippetItem = listModel.get(index);

			JTextField txtName = new SkinnedTextField(30);// new
															// JTextField(30);
			JTextField txtCommand = new SkinnedTextField(30);// new
																// JTextField(30);

			txtName.setText(snippetItem.getName());
			txtCommand.setText(snippetItem.getCommand());

			if (JOptionPane.showOptionDialog(null,
					new Object[] { "Snippet name", txtName, "Command",
							txtCommand },
					"New snippet", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.OK_OPTION) {
				if (txtCommand.getText().length() < 1
						|| txtName.getText().length() < 1) {
					JOptionPane.showMessageDialog(null,
							"Please enter name and command");
					return;
				}
				snippetItem.setCommand(txtCommand.getText());
				snippetItem.setName(txtName.getText());
				App.SNIPPET_MANAGER.saveSnippets();
			}
			callback2.accept(null);
		});

		btnDel.addActionListener(e -> {
			int index = listView.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, "Please select an item");
				return;
			}

			SnippetItem snippetItem = listModel.get(index);
			App.SNIPPET_MANAGER.getSnippetItems().remove(snippetItem);
			App.SNIPPET_MANAGER.saveSnippets();
			loadSnippets();
			callback2.accept(null);
		});

		btnCopy.addActionListener(e -> {
			int index = listView.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, "Please select an item");
				return;
			}

			SnippetItem snippetItem = listModel.get(index);

			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(snippetItem.getCommand()), null);
			callback2.accept(null);
		});

		btnInsert.addActionListener(e -> {
			int index = listView.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(null, "Please select an item");
				return;
			}

			SnippetItem snippetItem = listModel.get(index);
			callback.accept(snippetItem.getCommand());
			callback2.accept(null);
		});

		Box bottomBox = Box.createHorizontalBox();
		bottomBox.setBorder(new CompoundBorder(
				new MatteBorder(1, 0, 0, 0, App.SKIN.getDefaultBorderColor()),
				new EmptyBorder(10, 10, 10, 10)));
		bottomBox.add(btnInsert);
		bottomBox.add(Box.createHorizontalStrut(5));
		bottomBox.add(btnCopy);
		bottomBox.add(Box.createHorizontalGlue());
		bottomBox.add(Box.createHorizontalStrut(5));
		bottomBox.add(btnAdd);
		bottomBox.add(Box.createHorizontalStrut(5));
		bottomBox.add(btnEdit);
		bottomBox.add(Box.createHorizontalStrut(5));
		bottomBox.add(btnDel);

		setPreferredSize(new Dimension(400, 500));
		add(topBox, BorderLayout.NORTH);
		JScrollPane jScrollPane = new JScrollPane(listView);
		//jScrollPane.setBorder(null);
		add(jScrollPane);
		add(bottomBox, BorderLayout.SOUTH);

	}

	public void loadSnippets() {
		this.snippetList.clear();
		this.snippetList.addAll(App.SNIPPET_MANAGER.getSnippetItems());
		System.out.println("Snippet size: " + snippetList.size());
		filter();
	}

	private void filter() {
		this.listModel.clear();
		String text = searchTextField.getText().trim();
		if (text.length() < 1) {
			this.listModel.addAll(this.snippetList);
			return;
		}
		for (SnippetItem item : snippetList) {
			if (item.getCommand().contains(text)
					|| item.getName().contains(text)) {
				this.listModel.addElement(item);
			}
		}
	}
}
