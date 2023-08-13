package muon.ui.styles;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class FlatComboBoxUI extends BasicComboBoxUI {
    public static ComponentUI createUI(JComponent x) {
        return new FlatComboBoxUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        super.paint(g2, c);


        g.setColor(c.isFocusOwner() ? AppTheme.INSTANCE.getSelectionColor() :
                AppTheme.INSTANCE.getTextFieldBorderColor());
        g.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1,
                AppTheme.INSTANCE.getButtonBorderArc(), AppTheme.INSTANCE.getButtonBorderArc());

    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        //super.paintCurrentValue(g, bounds, hasFocus);
        ListCellRenderer<Object> renderer = comboBox.getRenderer();
        Component c = renderer.getListCellRendererComponent(listBox,
                comboBox.getSelectedItem(),
                -1,
                false,
                false);
        c.setBackground(UIManager.getColor("ComboBox.background"));
        c.setFont(comboBox.getFont());
        c.setForeground(comboBox.getForeground());
        c.setBackground(comboBox.getBackground());
        boolean shouldValidate = false;
        if (c instanceof JPanel) {
            shouldValidate = true;
        }
        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
        if (padding != null) {
            x = bounds.x + padding.left;
            y = bounds.y + padding.top;
            w = bounds.width - (padding.left + padding.right);
            h = bounds.height - (padding.top + padding.bottom);
        }

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);
    }

    @Override
    protected JButton createArrowButton() {
        var button = new JButton();
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFont(IconFont.getSharedInstance().getIconFont(16));
        button.setText(IconCode.RI_ARROW_DOWN_S_LINE.getValue());
        button.setForeground(AppTheme.INSTANCE.getForeground());
        button.setName("ComboBox.arrowButton");
        button.setBorder(new EmptyBorder(0, 5, 0, 5));
        button.putClientProperty("button.noPressed", Boolean.TRUE);
        return button;
    }
}
