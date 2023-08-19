package muon.ui.widgets;

import muon.model.ISessionListItem;
import muon.ui.styles.*;
import muon.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

public class HomePanel extends JPanel {

    public HomePanel(List<ISessionListItem> sessions,
                     ActionListener onSessionManagerClicked) {
        super(null);
        setBackground(AppTheme.INSTANCE.getBackground());
        setLayout(new GridBagLayout());

        var constraint1 = new GridBagConstraints();
        constraint1.weightx = 0.3;
        constraint1.weighty = 1;
        constraint1.gridx = 0;
        constraint1.fill = GridBagConstraints.BOTH;
        constraint1.anchor = GridBagConstraints.CENTER;

        var constraint2 = new GridBagConstraints();
        constraint2.weightx = 0.7;
        constraint2.weighty = 1;
        constraint2.gridx = 1;
        constraint2.fill = GridBagConstraints.BOTH;

        var leftPanel = createLeftPanel(onSessionManagerClicked);
        var p1 = new JPanel(new GridBagLayout());
        //p1.setBorder(new MatteBorder(1,0,0,0, AppTheme.INSTANCE.getBackground()));
        p1.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
        p1.add(leftPanel);

        var rightPanel = createRightPanel(sessions);
        var p2 = new JPanel(new GridBagLayout());
        p2.setBackground(AppTheme.INSTANCE.getBackground());
        p2.add(rightPanel);

        add(p1, constraint1);
        add(p2, constraint2);
    }


//    public HomePanel() {
//        super(null);
//        setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//        var centerPanel = new JPanel(new GridLayout(1, 2));
//        centerPanel.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        var logoLabel = new JLabel("m");
//        logoLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 128));
//        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        var titleLabel = new JLabel("Muon");
//        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
//        //titleLabel.setForeground(Color.DARK_GRAY);
//        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        var subTitleLabel = new JLabel("Modern SSH / SFTP client");
//        subTitleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
//        subTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//        subTitleLabel.setForeground(Color.DARK_GRAY);
//
//        var versionLabel = new JLabel("v1.0.1");
//        versionLabel.setForeground(Color.DARK_GRAY);
//        versionLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
//        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        var homePageLabel = new JLabel("muon.github.com");
//        homePageLabel.setForeground(Color.DARK_GRAY);
//        homePageLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
//        homePageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//        var vbox1 = Box.createVerticalBox();
//        vbox1.setAlignmentX(Component.CENTER_ALIGNMENT);
//        //vbox1.add(logoLabel);
//        vbox1.add(titleLabel);
//        vbox1.add(Box.createRigidArea(new Dimension(10,5)));
//        vbox1.add(subTitleLabel);
//        vbox1.add(Box.createRigidArea(new Dimension(5,5)));
//        vbox1.add(versionLabel);
//        vbox1.add(Box.createRigidArea(new Dimension(5,5)));
//        vbox1.add(homePageLabel);
//
//        centerPanel.add(vbox1);
//
//        var btn1=createIconButton1(FontIcon.RI_SERVER_FILL, "Connection manager", "Create and manage connections");
//        var btn2=createIconButton1(FontIcon.RI_TOOLS_FILL, "Settings", "Manage configurations");
//
//        var width=Math.max(btn1.getPreferredSize().width, btn2.getPreferredSize().width);
//        var height=Math.max(btn1.getPreferredSize().height, btn2.getPreferredSize().height);
//
//        btn1.setPreferredSize(new Dimension(width,height));
//        btn2.setPreferredSize(new Dimension(width,height));
//
//        btn1.setMinimumSize(new Dimension(width,height));
//        btn2.setMinimumSize(new Dimension(width,height));
//
//        btn1.setMaximumSize(new Dimension(width,height));
//        btn2.setMaximumSize(new Dimension(width,height));
//
//        var vbox2 = Box.createVerticalBox();
//        vbox2.add(btn1);
//        vbox2.add(Box.createRigidArea(new Dimension(10, 10)));
//        vbox2.add(btn2);
//        centerPanel.add(vbox2);
//
//        Box b2 = Box.createHorizontalBox();
//        b2.add(Box.createHorizontalGlue());
//        b2.add(centerPanel);
//        b2.add(Box.createHorizontalGlue());
//        b2.setAlignmentX(Component.CENTER_ALIGNMENT);
//
//        Box b1 = Box.createHorizontalBox();
//        b1.setAlignmentX(Component.CENTER_ALIGNMENT);
//        b1.add(createIconButton(FontIcon.RI_BOX_3_FILL, "Connect"));
//        b1.add(Box.createRigidArea(new Dimension(30, 20)));
//        b1.add(createIconButton(FontIcon.RI_TOOLS_FILL, "Settings"));
//
////        b1.add(createIconLabel(FontIcon.RI_COMPUTER_FILL, 78f));
////        b1.add(Box.createRigidArea(new Dimension(10, 20)));
////        b1.add(createIconLabel(FontIcon.RI_ARROW_LEFT_RIGHT_LINE, 32f));
////        b1.add(Box.createRigidArea(new Dimension(10, 20)));
////        b1.add(createIconLabel(FontIcon.RI_SERVER_FILL, 78f));
//
////        var btn2 = new JButton();
////        btn2.setLayout(new BoxLayout(btn2, BoxLayout.Y_AXIS));
////        btn2.add(createIconLabel(FontIcon.RI_SERVER_FILL, 78f));
////        var lbl2 = new JLabel("Connect");
////        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
////        btn2.add(lbl2);
////
////        var btn3 = new JButton();
////        btn3.setLayout(new BoxLayout(btn2, BoxLayout.Y_AXIS));
////        btn3.add(createIconLabel(FontIcon.RI_SERVER_FILL, 78f));
////        var lbl3 = new JLabel("Settings");
////        lbl3.setAlignmentX(Component.CENTER_ALIGNMENT);
////        btn2.add(lbl3);
////
////        btn2.setBackground(AppTheme.INSTANCE.getHomePanelButtonColor());
////        btn2.setForeground(Color.GRAY);
////        btn2.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
////        btn2.putClientProperty("button.arc", 15);
////        btn2.setBorderPainted(false);
////        btn2.setBorder(new EmptyBorder(10, 20, 10, 20));
////        btn2.setAlignmentX(Component.CENTER_ALIGNMENT);
//        add(Box.createVerticalGlue());
//        //add(b1);
//        add(b2);
////        add(Box.createRigidArea(new Dimension(0, 20)));
////        add(btn2);
//        add(Box.createVerticalGlue());
//    }

