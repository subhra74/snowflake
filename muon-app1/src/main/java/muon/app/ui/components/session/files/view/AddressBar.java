package muon.app.ui.components.session.files.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import muon.app.App;
import muon.app.ui.components.SkinnedTextField;
import util.LayoutUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddressBar extends JPanel {
	// private AddressBreadCrumbView addressBar;
	private AddressBarBreadCrumbs addressBar;
	private JComboBox<String> txtAddressBar;
	private JButton btnEdit;
	private JPanel addrPanel;
	private boolean updating = false;
	private ActionListener a;
	private JPopupMenu popup;
	private char separator;
	private JPanel panBtn2;

	public AddressBar(char separator, ActionListener popupTriggeredListener) {
		setLayout(new BorderLayout());
		addrPanel = new JPanel(new BorderLayout());
		addrPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		this.separator = separator;

		UIDefaults toolbarSkin = App.SKIN.createToolbarSkin();

		JButton btnRoot = new JButton();
		btnRoot.putClientProperty("Nimbus.Overrides", toolbarSkin);
		btnRoot.setFont(App.SKIN.getIconFont());
		// btnRoot.setForeground(Color.DARK_GRAY);
		btnRoot.setText("\uf0a0");
		btnRoot.addActionListener(e -> {
			createAndShowPopup();
		});

		DefaultComboBoxModel<String> model1 = new DefaultComboBoxModel<>();
		txtAddressBar = new JComboBox<>(model1);

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
		ComboBoxEditor cmdEdit = new BasicComboBoxEditor() {
			@Override
			protected JTextField createEditorComponent() {
				JTextField textField = new SkinnedTextField(10);
				textField.setBorder(new LineBorder(Color.black, 0));
				textField.setName("ComboBox.textField");
				return textField;
			}
		};
		txtAddressBar.setEditor(cmdEdit);
		System.out.println("Editor: " + txtAddressBar.getEditor());
		addressBar = new AddressBarBreadCrumbs(separator == '/',
				popupTriggeredListener);
		addressBar.addActionListener(e -> {
			if (a != null) {
				System.out.println("Performing action");
				a.actionPerformed(
						new ActionEvent(this, 0, e.getActionCommand()));
			}
		});

		panBtn2 = new JPanel(new BorderLayout());
		panBtn2.setBorder(new EmptyBorder(3, 3, 3, 3));

		btnEdit = new JButton();
		btnEdit.putClientProperty("Nimbus.Overrides", toolbarSkin);
		btnEdit.setFont(App.SKIN.getIconFont());
		// btnEdit.setForeground(Color.DARK_GRAY);
		btnEdit.setText("\uf023");
		// btnEdit.setBorder(new EmptyBorder(0,5,0,5));
//        btnEdit.setMargin(new Insets(0, 0, 0, 0));
//        btnEdit.setBorderPainted(false);
		// btnEdit.setContentAreaFilled(false);
		// btnEdit.setFocusPainted(false);
		btnEdit.addActionListener(e -> {
			if (!isSelected()) {
				switchToText();
			} else {
				switchToPathBar();
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

		LayoutUtilities.equalizeSize(btnRoot, btnEdit);

		panBtn2.add(btnRoot);

		addrPanel.add(addressBar);
		add(addrPanel);
		JPanel panBtn = new JPanel(new BorderLayout());
		panBtn.setBorder(new EmptyBorder(3, 3, 3, 3));
		panBtn.add(btnEdit);
		add(panBtn, BorderLayout.EAST);
		add(panBtn2, BorderLayout.WEST);
		btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
	}

	public void switchToPathBar() {
		add(panBtn2, BorderLayout.WEST);
		addrPanel.remove(txtAddressBar);
		addrPanel.add(addressBar);
		btnEdit.setIcon(UIManager.getIcon("AddressBar.edit"));
		btnEdit.putClientProperty("toggle.selected", Boolean.FALSE);
		btnEdit.setText("\uf023");
	}

	public void switchToText() {
		addrPanel.remove(addressBar);
		addrPanel.add(txtAddressBar);
		remove(panBtn2);
		btnEdit.setIcon(UIManager.getIcon("AddressBar.toggle"));
		btnEdit.putClientProperty("toggle.selected", Boolean.TRUE);
		txtAddressBar.getEditor().selectAll();
		btnEdit.setText("\uf13e");
	}

	public String getText() {
		return isSelected() ? (String) txtAddressBar.getSelectedItem()
				: addressBar.getSelectedText();
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

	private void createAndShowPopup() {
		if (popup == null) {
			popup = new JPopupMenu();
		} else {
			popup.removeAll();
		}

		if (separator == '/') {
			JMenuItem item = new JMenuItem("ROOT");
			item.putClientProperty("item.path", "/");
			item.addActionListener(e -> {
				String selectedText = (String) item
						.getClientProperty("item.path");
				if (a != null) {
					a.actionPerformed(new ActionEvent(this, 0, selectedText));
				}
			});
			popup.add(item);
		} else {
			File[] roots = File.listRoots();
			for (File f : roots) {
				JMenuItem item = new JMenuItem(f.getAbsolutePath());
				item.putClientProperty("item.path", f.getAbsolutePath());
				item.addActionListener(e -> {
					String selectedText = (String) item
							.getClientProperty("item.path");
					if (a != null) {
						a.actionPerformed(
								new ActionEvent(this, 0, selectedText));
					}
				});
				popup.add(item);
			}
		}

		popup.show(this, 0, getHeight());
	}
}
