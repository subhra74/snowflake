package muon.screens.dialogs;

import muon.styles.AppTheme;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserInputDialog extends JDialog {
    private JLabel lblUser;
    private java.util.List<String> inputs;
    private List<JPasswordField> txtInputs;
    private JPanel userInputContainer;
    private JButton loginButton;
    private JPanel container;

    public UserInputDialog(Window window) {
        super(window);
        setModal(true);
        setTitle("Authentication required");
        initUi();
        getContentPane().setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowActivated(WindowEvent e) {
                txtInputs.get(0).requestFocusInWindow();
            }
        });
        getContentPane().add(container);
    }

    public List<String> getInputs(String label, String[] prompt, boolean[] echo) {
        inputs = Collections.emptyList();
        System.out.println(Thread.currentThread());
        try {
            SwingUtilities.invokeAndWait(() -> {
                showPrompt(label, prompt, echo);
            });
            System.out.println("Dialog closed");
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> this.setVisible(false));
            throw new RuntimeException(ex);
        }
        return inputs;
    }

    private void initUi() {
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            inputs = this.txtInputs.stream().map(txt -> new String(txt.getPassword())).toList();
            setVisible(false);
        });

        var cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            setVisible(false);
        });

        var iconLabel = new JLabel();
        iconLabel.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        iconLabel.setText(IconCode.RI_LOCK_PASSWORD_LINE.getValue());
        iconLabel.setAlignmentY(Component.TOP_ALIGNMENT);

        lblUser = new JLabel();
        lblUser.setText("server\\user");
        lblUser.setBorder(new EmptyBorder(5, 0, 10, 0));
        lblUser.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        lblUser.setForeground(AppTheme.INSTANCE.getDarkForeground());
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInputContainer = new JPanel();
        userInputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInputContainer.setLayout(new BoxLayout(userInputContainer, BoxLayout.Y_AXIS));

        var lblTitle = new JLabel("Authentication required");
        lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        var hbox2 = Box.createHorizontalBox();
        hbox2.setAlignmentX(Component.LEFT_ALIGNMENT);
        hbox2.add(Box.createHorizontalGlue());
        hbox2.add(loginButton);
        hbox2.add(Box.createRigidArea(new Dimension(10, 10)));
        hbox2.add(cancelButton);

        var vbox = Box.createVerticalBox();
        vbox.add(lblTitle);
        vbox.add(lblUser);
        vbox.add(Box.createRigidArea(new Dimension(10, 20)));
        vbox.add(userInputContainer);
        vbox.add(Box.createRigidArea(new Dimension(10, 30)));
        vbox.add(Box.createVerticalGlue());
        vbox.add(hbox2);
        vbox.setAlignmentY(Component.TOP_ALIGNMENT);

        container.add(iconLabel);
        container.add(Box.createRigidArea(new Dimension(10, 10)));
        container.add(vbox);
        container.setBorder(new EmptyBorder(15, 15, 15, 15));
    }


    private void showPrompt(String label, String[] prompt, boolean[] echo) {
        var len = prompt.length;
        lblUser.setText(label);
        txtInputs = new ArrayList<>(len);
        userInputContainer.removeAll();
        userInputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (var i = 0; i < len; i++) {
            var txtInput = new JPasswordField(20);
            txtInput.addActionListener(e -> {
                loginButton.doClick();
            });
            txtInput.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (echo[i]) {
                txtInput.setEchoChar('*');
            }
            var d = txtInput.getPreferredSize();
            txtInput.setMaximumSize(new Dimension(Short.MAX_VALUE, d.height));
            txtInput.setPreferredSize(d);
            txtInputs.add(txtInput);
            var lbl = new JLabel(prompt[i]);
            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
            lbl.setHorizontalAlignment(JLabel.LEFT);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            userInputContainer.add(lbl);
            userInputContainer.add(txtInput);
        }
        this.pack();
        this.setSize(Math.max(400, this.getWidth()), Math.max(300, this.getHeight()));
        this.setLocationRelativeTo(this.getOwner());
        this.setVisible(true);
    }
}
