package muon.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public class CustomFrame extends JFrame {
    private final CustomFramePanel panel;

    public CustomFrame(String title) {
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setTitle(title);
        panel = new CustomFramePanel(this);
        super.getContentPane().add(panel);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("componentResized: "+getSize());
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        });
    }

    @Override
    public Component add(Component comp) {
        throw new RuntimeException("Can't add to custom frame directly, use getContentPane()");
    }

    @Override
    public Container getContentPane() {
        return this.panel.getContentPanel();
    }
}
