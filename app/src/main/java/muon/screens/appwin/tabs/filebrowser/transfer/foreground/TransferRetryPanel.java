package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TransferRetryPanel extends JPanel {
    private JButton btnRetry, btnCancel;
    private JLabel lblErrorDetails;

    public TransferRetryPanel(ActionListener retryAction, ActionListener cancelAction) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var lblIcon = new JLabel();
        lblIcon.setFont(IconFont.getSharedInstance().getIconFont(128.0f));
        lblIcon.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblErrorDetails = new JLabel("Operation failed");
        lblErrorDetails.setBorder(new EmptyBorder(5, 0, 15, 0));
        lblErrorDetails.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRetry = new JButton("Retry");
        btnCancel = new JButton("Cancel");
        btnRetry.addActionListener(retryAction);
        btnCancel.addActionListener(cancelAction);
        AppUtils.makeEqualSize(btnRetry, btnCancel);

        var hb2 = Box.createHorizontalBox();
        hb2.setBorder(new EmptyBorder(15, 0, 5, 0));
        hb2.add(Box.createHorizontalGlue());
        hb2.add(btnRetry);
        hb2.add(Box.createRigidArea(new Dimension(10, 10)));
        hb2.add(btnCancel);
        hb2.add(Box.createHorizontalGlue());
        hb2.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(Box.createVerticalGlue());
        this.add(lblIcon);
        this.add(lblErrorDetails);
        this.add(hb2);
        hb2.add(Box.createVerticalGlue());
    }
}
