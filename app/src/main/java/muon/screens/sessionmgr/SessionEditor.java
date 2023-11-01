package muon.screens.sessionmgr;

import muon.dto.session.NamedItem;
import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.util.DocumentChangeAdapter;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class SessionEditor extends JPanel {
    private JLabel lblName;
    private JTextField txtName;
    private TabbedPanel tabbedPanel;
    private JButton btnConnect, btnCancel;
    private SshInfoPanel sshInfoPanel;
    private ActionListener connectAction, cancelAction;
    private NamedItem selection;
    private Runnable callback;

    public SessionEditor(ActionListener connectAction, ActionListener cancelAction) {
        super(new GridBagLayout());
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

    private void createUI() {
        this.setBackground(AppTheme.INSTANCE.getBackground());

        lblName = new JLabel("Name");
        txtName = new JTextField();
        sshInfoPanel = new SshInfoPanel();

        txtName.getDocument().addDocumentListener(new DocumentChangeAdapter(txtName, this::updateName));

        btnConnect = new JButton("Connect");
        btnCancel = new JButton("Cancel");

        btnConnect.addActionListener(this.connectAction);
        btnCancel.addActionListener(this.cancelAction);

        var gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 5);
        this.add(lblName, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10, 5, 10, 10);
        gc.weightx = 1;
        gc.gridwidth = 3;
        this.add(txtName, gc);

        gc = new GridBagConstraints();
        gc.gridy = 1;
        gc.gridwidth = 4;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.PAGE_START;

        tabbedPanel = new TabbedPanel(
                false,
                false,
                new Color(52, 117, 233),
                AppTheme.INSTANCE.getDarkControlBackground(),
                new Color(52, 117, 233),
                new Color(100, 100, 100),
                AppTheme.INSTANCE.getBackground(),
                AppTheme.INSTANCE.getDarkControlBackground(),
                new Color(130, 130, 130),
                new Color(180, 180, 180),
                null,
                AppTheme.INSTANCE.getSplitPaneBackground(),
                null,
                false,
                true,
                false
        );

        tabbedPanel.addTab("SSH", null, sshInfoPanel);
        tabbedPanel.addTab("Proxy", null, new JPanel());
        tabbedPanel.addTab("Jump hosts", null, new JPanel());
        tabbedPanel.addTab("Port forwarding", null, new JPanel());
        tabbedPanel.setSelectedIndex(0);

        this.add(tabbedPanel, gc);

        gc = new GridBagConstraints();
        gc.gridy = 2;
        gc.gridx = 1;
        gc.gridwidth = 4;
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        this.add(new JLabel(), gc);

        gc = new GridBagConstraints();
        gc.gridy = 3;
        gc.gridx = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        this.add(new JLabel(), gc);

        gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 0);
        gc.gridy = 3;
        gc.gridx = 2;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        this.add(btnConnect, gc);

        gc = new GridBagConstraints();
        gc.gridy = 3;
        gc.gridx = 3;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(10, 10, 10, 10);

        this.add(btnCancel, gc);
    }
}
