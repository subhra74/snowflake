package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FlatPasswordFieldUI extends BasicPasswordFieldUI implements FocusListener {
    private JTextComponent editor;
    public static ComponentUI createUI(JComponent c) {
        return new FlatPasswordFieldUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JTextComponent) {
            editor = (JTextComponent) c;
            editor.putClientProperty("textField.arc", AppTheme.INSTANCE.getButtonBorderArc());
            editor.addFocusListener(this);
        }
    }

    @Override
    protected void paintSafely(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        super.paintSafely(g2);
    }

    @Override
    protected void paintBackground(Graphics g) {
        Integer arc = (Integer) editor.getClientProperty("textField.arc");
        g.setColor(editor.getBackground());
        g.fillRoundRect(0, 0, editor.getWidth(), editor.getHeight(), arc, arc);

        g.setColor(editor.isFocusOwner() ? AppTheme.INSTANCE.getSelectionColor() : AppTheme.INSTANCE.getButtonBorderColor());
        g.drawRoundRect(0, 0, editor.getWidth() - 1, editor.getHeight() - 1, arc, arc);
    }

    @Override
    public void focusGained(FocusEvent e) {
        editor.repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        editor.repaint();
    }
}
