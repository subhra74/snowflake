package muon.widgets;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InteractivePromptPanel extends JPanel {
    private JLabel lblUser;
    private List<String> inputs;
    private List<JPasswordField> txtInputs;
    private JPanel userInputContainer;
    private JButton loginButton;

    public InteractivePromptPanel(ActionListener loginClicked) {
        super(new GridBagLayout());
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            inputs = this.txtInputs.stream().map(txt -> new String(txt.getPassword())).toList();
            loginClicked.actionPerformed(e);
        });

        var iconLabel = new JLabel();
        iconLabel.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        iconLabel.setText(IconCode.RI_ACCOUNT_CIRCLE_FILL.getValue());

        lblUser = new JLabel();
        lblUser.setText("user");
        lblUser.setBorder(new EmptyBorder(0, 0, 8, 0));
        lblUser.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        var iconPanel = new JPanel(new BorderLayout(10, 0));
        iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        iconPanel.add(iconLabel, BorderLayout.WEST);
        iconPanel.add(lblUser, BorderLayout.CENTER);

        var hbox = Box.createHorizontalBox();
        hbox.add(Box.createHorizontalGlue());
        hbox.add(loginButton);
        hbox.setMaximumSize(new Dimension(Short.MAX_VALUE, hbox.getPreferredSize().height));
        hbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInputContainer = new JPanel();
        userInputContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInputContainer.setLayout(new BoxLayout(userInputContainer, BoxLayout.Y_AXIS));

        var vbox = Box.createVerticalBox();
        vbox.add(iconPanel);
        vbox.add(Box.createRigidArea(new Dimension(10, 20)));
        vbox.add(userInputContainer);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(hbox);

        this.add(vbox, new GridBagConstraints());
    }

    public void showPrompt(String label, String[] prompt, boolean[] echo) {
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
            txtInputs.add(txtInput);
            var lbl = new JLabel(prompt[i]);
            lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
            lbl.setHorizontalAlignment(JLabel.LEFT);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            userInputContainer.add(lbl);
            userInputContainer.add(txtInput);
        }
        revalidate();
        repaint();
        txtInputs.get(0).requestFocusInWindow();
    }

    public List<String> getInputs() {
        return inputs;
    }
}
