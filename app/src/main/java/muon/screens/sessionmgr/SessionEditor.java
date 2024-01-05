package muon.screens.sessionmgr;

import muon.dto.session.NamedItem;
import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.util.DocumentChangeAdapter;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class SessionEditor extends JPanel {
    private JLabel lblName;
    private JTextField txtName;
    private JTabbedPane tabbedPanel;
    private JButton btnConnect, btnCancel;
    private SshInfoPanel sshInfoPanel;
    private ActionListener connectAction, cancelAction;
    private NamedItem selection;
    private Runnable callback;
    private int headerHeight;

    public SessionEditor(ActionListener connectAction, ActionListener cancelAction) {
        super(new BorderLayout());
        this.connectAction = connectAction;
        this.cancelAction = cancelAction;
        createUI();
    }

    public void setValue(NamedItem value, Runnable callback) {
        this.selection = value;
        this.callback = callback;
        if (value instanceof SessionInfo) {
            tabbedPanel.setVisible(true);
            SessionInfo info = (SessionInfo) value;
            tabbedPanel.setVisible(true);
            sshInfoPanel.setValue(info);
//            sessionInfoPanel.setSessionInfo(info);
//            selectedInfo = info;
            txtName.setVisible(true);
            lblName.setVisible(true);
            txtName.setText(info.getName());
            btnConnect.setVisible(true);
        } else if (value instanceof NamedItem) {
            //selectedInfo = (NamedItem) nodeInfo;
            lblName.setVisible(true);
            txtName.setVisible(true);
            txtName.setText(value.getName());
            tabbedPanel.setVisible(false);
            btnConnect.setVisible(false);
        }
    }

    private void updateName(String text) {
        selection.setName(text);
        callback.run();
    }

    public NamedItem getSelection() {
        return selection;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    private void createUI() {
        //this.setBackground(AppTheme.INSTANCE.getBackground());

        lblName = new JLabel("Name");
        txtName = new JTextField();
        sshInfoPanel = new SshInfoPanel();

        txtName.getDocument().addDocumentListener(new DocumentChangeAdapter(txtName, this::updateName));

        btnConnect = new JButton("Connect");
        btnCancel = new JButton("Cancel");

        btnConnect.addActionListener(this.connectAction);
        btnCancel.addActionListener(this.cancelAction);

        tabbedPanel = new JTabbedPane();

        tabbedPanel.addTab("SSH", sshInfoPanel);
        tabbedPanel.addTab("Proxy", new JPanel());
        tabbedPanel.addTab("Jump hosts", new JPanel());
        tabbedPanel.addTab("Port forwarding", new JPanel());
        tabbedPanel.setSelectedIndex(0);

        var hbox1 = Box.createHorizontalBox();
        hbox1.add(lblName);
        hbox1.add(Box.createRigidArea(new Dimension(10, 10)));
        hbox1.add(txtName);
        hbox1.setBorder(new EmptyBorder(8, 10, 8, 10));
        headerHeight = hbox1.getPreferredSize().height;

        var hbox2 = Box.createHorizontalBox();
        hbox2.add(Box.createHorizontalGlue());
        hbox2.add(btnConnect);
        hbox2.add(Box.createRigidArea(new Dimension(10, 10)));
        hbox2.add(btnCancel);
        hbox2.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.add(hbox1, BorderLayout.NORTH);
        this.add(tabbedPanel);
        this.add(hbox2, BorderLayout.SOUTH);

//        var gc = new GridBagConstraints();
//        gc.insets = new Insets(10, 10, 10, 5);
//        this.add(lblName, gc);
//
//        gc = new GridBagConstraints();
//        gc.gridx = 1;
//        gc.fill = GridBagConstraints.HORIZONTAL;
//        gc.insets = new Insets(10, 5, 10, 10);
//        gc.weightx = 1;
//        gc.gridwidth = 3;
//        this.add(txtName, gc);
//
//        gc = new GridBagConstraints();
//        gc.gridy = 1;
//        gc.gridwidth = 4;
//        gc.fill = GridBagConstraints.BOTH;
//        gc.weightx = 1;
//        gc.weighty = 1;
//        gc.anchor = GridBagConstraints.PAGE_START;
//
//
//
//        this.add(tabbedPanel, gc);
//
//        gc = new GridBagConstraints();
//        gc.gridy = 2;
//        gc.gridx = 1;
//        gc.gridwidth = 4;
//        gc.fill = GridBagConstraints.BOTH;
//        gc.weightx = 1;
//        gc.weighty = 1;
//        this.add(new JLabel(), gc);
//
//        gc = new GridBagConstraints();
//        gc.gridy = 3;
//        gc.gridx = 1;
//        gc.gridwidth = 1;
//        gc.weightx = 1;
//        this.add(new JLabel(), gc);
//
//        gc = new GridBagConstraints();
//        gc.insets = new Insets(10, 0, 10, 0);
//        gc.gridy = 3;
//        gc.gridx = 2;
//        gc.gridwidth = 1;
//        gc.weightx = 0;
//        gc.anchor = GridBagConstraints.LINE_END;
//        this.add(btnConnect, gc);
//
//        gc = new GridBagConstraints();
//        gc.gridy = 3;
//        gc.gridx = 3;
//        gc.gridwidth = 1;
//        gc.anchor = GridBagConstraints.LINE_END;
//        gc.insets = new Insets(10, 10, 10, 10);
//
//        this.add(btnCancel, gc);
    }
}
