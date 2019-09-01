package snowflake.components.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AppFrame extends JPanel {
    private RoundPanel roundPanel;
    private Box mainTabHolder;

    public AppFrame() {
        super(new BorderLayout());
        createTopPanel();
    }

    private Box createMainTabs() {
        JLabel lblServerTab = new JLabel("SERVER");
        lblServerTab.setFont(lblServerTab.getFont().deriveFont(Font.BOLD, 12));
        lblServerTab.setForeground(Color.LIGHT_GRAY);

        JLabel lblSettingsTab = new JLabel("SETTINGS");
        lblSettingsTab.setFont(lblSettingsTab.getFont().deriveFont(Font.PLAIN, 12));
        lblSettingsTab.setForeground(Color.GRAY);

        JLabel lblHelpTab = new JLabel("HELP");
        lblHelpTab.setFont(lblHelpTab.getFont().deriveFont(Font.PLAIN, 12));
        lblHelpTab.setForeground(Color.GRAY);

        Box b1 = Box.createHorizontalBox();
        b1.setBorder(new EmptyBorder(10, 10, 5, 10));
        b1.add(lblServerTab);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
        b1.add(lblSettingsTab);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
        b1.add(lblHelpTab);
        b1.setAlignmentX(Box.LEFT_ALIGNMENT);

        return b1;
    }

    private void createTopPanel() {
        roundPanel = new RoundPanel();
        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        topContent.add(roundPanel, BorderLayout.WEST);

        mainTabHolder = Box.createVerticalBox();
        mainTabHolder.add(createMainTabs());

        Box b3 = Box.createHorizontalBox();
        b3.setBorder(new EmptyBorder(0, 10, 5, 10));
        JLabel lblFiles = new JLabel("Files");
        lblFiles.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblFiles.setForeground(Color.LIGHT_GRAY);
        lblFiles.setFont(lblFiles.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblEditor = new JLabel("Editor");
        lblEditor.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblEditor.setForeground(Color.DARK_GRAY);
        lblEditor.setFont(lblEditor.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblLogs = new JLabel("Logs");
        lblLogs.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblLogs.setForeground(Color.DARK_GRAY);
        lblLogs.setFont(lblLogs.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblTasks = new JLabel("Monitoring");
        lblTasks.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblTasks.setForeground(Color.DARK_GRAY);
        lblTasks.setFont(lblTasks.getFont().deriveFont(Font.PLAIN, 20));
        JLabel lblSearch = new JLabel("Search");
        lblSearch.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblSearch.setForeground(Color.DARK_GRAY);
        lblSearch.setFont(lblSearch.getFont().deriveFont(Font.PLAIN, 18));

        b3.add(lblFiles);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblEditor);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblLogs);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblTasks);
        b3.add(Box.createRigidArea(new Dimension(15, 10)));
        b3.add(lblSearch);

        b3.setAlignmentX(Box.LEFT_ALIGNMENT);
        mainTabHolder.add(b3);

        topContent.add(mainTabHolder);

        add(topContent, BorderLayout.NORTH);
    }
}
