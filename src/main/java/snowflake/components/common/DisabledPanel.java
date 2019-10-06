package snowflake.components.common;

import snowflake.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisabledPanel extends JPanel {
    double angle = 0.0;
    JButton btn = new JButton();
    AtomicBoolean stopFlag;
    Color c1 = new Color(3, 155, 229);
    Stroke basicStroke = new BasicStroke(5);
    Timer timer;
    AlphaComposite alphaComposite = AlphaComposite.SrcOver.derive(0.65f);
    AlphaComposite alphaComposite1 = AlphaComposite.SrcOver.derive(0.85f);

    public DisabledPanel() {
        BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
        setLayout(layout);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(App.getFontAwesomeFont().deriveFont(20.0f));
        btn.setForeground(Color.WHITE);
        btn.setText("\uf00d");
        btn.setAlignmentX(Box.CENTER_ALIGNMENT);
        setOpaque(false);
        btn.addActionListener(e -> {
            if (stopFlag != null) {
                stopFlag.set(true);
            }
        });
        add(Box.createVerticalGlue());
        add(btn);
        add(Box.createVerticalGlue());
        timer = new Timer(20, e -> {
            angle += Math.toRadians(5); // 5 degrees per 100 ms = 50 degrees/second
            while (angle > 2 * Math.PI)
                angle -= 2 * Math.PI;  // keep angle in reasonable range.
            int x = getWidth() / 2 - 70 / 2;
            int y = getHeight() / 2 - 70 / 2;
            repaint(x, y, 70, 70);
        });
        addMouseListener(new MouseAdapter() {
        });
        addMouseMotionListener(new MouseAdapter() {
        });
        addKeyListener(new KeyAdapter() {
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });
        setFocusTraversalKeysEnabled(false);
    }

    public void startAnimation(AtomicBoolean stopFlag) {
        this.stopFlag = stopFlag;
        this.btn.setVisible(stopFlag != null);
        timer.start();
    }

    public void stopAnimation() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(alphaComposite);
        g2.setColor(Color.BLACK);
        Rectangle r = g.getClipBounds();
        g2.fillRect(r.x, r.y, r.width, r.height);
        g2.setComposite(alphaComposite1);
        g2.setStroke(basicStroke);
        int x = getWidth() / 2 - 70 / 2;
        int y = getHeight() / 2 - 70 / 2;
        if (btn.isVisible()) {
            g2.setColor(Color.BLACK);
            g2.fillOval(x + 5, y + 5, 70 - 10, 70 - 10);
        }
        g2.setColor(c1);
        g2.rotate(angle, getWidth() / 2, getHeight() / 2);
        g2.drawArc(x + 5, y + 5, 70 - 10, 70 - 10, 0, 90);
        g2.rotate(-angle, getWidth() / 2, getHeight() / 2);
    }
}
