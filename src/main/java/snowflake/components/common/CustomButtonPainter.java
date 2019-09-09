package snowflake.components.common;

import javax.swing.*;
import java.awt.*;

public class CustomButtonPainter {
    private Color bgColor, textColor, hotColor, pressedColor;
    private boolean roundedBorder;
    private Painter<JButton> normalPainter, hotPainter, pressedPainter;

    public CustomButtonPainter(Color bgColor, Color hotColor, Color pressedColor) {
        this.bgColor = bgColor;
        this.hotColor = hotColor;
        this.pressedColor = pressedColor;

        normalPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setColor(bgColor);
            System.out.println("normal painter called");
            g.fillRect(0, 0, width, height);
        };

        hotPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setColor(hotColor);
            g.fillRect(0, 0, width, height);
        };

        pressedPainter = (Graphics2D g, JButton object, int width, int height) -> {
            g.setColor(pressedColor);
            g.fillRect(0, 0, width, height);
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