    private JComponent createRightPanel(List<ISessionListItem> sessions) {
        var containerBox = Box.createVerticalBox();
        containerBox.setOpaque(true);
        containerBox.setBackground(AppTheme.INSTANCE.getBackground());

        var titleLabel = new JLabel("Recent sessions");
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        containerBox.add(titleLabel);

        if (Objects.nonNull(sessions) && !sessions.isEmpty()) {

            var buttons = new JButton[sessions.size()];
            for (var i = 0; i < buttons.length; i++) {
                var btn1 = createIconButton2(IconCode.RI_INSTANCE_LINE, sessions.get(i).getName(), sessions.get(i).getUser());
                buttons[i] = btn1;
            }

            var width = 0;
            var height = 0;
            for (var button :
                    buttons) {
                width = Math.max(button.getPreferredSize().width, width);
                height = Math.max(button.getPreferredSize().height, height);
            }

            var buttonSize = new Dimension(width, height);

            containerBox.add(Box.createRigidArea(new Dimension(10, 20)));

            for (var button :
                    buttons) {
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                button.setAlignmentY(Component.TOP_ALIGNMENT);
                width = Math.max(button.getPreferredSize().width, width);
                height = Math.max(button.getPreferredSize().height, height);
                button.setPreferredSize(buttonSize);
                button.setMaximumSize(buttonSize);
                containerBox.add(button);
                containerBox.add(Box.createRigidArea(new Dimension(10, 10)));
            }
            containerBox.add(Box.createRigidArea(new Dimension(10, 30)));

        } else {
            var iconLabel = createIconLabel(IconCode.RI_FOLDER_FORBID_FILL, 128f);
            iconLabel.setForeground(AppTheme.INSTANCE.getButtonBorderColor());
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            var infoLabel = new JLabel("No recent sessions found");
            infoLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());
            infoLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
            infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            containerBox.add(Box.createRigidArea(new Dimension(30, 30)));
            containerBox.add(iconLabel);
            containerBox.add(Box.createRigidArea(new Dimension(30, 20)));
            containerBox.add(infoLabel);
            containerBox.add(Box.createRigidArea(new Dimension(10, 30)));
        }


