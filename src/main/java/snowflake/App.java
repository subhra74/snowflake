package snowflake;

import snowflake.common.GlobalSettings;
import snowflake.components.main.AppFrame;
import snowflake.components.main.MainContent;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class App {
    private static Properties config = new Properties();
    private static Font fontAwesomeFont;
    private static GlobalSettings globalSettings;

    public static String getConfig(String key) {
        return config.getProperty(key);
    }

    public static Font getFontAwesomeFont() {
        return fontAwesomeFont;
    }

    public static GlobalSettings getGlobalSettings() {
        return globalSettings;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        System.out.println("Hello");
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
//        UIManager.put("control", new Color(11, 11, 11));
//        UIManager.put("text", new Color(208, 208, 208));

        //UIManager.put("ScrollBar.thumbHeight", 8);
        //UIManager.put("ScrollBar:\"ScrollBar.button\".size", 5);
        //UIManager.put("Panel.background", new Color(245, 245, 245));
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });
//
//        UIManager.put("ComboBox[Enabled].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
//            }
//        });


//        UIManager.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//
//            }
//        });
//        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//
//            }
//        });


        config.put("temp.dir",
                PathUtils.combine(System.getProperty("user.home"),
                        "nix-explorer" + File.separator + "temp",
                        File.separator));

        config.put("app.dir", PathUtils.combine(System.getProperty("user.home"),
                "nix-explorer", File.separator));

        new File(config.get("app.dir").toString()).mkdirs();
        new File(config.get("temp.dir").toString()).mkdirs();

        loadFonts();


        JFrame f = new JFrame("Frame");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(800, 600);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);


        f.add(new MainContent());
        f.setLocationRelativeTo(null);
        f.setVisible(true);

