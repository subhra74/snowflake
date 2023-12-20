package muon.screens.sessiontabs;

import muon.styles.AppTheme;
import muon.util.IconCode;
import muon.util.IconFont;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

public class InputBlockerDialog extends JDialog implements
        SshUiCallback {
    private Lock lock;
    private Condition signal;
    private CardLayout cardLayout;
    private JPasswordField txtPassword;
    private String providedPassword;
    private boolean retry;
    private Consumer<Boolean> closeCallback;
    private JTextArea txtBanner;

    public InputBlockerDialog(JFrame frame) {
        super(frame);
        setTitle("Muon");
        setSize(640, 480);
        cardLayout = new CardLayout();
        getContentPane().setLayout(cardLayout);
        setModal(true);
        lock = new ReentrantLock();
        signal = lock.newCondition();
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                signal();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (Objects.nonNull(closeCallback)) {
                    closeCallback.accept(Boolean.TRUE);
                    signal();
                }
            }
        });
        add("PasswordPanel", createPasswordPanel());
        add("ProgressPanel", createProgressPanel());
        add("RetryPanel", createRetryPanel());
        add("BannerPanel", createBannerPanel());
    }

    private JPanel createProgressPanel() {
        var label = new JLabel("Please wait");
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        var panel = new JPanel(new GridBagLayout());
        var prg = new JProgressBar();
        prg.setPreferredSize(new Dimension(200, 5));
        prg.setIndeterminate(true);
        var gc = new GridBagConstraints();
        panel.add(label, gc);
        gc = new GridBagConstraints();
        gc.gridy = 1;
        panel.add(prg, gc);
        return panel;
    }

    private JPanel createBannerPanel() {
        txtBanner = new JTextArea();
        txtBanner.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtBanner.setEditable(false);
        var jsp = new JScrollPane(txtBanner);
        //jsp.setBorder(new LineBorder(AppTheme.INSTANCE.getButtonBorderColor(), 1));
        jsp.setBorder(new LineBorder(AppTheme.INSTANCE.getButtonBorderColor(), 0));

        var button = new JButton("Continue");
        button.addActionListener(e -> {
            cardLayout.show(getContentPane(), "ProgressPanel");
            signal();
        });

        var bottom = Box.createHorizontalBox();
        bottom.add(Box.createHorizontalGlue());
        bottom.add(button);

        var panel = new JPanel(new BorderLayout(10, 10));
        panel.add(jsp);
        panel.add(bottom, BorderLayout.SOUTH);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JPanel createRetryPanel() {
        var label = new JLabel();
        label.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        label.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());

        var lblError = new JLabel("Unable to connect");
        lblError.setBorder(new EmptyBorder(10, 10, 10, 10));

        var button = new JButton("Try again");
        button.addActionListener(e -> {
            cardLayout.show(getContentPane(), "ProgressPanel");
            retry = true;
            signal();
        });

        var panel = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        panel.add(label, gc);

        gc = new GridBagConstraints();
        gc.gridy = 1;
        panel.add(lblError, gc);

        gc = new GridBagConstraints();
        gc.gridy = 2;
        panel.add(button, gc);

        return panel;
    }

    private JPanel createPasswordPanel() {
        txtPassword = new JPasswordField(20);
        txtPassword.setEchoChar('*');
        txtPassword.setMaximumSize(new Dimension(Short.MAX_VALUE, txtPassword.getPreferredSize().height));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        var button = new JButton("Login");
        button.addActionListener(e -> {
            providedPassword = new String(txtPassword.getPassword());
            cardLayout.show(getContentPane(), "ProgressPanel");
            signal();
        });

        var iconLabel = new JLabel();
        iconLabel.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        iconLabel.setText(IconCode.RI_ACCOUNT_CIRCLE_FILL.getValue());

        var userLabel = new JLabel();
        userLabel.setText("subhro");
        userLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        userLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        var iconPanel = new JPanel(new BorderLayout(10, 0));
        iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconPanel.add(iconLabel, BorderLayout.WEST);
        iconPanel.add(userLabel, BorderLayout.CENTER);

        var label = new JLabel("Password");
        label.setAlignmentX(1.0f);
        label.setHorizontalTextPosition(JLabel.LEFT);
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, label.getPreferredSize().height));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        var hbox = Box.createHorizontalBox();
        var chkShowPassword = new JLabel("Show Password");

        hbox.add(chkShowPassword);
        hbox.add(Box.createHorizontalGlue());
        hbox.add(button);
        hbox.setMaximumSize(new Dimension(Short.MAX_VALUE, hbox.getPreferredSize().height));
        hbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        var vbox = Box.createVerticalBox();
        vbox.add(iconPanel);
        vbox.add(Box.createRigidArea(new Dimension(10, 20)));
        vbox.add(label);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(txtPassword);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(hbox);

        var panel = new JPanel(new GridBagLayout());
        panel.add(vbox);


