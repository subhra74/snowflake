package snowflake.components.common;

import javax.swing.*;
import java.awt.*;

public class RoundedButtonPainter {
    private Painter<JButton> normalPainter, hotPainter, pressedPainter;
    private GradientPaint normalGradient, hotGradient, pressedGradient;
    private Color borderColor;

    public RoundedButtonPainter() {
        this.normalGradient = new GradientPaint(0, 0,
                new Color(245, 245, 245),
                0, 50,
                new Color(235, 235, 235));
        this.hotGradient = new GradientPaint(0, 0,
                new Color(255, 255, 255),
                0, 50,
                new Color(230, 230, 230));
        this.pressedGradient = new GradientPaint(0, 0,
                new Color(230, 230, 230),
                0, 50,
                new Color(180, 180, 180));
        this.borderColor = new Color(230, 230, 230);

        normalPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(normalGradient);
            g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
            g.setColor(borderColor);
            g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
        };

        hotPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(hotGradient);
            g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
            g.setColor(borderColor);
            g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
        };

        pressedPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(pressedGradient);
            g.fillRoundRect(1, 1, width - 2, height - 2, 7, 7);
            g.setColor(borderColor);
            g.drawRoundRect(1, 1, width - 2, height - 2, 7, 7);
        };
    }

    public Painter<JButton> getNormalPainter() {
        return normalPainter;
    }

    public Painter<JButton> getHotPainter() {
        return hotPainter;
    }

    public Painter<JButton> getPressedPainter() {
        return pressedPainter;
    }
}
