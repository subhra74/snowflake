package muon.panels;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RetryPanel extends JPanel {
    public RetryPanel(ActionListener retryCallback, ActionListener cancelCallback) {
        var label = new JLabel();
        label.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        label.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        var lblError = new JLabel("Unable to connect");
        lblError.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        var btnRetry = new JButton("Try again");
        btnRetry.addActionListener(retryCallback);
        var btnCancel = new JButton("Cencel");
        btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCancel.addActionListener(cancelCallback);

        var hb = Box.createHorizontalBox();
        hb.add(btnRetry);
        hb.add(Box.createRigidArea(new Dimension(10, 10)));
        hb.add(btnCancel);
        hb.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createVerticalGlue());
        this.add(label);
        this.add(lblError);
        this.add(hb);
        this.add(Box.createVerticalGlue());
    }
}
