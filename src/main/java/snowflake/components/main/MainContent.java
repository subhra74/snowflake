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
import snowflake.components.settings.SettingsPanel;
import snowflake.utils.GraphicsUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class MainContent extends JPanel {
    //private GradientPaint gradientPaint;
    private JFrame frame;
    private SettingsPanel settingsPanel;
    private JComboBox<SessionInfo> cmb;

    public MainContent(JFrame frame) {
        super(new BorderLayout(0, 0));
        this.frame = frame;
//        gradientPaint = new GradientPaint(0.0f, 0.0f, new Color(200, 200, 200),
//                0.0f, 50.0f, new Color(150, 150, 150));
        init();
    }

    private UIDefaults getSkinnedDropDown() {
        UIDefaults comboBoxSkin = new UIDefaults();
        Painter<JComboBox> comboBoxPainterNormal = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(62, 68, 81));//g.setColor(new Color(62,68,81));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<JComboBox> comboBoxPainterHot = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(90, 90, 90));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<JComboBox> comboBoxPainterPressed = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(50, 50, 50));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };


        comboBoxSkin.put("ComboBox.foreground", Color.WHITE);
        comboBoxSkin.put("ComboBox[Enabled].backgroundPainter", comboBoxPainterNormal);
        comboBoxSkin.put("ComboBox[Focused].backgroundPainter", comboBoxPainterNormal);
        comboBoxSkin.put("ComboBox[MouseOver].backgroundPainter", comboBoxPainterHot);
        comboBoxSkin.put("ComboBox[Pressed].backgroundPainter", comboBoxPainterPressed);

        comboBoxSkin.put("ComboBox[Focused+Pressed].backgroundPainter", comboBoxPainterPressed);
        comboBoxSkin.put("ComboBox[Focused+MouseOver].backgroundPainter", comboBoxPainterHot);
        comboBoxSkin.put("ComboBox[Enabled+Selected].backgroundPainter", comboBoxPainterNormal);


        return comboBoxSkin;
    }


    private void init() {
        setBackground(new Color(245, 245, 245));
        this.settingsPanel = new SettingsPanel(frame);
        SessionContentPanel contentPanel = new SessionContentPanel();
        //setBackground(new Color(80,80,80));
        contentPanel.setOpaque(true);
        add(contentPanel);

        DefaultComboBoxModel<SessionInfo> model = new DefaultComboBoxModel<>();

        Box topPanel = Box.createHorizontalBox();
//        topPanel.setBackground(new Color(33, 136, 255));
        //topPanel.setBackground(new Color(36, 41, 46));
        //topPanel.setBackground(new Color(36, 41, 46));
//        topPanel.setBackground(new Color(20, 23, 41));
        //topPanel.setBackground(new Color(29,32,51));
        topPanel.setBackground(new Color(47, 51, 62));
        topPanel.setOpaque(true);

        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton newConnection = GraphicsUtils.createSkinnedButton(new Color(92, 167, 25), new Color(128, 167, 25), new Color(50, 167, 25));// new JButton("New connection");
        newConnection.setText("New connection");
        //newConnection.setBackground(new Color(0, 105, 0));
        //newConnection.setFocusPainted(false);
        newConnection.setForeground(Color.WHITE);
        newConnection.addActionListener(e -> {
            SessionInfo info = new NewSessionDlg(SwingUtilities.windowForComponent(this)).newSession();
            if (info != null) {
                int index = model.getSize();
                model.addElement(info);
                contentPanel.addNewSession(info);
                cmb.setSelectedIndex(index);
            }
        });
        //newConnection.setBackground(Color.GREEN);
        topPanel.add(newConnection);
        topPanel.add(Box.createHorizontalGlue());

        cmb = new JComboBox<>(model);
        cmb.putClientProperty("Nimbus.Overrides", getSkinnedDropDown());
        cmb.setRenderer(new ListCellRenderer<SessionInfo>() {
            JLabel lbl = new JLabel();

            {
                setOpaque(true);
                lbl.setBackground(Color.DARK_GRAY);
                lbl.setForeground(Color.WHITE);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends SessionInfo> list, SessionInfo value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    lbl.setText(value.toString());
                } else {
                    lbl.setText("");
                }
                if (isSelected) {
                    lbl.setBackground(Color.BLACK);
                } else {
                    lbl.setBackground(Color.DARK_GRAY);
                }

                return lbl;
            }
        });

        cmb.addItemListener(e -> {
            int index = cmb.getSelectedIndex();
            if (index >= 0) {
                contentPanel.selectSession(model.getElementAt(index));
            }
        });
        topPanel.add(cmb);

        Color c1 = new Color(3, 155, 229);
        Color c2 = new Color(2, 132, 195);
        Color c3 = new Color(70, 130, 180);

        Dimension maxDim = new Dimension(0, 0);

        JButton disconnect = GraphicsUtils.createSkinnedButton(c1, c2, c3);//new JButton("Disconnect");
        disconnect.setFont(App.getFontAwesomeFont());
        disconnect.setToolTipText("Disconnect session");
        //disconnect.setText("Disconnect");
        disconnect.setText("\uf052");
        //disconnect.setBackground(new Color(100, 0, 0));
        disconnect.setForeground(Color.WHITE);
        disconnect.addActionListener(e -> {
            int index = cmb.getSelectedIndex();
            if (index != -1) {
                SessionInfo info = model.getElementAt(index);
                if (contentPanel.removeSession(info)) {
                    model.removeElementAt(index);
                }
                if (model.getSize() < 1) {
                    cmb.setSelectedIndex(-1);
                    cmb.setSelectedItem("");
                }
            }
        });

        if (disconnect.getPreferredSize().width > maxDim.width) {
            maxDim.width = disconnect.getPreferredSize().width;
        }


        JButton settings = GraphicsUtils.createSkinnedButton(c1, c2, c3);//new JButton("Disconnect");
        settings.setToolTipText("Settings");
        settings.setFont(App.getFontAwesomeFont());
        //settings.setText("Settings");
        settings.setText("\uf085");
        //disconnect.setBackground(new Color(100, 0, 0));
        settings.setForeground(Color.WHITE);
        settings.addActionListener(e -> {
            settingsPanel.showDialog(App.getGlobalSettings());
        });

        if (settings.getPreferredSize().width > maxDim.width) {
            maxDim.width = settings.getPreferredSize().width;
        }

        JButton info = GraphicsUtils.createSkinnedButton(c1, c2, c3);//new JButton("Disconnect");
        info.setToolTipText("Help and about");
        info.setFont(App.getFontAwesomeFont());
        //settings.setText("Settings");
        info.setText("\uf05a");
        //disconnect.setBackground(new Color(100, 0, 0));
        info.setForeground(Color.WHITE);
        info.addActionListener(e -> {
            new AppInfoDialog(SwingUtilities.windowForComponent(this)).setVisible(true);
        });

        if (info.getPreferredSize().width > maxDim.width) {
            maxDim.width = info.getPreferredSize().width;
        }

        for (JButton btn : new JButton[]{disconnect, settings, info}) {
            btn.setPreferredSize(new Dimension(maxDim.width, btn.getPreferredSize().height));
        }

        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(disconnect);

        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(settings);

        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(info);

        add(topPanel, BorderLayout.NORTH);
        setOpaque(true);
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//        g2.setPaint(gradientPaint);
//        g2.fillRect(0, 0, getWidth(), getHeight());
//    }
}
