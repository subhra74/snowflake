package snowflake;

import snowflake.common.GlobalSettings;
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

    public static UIDefaults comboBoxSkin = new UIDefaults();
    public static UIDefaults toolBarButtonSkin = new UIDefaults();

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        System.out.println("Hello");
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        UIManager.put("control", Color.WHITE);
        //UIManager.put("nimbusBase", new Color(200, 200, 200));
//        UIManager.put("text", new Color(208, 208, 208));

        //UIManager.put("ScrollBar.thumbHeight", 8);
        //UIManager.put("ScrollBar:\"ScrollBar.button\".size", 5);
        //UIManager.put("Panel.background", new Color(245, 245, 245));
        UIManager.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });

        Painter<JComboBox> comboBoxPainterNormal = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
            }
        };

        Painter<JComboBox> comboBoxPainterFocused = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(220, 220, 220));
                g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
            }
        };

        Painter<JComboBox> comboBoxPainterHot = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(230, 230, 230));
                g.drawRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
            }
        };

        Painter<JComboBox> comboBoxPainterPressed = new Painter<JComboBox>() {
            @Override
            public void paint(Graphics2D g, JComboBox object, int width, int height) {
                g.setColor(new Color(220, 220, 220));
                g.fillRect(0, 0, width - 1, height - 1);
//                g.setColor(Color.BLACK);
//                g.fill(new Rectangle(0,0,width,height));
            }
        };


        comboBoxSkin.put("ComboBox[Enabled].backgroundPainter", comboBoxPainterNormal);
        comboBoxSkin.put("ComboBox[Focused].backgroundPainter", comboBoxPainterFocused);
        comboBoxSkin.put("ComboBox[MouseOver].backgroundPainter", comboBoxPainterHot);
        comboBoxSkin.put("ComboBox[Pressed].backgroundPainter", comboBoxPainterPressed);

        comboBoxSkin.put("ComboBox[Focused+Pressed].backgroundPainter", comboBoxPainterPressed);
        comboBoxSkin.put("ComboBox[Focused+MouseOver].backgroundPainter", comboBoxPainterHot);
        comboBoxSkin.put("ComboBox[Enabled+Selected].backgroundPainter", comboBoxPainterNormal);

        Painter<JButton> toolBarButtonPainterNormal = new Painter<JButton>() {
            @Override
            public void paint(Graphics2D g, JButton object, int width, int height) {

            }
        };

        Painter<JButton> toolBarButtonPainterHot = new Painter<JButton>() {
            @Override
            public void paint(Graphics2D g, JButton object, int width, int height) {
                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        Painter<JButton> toolBarButtonPainterPressed = new Painter<JButton>() {
            @Override
            public void paint(Graphics2D g, JButton object, int width, int height) {
                g.setColor(new Color(230, 230, 230));
                g.fillRect(0, 0, width - 1, height - 1);
            }
        };

        toolBarButtonSkin.put("Button[Enabled].backgroundPainter", toolBarButtonPainterNormal);
        toolBarButtonSkin.put("Button[Focused].backgroundPainter", toolBarButtonPainterNormal);
        toolBarButtonSkin.put("Button[Default].backgroundPainter", toolBarButtonPainterNormal);
        toolBarButtonSkin.put("Button[Default+Focused].backgroundPainter", toolBarButtonPainterNormal);

        toolBarButtonSkin.put("Button[Pressed].backgroundPainter", toolBarButtonPainterPressed);
        toolBarButtonSkin.put("Button[Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
        toolBarButtonSkin.put("Button[Default+Focused+Pressed].backgroundPainter", toolBarButtonPainterPressed);
        toolBarButtonSkin.put("Button[Default+Pressed].backgroundPainter", toolBarButtonPainterPressed);

        toolBarButtonSkin.put("Button[MouseOver].backgroundPainter", toolBarButtonPainterHot);
        toolBarButtonSkin.put("Button[Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);
        toolBarButtonSkin.put("Button[Default+MouseOver].backgroundPainter", toolBarButtonPainterHot);
        toolBarButtonSkin.put("Button[Default+Focused+MouseOver].backgroundPainter", toolBarButtonPainterHot);






//        UIManager.put("ComboBox[Enabled+Selected].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.drawRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });

//        UIManager.put("ComboBox[Focused+MouseOver].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.fillRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });

//        UIManager.put("ComboBox[Focused+Pressed].backgroundPainter", new Painter() {
//            @Override
//            public void paint(Graphics2D g, Object object, int width, int height) {
//                g.setColor(new Color(240,240,240));
//                g.fillRect(0,0,width-1,height-1);
////                g.setColor(Color.BLACK);
////                g.fill(new Rectangle(0,0,width,height));
//            }
//        });


        UIManager.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {
                g.setColor(Color.WHITE);
                g.fill(new Rectangle(0, 0, width, height));
            }
        });


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
