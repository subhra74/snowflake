//package muon.screens.sessiontabs;
//
//import muon.model.SessionInfo;
//import muon.util.AppUtils;
//import muon.util.IconCode;
//import org.apache.sshd.client.auth.keyboard.UserInteraction;
//import org.apache.sshd.client.auth.password.*;
//import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
//import org.apache.sshd.client.session.ClientSession;
//import org.apache.sshd.common.session.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionListener;
//import java.io.*;
//import java.security.GeneralSecurityException;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//public class ConnectionProgressPanel
//        extends
//        JPanel
//        implements
//        SshUiCallback {
//    private SessionInfo info;
//    private JPanel progressPanel;
//    private JPanel passwordPanel;
//    private JPanel errorRetryPanel;
//    private BlockingQueue<String> passwordQueue = new ArrayBlockingQueue<>(1);
//    private JPasswordField txtPassword;
//    private AtomicBoolean firstPasswordAttempt = new AtomicBoolean(true);
//    private AtomicBoolean passwordProvided = new AtomicBoolean(true);
//
//    public ConnectionProgressPanel(SessionInfo info, ActionListener retryCallback) {
//        super(new GridBagLayout());
//        this.info = info;
//        progressPanel = createProgressPanel();
//        passwordPanel = createPasswordPrompt();
//        errorRetryPanel = createRetryPanel(retryCallback);
//        var gc = new GridBagConstraints();
//        add(progressPanel, gc);
//    }
//
//    private JPanel createProgressPanel() {
//        var panel = new JPanel(null);
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.add(new JLabel("Connecting..."));
//        panel.add(Box.createRigidArea(new Dimension(0, 10)));
//        panel.add(new JButton("Cancel"));
//        return panel;
//    }
//
//    private JPanel createRetryPanel(ActionListener retryCallback) {
//        var panel = new JPanel(null);
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        panel.add(new JLabel("Error"));
//        panel.add(Box.createRigidArea(new Dimension(0, 10)));
//        var button = new JButton("Retry");
//        button.addActionListener(retryCallback);
//        panel.add(button);
//        return panel;
//    }
//
//    private JPanel createPasswordPrompt() {
//        var panel = new JPanel(null);
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        var lblPrompt = new JLabel("Password");
//        txtPassword = new JPasswordField(10);
//        txtPassword.setEchoChar('*');
//        var btnNext = AppUtils.createIconButton(IconCode.RI_ARROW_RIGHT_LINE);
//        btnNext.addActionListener(e -> {
//            passwordProvided.set(true);
//            passwordQueue.add(new String(txtPassword.getPassword()));
//            showProgressPanel();
//        });
//
//        panel.add(lblPrompt);
//        var b2 = Box.createHorizontalBox();
//        b2.add(txtPassword);
//        b2.add(btnNext);
//
//        panel.add(b2);
//        return panel;
//    }
//
//    private void showPasswordPanel() {
//        this.removeAll();
//        var gc = new GridBagConstraints();
//        txtPassword.setText("");
//        add(passwordPanel, gc);
//        revalidate();
//        repaint();
//    }
//
//    public void showErrorRetryPanel() {
//        this.removeAll();
//        var gc = new GridBagConstraints();
//        add(errorRetryPanel, gc);
//        revalidate();
//        repaint();
//    }
//
//    private void showProgressPanel() {
//        this.removeAll();
//        var gc = new GridBagConstraints();
//        add(progressPanel, gc);
//        revalidate();
//        repaint();
//    }
//
//    private String getPassword() {
//        if (firstPasswordAttempt.get()) {
//            firstPasswordAttempt.set(false);
//            this.info.setLastPassword(new String(this.info.getPassword()));
//            return this.info.getLastPassword();
//        }
//        SwingUtilities.invokeLater(this::showPasswordPanel);
//        try {
//            var password = passwordQueue.take();
//            if (passwordProvided.get()) {
//                this.info.setLastPassword(password);
//                return this.info.getLastPassword();
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
//
//    @Override
//    public Iterable<String> loadPasswords(SessionContext session) throws IOException, GeneralSecurityException {
//        return java.util.List.of(getPassword());
//    }
//
//    @Override
//    public void signalAuthenticationAttempt(ClientSession session, String service, String oldPassword, boolean modified, String newPassword) throws Exception {
//        System.out.println("signalAuthenticationAttempt");
//    }
//
//    @Override
//    public void signalAuthenticationFailure(ClientSession session, String service, String password, boolean partial, List<String> serverMethods) throws Exception {
//        System.out.println("signalAuthenticationFailure");
//    }
//
//    @Override
//    public void signalAuthenticationExhausted(ClientSession session, String service) throws Exception {
//        System.out.println("signalAuthenticationExhausted");
//    }
//
//    @Override
//    public void signalAuthenticationSuccess(ClientSession session, String service, String password) throws Exception {
//        System.out.println("signalAuthenticationSuccess");
//    }
//
//    @Override
//    public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
//        System.out.println(name + " " + instruction + " " + lang + " " + String.join(" ", prompt));
//        return new String[]{getPassword()};
//    }
//
//    @Override
//    public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
//        return getPassword();
//    }
//
//    @Override
//    public String resolveAuthPasswordAttempt(ClientSession session) throws Exception {
//        return getPassword();
//    }
//
//    public String getLastPassword() {
//        return this.info.getLastPassword();
//    }
//}
