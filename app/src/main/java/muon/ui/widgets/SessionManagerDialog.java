package muon.ui.widgets;

import muon.ui.styles.AppTheme;

import javax.swing.*;
import java.awt.*;

public class SessionManagerDialog extends JDialog {
    public SessionManagerDialog(Window window) {
        super(window);
        setTitle("Session manager");
        setModal(true);
        setSize(800, 600);
        setLocationRelativeTo(window);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(
                AppTheme.INSTANCE.getDarkControlBackground());

        add(new SessionEditorPanel());
    }
}
