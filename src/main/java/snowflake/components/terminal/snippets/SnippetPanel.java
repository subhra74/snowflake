package snowflake.components.terminal.snippets;

import snowflake.App;
import snowflake.utils.GraphicsUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SnippetPanel extends JPanel {
    private DefaultListModel<SnippetItem> listModel = new DefaultListModel<>();
    private List<SnippetItem> snippetList = new ArrayList<>();
    private JList<SnippetItem> listView = new JList<>(listModel);
    private JTextField searchTextField;
    private JButton btnCopy, btnInsert, btnAdd, btnEdit, btnDel;

    public SnippetPanel(Consumer<String> callback, Consumer<String> callback2) {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        Box topBox = Box.createHorizontalBox();
        topBox.add(Box.createHorizontalStrut(10));
        JLabel lblSearch = new JLabel();
        lblSearch.setFont(App.getFontAwesomeFont());
        lblSearch.setText("\uf002");
        topBox.add(lblSearch);
        topBox.add(Box.createHorizontalStrut(10));
        listView.setCellRenderer(new SnippetListRenderer());

        searchTextField = GraphicsUtils.createTextField(30);//new JTextField(30);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
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

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDel = new JButton("Delete");
        btnInsert = new JButton("Insert");
        btnCopy = new JButton("Copy");

        btnAdd.addActionListener(e -> {
            JTextField txtName = GraphicsUtils.createTextField(30);//new JTextField(30);
            JTextField txtCommand = GraphicsUtils.createTextField(30);//new JTextField(30);

            if (JOptionPane.showOptionDialog(null,
                    new Object[]{"Snippet name", txtName, "Command", txtCommand},
                    "New snippet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
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
                JOptionPane.showMessageDialog(null, "Please select an item to edit");
                return;
            }

            SnippetItem snippetItem = listModel.get(index);

            JTextField txtName = GraphicsUtils.createTextField(30);//new JTextField(30);
            JTextField txtCommand = GraphicsUtils.createTextField(30);//new JTextField(30);

            txtName.setText(snippetItem.getName());
            txtCommand.setText(snippetItem.getCommand());

            if (JOptionPane.showOptionDialog(null,
                    new Object[]{"Snippet name", txtName, "Command", txtCommand},
                    "New snippet", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
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
                JOptionPane.showMessageDialog(null, "Please select an item");
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
        jScrollPane.setBorder(null);
        add(jScrollPane);
        add(bottomBox, BorderLayout.SOUTH);

    }

    public void loadSnippets() {
        this.snippetList.clear();
        this.snippetList.addAll(App.getSnippetItems());
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
            if (item.getCommand().contains(text) || item.getName().contains(text)) {
                this.listModel.addElement(item);
            }
        }
    }
}
