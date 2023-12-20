package muon.screens.sessiontabs.filebrowser;

import muon.dto.session.SessionInfo;
import muon.screens.sessiontabs.SshClientInstance;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class RemoteFileBrowser extends JPanel {
    public RemoteFileBrowser(SessionInfo sessionInfo,
                             SshClientInstance initialInstance) {
        super(new BorderLayout());
        var c1 = AppTheme.INSTANCE.getBackground();
        var toolbar = Box.createHorizontalBox();
        toolbar.setOpaque(true);
        toolbar.setBackground(c1);

        toolbar.add(Box.createRigidArea(new Dimension(5,30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_ARROW_LEFT_CIRCLE_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5,30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_ARROW_RIGHT_CIRCLE_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5,30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_ARROW_UP_CIRCLE_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5,30)));
        toolbar.add(AppUtils.createIconButton(IconCode.RI_HOME_4_LINE));
        var addressBar = new JPanel(new BorderLayout());
        addressBar.setBackground(c1);
        var txtAddress = new JTextField();
        txtAddress.putClientProperty("textField.noBorder", Boolean.TRUE);
        txtAddress.setBackground(c1);
        txtAddress.setForeground(AppTheme.INSTANCE.getForeground());
        txtAddress.setBorder(new EmptyBorder(0, 5, 0, 0));
        txtAddress.setText("/usr/home/user/documents");
        toolbar.add(txtAddress);
        toolbar.add(AppUtils.createIconButton(IconCode.RI_MORE_2_LINE));
        toolbar.add(Box.createRigidArea(new Dimension(5,30)));
        toolbar.setBorder(new EmptyBorder(2, 5, 0, 0));

        setBackground(c1);
        String data[][] = {{"Documents", "20 M", "25/02/2002"},
                {"Video", "100 G", "04/12/2012"},
                {"Downloads", "32 K", "15/02/2002"}};
        String column[] = {"Name", "Size", "Date"};
        JTable jt = new JTable(data, column);
        jt.setShowGrid(false);
        jt.setBackground(c1);
        jt.setForeground(Color.GRAY);
        jt.setFillsViewportHeight(true);
        var header = jt.getTableHeader();
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
        header.setDefaultRenderer((a, b, c, d, e, f) -> {
            var label = new JLabel(b.toString());
            label.setBorder(
                    new CompoundBorder(new EmptyBorder(0, 0, 5, 0),
                            new CompoundBorder(
                                    new MatteBorder(1, f == 0 ? 0 : 1, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()),
                                    new EmptyBorder(5, 10, 5, 10)
                            )));
            label.setOpaque(true);
            label.setBackground(c1);
            //label.setForeground(new Color(80, 80, 80));
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            return label;
        });
        jt.setRowHeight(30);

        jt.setDefaultRenderer(Object.class, (a, b, c, d, e, f) -> {
            var p = new JPanel(new BorderLayout());
            p.setBackground(c1);
            if (f == 0) {
                var folderIconLbl = new JLabel();
                folderIconLbl.setVerticalAlignment(JLabel.CENTER);
                folderIconLbl.setVerticalTextPosition(JLabel.CENTER);
                folderIconLbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                folderIconLbl.setForeground(new Color(0, 120, 212));
                folderIconLbl.setFont(IconFont.getSharedInstance().getIconFont(24.0f));
                folderIconLbl.setText("\uED61");
                p.add(folderIconLbl, BorderLayout.WEST);
            }
            var label = new JLabel(b.toString());
            label.setBorder(
                    new CompoundBorder(
                            new MatteBorder(0, 0, 0, 0, c1),
                            new EmptyBorder(5, 10, 5, 10)));
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            p.add(label);
            return p;
        });
        JScrollPane sp = new JScrollPane(jt);
        sp.setBackground(c1);
        sp.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(sp);
        add(toolbar, BorderLayout.NORTH);
    }
}
