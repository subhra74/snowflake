package snowflake.components.main;

import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundPanel extends JPanel {
    private Color color = new Color(29, 125, 212);

    public RoundPanel() {
        setLayout(new BorderLayout());
        JButton btn = new JButton();
        btn.setFont(App.getFontAwesomeFont().deriveFont(Font.BOLD, 20.0f));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setText("\uf2dc");
        add(btn);
        setPreferredSize(new Dimension(60, 60));
        setMaximumSize(new Dimension(60, 60));
        setMinimumSize(new Dimension(60, 60));
        setBorder(new EmptyBorder(5,5,5,5));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        int radius = Math.min(getWidth() - 10, getHeight() - 10);
        int x = getWidth() / 2 - radius / 2;
        int y = getHeight() / 2 - radius / 2;
        g2.fillOval(x, y, radius, radius);
    }
}
