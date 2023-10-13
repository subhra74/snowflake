package muon.widgets;

import javax.swing.*;
import java.awt.*;

public class CustomFrame extends JFrame {
    private final CustomFramePanel panel;

    public CustomFrame(String title) {
        setTitle(title);
        panel = new CustomFramePanel(this);
        super.getContentPane().add(panel);
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
