package snowflake.components.settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JCheckBox chkConfirmBeforeDelete, chkConfirmBeforeMoveOrCopy, chkShowHiddenFilesByDefault,
            chkPromptForSudo, chkDirectoryCache, chkShowPathBar,
            chkConfirmBeforeTerminalClosing, chkUseDarkThemeForTerminal;
    private JComboBox<String> cmbDefaultOpenAction, cmbNumberOfSimultaneousConnection;

    public SettingsPanel() {
        super(new BorderLayout());
        chkConfirmBeforeDelete = new JCheckBox("Confirm before deleting files");
        chkConfirmBeforeMoveOrCopy = new JCheckBox("Confirm before moving or copying files");
        chkShowHiddenFilesByDefault = new JCheckBox("Confirm before delete");
        chkPromptForSudo = new JCheckBox("Prompt for sudo if operation fails due to permission issues");
        chkDirectoryCache = new JCheckBox("Use directory caching");
        chkShowPathBar = new JCheckBox("Show current folder in path bar style");
        chkConfirmBeforeTerminalClosing = new JCheckBox("Confirm before closing a terminal session");
        chkUseDarkThemeForTerminal = new JCheckBox("Use dark terminal theme");

        cmbDefaultOpenAction = new JComboBox<>(new String[]{"Open with default application", "Open with default editor", "Open with internal editor"});
        cmbDefaultOpenAction.setMaximumSize(cmbDefaultOpenAction.getPreferredSize());
        cmbNumberOfSimultaneousConnection = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        cmbNumberOfSimultaneousConnection.setMaximumSize(new Dimension(cmbDefaultOpenAction.getPreferredSize().width * 2, cmbDefaultOpenAction.getPreferredSize().height));

        Box hb1 = Box.createHorizontalBox();
        hb1.add(new JLabel("Default action for double click on files"));
        hb1.add(Box.createHorizontalGlue());
        hb1.add(cmbDefaultOpenAction);

        Box hb2 = Box.createHorizontalBox();
        hb2.add(new JLabel("Number of simultaneous connection for background file transfer"));
        hb2.add(Box.createHorizontalGlue());
        hb2.add(cmbNumberOfSimultaneousConnection);

        Box vbox = Box.createVerticalBox();
        chkConfirmBeforeDelete.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkConfirmBeforeMoveOrCopy.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkShowHiddenFilesByDefault.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkPromptForSudo.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkDirectoryCache.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkShowPathBar.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkConfirmBeforeTerminalClosing.setAlignmentX(Box.LEFT_ALIGNMENT);
        chkUseDarkThemeForTerminal.setAlignmentX(Box.LEFT_ALIGNMENT);
        hb1.setAlignmentX(Box.LEFT_ALIGNMENT);
        hb2.setAlignmentX(Box.LEFT_ALIGNMENT);

        vbox.add(chkConfirmBeforeDelete);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkConfirmBeforeMoveOrCopy);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkShowHiddenFilesByDefault);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkPromptForSudo);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkDirectoryCache);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkShowPathBar);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkConfirmBeforeTerminalClosing);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(chkUseDarkThemeForTerminal);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(hb1);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.add(hb2);
        vbox.add(Box.createRigidArea(new Dimension(10, 10)));
        vbox.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(vbox);

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        JButton btnOK = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        box.add(btnOK);
        box.add(Box.createHorizontalStrut(10));
        box.add(btnCancel);
        box.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(box, BorderLayout.SOUTH);
    }

    public void showDialog(JFrame frame) {
        JDialog dlg = new JDialog(frame);
        dlg.setTitle("Settings");
        dlg.add(this);
        dlg.setModal(true);
        dlg.setSize(640, 480);
        dlg.setLocationRelativeTo(frame);
        dlg.setVisible(true);
    }
}
