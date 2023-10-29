package muon.screens.appwin.tabs.terminal;

import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TerminalHomePage extends JPanel {
    public TerminalHomePage(ActionListener callback) {
        super(new GridBagLayout());
        setBackground(AppTheme.INSTANCE.getDarkControlBackground());

        var btn1 = createIconButton3(IconCode.RI_TERMINAL_BOX_FILL, "New Terminal session", "Open a Terminal session over SSH");
        var btn2 = createIconButton3(IconCode.RI_CODE_BOX_FILL, "Manage Command Snippets", "Create snippet for useful commands");
        AppUtils.makeEqualSize(btn1, btn2);
        btn1.addActionListener(callback);

        var titleLabel = new JLabel("Remote Terminal");
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titleLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        var iconLbl = createIconLabel(IconCode.RI_TERMINAL_BOX_LINE, 128f);
        iconLbl.setForeground(AppTheme.INSTANCE.getButtonRollOverBackground());

        var i = 0;
        var gc = new GridBagConstraints();
        gc.gridy = i;
        add(iconLbl, gc);
        //add(createIconGrid(), gc);
        i++;
        gc.gridy = i;
        add(titleLabel, gc);
        //add(iconLabel, gc);
        i++;
        gc.gridy = i;
        add(Box.createRigidArea(new Dimension(10, 10)), gc);
        i++;
        gc.gridy = i;
        add(btn1, gc);
        i++;
        gc.gridy = i;
        add(Box.createRigidArea(new Dimension(10, 10)), gc);
        i++;
        gc.gridy = i;
        add(btn2, gc);

    }

    private JButton createIconButton3(IconCode icon, String title, String subtitle) {
        var hbox = Box.createHorizontalBox();

        var titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        var subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        subtitleLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());

        var vbox = Box.createVerticalBox();
        vbox.add(Box.createRigidArea(new Dimension(10, 3)));
        vbox.add(titleLabel);
        vbox.add(Box.createRigidArea(new Dimension(10, 3)));
        vbox.add(subtitleLabel);
        vbox.add(Box.createRigidArea(new Dimension(10, 5)));

        var lbl = createIconLabel(icon, 42f);
        lbl.setForeground(AppTheme.INSTANCE.getDisabledForeground());

        hbox.add(lbl);
        hbox.add(Box.createRigidArea(new Dimension(5, 0)));
        hbox.add(vbox);
        hbox.add(Box.createRigidArea(new Dimension(10, 0)));
        hbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        var button = new JButton();
        button.putClientProperty("button.arc", 10);
        button.setBackground(AppTheme.INSTANCE.getBackground());
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.add(hbox);

        return button;
    }


    private JLabel createIconLabel(IconCode icon, Float size) {
        var iconLabel = new JLabel();
        iconLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        iconLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());
        iconLabel.setFont(IconFont.getSharedInstance().getIconFont(size));
        iconLabel.setText(icon.getValue());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        return iconLabel;
    }
}
