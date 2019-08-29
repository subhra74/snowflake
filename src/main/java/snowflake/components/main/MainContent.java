package snowflake.components.main;

import snowflake.components.newsession.NewSessionDlg;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class MainContent extends JPanel {
    public MainContent() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        SessionContentPanel contentPanel = new SessionContentPanel();
        add(contentPanel);

        DefaultComboBoxModel<SessionInfo> model = new DefaultComboBoxModel<>();
        Box topPanel = Box.createHorizontalBox();
        topPanel.setOpaque(true);
        topPanel.setBackground(new Color(200, 200, 200));
        topPanel.setBorder(new EmptyBorder(5,5,5,5));
        JButton newConnection = new JButton("New connection");
        newConnection.addActionListener(e -> {
            SessionInfo info = new NewSessionDlg().newSession();
            if (info != null) {
                model.addElement(info);
                contentPanel.addNewSession(info);
            }
        });
        //newConnection.setBackground(Color.GREEN);
        topPanel.add(newConnection);
        topPanel.add(Box.createHorizontalGlue());
        JComboBox<SessionInfo> cmb = new JComboBox<>(model);
        cmb.addItemListener(e -> {
            int index = cmb.getSelectedIndex();
            if (index >= 0) {
                contentPanel.selectSession(model.getElementAt(index));
            }
        });
        topPanel.add(cmb);

        JButton disconnect = new JButton("Disconnect");
        disconnect.addActionListener(e -> {
            int index = cmb.getSelectedIndex();
            if (index != -1) {
                SessionInfo info = model.getElementAt(index);
                if (contentPanel.removeSession(info)) {
                    model.removeElementAt(index);
                }
            }
        });

        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(disconnect);

        add(topPanel, BorderLayout.NORTH);


    }
}
