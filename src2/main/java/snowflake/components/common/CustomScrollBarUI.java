package snowflake.components.common;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomScrollBarUI extends BasicScrollBarUI {
    private AtomicBoolean hot = new AtomicBoolean(false);

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hot.set(true);
                c.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hot.set(false);
                c.repaint();
            }
        });
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(Color.WHITE);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if(hot.get()){
            g.setColor(new Color(240, 240, 240));
        }
        else{
            g.setColor(new Color(230, 230, 230));
        }
        g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton btn = new JButton();
        btn.setMaximumSize(new Dimension(0, 0));
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton btn = new JButton();
        btn.setMaximumSize(new Dimension(0, 0));
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }
}
