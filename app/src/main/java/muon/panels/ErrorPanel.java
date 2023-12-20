package muon.panels;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ErrorPanel extends JPanel {
    public ErrorPanel(ActionListener errorAction) {
        var label = new JLabel();
        label.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        label.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        var lblError = new JLabel("Operation failed");
        lblError.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        var button = new JButton("OK");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(errorAction);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.add(label);
        this.add(lblError);
        this.add(button);
        this.add(Box.createVerticalGlue());
    }
}
