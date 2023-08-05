package muon.ui.styles;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class FlatButtonUI extends BasicButtonUI {
    static FlatButtonUI buttonUI;

    public static ComponentUI createUI(JComponent c) {
        if (buttonUI == null) {
            buttonUI = new FlatButtonUI();
        }
        return buttonUI;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JButton) {
            JButton btn = (JButton) c;
        }
    }

    protected void paintButtonNormal(Graphics g, AbstractButton b) {
        if (!b.isOpaque()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setPaint(b.getBackground());
            g2.fillRect(0, 0, b.getWidth(), b.getHeight());
        }
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setColor(b.getBackground());
        // Color color = (Color) b.getClientProperty("xdmbutton.pressedcolor");
        // if (color != null) {
        // g2.setPaint(color);
        // } else {
        // g2.setPaint(Color.GRAY);
        // }
        g2.fillRect(0, 0, b.getWidth(), b.getHeight());
    }

    protected void paintButtonRollOver(Graphics g, AbstractButton b) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        // if (b.getClientProperty("xdmbutton.grayrollover") != null) {
        // g2.setPaint(Color.DARK_GRAY);
        // } else {
        // g2.setPaint(ColorResource.getSelectionColor());
        // }
        g2.setColor(b.getBackground());
        g2.fillRect(0, 0, b.getWidth(), b.getHeight());
    }

    public void paint(Graphics g, JComponent c) {
        try {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

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
}
