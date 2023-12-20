package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class FlatProgressBarUI extends BasicProgressBarUI {

    public static ComponentUI createUI(JComponent c) {
        return new FlatProgressBarUI();
    }
    //private JProgressBar progressBar;

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (c.getWidth() <= 0 || c.getHeight() <= 0) {
            return;
        }

        // amount of progress to draw
        int amountFull = getAmountFull(c.getInsets(), c.getWidth(), c.getHeight());

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(AppTheme.INSTANCE.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.setColor(AppTheme.INSTANCE.getButtonRollOverBackground());
        g2.fillRoundRect(0, 0,
                c.getWidth(), c.getHeight(), 10, 10);
        g2.setColor(progressBar.getForeground());
        g2.fillRoundRect(0, 0, amountFull, c.getHeight(), 10, 10);
    }

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        if (c.getWidth() <= 0 || c.getHeight() <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(AppTheme.INSTANCE.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
        g2.setColor(AppTheme.INSTANCE.getButtonRollOverBackground());
        g2.fillRoundRect(0, 0,
                c.getWidth(), c.getHeight(), 10, 10);
        boxRect = getBox(boxRect);
        if (boxRect != null) {
            g2.setColor(progressBar.getForeground());
            g2.fillRoundRect(boxRect.x, 0, boxRect.width, c.getHeight(), 10, 10);
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        super.paint(g2, c);
    }
}