//        var gc = new GridBagConstraints();
//        gc.weightx = 1;
//        gc.gridwidth = 2;
//        gc.anchor=GridBagConstraints.LINE_START;
//        panel.add(new JLabel("Password"), gc);
//        gc = new GridBagConstraints();
//        gc.gridy = 1;
//        gc.weightx = 1;
//        gc.gridwidth = 2;
//        //gc.fill = GridBagConstraints.HORIZONTAL;
//        panel.add(txtPassword, gc);
//
//
//
//        gc = new GridBagConstraints();
//        gc.gridy = 2;
//        gc.gridx = 1;
//        gc.gridwidth = 1;
//        panel.add(button, gc);

        return panel;

    }

    public void setCloseCallback(Consumer<Boolean> callback) {
        this.closeCallback = callback;
    }

    @Override
    public void showBlocker() {
        SwingUtilities.invokeLater(() -> {
            this.setLocationRelativeTo(this.getParent());
            cardLayout.show(getContentPane(), "ProgressPanel");
            this.setVisible(true);
        });
        await();
    }

    @Override
    public boolean shouldRetry() {
        retry = false;
        signal();
        SwingUtilities.invokeLater(() -> {
            cardLayout.show(getContentPane(), "RetryPanel");
        });
        await();
        return retry;
    }

    @Override
    public void hideBlocker() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    @Override
    public boolean isInteractionAllowed(ClientSession session) {
        return true;
    }

    @Override
    public void serverVersionInfo(ClientSession session, List<String> lines) {
        System.out.println(String.join(" ", lines));
    }

    @Override
    public void welcome(ClientSession session, String banner, String lang) {
        SwingUtilities.invokeLater(() -> {
            txtBanner.setText(banner);
            cardLayout.show(getContentPane(), "BannerPanel");
            txtBanner.setCaretPosition(0);
        });
        await();
    }

    @Override
    public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
        return new String[]{getPassword()};
    }

    private String getPassword() {
        SwingUtilities.invokeLater(() -> {
            txtPassword.setText("");
            cardLayout.show(getContentPane(), "PasswordPanel");
        });
        await();
        return providedPassword;
    }

    @Override
    public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
        return null;
    }

    @Override
    public String resolveAuthPasswordAttempt(ClientSession session) throws Exception {
        System.out.println("resolveAuthPasswordAttempt");
        return getPassword();
    }

    @Override
    public KeyPair resolveAuthPublicKeyIdentityAttempt(ClientSession session) throws Exception {
        return SshUiCallback.super.resolveAuthPublicKeyIdentityAttempt(session);
    }

    @Override
    public Iterable<String> loadPasswords(SessionContext session) throws IOException, GeneralSecurityException {
        return null;
    }

    @Override
    public void signalAuthenticationAttempt(ClientSession session, String service, String oldPassword, boolean modified, String newPassword) throws Exception {
        SshUiCallback.super.signalAuthenticationAttempt(session, service, oldPassword, modified, newPassword);
    }

    @Override
    public void signalAuthenticationExhausted(ClientSession session, String service) throws Exception {
        SshUiCallback.super.signalAuthenticationExhausted(session, service);
    }

    @Override
    public void signalAuthenticationSuccess(ClientSession session, String service, String password) throws Exception {
        SshUiCallback.super.signalAuthenticationSuccess(session, service, password);
    }

    @Override
    public void signalAuthenticationFailure(ClientSession session, String service, String password, boolean partial, List<String> serverMethods) throws Exception {
        SshUiCallback.super.signalAuthenticationFailure(session, service, password, partial, serverMethods);
    }

    private void signal() {
        try {
            System.out.println("Acquire lock before signal: " + Thread.currentThread());
            lock.lock();
            System.out.println("Acquired lock, signalling: " + Thread.currentThread());
            signal.signalAll(); //unblock invoking thread
        } finally {
            lock.unlock();
        }
    }

    private void await() {
        try {
            lock.lock();
            System.out.println("Waiting for signal: " + Thread.currentThread());
            signal.await(); //block current thread until signalled
            System.out.println("Singal received: " + Thread.currentThread());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
