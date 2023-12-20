package muon.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SpinnerPanel extends JPanel {
    public SpinnerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var label = new JLabel("Please wait");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        var prg = new JProgressBar();
        prg.setAlignmentX(Component.CENTER_ALIGNMENT);
        prg.setPreferredSize(new Dimension(200, 5));
        prg.setMaximumSize(new Dimension(200, 5));
        prg.setIndeterminate(true);

        this.add(Box.createVerticalGlue());
        this.add(label);
        this.add(prg);
        this.add(Box.createVerticalGlue());
    }
}
