package snowflake.components.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AppFrame extends JPanel {
    private RoundPanel roundPanel;
    private Box mainTabHolder;
    private CardLayout mainCard;
    private JPanel mainCardPanel;
    private JPanel serverPage, settingPage, helpPage;
    private JLabel lblServerTab, lblSettingsTab, lblHelpTab;
    private Font plainFont, boldFont;
    private Color lightText, darkText;
    private Component subTab1, subTab2, subTab3;
    private CardLayout subTabCard;
    private JPanel subTabHolder;
    private ServerPageContainer serverPageContainer;

    public AppFrame() {
        super(new BorderLayout());
        boldFont = new Font(Font.DIALOG, Font.BOLD, 12);
        plainFont = new Font(Font.DIALOG, Font.PLAIN, 12);
        darkText = Color.GRAY;
        lightText = Color.LIGHT_GRAY;
        serverPageContainer = new ServerPageContainer();
        createTopPanel();
    }

    private JPanel createServerPage() {
        return new JPanel();
    }

    private JPanel createSettingsPage() {
        return new JPanel();
    }

    private JPanel createHelpPage() {
        return new JPanel();
    }

    public Box createSubTab1() {
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
        lblSearch.setFont(lblSearch.getFont().deriveFont(Font.PLAIN, 20));

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
        return b3;
    }

    private void selectMainTab(int selected) {
        lblServerTab.setFont(plainFont);
        lblServerTab.setForeground(darkText);
        lblSettingsTab.setFont(plainFont);
        lblSettingsTab.setForeground(darkText);
        lblHelpTab.setFont(plainFont);
        lblHelpTab.setForeground(darkText);
        if (selected == 0) {
            lblServerTab.setFont(boldFont);
            lblServerTab.setForeground(lightText);
        } else if (selected == 1) {
            lblSettingsTab.setFont(boldFont);
            lblSettingsTab.setForeground(lightText);
        } else if (selected == 2) {
            lblHelpTab.setFont(boldFont);
            lblHelpTab.setForeground(lightText);
        }
    }

    private Box createMainTabs() {
        mainCard = new CardLayout();
        mainCardPanel = new JPanel(mainCard);

        this.serverPage = createServerPage();
        this.settingPage = createSettingsPage();
        this.helpPage = createHelpPage();

        mainCardPanel.add(serverPage, "SERVER_PAGE");
        mainCardPanel.add(settingPage, "SETTINGS_PAGE");
        mainCardPanel.add(helpPage, "HELP_PAGE");


        lblServerTab = new JLabel("SERVER");
        lblServerTab.setFont(boldFont);
        lblServerTab.setForeground(lightText);

        lblSettingsTab = new JLabel("SETTINGS");
        lblSettingsTab.setFont(plainFont);
        lblSettingsTab.setForeground(darkText);

        lblHelpTab = new JLabel("HELP");
        lblHelpTab.setFont(plainFont);
        lblHelpTab.setForeground(darkText);

        lblServerTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainCard.show(mainCardPanel, "SERVER_PAGE");
                subTabCard.show(subTabHolder, "SERVER_TABS");
                selectMainTab(0);
            }
        });

        lblSettingsTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainCard.show(mainCardPanel, "SETTINGS_PAGE");
                subTabCard.show(subTabHolder, "SETTINGS_TABS");
                selectMainTab(1);
            }
        });

        lblHelpTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainCard.show(mainCardPanel, "HELP_PAGE");
                subTabCard.show(subTabHolder, "HELP_TABS");
                selectMainTab(2);
            }
        });

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

        subTabCard = new CardLayout();
        subTabHolder = new JPanel(subTabCard);
        subTabHolder.setAlignmentX(Box.LEFT_ALIGNMENT);

        mainTabHolder = Box.createVerticalBox();
        mainTabHolder.add(createMainTabs());

        this.subTab1 = serverPageContainer.getPageComponent();
        this.subTab2 = Box.createHorizontalBox();
        this.subTab3 = Box.createHorizontalBox();
        this.subTabHolder.add(subTab1, "SERVER_TABS");
        this.subTabHolder.add(subTab2, "SETTINGS_TABS");
        this.subTabHolder.add(subTab3, "HELP_TABS");

        mainTabHolder.add(subTabHolder);

        topContent.add(mainTabHolder);

        add(topContent, BorderLayout.NORTH);
    }
}
