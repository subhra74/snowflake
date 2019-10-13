package snowflake.components.files.logviewer;

import snowflake.common.FileInfo;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.TabHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class LogViewerPanel extends JPanel {
    private JTabbedPane tabs;
    private FileComponentHolder holder;
    private Set<LogPage> tabSet = new HashSet<>();
    private CardLayout cardLayout;

    public LogViewerPanel(FileComponentHolder holder) {
        super(new BorderLayout());
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        this.holder = holder;
        tabs = new JTabbedPane();
        add(tabs, "Tabs");

        JLabel lblTitle = new JLabel("Please enter full path of the file below to open");
        JTextField txtFilePath = new JTextField(30);
        JButton btnOpenFile = new JButton("Open");
        JLabel lblTitle2 = new JLabel("Alternatively you can select the file from file browser");

        ActionListener act=e -> {
            String text = txtFilePath.getText();
            if (text.trim().length() < 1) {
                JOptionPane.showMessageDialog(null,
                        "Please enter full path of the file to be opened");
                return;
            }
            holder.statAsync(txtFilePath.getText(), (a, b) -> {
                if (!b) {
                    JOptionPane.showMessageDialog(null, "Unable to open file");
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    holder.openWithLogViewer(a);
                });
            });
        };

        btnOpenFile.addActionListener(act);
        txtFilePath.addActionListener(act);

        Box textBox = Box.createHorizontalBox();
        textBox.add(txtFilePath);
        textBox.add(Box.createHorizontalStrut(10));
        textBox.add(btnOpenFile);

        Box startPanel = Box.createVerticalBox();
        lblTitle.setAlignmentX(Box.CENTER_ALIGNMENT);
        textBox.setAlignmentX(Box.CENTER_ALIGNMENT);
        lblTitle2.setAlignmentX(Box.CENTER_ALIGNMENT);
        startPanel.add(Box.createVerticalStrut(50));
        startPanel.add(lblTitle);
        startPanel.add(Box.createVerticalStrut(10));
        startPanel.add(textBox);
        startPanel.add(Box.createVerticalStrut(5));
        startPanel.add(lblTitle2);

        JPanel msgPanel = new JPanel();
        msgPanel.add(startPanel);
        add(msgPanel, "Labels");

        cardLayout.show(this, "Labels");

        tabs.addChangeListener(e -> {
            System.out.println("Tab changed");
            if (tabs.getTabCount() == 0) {
                txtFilePath.setText("");
                cardLayout.show(this, "Labels");
            } else {
                cardLayout.show(this, "Tabs");
            }
        });
    }

    public void openLog(FileInfo fileInfo) {
        LogPage logPage = new LogPage(fileInfo.getPath(), this.holder);
        int index = tabs.getTabCount();
        TabHeader tabHeader = new TabHeader(fileInfo.getName());
        int count = tabs.getTabCount();
        tabHeader.getBtnClose().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = tabs.indexOfTabComponent(tabHeader);
                System.out.println("Closing tab at: " + index);
                closeTab(index);
            }
        });
        tabs.addTab(null, logPage);
        tabs.setTabComponentAt(count, tabHeader);
        tabSet.add(logPage);
        tabs.setSelectedIndex(index);
    }

    private void closeTab(int index) {
        LogPage logPage = (LogPage) tabs.getComponentAt(index);
        tabs.removeTabAt(index);
        logPage.close();
    }

    public void close() {
        for (LogPage page : tabSet) {
            page.close();
        }
    }
}