//        createSampleWindow();
//        createSampleWindow1();
    }

    public static void createSampleWindow1() {
        JFrame f = new JFrame();
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setUndecorated(true);
        f.add(new AppFrame());
        f.setVisible(true);
    }

    public static void createSampleWindow() {
        JFrame f = new JFrame();
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setUndecorated(true);
        //f.setOpacity(0.9f);


        JPanel p1 = new JPanel() {
            {
                setLayout(new BorderLayout());
                JButton btn = new JButton();
                btn.setFont(getFontAwesomeFont().deriveFont(Font.BOLD, 20.0f));
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                //btn.setText("\uf108");
                btn.setText("\uf2dc");
                add(btn);
                setPreferredSize(new Dimension(60, 60));
                setMaximumSize(new Dimension(60, 60));
                setMinimumSize(new Dimension(60, 60));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(29, 125, 212));
                int radius = Math.min(getWidth() - 10, getHeight() - 10);
                int x = getWidth() / 2 - radius / 2;
                int y = getHeight() / 2 - radius / 2;
                g2.fillOval(x, y, radius, radius);
                //g2.fillOval(6, 5, getWidth() - 10, getHeight() - 10);
            }
        };

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBorder(new EmptyBorder(10, 10, 10, 10));
        p2.add(p1, BorderLayout.WEST);
        f.add(p2, BorderLayout.NORTH);

        Box b2 = Box.createVerticalBox();
        JLabel lbl1 = new JLabel("SERVER");
        lbl1.setFont(lbl1.getFont().deriveFont(Font.BOLD, 12));
        lbl1.setForeground(Color.LIGHT_GRAY);
        JLabel lbl2 = new JLabel("SETTINGS");
        lbl2.setFont(lbl2.getFont().deriveFont(Font.PLAIN, 12));
        lbl2.setForeground(Color.GRAY);
        JLabel lbl3 = new JLabel("HELP");
        lbl3.setFont(lbl3.getFont().deriveFont(Font.PLAIN, 12));
        lbl3.setForeground(Color.GRAY);

        Box b1 = Box.createHorizontalBox();
        b1.setBorder(new EmptyBorder(10, 10, 5, 10));
        b1.add(lbl1);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
        b1.add(lbl2);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
        b1.add(lbl3);
        b1.setAlignmentX(Box.LEFT_ALIGNMENT);
        b2.add(b1);

        Box b3 = Box.createHorizontalBox();
        b3.setBorder(new EmptyBorder(0, 10, 5, 10));
        JLabel lblFiles = new JLabel("Files");
        lblFiles.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblFiles.setForeground(Color.LIGHT_GRAY);
        lblFiles.setFont(lblFiles.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblEditor = new JLabel("Editor");
        lblEditor.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblEditor.setForeground(Color.DARK_GRAY);
        lblEditor.setFont(lblEditor.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblLogs = new JLabel("Logs");
        lblLogs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblLogs.setForeground(Color.DARK_GRAY);
        lblLogs.setFont(lblLogs.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblTasks = new JLabel("Monitoring");
        lblTasks.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblTasks.setForeground(Color.DARK_GRAY);
        lblTasks.setFont(lblTasks.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblSearch = new JLabel("Search");
        lblSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblSearch.setForeground(Color.DARK_GRAY);
        lblSearch.setFont(lblSearch.getFont().deriveFont(Font.PLAIN, 18));

        b3.add(lblFiles);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblEditor);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblLogs);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblTasks);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblSearch);

        b3.setAlignmentX(Box.LEFT_ALIGNMENT);
        b2.add(b3);

        p2.add(b2);

        JPanel p3 = new JPanel(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        {
            JPanel pp1 = new JPanel(new BorderLayout());
            Box b11 = Box.createHorizontalBox();
            b11.setOpaque(false);
            b11.setBorder(new EmptyBorder(10, 10, 10, 10));
//            b11.setBackground(new Color(30,30,30));
//            b11.setBorder(new EmptyBorder(5,10,5,10));
            JLabel l1 = new JLabel();
            l1.setForeground(Color.GRAY);
            l1.setFont(getFontAwesomeFont());
            l1.setText("\uf07c");
            b11.add(l1);
            b11.add(Box.createHorizontalStrut(10));
            JLabel l2 = new JLabel("Remote - /home/subhro");
            l2.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
            l2.setForeground(Color.GRAY);
            b11.add(l2);
            b11.add(Box.createHorizontalGlue());
            JLabel l3 = new JLabel();
            l3.setFont(getFontAwesomeFont());
            l3.setText("\uf0d7");
            l3.setForeground(Color.GRAY);
            b11.add(l3);
            pp1.add(b11, BorderLayout.NORTH);

            JPanel pp2 = new JPanel(new BorderLayout(5, 5));
            Box bb1 = Box.createHorizontalBox();
//            bb1.setOpaque(true);
//            bb1.setBackground(new Color(30, 30, 30));
            bb1.add(createFontAwesomeButton("\uf104", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf105", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf015", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf106", Color.GRAY));


            bb1.add(createFontAwesomeButton("\uf0a0", Color.GRAY));
            JLabel lll1 = new JLabel("home");
            lll1.setForeground(Color.GRAY);

            bb1.add(createFontAwesomeButton("\uf0da", Color.GRAY));
            bb1.add(lll1);


            bb1.add(createFontAwesomeButton("\uf0da", Color.GRAY));

            JLabel lll2 = new JLabel("subhro");
            lll2.setForeground(Color.LIGHT_GRAY);
            bb1.add(lll2);
            //bb1.add(createFontAwesomeButton("subhro", Color.LIGHT_GRAY));

            pp2.add(bb1, BorderLayout.NORTH);

            JLabel lbllll = new JLabel();
            lbllll.setOpaque(true);
            lbllll.setMinimumSize(new Dimension(1, 100));
            lbllll.setMaximumSize(new Dimension(1, 100));
            lbllll.setPreferredSize(new Dimension(1, 100));

            lbllll.setBackground(new Color(30, 30, 30));
            pp2.add(lbllll, BorderLayout.EAST);

            ListCellRenderer<String> r = new ListCellRenderer<String>() {
                JPanel panel = new JPanel(new BorderLayout());

                {
                    JLabel lblIcon = new JLabel();
                    lblIcon.setBorder(new LineBorder(new Color(11, 11, 11), 5));
                    lblIcon.setText("\uf1c6");
                    lblIcon.setFont(getFontAwesomeFont().deriveFont(Font.PLAIN, 25.f));
                    lblIcon.setHorizontalAlignment(JLabel.CENTER);
                    lblIcon.setVerticalAlignment(JLabel.CENTER);
                    lblIcon.setForeground(Color.WHITE);
                    lblIcon.setBackground(new Color(92, 167, 25));
                    lblIcon.setOpaque(true);
                    lblIcon.setMinimumSize(new Dimension(50, 50));
                    lblIcon.setPreferredSize(new Dimension(50, 50));

                    JLabel lblTitle = new JLabel("Documents");
                    lblTitle.setForeground(Color.GRAY);
                    lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
                    lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);

                    Box b32 = Box.createHorizontalBox();
                    JLabel lx1 = new JLabel("24 MB - 10/12/2018");
                    lx1.setForeground(Color.DARK_GRAY);
                    JLabel lx2 = new JLabel("subhro - drwxr--r--");
                    lx2.setForeground(Color.DARK_GRAY);

                    b32.add(lx1);
                    b32.add(Box.createHorizontalGlue());
                    b32.add(lx2);
                    b32.setAlignmentX(Box.LEFT_ALIGNMENT);

                    panel.add(lblIcon, BorderLayout.WEST);
                    Box b43 = Box.createVerticalBox();
                    b43.add(Box.createVerticalGlue());
                    b43.add(lblTitle);
                    b43.add(b32);
                    b43.add(Box.createVerticalGlue());

                    panel.add(b43);

                    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                }


                @Override
                public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
                    return panel;
                }
            };

            JList<String> list = new JList<>(new String[]{"a", "b", "c"});
            list.setBackground(new Color(11, 11, 11));
            list.setCellRenderer(r);

            JScrollPane jsp = new JScrollPane(list);
            jsp.setBorder(null);
            pp2.add(jsp);
            pp1.add(pp2);

            splitPane.setLeftComponent(pp1);
        }

        {
            JPanel pp1 = new JPanel(new BorderLayout());
            Box b11 = Box.createHorizontalBox();
            b11.setOpaque(true);
            b11.setBorder(new EmptyBorder(10, 10, 10, 10));
//            b11.setBackground(new Color(30,30,30));
//            b11.setBorder(new EmptyBorder(5,10,5,10));
            JLabel l1 = new JLabel();
            l1.setForeground(Color.GRAY);
            l1.setFont(getFontAwesomeFont());
            l1.setText("\uf07c");
            b11.add(l1);
            b11.add(Box.createHorizontalStrut(10));
            JLabel l2 = new JLabel("Remote - /home/subhro");
            l2.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
            l2.setForeground(Color.GRAY);
            b11.add(l2);
            b11.add(Box.createHorizontalGlue());
            JLabel l3 = new JLabel();
            l3.setFont(getFontAwesomeFont());
            l3.setText("\uf0d7");
            l3.setForeground(Color.GRAY);
            b11.add(l3);
            pp1.add(b11, BorderLayout.NORTH);

            JPanel pp2 = new JPanel(new BorderLayout());
            Box bb1 = Box.createHorizontalBox();
//            bb1.setOpaque(true);
//            bb1.setBackground(new Color(30, 30, 30));
            bb1.add(createFontAwesomeButton("\uf104", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf105", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf015", Color.GRAY));
            bb1.add(createFontAwesomeButton("\uf106", Color.GRAY));


            bb1.add(createFontAwesomeButton("\uf0a0", Color.GRAY));
            JLabel lll1 = new JLabel("home");
            lll1.setForeground(Color.GRAY);

            bb1.add(createFontAwesomeButton("\uf0da", Color.GRAY));
            bb1.add(lll1);


            bb1.add(createFontAwesomeButton("\uf0da", Color.GRAY));

            JLabel lll2 = new JLabel("subhro");
            lll2.setForeground(Color.LIGHT_GRAY);
            bb1.add(lll2);
            //bb1.add(createFontAwesomeButton("subhro", Color.LIGHT_GRAY));

            pp2.add(bb1, BorderLayout.NORTH);

            ListCellRenderer<String> r = new ListCellRenderer<String>() {
                JPanel panel = new JPanel(new BorderLayout());

                {
                    JLabel lblIcon = new JLabel();
                    lblIcon.setBorder(new LineBorder(new Color(11, 11, 11), 5));
                    lblIcon.setText("\uf1c6");
                    lblIcon.setFont(getFontAwesomeFont().deriveFont(Font.PLAIN, 25.f));
                    lblIcon.setHorizontalAlignment(JLabel.CENTER);
                    lblIcon.setVerticalAlignment(JLabel.CENTER);
                    lblIcon.setForeground(Color.WHITE);
                    lblIcon.setBackground(new Color(92, 167, 25));
                    lblIcon.setOpaque(true);
                    lblIcon.setMinimumSize(new Dimension(50, 50));
                    lblIcon.setPreferredSize(new Dimension(50, 50));

                    JLabel lblTitle = new JLabel("Documents");
                    lblTitle.setForeground(Color.GRAY);
                    lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
                    lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);

                    Box b32 = Box.createHorizontalBox();
                    JLabel lx1 = new JLabel("24 MB - 10/12/2018");
                    lx1.setForeground(Color.DARK_GRAY);
                    JLabel lx2 = new JLabel("subhro - drwxr--r--");
                    lx2.setForeground(Color.DARK_GRAY);

                    b32.add(lx1);
                    b32.add(Box.createHorizontalGlue());
                    b32.add(lx2);
                    b32.setAlignmentX(Box.LEFT_ALIGNMENT);

                    panel.add(lblIcon, BorderLayout.WEST);
                    Box b43 = Box.createVerticalBox();
                    b43.add(Box.createVerticalGlue());
                    b43.add(lblTitle);
                    b43.add(b32);
                    b43.add(Box.createVerticalGlue());

                    panel.add(b43);

                    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                }


                @Override
                public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
                    return panel;
                }
            };

            JList<String> list = new JList<>(new String[]{"a", "b"});
            list.setBackground(new Color(11, 11, 11));
            list.setCellRenderer(r);

            JScrollPane jsp = new JScrollPane(list);
            jsp.setBorder(null);
            pp2.add(jsp);
            pp1.add(pp2);

            splitPane.setRightComponent(pp1);
        }


        //splitPane.setRightComponent(new JPanel());
        p3.add(splitPane);
        p3.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        jSplitPane.setTopComponent(p3);

        JPanel bottompJPanel = new JPanel(new BorderLayout());
        JLabel line = new JLabel();
        line.setPreferredSize(new Dimension(100, 1));
        line.setOpaque(true);
        line.setBackground(new Color(30, 30, 30));
        bottompJPanel.add(line, BorderLayout.NORTH);

        JPanel pp3 = new JPanel(new BorderLayout());

        Box bb4 = Box.createHorizontalBox();

        JLabel tp1 = new JLabel();
        tp1.setForeground(Color.DARK_GRAY);
        tp1.setFont(getFontAwesomeFont());
        tp1.setText("\uf120");

        JLabel tp2 = new JLabel("TERMINAL");
        tp2.setForeground(Color.DARK_GRAY);

        JLabel tp3 = new JLabel("Terminal 1");
        tp3.setForeground(Color.DARK_GRAY);

        JLabel tp4 = new JLabel();
        tp4.setForeground(Color.DARK_GRAY);
        tp4.setFont(getFontAwesomeFont());
        tp4.setText("\uf0d7");

        bb4.add(tp1);
        bb4.add(Box.createHorizontalStrut(5));
        bb4.add(tp2);
        bb4.add(Box.createHorizontalGlue());
        bb4.add(tp3);
        bb4.add(Box.createHorizontalStrut(5));
        bb4.add(tp4);

        bb4.setBorder(new EmptyBorder(5,10,5,10));

        pp3.add(bb4, BorderLayout.NORTH);

        bottompJPanel.add(pp3);

        jSplitPane.setBottomComponent(bottompJPanel);

        f.add(jSplitPane);
        splitPane.setResizeWeight(0.8);
        f.setVisible(true);
    }

    static JButton createFontAwesomeButton(String text, Color foreColor) {
        JButton btn = new JButton();
        btn.setForeground(foreColor);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFont(getFontAwesomeFont());
        btn.setText(text);
        return btn;
    }

    public static void loadFonts() {
        try (InputStream is = App.class.getResourceAsStream("/fontawesome-webfont.ttf")) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            fontAwesomeFont = font.deriveFont(Font.PLAIN, 14f);
            System.out.println("Font loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
