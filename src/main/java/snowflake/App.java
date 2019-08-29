package snowflake;

import snowflake.common.GlobalSettings;
import snowflake.components.main.MainContent;
import snowflake.utils.PathUtils;

import javax.swing.*;
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

        //UIManager.put("ScrollBar.thumbHeight", 8);
        //UIManager.put("ScrollBar:\"ScrollBar.button\".size", 5);
        UIManager.put("Panel.background", new Color(245, 245, 245));
        UIManager.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {

            }
        });
        UIManager.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new Painter() {
            @Override
            public void paint(Graphics2D g, Object object, int width, int height) {

            }
        });
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