        return containerBox;
    }

    private JComponent createLeftPanel(ActionListener actionListener) {
        var titleLabel = new JLabel("Muon");
        //titleLabel.setForeground(AppTheme.INSTANCE.getSelectionColor());
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        var versionLabel = new JLabel("v1.0.1");
        versionLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());
        versionLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        versionLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        versionLabel.setBorder(new EmptyBorder(0, 5, 5, 0));

        var titleBox = Box.createHorizontalBox();
        titleBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleBox.setAlignmentY(Component.TOP_ALIGNMENT);
        titleBox.add(titleLabel);
        titleBox.add(versionLabel);

        var subTitleLabel = new JLabel("Modern SSH / SFTP client");
        subTitleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitleLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        subTitleLabel.setForeground(AppTheme.INSTANCE.getDarkForeground());


        var containerBox = Box.createVerticalBox();
        //containerBox.setOpaque(true);
        //containerBox.setBackground(Color.RED);
        //containerBox.setAlignmentY(Component.TOP_ALIGNMENT);
        containerBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        //containerBox.add(Box.createRigidArea(new Dimension(0, 50)));
        containerBox.add(titleBox);
        //containerBox.add(Box.createRigidArea(new Dimension(0,0)));
        containerBox.add(subTitleLabel);
        //containerBox.add(versionLabel);
        //containerBox.add(versionLabel);


        var btn1 = createIconButton3(IconCode.RI_SERVER_FILL, "Session manager", "Create and manage connections");
        var btn2 = createIconButton3(IconCode.RI_SAFE_2_FILL, "Manage Keys", "Create and manage local keys");
        var btn3 = createIconButton3(IconCode.RI_TOOLS_FILL, "Settings", "Manage configurations");

        btn1.addActionListener(actionListener);

        var buttons = new JButton[]{btn1, btn2, btn3};

        var width = 0;
        var height = 0;
        for (var button :
                buttons) {
            width = Math.max(button.getPreferredSize().width, width);
            height = Math.max(button.getPreferredSize().height, height);
        }

        var buttonSize = new Dimension(width, height);

        containerBox.add(Box.createRigidArea(new Dimension(10, 20)));

        for (var button :
                buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setAlignmentY(Component.TOP_ALIGNMENT);
            width = Math.max(button.getPreferredSize().width, width);
            height = Math.max(button.getPreferredSize().height, height);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            containerBox.add(button);
            containerBox.add(Box.createRigidArea(new Dimension(10, 10)));
        }
        containerBox.add(Box.createRigidArea(new Dimension(10, 30)));

        return containerBox;
    }

    private JButton createIconButton(IconCode icon, String text) {
        var button = new JButton();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.add(createIconLabel(icon, 78f));
        var lbl2 = new JLabel(text);
        lbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.add(lbl2);

        button.setBackground(AppTheme.INSTANCE.getHomePanelButtonColor());
        button.setForeground(Color.GRAY);
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        button.putClientProperty("button.arc", 15);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        return button;
    }

    private JButton createIconButton1(IconCode icon, String title, String subtitle) {
        var hbox = Box.createHorizontalBox();

        var titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        var vbox = Box.createVerticalBox();
        vbox.add(titleLabel);
        vbox.add(Box.createRigidArea(new Dimension(10, 5)));
        vbox.add(new JLabel(subtitle));
        vbox.add(Box.createRigidArea(new Dimension(10, 5)));

        hbox.add(createIconLabel(icon, 64f));
        hbox.add(Box.createRigidArea(new Dimension(10, 10)));
        hbox.add(vbox);
        hbox.add(Box.createRigidArea(new Dimension(10, 10)));
        hbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        var button = new JButton();
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.add(hbox);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
    }

    private JButton createIconButton2(IconCode icon, String title, String subtitle) {
        var hbox = Box.createHorizontalBox();

        var titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));

        var vbox = Box.createVerticalBox();
        vbox.add(titleLabel);
        vbox.add(Box.createRigidArea(new Dimension(0, 3)));
        vbox.add(new JLabel(subtitle));

        hbox.add(createIconLabel(icon, 32f));
        hbox.add(Box.createRigidArea(new Dimension(10, 5)));
        hbox.add(vbox);
        hbox.add(Box.createRigidArea(new Dimension(10, 5)));
        hbox.setAlignmentX(Component.LEFT_ALIGNMENT);

        var button = new JButton();
        button.setBorder(new EmptyBorder(5, 20, 8, 20));
        button.add(hbox);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
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

        hbox.add(createIconLabel(icon, 42f));
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
