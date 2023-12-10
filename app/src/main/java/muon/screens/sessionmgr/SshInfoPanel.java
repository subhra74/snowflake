package muon.screens.sessionmgr;

import muon.constants.AuthMode;
import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.util.DocumentChangeAdapter;
import muon.util.IconCode;
import muon.util.IconFont;
import muon.util.NumericDocumentFilter;
import muon.widgets.SwitchButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

public class SshInfoPanel extends JPanel {
    private JTextField txtHost, txtPort, txtUser,
            txtKeyFile, txtRemoteFolder, txtLocalFolder;
    private JPasswordField txtPass;
    private SwitchButton swCombinedMode;
    private JComboBox<String> cmbStartPage, cmbAuthMethod, cmbIdentity;
    private SessionInfo sessionInfo;
    private JLabel lblPassword, lblKeyFile, lblIdentity, lblUserName;
    private JButton btnBrowseKey, btnEditIdentities;

    public SshInfoPanel() {
        super(new GridBagLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(AppTheme.INSTANCE.getBackground());

        var cmbFont = new Font(Font.DIALOG, Font.PLAIN, 12);

        txtHost = new JTextField();
        txtPort = new JTextField();
        cmbAuthMethod = new JComboBox<>(new String[]{"Password based", "Key based", "Saved credentials"});
        cmbAuthMethod.setFont(cmbFont);
        cmbIdentity = new JComboBox<>(new String[]{});
        cmbIdentity.setFont(cmbFont);
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        txtKeyFile = new JTextField();
        txtRemoteFolder = new JTextField();
        txtLocalFolder = new JTextField();
        cmbStartPage = new JComboBox<>(new String[]{"SFTP+Terminal", "SFTP", "Terminal", "Port forwarding"});
        swCombinedMode = new SwitchButton();

        cmbStartPage.setFont(cmbFont);
        txtPass.setEchoChar('*');

        cmbAuthMethod.addActionListener(e -> {
            var index = cmbAuthMethod.getSelectedIndex();
            updateAuthMethod(index);
            sessionInfo.setAuthMode(index);
        });

        createUI();

        PlainDocument doc = (PlainDocument) txtPort.getDocument();
        doc.setDocumentFilter(new NumericDocumentFilter());

        attachTextListener(txtHost, text -> sessionInfo.setHost(text));
        attachTextListener(txtPort, text -> {
            try {
                var port = Integer.parseInt(text);
                sessionInfo.setPort(port);
            } catch (Exception ex) {
            }
        });
        attachTextListener(txtUser, text -> sessionInfo.setUser(text));
        attachTextListener(txtKeyFile, text -> sessionInfo.setPrivateKeyFile(text));
        attachTextListener(txtRemoteFolder, text -> sessionInfo.setRemoteFolder(text));
        attachTextListener(txtLocalFolder, text -> sessionInfo.setLocalFolder(text));

        attachPasswordListener(txtPass, password -> {
            sessionInfo.setPassword(new String(password));
        });
    }

    private void updateAuthMethod(int index) {
        lblUserName.setVisible(index < 2);
        txtUser.setVisible(index < 2);
        lblPassword.setVisible(index == 0);
        txtPass.setVisible(index == 0);
        lblKeyFile.setVisible(index == 1);
        txtKeyFile.setVisible(index == 1);
        btnBrowseKey.setVisible(index == 1);
        lblIdentity.setVisible(index == 2);
        cmbIdentity.setVisible(index == 2);
        btnEditIdentities.setVisible(index == 2);
        revalidate();
        repaint();
    }

    public void setValue(SessionInfo info) {
        this.sessionInfo = info;
        txtHost.setText(info.getHost());
        txtPort.setText(String.valueOf(info.getPort()));
        txtUser.setText(info.getUser());
        txtKeyFile.setText(info.getPrivateKeyFile());
        txtRemoteFolder.setText(info.getRemoteFolder());
        txtLocalFolder.setText(info.getLocalFolder());
        cmbAuthMethod.setSelectedIndex(info.getAuthMode());
        txtPass.setText(info.getPassword());
    }

    private void attachTextListener(JTextField txt, Consumer<String> consumer) {
        txt.getDocument().addDocumentListener(new DocumentChangeAdapter(txt, consumer));
    }

    private void attachPasswordListener(JPasswordField txtPass, Consumer<char[]> callback) {
        txtPass.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                callback.accept(txtPass.getPassword());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                callback.accept(txtPass.getPassword());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                callback.accept(txtPass.getPassword());
            }
        });
    }

    private void createUI() {
        var lblHost = new JLabel("Host name", JLabel.RIGHT);
        var lblPort = new JLabel("Port", JLabel.RIGHT);
        lblUserName = new JLabel("User name", JLabel.RIGHT);
        var lblAuthMethod = new JLabel("Login", JLabel.RIGHT);
        lblPassword = new JLabel("Password", JLabel.RIGHT);
        lblKeyFile = new JLabel("Key file", JLabel.RIGHT);
        lblIdentity = new JLabel("Identity", JLabel.RIGHT);
        var lblRemoteFolder = new JLabel("Remote folder", JLabel.RIGHT);
        var lblLocalFolder = new JLabel("Local folder", JLabel.RIGHT);
        var lblCombinedMode = new JLabel("Show files and terminal in same tab", JLabel.LEFT);
        var lblStartPage = new JLabel("Start page", JLabel.RIGHT);

        btnBrowseKey = new JButton("...");
        btnEditIdentities = new JButton();
        btnEditIdentities.setFont(IconFont.getSharedInstance().getIconFont(12.0f));
        btnEditIdentities.setText(IconCode.RI_EDIT_2_FILL.getValue());
        var btnBrowseFolder = new JButton("...");

        var c = 0;
        var insets = new Insets(5, 5, 5, 5);

        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblHost, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtHost, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblPort, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtPort, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblAuthMethod, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(cmbAuthMethod, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblUserName, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtUser, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblPassword, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtPass, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblKeyFile, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtKeyFile, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        this.add(btnBrowseKey, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblIdentity, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(cmbIdentity, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        this.add(btnEditIdentities, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblRemoteFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtRemoteFolder, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblLocalFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(txtLocalFolder, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        this.add(btnBrowseFolder, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridwidth = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblCombinedMode, gc);

        gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = c;
        gc.gridwidth = 1;
        gc.insets = insets;
        this.add(swCombinedMode, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.gridy = c;
        gc.insets = insets;
        this.add(lblStartPage, gc);

        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = c;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.insets = insets;
        this.add(cmbStartPage, gc);

        c++;

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = c;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.PAGE_START;
        gc.weightx = 1;
        gc.weighty = 1;
        this.add(new JLabel(), gc);
    }
}
