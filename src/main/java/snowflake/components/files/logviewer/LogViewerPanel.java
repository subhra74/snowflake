package snowflake.components.files.logviewer;

import snowflake.common.FileInfo;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.TabHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class LogViewerPanel extends JPanel {
    private JTabbedPane tabs;
    private FileComponentHolder holder;
    private Set<LogPage> tabSet = new HashSet<>();

    public LogViewerPanel(FileComponentHolder holder) {
        super(new BorderLayout());
        this.holder = holder;
        tabs = new JTabbedPane();
        add(tabs);
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

    }
}
