package muon.ui.widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FrameDragPanel extends JPanel {

    private Component parentWindow;
    private int diffx, diffy;

    public FrameDragPanel(LayoutManager lm, Window w) {
        super(lm);
        parentWindow = w;
        registerMouseListener();
    }

    public void registerMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                diffx = me.getXOnScreen()
                        - parentWindow.getLocationOnScreen().x;
                diffy = me.getYOnScreen()
                        - parentWindow.getLocationOnScreen().y;
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                parentWindow.setLocation(me.getXOnScreen() - diffx,
                        me.getYOnScreen() - diffy);
            }
        });
    }
}
