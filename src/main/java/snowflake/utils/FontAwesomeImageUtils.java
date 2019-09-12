package snowflake.utils;

import snowflake.App;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class FontAwesomeImageUtils {
    private static final Graphics2D BASE_GRAPHICS = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB).createGraphics();
    private static Font fontAwesomeFont;

    static {
        try (InputStream is = App.class.getResourceAsStream("/fontawesome-webfont.ttf")) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            fontAwesomeFont = font.deriveFont(Font.PLAIN, 14f);
            System.out.println("Font loaded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Image createImage(String text, Color color) {
        BASE_GRAPHICS.setFont(fontAwesomeFont);
        Rectangle r = BASE_GRAPHICS.getFontMetrics().getStringBounds(text, BASE_GRAPHICS).getBounds();
        BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        g.setFont(fontAwesomeFont);
        g.setColor(color);
        g.drawString(text, 0, g.getFontMetrics(fontAwesomeFont).getAscent());
        g.dispose();
        return img;
    }

    public static Image createImage(String text, Color color, int size) {
        Font font = fontAwesomeFont.deriveFont(Font.PLAIN, size);
        BASE_GRAPHICS.setFont(font);
        Rectangle r = BASE_GRAPHICS.getFontMetrics().getStringBounds(text, BASE_GRAPHICS).getBounds();
        BufferedImage img = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, 0, g.getFontMetrics(font).getAscent());
        g.dispose();
        return img;
    }

//    public static Image createImage(String text, int size) {
//        Font f = App.getFontAwesomeFont().deriveFont(Font.PLAIN, size);
//        BufferedImage img = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2 = img.createGraphics();
//        g2.setFont(f);
//        Rectangle r = g2.getFontMetrics().getStringBounds(text, g2).getBounds();
//        System.out.println("Image rect: " + r);
//        BufferedImage img2 = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g = img2.createGraphics();
//        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        g.setFont(f);
//        g.setColor(Color.GRAY);
//        g.drawString(text, 0, g.getFontMetrics(f).getAscent());
//        g2.dispose();
//        img.flush();
//        return img2;
//    }

    public static ImageIcon createIcon(String text, Color color, int size) {
        return new ImageIcon(createImage(text, color, size));
    }

    public static ImageIcon createIcon(String text, Color color) {
        return new ImageIcon(createImage(text, color));
    }
}
