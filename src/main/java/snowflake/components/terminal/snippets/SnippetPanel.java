package snowflake.components.terminal.snippets;

import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

public class SnippetPanel extends JPanel {
    private DefaultListModel<SnippetItem> listModel = new DefaultListModel<>();
    private JList<SnippetItem> listView = new JList<>(listModel);
    private JTextField searchTextField;
    private JButton btnCopy, btnInsert, btnAdd, btnEdit, btnDel;

    public SnippetPanel(Consumer<String> callback, Consumer<String> callback2) {
        super(new BorderLayout());
        Box topBox = Box.createHorizontalBox();
        JLabel lblSearch = new JLabel();
        lblSearch.setFont(App.getFontAwesomeFont());
        lblSearch.setText("\uf002");
        topBox.add(lblSearch);

        listView.setCellRenderer(new SnippetListRenderer());

        searchTextField = new JTextField(30);
        topBox.add(searchTextField);

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDel = new JButton("Delete");
        btnInsert = new JButton("Insert");
        btnCopy = new JButton("Copy");

        btnAdd.addActionListener(e -> {
            JTextField txtName = new JTextField(30);
            JTextField txtCommand = new JTextField(30);

            if (JOptionPane.showOptionDialog(null,
                    new Object[]{"Snippet name", txtName, "Command", txtCommand},
                    "NEw snippet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                if (txtCommand.getText().length() < 1 || txtName.getText().length() < 1) {
                    JOptionPane.showMessageDialog(null, "Please enter name and command");
                    return;
                }
                App.getSnippetItems().add(new SnippetItem(txtName.getText(), txtCommand.getText()));
                App.saveSnippets();
            }
            callback2.accept(null);
        });

        btnEdit.addActionListener(e -> {
            int index = listView.getSelectedIndex();
            if (index < 0) {
                return;
            }

            SnippetItem snippetItem = listModel.get(index);

            JTextField txtName = new JTextField(30);
            JTextField txtCommand = new JTextField(30);

            txtName.setText(snippetItem.getName());
            txtCommand.setText(snippetItem.getCommand());

            if (JOptionPane.showOptionDialog(null,
                    new Object[]{"Snippet name", txtName, "Command", txtCommand},
                    "NEw snippet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                if (txtCommand.getText().length() < 1 || txtName.getText().length() < 1) {
                    JOptionPane.showMessageDialog(null, "Please enter name and command");
                    return;
                }
                snippetItem.setCommand(txtCommand.getText());
                snippetItem.setName(txtName.getText());
                App.saveSnippets();
            }
            callback2.accept(null);
        });

        btnDel.addActionListener(e -> {
            int index = listView.getSelectedIndex();
            if (index < 0) {
                return;
            }

            SnippetItem snippetItem = listModel.get(index);
            App.getSnippetItems().remove(snippetItem);
            App.saveSnippets();
            loadSnippets();
            callback2.accept(null);
        });

        btnCopy.addActionListener(e -> {
            int index = listView.getSelectedIndex();
            if (index < 0) {
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
                return;
            }

            SnippetItem snippetItem = listModel.get(index);
            callback.accept(snippetItem.getCommand());
            callback2.accept(null);
        });

        Box bottomBox = Box.createHorizontalBox();
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
        bottomBox.setBorder(new EmptyBorder(5, 5, 5, 5));

        setPreferredSize(new Dimension(400, 500));
        add(topBox, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(listView);
        add(jScrollPane);
        add(bottomBox, BorderLayout.SOUTH);

    }

    public void loadSnippets() {
        this.listModel.clear();
        System.out.println("Snippet size: " + App.getSnippetItems().size());
        this.listModel.addAll(App.getSnippetItems());
    }
}
