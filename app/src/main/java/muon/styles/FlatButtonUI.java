package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.Objects;

public class FlatButtonUI extends BasicButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new FlatButtonUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JButton btn) {
            btn.setRolloverEnabled(true);
            if (btn.getBackground() == null) {
                btn.setBackground(AppTheme.INSTANCE.getButtonBackground());
            }
            if (btn.getClientProperty("button.arc") == null) {
                btn.putClientProperty("button.arc", AppTheme.INSTANCE.getButtonBorderArc());
            }
            btn.setOpaque(false);
        }
    }

    protected void paintButtonNormal(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(b.getBackground());
            Integer arc = (Integer) b.getClientProperty("button.arc");
            if (arc == 0) {
                g2.fillRect(0, 0, b.getWidth(), b.getHeight());
            } else {
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
            }

            if (b.isBorderPainted()) {
                g2.setColor(AppTheme.INSTANCE.getButtonBorderColor());
                if (arc == 0) {
                    g2.drawRect(0, 0, b.getWidth() - 1, b.getHeight() - 1);
                } else {
                    g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, arc, arc);
                }
            }
        }
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (!Boolean.TRUE.equals(b.getClientProperty("button.noPressed"))) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(AppTheme.INSTANCE.getButtonPressedBackground());
            Integer arc = (Integer) b.getClientProperty("button.arc");
            if (arc == 0) {
                g2.fillRect(0, 0, b.getWidth(), b.getHeight());
            } else {
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
            }
        }
    }

    protected void paintButtonRollOver(Graphics g, AbstractButton b) {
        if (b.isRolloverEnabled()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(AppTheme.INSTANCE.getButtonRollOverBackground());
            Integer arc = (Integer) b.getClientProperty("button.arc");
            if (arc == 0) {
                g2.fillRect(0, 0, b.getWidth(), b.getHeight());
            } else {
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc);
            }
        }
    }


    public void paint(Graphics g, JComponent c) {
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            AbstractButton b = (AbstractButton) c;
            ButtonModel bm = b.getModel();
            if (bm.isRollover()) {
                paintButtonRollOver(g2, b);
            } else {
                paintButtonNormal(g2, b);
            }
            super.paint(g2, c);
        } catch (Exception e) {
        }
    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = c.getFontMetrics(g.getFont());
        if (model.isEnabled()) {
            g.setColor(b.getForeground());
        } else {
            var color = UIManager.getColor("Button.disabledForeground");
            if (Objects.nonNull(color)) {
                g.setColor(color);
            }
        }
        if (Objects.nonNull(text)) {
            g.drawString(text, textRect.x, textRect.y + fm.getAscent());
        }
    }
}
