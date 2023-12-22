package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.util.AppUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TransferProgressPanel extends JPanel {
    private JLabel lblFileDetails, lblProgressInfo;
    private JProgressBar prgTransfer;
    private JButton btnCancel;

    public TransferProgressPanel(ActionListener onCancel) {
        super(new GridBagLayout());
        lblFileDetails = new JLabel("Transferring abc.txt");
        lblFileDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFileDetails.setBorder(new EmptyBorder(0, 0, 10, 0));
        prgTransfer = new JProgressBar();
        prgTransfer.setAlignmentX(Component.LEFT_ALIGNMENT);
        prgTransfer.setPreferredSize(new Dimension(300, 5));
        lblProgressInfo = new JLabel("-- KB of -- MB at 23K/s, ETA 01:23:04 s");
        lblProgressInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblProgressInfo.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(onCancel);

        var hb1 = Box.createHorizontalBox();
        hb1.setAlignmentX(Component.LEFT_ALIGNMENT);
        hb1.add(Box.createHorizontalGlue());
        hb1.add(Box.createRigidArea(new Dimension(10, 10)));
        hb1.add(btnCancel);

        var vbox = Box.createVerticalBox();
        vbox.add(lblFileDetails);
        vbox.add(prgTransfer);
        vbox.add(lblProgressInfo);
        vbox.add(hb1);

        add(vbox);
    }

    public void setProgress(int progress) {
        prgTransfer.setValue(progress);
    }
}