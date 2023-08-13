package muon.ui.styles;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import java.awt.*;

public class FontIcon implements Icon {

    private int width, height;
    private IconCode iconCode;
    private Font font;
    private Color color;

    public FontIcon(IconCode iconCode, int width, int height, float fontSize, Color color) {
        this.iconCode = iconCode;
        this.width = width;
        this.height = height;
        this.font = IconFont.getSharedInstance().getIconFont(fontSize);
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        var color = g.getColor();
        var font = g.getFont();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(this.color);
        g2.setFont(this.font);
        g2.drawString(this.iconCode.getValue(),
                x / 2 + width / 2, y + g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent());
        g2.setFont(font);
        g2.setColor(color);
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
