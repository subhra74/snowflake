//package snowflake.components.main;
//
//import snowflake.App;
//import snowflake.components.newsession.NewSessionDlg;
//import snowflake.components.newsession.SessionInfo;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.MatteBorder;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//
//public class MainContent extends JPanel {
//    private DefaultComboBoxModel<SessionInfo> model;
//    private SessionContentPanel contentPanel;
//
//    public MainContent() {
//        super(new BorderLayout());
//        init();
//    }
//
////    private JComponent createNewButton() {
////        JPanel panel = new JPanel(new BorderLayout(10, 10));
////        panel.setBackground(new Color(47, 54, 61));
////        panel.setOpaque(false);
////        panel.setBorder(new MatteBorder(0, 0, 0, 1, Color.BLACK));
////
////        JLabel lblIcon = new JLabel();
////        lblIcon.setBorder(new EmptyBorder(0, 15, 0, 0));
////        lblIcon.setForeground(new Color(200, 200, 200));
////        lblIcon.setFont(App.getFontAwesomeFont());
////        lblIcon.setText("\uf0c2");
////        panel.add(lblIcon, BorderLayout.WEST);
////
////        JLabel lblText = new JLabel();
////        lblText.setBorder(new EmptyBorder(0, 0, 0, 15));
////        lblText.setText("New connection");
////        lblText.setForeground(new Color(200, 200, 200));
////        panel.add(lblText, BorderLayout.CENTER);
////
////        MouseAdapter mouseAdapter = new MouseAdapter() {
////            @Override
////            public void mouseClicked(MouseEvent e) {
////                SessionInfo info = new NewSessionDlg().newSession();
////                if (info != null) {
////                    model.addElement(info);
////                    contentPanel.addNewSession(info);
////                }
////            }
////
////            @Override
////            public void mouseEntered(MouseEvent e) {
////                panel.setOpaque(true);
////                panel.revalidate();
////                panel.repaint();
////            }
////
////            @Override
////            public void mouseExited(MouseEvent e) {
////                panel.setOpaque(false);
////                panel.revalidate();
////                panel.repaint();
////            }
////        };
////
////        panel.addMouseListener(mouseAdapter);
////        panel.addMouseMotionListener(mouseAdapter);
////        lblIcon.addMouseListener(mouseAdapter);
////        lblText.addMouseListener(mouseAdapter);
////        return panel;
////    }
//
//    private void init() {
//        contentPanel = new SessionContentPanel();
//        add(contentPanel);
//
//        model = new DefaultComboBoxModel<>();
//
//
//
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.setBackground(new Color(36, 41, 46));
//        topPanel.add(createNewButton(), BorderLayout.WEST);
//
//        Box topBox = Box.createHorizontalBox();
//        topBox.add(Box.createRigidArea(new Dimension(0, 40)));
//
//
//        //newConnection.setBackground(Color.GREEN);
//        //topBox.add();
//        topBox.add(Box.createHorizontalGlue());
//        JComboBox<SessionInfo> cmb = new JComboBox<>(model);
//        cmb.addItemListener(e -> {
//            int index = cmb.getSelectedIndex();
//            if (index >= 0) {
//                contentPanel.selectSession(model.getElementAt(index));
//            }
//        });
//        topBox.add(cmb);
//
//        JButton disconnect = new JButton("Disconnect");
//        disconnect.addActionListener(e -> {
//            int index = cmb.getSelectedIndex();
//            if (index != -1) {
//                SessionInfo info = model.getElementAt(index);
//                if (contentPanel.removeSession(info)) {
//                    model.removeElementAt(index);
//                }
//            }
//        });
//
//        topBox.add(Box.createHorizontalStrut(5));
//        topBox.add(disconnect);
//
//        topPanel.add(topBox);
//
//        add(topPanel, BorderLayout.NORTH);
//
//
//    }
//}


package snowflake.components.main;

import snowflake.App;
import snowflake.components.newsession.NewSessionDlg;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class MainContent extends JPanel {
    //private GradientPaint gradientPaint;

    public MainContent() {
        super(new BorderLayout());
//        gradientPaint = new GradientPaint(0.0f, 0.0f, new Color(200, 200, 200),
//                0.0f, 50.0f, new Color(150, 150, 150));
        init();
    }

    private void init() {
        SessionContentPanel contentPanel = new SessionContentPanel();
        //contentPanel.setBackground(new Color(80,80,80));
        contentPanel.setOpaque(true);
        add(contentPanel);

        DefaultComboBoxModel<SessionInfo> model = new DefaultComboBoxModel<>();

        Box topPanel = Box.createHorizontalBox();
//        topPanel.setBackground(new Color(36,41,46));
//        topPanel.setOpaque(true);

        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
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
        cmb.putClientProperty("Nimbus.Overrides", App.comboBoxSkin);
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

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setPaint(gradientPaint);
//        g2.fillRect(0, 0, getWidth(), getHeight());
//    }
}
