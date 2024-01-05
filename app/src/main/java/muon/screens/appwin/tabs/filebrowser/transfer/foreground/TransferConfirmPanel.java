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
    private JScrollPane scrollPane;
    private DefaultListModel<FileInfo> conflictingFilesModel;
    private JCheckBox chkBackground;
    private JComboBox<String> cmbConflict;
    private JButton btnOK, btnCancel;

    public TransferConfirmPanel(ActionListener onOK, ActionListener onCancel) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

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
        lblConflictAction.setBorder(new EmptyBorder(10, 0, 0, 0));
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
        lblFileConflict.setBorder(new EmptyBorder(10, 0, 5, 0));
        conflictingFiles.setCellRenderer(new FileListRenderer<FileInfo>());
        scrollPane = new JScrollPane(conflictingFiles);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        var dim = new Dimension(txtPath.getPreferredSize().width, 100);
        scrollPane.setPreferredSize(dim);

        var vbox1 = Box.createVerticalBox();
        vbox1.add(lblFileDetails);
        vbox1.add(txtPath);
        vbox1.add(lblFileConflict);

        var vbox2 = Box.createVerticalBox();
        vbox2.add(scrollPane);
        vbox2.add(lblConflictAction);

        var hb2 = Box.createHorizontalBox();
        hb2.setBorder(new EmptyBorder(5, 0, 5, 0));
        hb2.add(cmbConflict);
        hb2.add(Box.createHorizontalGlue());
        hb2.add(btnOK);
        hb2.add(Box.createRigidArea(new Dimension(10, 10)));
        hb2.add(btnCancel);
        hb2.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(vbox1, BorderLayout.NORTH);
        add(vbox2);
        add(hb2, BorderLayout.SOUTH);
    }

    public void setFileInfo(String info, String path, List<FileInfo> files) {
        txtPath.setText(path);
        lblFileDetails.setText(info);
        conflictingFilesModel.clear();
        conflictingFilesModel.addAll(files);
        lblFileConflict.setVisible(files.size() > 0);
        cmbConflict.setVisible(files.size() > 0);
        lblConflictAction.setVisible(files.size() > 0);
        scrollPane.setVisible(files.size() > 0);
    }

    public void setFocus() {
        btnOK.setFocusable(true);
        btnOK.setDefaultCapable(true);
        btnOK.requestFocusInWindow();
    }
}
