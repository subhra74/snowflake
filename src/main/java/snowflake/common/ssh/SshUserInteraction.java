package snowflake.common.ssh;

import com.jcraft.jsch.*;
import snowflake.App;
import snowflake.common.UserInteraction;
import snowflake.components.newsession.SessionInfo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class SshUserInteraction implements UserInfo, UIKeyboardInteractive, UserInteraction {
    private static Map<String, String> passwordMap = new ConcurrentHashMap<>();
    private static Map<String, String> passphraseMap = new ConcurrentHashMap<>();
    private SessionInfo info;
    private JPasswordField password = new JPasswordField(30);
    private static AtomicBoolean confirmYes = new AtomicBoolean(false);
    private static AtomicBoolean suppressMessage = new AtomicBoolean(false);
    private AtomicLong attempt = new AtomicLong(0);
    private JRootPane rootPane;

    public SshUserInteraction(SessionInfo info, JRootPane rootPane) {
        this.info = info;
        this.rootPane = rootPane;
    }

    public SessionInfo getInfo() {
        return this.info;
    }

    private boolean showModal(List<JComponent> components, boolean yesNo, JRootPane root) {
        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });
        Box box = Box.createVerticalBox();
        box.setOpaque(true);
        for (JComponent c : components) {
            c.setAlignmentX(Box.LEFT_ALIGNMENT);
            box.add(c);
        }

        JButton btnOk = new JButton("OK");
        JButton btnCancel = null;

        if (yesNo) {
            btnCancel = new JButton("Cancel");
        }

        final AtomicBoolean isOk = new AtomicBoolean(false);

        btnOk.addActionListener(e -> {
            synchronized (this) {
                panel.setVisible(false);
                isOk.set(true);
                this.notify();
            }
        });

        if (yesNo) {
            btnCancel.addActionListener(e -> {
                synchronized (this) {
                    panel.setVisible(false);
                    isOk.set(false);
                    this.notify();
                }
            });
        }


        Box b1 = Box.createHorizontalBox();
        b1.add(Box.createHorizontalGlue());
        b1.add(btnOk);
        if (yesNo) {
            b1.add(btnCancel);
        }

        b1.setAlignmentX(Box.LEFT_ALIGNMENT);
        box.add(b1);


        panel.add(box);
        panel.setOpaque(false);

        SwingUtilities.invokeLater(() -> {
            rootPane.setGlassPane(panel);
            panel.setVisible(true);
        });

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return isOk.get();
    }

    @Override
    public String[] promptKeyboardInteractive(String destination, String name,
                                              String instruction, String[] prompt, boolean[] echo) {
        if (attempt.get() == 0) {
            if (prompt.length == 1 && prompt[0] != null
                    && prompt[0].toLowerCase().startsWith("password")
                    && info.getPassword() != null) {
                System.out.println(
                        "Keyboard interactive - Assuming password is being asked for");
                return new String[]{info.getPassword()};
            }
        }

        attempt.incrementAndGet();

        List<JComponent> list = new ArrayList<>();
        list.add(new JLabel(destination));
        list.add(new JLabel(name));
        list.add(new JLabel(instruction));

        int i = 0;
        for (String s : Arrays.asList(prompt)) {
            System.out.println(s);
            list.add(new JLabel(s));
            if (echo[i++]) {
                JTextField txt = new JTextField(30);
                list.add(txt);
            } else {
                JPasswordField pass = new JPasswordField(30);
                list.add(pass);
            }
        }

        if (showModal(list, true, rootPane)) {
            List<String> responses = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof JPasswordField) {
                    responses.add(
                            new String(((JPasswordField) obj).getPassword()));
                } else if (obj instanceof JTextField) {
                    responses.add(((JTextField) obj).getText());
                }
            }

            String arr1[] = new String[responses.size()];
            responses.toArray(arr1);
            return arr1;
        }

        return null;
    }

    @Override
    public void showMessage(String message) {
        System.out.println("showMessage: " + message);
        if (!App.getGlobalSettings().isShowMessage()) {
            return;
        }
        if (!SshUserInteraction.suppressMessage.get()) {
            JCheckBox chkHideWarn = new JCheckBox("Hide warnings");
            chkHideWarn.setSelected(true);
            JTextArea txtMsg = new JTextArea();
            txtMsg.setEditable(false);
            txtMsg.setText(message);
            JScrollPane jsp = new JScrollPane(txtMsg);
            jsp.setPreferredSize(
                    new Dimension(600, 300));
            jsp.setBorder(
                    new LineBorder(UIManager.getColor("DefaultBorder.color"),
                            1));

            List<JComponent> list = new ArrayList<>();
            list.add(jsp);
            list.add(chkHideWarn);

            showModal(list, false, rootPane);

            if (chkHideWarn.isSelected()) {
                SshUserInteraction.suppressMessage.set(true);
                App.getGlobalSettings().setShowMessage(false);
            }
        }
    }

    @Override
    public boolean promptYesNo(String message) {
        System.out.println("promptYesNo: " + message);
        if (SshUserInteraction.confirmYes.get()) {
            return true;
        }

        if (showModal(Arrays.asList(new JLabel(message)), true, rootPane)) {
            if (!SshUserInteraction.confirmYes.get()) {
                SshUserInteraction.confirmYes.set(true);
            }
            return true;
        }
        return false;
        // return true;
    }

    @Override
    public boolean promptPassword(String message) {
        System.out.println("promptPassword: " + message);
        if (attempt.get() == 0
                && SshUserInteraction.getPreEnteredPassword(info.getId()) != null
                && SshUserInteraction.getPreEnteredPassword(info.getId())
                .length() > 0) {
            return true;
        }

        attempt.getAndIncrement();
        password.setText("");

        if (showModal(Arrays.asList(new JLabel(message), password), true, rootPane)) {
            SshUserInteraction.setPreEnteredPassword(info.getId(),
                    new String(password.getPassword()));
            return true;
        }

        return false;
    }

    @Override
    public boolean promptPassphrase(String message) {
        System.out.println("prompt Passphrase: " + message);
        if (attempt.get() == 0
                && SshUserInteraction.getPreEnteredPassphrase(info.getId()) != null
                && SshUserInteraction.getPreEnteredPassphrase(info.getId())
                .length() > 0) {
            return true;
        }
        attempt.getAndIncrement();
        password.setText("");

        if (showModal(Arrays.asList(new JLabel(message), password), true, rootPane)) {
            SshUserInteraction.setPreEnteredPassphrase(info.getId(),
                    new String(password.getPassword()));
            return true;
        }

        return false;
    }

    @Override
    public String getPassword() {
        System.out.println("getPassword");
        return SshUserInteraction.getPreEnteredPassword(info.getId());
    }

    @Override
    public String getPassphrase() {
        System.out.println("getPassphrase");
        return SshUserInteraction.getPreEnteredPassphrase(info.getId());
    }

    public static synchronized String getPreEnteredPassword(String id) {
        return passwordMap.get(id);
    }

    public static synchronized void setPreEnteredPassword(String id,
                                                          String preEnteredPassword) {
        passwordMap.put(id, preEnteredPassword);
    }

    public static synchronized String getPreEnteredPassphrase(String id) {
        return passphraseMap.get(id);
    }

    public static synchronized void setPreEnteredPassphrase(String id,
                                                            String preEnteredPassphrase) {
        passphraseMap.put(id, preEnteredPassphrase);
    }
}
