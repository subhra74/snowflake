package snowflake.components.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AppInfoDialog extends JDialog {
    public AppInfoDialog(Window window) {
        super(window);
        setModal(true);
        JLabel lblIcon = new JLabel(new ImageIcon(getClass().getResource("/snowflake-logo256.png")));
        lblIcon.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(lblIcon, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Snowflake");
        lblTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        JLabel lblVersion = new JLabel("v1.0.0");
        JLabel lblAuthor = new JLabel("Subhra Das Gupta");

        JButton btnVisitPage = new JButton("View in github");
        JButton btnHelp = new JButton("Help and FAQ");
        JButton btnCheckForUpdate = new JButton("Check for update");

        btnVisitPage.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/subhra74/snowflake"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnHelp.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/subhra74/snowflake"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        btnCheckForUpdate.addActionListener(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/subhra74/snowflake"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                }
            }
        });

        lblTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
        lblVersion.setAlignmentX(Box.CENTER_ALIGNMENT);
        lblAuthor.setAlignmentX(Box.CENTER_ALIGNMENT);

        Box vbox = Box.createVerticalBox();
        vbox.add(lblTitle);
        vbox.add(lblVersion);
        vbox.add(lblAuthor);

        add(vbox);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        bottomPanel.add(btnVisitPage);
        bottomPanel.add(btnHelp);
        bottomPanel.add(btnCheckForUpdate);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(640, 480);
        setLocationRelativeTo(null);
    }
}
