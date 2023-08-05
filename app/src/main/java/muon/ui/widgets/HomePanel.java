package muon.ui.widgets;

import muon.ui.styles.AppTheme;
import muon.ui.styles.FlatButtonUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class HomePanel extends JPanel {
    public HomePanel() {
        super(null);
        setBackground(AppTheme.INSTANCE.getBackground());
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        var iconLabel = new JLabel("Jdadasdasd");
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        var btn2 = new JButton("Jdadasdasd");
        btn2.setUI(new FlatButtonUI());
        btn2.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(iconLabel);
        add(Box.createRigidArea(new Dimension(0,10)));
        add(btn2);
        add(Box.createVerticalGlue());
    }
}
