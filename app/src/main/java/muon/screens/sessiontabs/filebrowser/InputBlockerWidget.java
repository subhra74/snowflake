package muon.screens.sessiontabs.filebrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

public class InputBlockerWidget extends JComponent {
    public InputBlockerWidget(){
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
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
}
