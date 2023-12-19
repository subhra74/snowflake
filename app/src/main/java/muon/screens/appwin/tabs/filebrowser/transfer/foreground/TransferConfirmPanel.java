package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.dto.file.FileInfo;
import muon.styles.AppTheme;
import muon.util.AppUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TransferConfirmPanel extends JPanel {
    private JTextField txtPath;
    private JLabel lblFileDetails, lblFileConflict, lblConflictAction;
    private JList<FileInfo> conflictingFiles;
    private DefaultListModel<FileInfo> conflictingFilesModel;
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
        conflictingFilesModel = new DefaultListModel<>();
        conflictingFiles = new JList<>(conflictingFilesModel);
        chkBackground = new JCheckBox("Transfer in background");
        chkBackground.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbConflict = new JComboBox<>(new String[]{"Replace if newer", "Replace", "Auto rename", "Skip"});
        cmbConflict.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblConflictAction = new JLabel("What to do");
        lblConflictAction.setBorder(new EmptyBorder(15, 0, 0, 0));
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        btnOK.addActionListener(onOK);
        btnCancel.addActionListener(onCancel);
        AppUtils.makeEqualSize(btnOK, btnCancel);

        var width = btnOK.getPreferredSize().width * 2 + 40;
        cmbConflict.setPreferredSize(new Dimension(width, cmbConflict.getPreferredSize().height));
        cmbConflict.setMaximumSize(new Dimension(width, cmbConflict.getPreferredSize().height));
        cmbConflict.setAlignmentX(Component.LEFT_ALIGNMENT);
        //cmbConflict.setMaximumSize(cmbConflict.getPreferredSize());

        lblFileConflict = new JLabel("Following files already exist");
        lblFileConflict.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFileConflict.setBorder(new EmptyBorder(10, 0, 10, 0));

        conflictingFiles.setSelectionBackground(AppTheme.INSTANCE.getBackground());
        conflictingFiles.setCellRenderer(new FileListRenderer<FileInfo>());
        var listScrollPane = new JScrollPane(conflictingFiles);
        listScrollPane.getViewport().setBackground(AppTheme.INSTANCE.getBackground());
        listScrollPane.setBackground(AppTheme.INSTANCE.getBackground());
        listScrollPane.setForeground(AppTheme.INSTANCE.getForeground());
        listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        var dim = new Dimension(txtPath.getPreferredSize().width, 100);
        listScrollPane.setPreferredSize(dim);

        vbox.add(lblFileDetails);
        vbox.add(txtPath);
        vbox.add(lblFileConflict);
        vbox.add(listScrollPane);

        vbox.add(lblConflictAction);
        //vbox.add(cmbConflict);

//        var hb1 = Box.createHorizontalBox();
//        hb1.setBorder(new EmptyBorder(10, 0, 5, 0));
//        hb1.add(lblConflictAction);
//        hb1.add(Box.createHorizontalGlue());
//        hb1.add(cmbConflict);
//        hb1.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        vbox.add(hb1);
        //chkBackground.setBorder(new EmptyBorder(20,0,10,0));
        //vbox.add(chkBackground);

        var hb2 = Box.createHorizontalBox();
        hb2.setBorder(new EmptyBorder(5, 0, 5, 0));
        hb2.add(cmbConflict);
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

    public void setFileInfo(String info, String path, List<FileInfo> files) {
        txtPath.setText(path);
        lblFileDetails.setText(info);
        conflictingFilesModel.clear();
        conflictingFilesModel.addAll(files);
        lblFileConflict.setVisible(files.size() > 0);
        cmbConflict.setVisible(files.size() > 0);
        lblConflictAction.setVisible(files.size() > 0);
    }

    public void setFocus() {
        btnOK.setFocusable(true);
        btnOK.setDefaultCapable(true);
        btnOK.requestFocusInWindow();
    }
}
