package snowflake.components.common;

import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddressBar extends JPanel {
    //private AddressBreadCrumbView addressBar;
    private AddressBarBreadCrumbs addressBar;
    private JComboBox<String> txtAddressBar;
    private DefaultComboBoxModel<String> model;
    private JButton btnEdit;
    private JPanel addrPanel;
    private boolean updating = false;
    private ActionListener a;

    public AddressBar(char separator, ActionListener popupTriggeredListener) {
        setLayout(new BorderLayout());
        addrPanel = new JPanel(new BorderLayout());
        addrPanel.setBorder(
                new EmptyBorder(3, 3, 3, 3));
        model = new DefaultComboBoxModel<>();
        txtAddressBar = new JComboBox<>(model);
        txtAddressBar.addActionListener(e -> {
            if (updating) {
                return;
            }
            System.out.println("calling action listener");
            String item = (String) txtAddressBar.getSelectedItem();
            if (e.getActionCommand().equals("comboBoxEdited")) {
                System.out.println("Editted");
                ComboBoxModel<String> model = txtAddressBar.getModel();
                boolean found = false;
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(item)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    txtAddressBar.addItem(item);
                }
                if (a != null) {
                    a.actionPerformed(new ActionEvent(this, 0, item));
                }
            }
        });
        txtAddressBar.setEditable(true);
        addressBar = new AddressBarBreadCrumbs(separator == '/', popupTriggeredListener);
        addressBar.addActionListener(e -> {
            if (a != null) {
                System.out.println("Performing action");
                a.actionPerformed(new ActionEvent(this, 0, e.getActionCommand()));
            }
        });
        btnEdit = new JButton();
        btnEdit.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnEdit.setFont(App.getFontAwesomeFont());
        btnEdit.setForeground(Color.DARK_GRAY);
        btnEdit.setText("\uf023");
        //btnEdit.setBorder(new EmptyBorder(0,5,0,5));
//        btnEdit.setMargin(new Insets(0, 0, 0, 0));
//        btnEdit.setBorderPainted(false);
        //btnEdit.setContentAreaFilled(false);
        // btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(e -> {
            if (!isSelected()) {
                addrPanel.remove(addressBar);
                addrPanel.add(txtAddressBar);
                btnEdit.setIcon(UIManager.getIcon("AddressBar.toggle"));
                btnEdit.putClientProperty("toggle.selected", Boolean.TRUE);
                txtAddressBar.getEditor().selectAll();
                btnEdit.setText("\uf13e");
            } else {
                addrPanel.remove(txtAddressBar);
                addrPanel.add(addressBar);
                btnEdit.setIcon(UIManager.getIcon("AddressBar.edit"));
                btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
                btnEdit.setText("\uf023");
            }
            revalidate();
            repaint();
        });

//        btnEdit.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (!isSelected()) {
//                    addrPanel.remove(addressBar);
//                    addrPanel.add(txtAddressBar);
//                    btnEdit.setIcon(UIManager.getIcon("AddressBar.toggle"));
//                    btnEdit.putClientProperty("toggle.selected", Boolean.TRUE);
//                    txtAddressBar.getEditor().selectAll();
//                } else {
//                    addrPanel.remove(txtAddressBar);
//                    addrPanel.add(addressBar);
//                    btnEdit.setIcon(UIManager.getIcon("AddressBar.edit"));
//                    btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
//                }
//                revalidate();
//                repaint();
//            }
//        });
//        btnEdit.addActionListener(e -> {
//            if (!isSelected()) {
//                addrPanel.remove(addressBar);
//                addrPanel.add(txtAddressBar);
//                btnEdit.setIcon(UIManager.getIcon("AddressBar.toggle"));
//                btnEdit.putClientProperty("toggle.selected", Boolean.TRUE);
//                txtAddressBar.getEditor().selectAll();
//            } else {
//                addrPanel.remove(txtAddressBar);
//                addrPanel.add(addressBar);
//                btnEdit.setIcon(UIManager.getIcon("AddressBar.edit"));
//                btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
//            }
//            revalidate();
//            repaint();
//        });
        addrPanel.add(addressBar);
        add(addrPanel);
        JPanel panBtn = new JPanel(new BorderLayout());
        panBtn.setBorder(new EmptyBorder(3, 3, 3, 3));
        panBtn.add(btnEdit);
        add(panBtn, BorderLayout.EAST);
        btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
    }

    public String getText() {
        return isSelected() ? (String) txtAddressBar.getSelectedItem() : addressBar.getSelectedText();
    }

    public void setText(String text) {
        System.out.println("Setting text: " + text);
        updating = true;
        txtAddressBar.setSelectedItem(text);
        addressBar.setPath(text);
        updating = false;
        System.out.println("Setting text done: " + text);
    }

    public void addActionListener(ActionListener e) {
        this.a = e;
    }

    private boolean isSelected() {
        return btnEdit.getClientProperty("toggle.selected") == Boolean.TRUE;
    }
}
