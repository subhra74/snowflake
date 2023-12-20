package muon.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

public class CustomFrame extends JFrame {
    private final CustomFramePanel panel;

    public CustomFrame(String title) {
        setTitle(title);
        panel = new CustomFramePanel(this);
        super.setContentPane(panel);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                Dwmapi.applyRoundCorner(CustomFrame.this);
            }
        });
//
//        this.addWindowStateListener(new WindowAdapter() {
//
//            @Override
//            public void windowStateChanged(WindowEvent e) {
//                if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
//                    setShape(null);
//                }
//            }
//        });
//        this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowOpened(WindowEvent e) {
//                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
//            }
//
//            @Override
//            public void windowDeiconified(WindowEvent e) {
//                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
//            }
//        });
//
//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//
//                if ((getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
//                    setShape(null);
//                } else {
//                    setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
//                }
//            }
//
//            @Override
//            public void componentShown(ComponentEvent e) {
//                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
//            }
//
//
//        });
    }

    @Override
    public Container getContentPane() {
        return panel.getContentPanel();
    }

    @Override
    public Component add(Component comp) {
        return panel.addContent(comp);
    }

    @Override
    public Container getContentPane() {
        return this.panel.getContentPanel();
    }
}
