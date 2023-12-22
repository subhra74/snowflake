package muon.screens.sessionmgr;

import muon.model.SessionInfo;
import muon.styles.AppTheme;
import muon.util.DocumentChangeAdapter;
import muon.util.NumericDocumentFilter;
import muon.widgets.SwitchButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class SshInfoPanel extends JPanel {
    private JTextField txtHost, txtPort, txtUser,
            txtKeyFile, txtRemoteFolder, txtLocalFolder;
    private JPasswordField txtPass;
    private SwitchButton swCombinedMode;
    private JComboBox<String> cmbStartPage;
    private SessionInfo sessionInfo;

    public SshInfoPanel() {
        super(new GridBagLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setBackground(AppTheme.INSTANCE.getBackground());

        txtHost = new JTextField();
        txtPort = new JTextField();
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        txtKeyFile = new JTextField();
        txtRemoteFolder = new JTextField();
        txtLocalFolder = new JTextField();
        cmbStartPage = new JComboBox<>(new String[]{"SFTP+Terminal", "SFTP", "Terminal", "Port forwarding"});
        swCombinedMode = new SwitchButton();

        txtPass.setEchoChar('*');

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
            sessionInfo.setPassword(password);
        });
    }

    public void setValue(SessionInfo info) {
        this.sessionInfo = info;
        txtHost.setText(info.getHost());
        txtPort.setText(String.valueOf(info.getPort()));
        txtUser.setText(info.getUser());
        txtPass.setText("*****************");
        txtKeyFile.setText(info.getPrivateKeyFile());
        txtRemoteFolder.setText(info.getRemoteFolder());
        txtLocalFolder.setText(info.getLocalFolder());



        txtHost.setText("localhost");
        txtUser.setText("subhro");
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
        var lblUserName = new JLabel("User name", JLabel.RIGHT);
        var lblPassword = new JLabel("Password", JLabel.RIGHT);
        var lblKeyFile = new JLabel("Key file", JLabel.RIGHT);
        var lblRemoteFolder = new JLabel("Remote folder", JLabel.RIGHT);
        var lblLocalFolder = new JLabel("Local folder", JLabel.RIGHT);
        var lblCombinedMode = new JLabel("Show files and terminal in same tab", JLabel.LEFT);
        var lblStartPage = new JLabel("Start page", JLabel.RIGHT);

        var btnBrowseKey = new JButton("...");
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