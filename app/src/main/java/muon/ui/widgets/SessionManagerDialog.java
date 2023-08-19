package muon.ui.widgets;

import muon.ui.styles.AppTheme;

import javax.swing.*;
import java.awt.*;

public class SessionManagerDialog extends JDialog {
    private final CustomFramePanel panel;
    public SessionManagerDialog(Window window) {
        super(window);
        setTitle("Session manager");
        setModal(true);
        setSize(800,600);
        panel = new CustomFramePanel(this);
        super.getContentPane().add(panel);
        panel.addContent(new SessionEditorPanel());
        setLocationRelativeTo(window);
        panel.setMaximizable(false);
        panel.setMinimizable(false);
    }

    @Override
    public Component add(Component comp) {
        return panel.addContent(comp);
    }
}
