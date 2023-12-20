package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.Optional;

public class FlatScrollBarUI extends BasicScrollBarUI {
    public static ComponentUI createUI(JComponent c) {
        return new FlatScrollBarUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.setPreferredSize(new Dimension(14, 15));
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        int w = thumbBounds.width;
        int h = thumbBounds.height;

        g.translate(thumbBounds.x, thumbBounds.y);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2.setColor(getTrackColor(c));
        g2.fillRect(0, 0, w, h);

        if (isThumbRollover()) {
            g2.setColor(AppTheme.INSTANCE.getScrollThumbRollOverColor());
        } else {
            g2.setColor(AppTheme.INSTANCE.getScrollThumbColor());
        }

        g2.fillRoundRect(3, 0, w - 6, h, 10, 10);
        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(getTrackColor(c));
        g.translate(r.x, r.y);
        g.fillRect(0, 0, r.width, r.height);
        g.translate(-r.x, -r.y);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    protected JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    private Color getTrackColor(JComponent c) {
        var trackColor = (Color) c.getClientProperty("scrollbar.background");
        return Optional.ofNullable(trackColor).orElse(AppTheme.INSTANCE.getBackground());
    }

}
