package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.util.AppUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TransferConfirmPanel extends JPanel {
    private JTextField txtPath;
    private JLabel lblFileDetails;
    private JCheckBox chkBackground;
    private JComboBox<String> cmbConflict;
    private JButton btnOK, btnCancel;

    public TransferConfirmPanel(ActionListener onOK, ActionListener onCancel) {
        super(new GridBagLayout());

        var vbox = Box.createVerticalBox();

        lblFileDetails = new JLabel("Transfer 5 files and 2 folders to");
        lblFileDetails.setBorder(new EmptyBorder(5, 0, 5, 0));
        lblFileDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPath = new JTextField(30);
        txtPath.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkBackground = new JCheckBox("Transfer in background");
        chkBackground.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbConflict = new JComboBox<>(new String[]{"Ask what to do", "Replace if newer", "Skip"});
        var lblConflict = new JLabel("If same file already exist");
        lblConflict.setBorder(new EmptyBorder(15, 0, 5, 10));
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        btnOK.addActionListener(onOK);
        btnCancel.addActionListener(onCancel);
        AppUtils.makeEqualSize(btnOK, btnCancel);

        var width = btnOK.getPreferredSize().width * 2 + 10;
        cmbConflict.setPreferredSize(new Dimension(width, cmbConflict.getPreferredSize().height));
        cmbConflict.setMaximumSize(new Dimension(width, cmbConflict.getPreferredSize().height));
        cmbConflict.setAlignmentX(Component.LEFT_ALIGNMENT);
        //cmbConflict.setMaximumSize(cmbConflict.getPreferredSize());

        vbox.add(lblFileDetails);
        vbox.add(txtPath);

        vbox.add(lblConflict);
        vbox.add(cmbConflict);

//        var hb1 = Box.createHorizontalBox();
//        hb1.setBorder(new EmptyBorder(10, 0, 5, 0));
//        hb1.add(lblConflict);
//        hb1.add(Box.createHorizontalGlue());
//        hb1.add(cmbConflict);
//        hb1.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        vbox.add(hb1);
        //chkBackground.setBorder(new EmptyBorder(20,0,10,0));
        //vbox.add(chkBackground);

        var hb2 = Box.createHorizontalBox();
        hb2.setBorder(new EmptyBorder(15, 0, 5, 0));
        hb2.add(chkBackground);
        hb2.add(Box.createHorizontalGlue());
        hb2.add(btnOK);
        hb2.add(Box.createRigidArea(new Dimension(10, 10)));
        hb2.add(btnCancel);
        hb2.setAlignmentX(Component.LEFT_ALIGNMENT);

        vbox.add(hb2);

        var c = 0;
        var gc = new GridBagConstraints();
        add(vbox, gc);
    }

    public void setFileInfo(String info, String path) {
        txtPath.setText(path);
        lblFileDetails.setText(info);
    }

    public void setFocus() {
        btnOK.setFocusable(true);
        btnOK.setDefaultCapable(true);
        btnOK.requestFocusInWindow();
    }
}
