package snowflake.components.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerPageContainer implements PageContainer {

    private JLabel lblFiles, lblEditor, lblLogs, lblTasks, lblSearch;
    private Font font;
    private Color lightText, darkText;
    private JPanel filesPanel, editorPanel, logsPanel, tasksPanel, searchPanel;
    private Box pageTitleBox;
    private CardLayout card;
    private JPanel cardPanel;



    private String tags[] = {"FILES", "EDITOR", "LOGS", "TASKS", "SEARCH"};

    public ServerPageContainer() {
        createPageTitles();

        filesPanel = createFilesPanel();
        editorPanel = createEditorsPanel();
        logsPanel = createLogsPanel();
        tasksPanel = createTasksPanel();
        searchPanel = createSearchPanel();

        card = new CardLayout();
        cardPanel = new JPanel(card);
        cardPanel.add(filesPanel, tags[0]);
        cardPanel.add(editorPanel, tags[1]);
        cardPanel.add(logsPanel, tags[2]);
        cardPanel.add(tasksPanel, tags[3]);
        cardPanel.add(searchPanel, tags[4]);

        lightText = Color.LIGHT_GRAY;
        darkText = Color.DARK_GRAY;
    }

    private void selectMainTab(int selected) {
        lblFiles.setForeground(darkText);
        lblEditor.setForeground(darkText);
        lblLogs.setForeground(darkText);
        lblTasks.setForeground(darkText);
        lblSearch.setForeground(darkText);
        System.out.println(selected);
        if (selected == 0) {
            lblFiles.setForeground(lightText);
        } else if (selected == 1) {
            lblEditor.setForeground(lightText);
        } else if (selected == 2) {
            lblLogs.setForeground(lightText);
        } else if (selected == 3) {
            lblTasks.setForeground(lightText);
        } else if (selected == 4) {
            lblSearch.setForeground(lightText);
        }
    }

    @Override
    public void selectPage(int index) {

    }

    private JPanel createFilesPanel() {
        return new JPanel();
    }

    private JPanel createEditorsPanel() {
        return new JPanel();
    }

    private JPanel createSearchPanel() {
        return new JPanel();
    }

    private JPanel createTasksPanel() {
        return new JPanel();
    }

    private JPanel createLogsPanel() {
        return new JPanel();
    }

    @Override
    public Component getPageComponent() {


        return pageTitleBox;
    }

    @Override
    public Component getPageContent() {
        return cardPanel;
    }

    @Override
    public int getSelectedPage() {
        return 0;
    }


    private void createPageTitles() {
        font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        lightText = Color.LIGHT_GRAY;
        darkText = Color.DARK_GRAY;
        pageTitleBox = Box.createHorizontalBox();
        pageTitleBox.setBorder(new EmptyBorder(0, 10, 5, 10));

        lblFiles = new JLabel("Files");
        lblFiles.setFont(font);
        lblFiles.setForeground(lightText);

        lblEditor = new JLabel("Editor");
        lblEditor.setFont(font);
        lblEditor.setForeground(darkText);

        lblLogs = new JLabel("Logs");
        lblLogs.setFont(font);
        lblLogs.setForeground(darkText);

        lblTasks = new JLabel("Monitoring");
        lblTasks.setFont(font);
        lblTasks.setForeground(darkText);

        lblSearch = new JLabel("Search");
        lblSearch.setFont(font);
        lblSearch.setForeground(darkText);

        pageTitleBox.add(lblFiles);
        pageTitleBox.add(Box.createRigidArea(new Dimension(15, 10)));
        pageTitleBox.add(lblEditor);
        pageTitleBox.add(Box.createRigidArea(new Dimension(15, 10)));
        pageTitleBox.add(lblLogs);
        pageTitleBox.add(Box.createRigidArea(new Dimension(15, 10)));
        pageTitleBox.add(lblTasks);
        pageTitleBox.add(Box.createRigidArea(new Dimension(15, 10)));
        pageTitleBox.add(lblSearch);

        pageTitleBox.setAlignmentX(Box.LEFT_ALIGNMENT);

        lblFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.show(cardPanel, tags[0]);
                selectMainTab(0);
            }
        });

        lblEditor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.show(cardPanel, tags[1]);
                selectMainTab(1);
            }
        });

        lblLogs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.show(cardPanel, tags[2]);
                selectMainTab(2);
            }
        });

        lblTasks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.show(cardPanel, tags[3]);
                selectMainTab(3);
            }
        });

        lblSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.show(cardPanel, tags[4]);
                selectMainTab(4);
            }
        });
    }
}
