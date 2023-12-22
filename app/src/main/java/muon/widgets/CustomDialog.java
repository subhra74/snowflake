package muon.widgets;

import javax.swing.*;
import java.awt.*;

public class CustomDialog extends JDialog {
    private final CustomFramePanel panel;

    public CustomDialog(Window parent, String title) {
        super(parent);
        setTitle(title);
        panel = new CustomFramePanel(this);
        panel.setMaximizable(false);
        panel.setMinimizable(false);
        super.getContentPane().add(panel);
    }

    @Override
    public Component add(Component comp) {
        //return panel.addContent(comp);
        throw new RuntimeException("Can't add to custom frame directly, use getContentPane()");
    }

    @Override
    public Container getContentPane() {
        return this.panel.getContentPanel();
    }
}
