package muon.screens.appwin.tabs.filebrowser;

import muon.styles.AppTheme;
import muon.styles.FontIcon;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class FileBrowserHomePage extends JPanel {
    public FileBrowserHomePage(ActionListener localClicked, ActionListener remoteClicked) {
        super(new GridBagLayout());
        setBackground(AppTheme.INSTANCE.getDarkControlBackground());

        var btn1 = createIconButton3(IconCode.RI_SERVER_FILL, "Browse Remote Files", "Browse and manage remote files");
        btn1.addActionListener(remoteClicked);
        var btn2 = createIconButton3(IconCode.RI_HARD_DRIVE_3_FILL, "Browse Local Files", "Create and manage local files");
        btn2.addActionListener(localClicked);

        AppUtils.makeEqualSize(btn1, btn2);

        var titleLabel = new JLabel("File Browser");
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        titleLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));

        var iconLabel = createIconLabel(IconCode.RI_FOLDER_FILL, 128f);
        iconLabel.setForeground(AppTheme.INSTANCE.getButtonRollOverBackground());

        var i = 0;
        var gc = new GridBagConstraints();
        gc.gridy = i;
        add(iconLabel, gc);
        //add(createIconGrid(), gc);
        i++;
        gc.gridy = i;
        add(titleLabel, gc);
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
        button.setBorder(new EmptyBorder(5, 10, 5, 20));
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
