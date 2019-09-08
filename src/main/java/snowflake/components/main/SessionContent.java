package snowflake.components.main;

import snowflake.App;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.taskmgr.TaskManager;
import snowflake.components.terminal.TerminalHolder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SessionContent extends JPanel {
    private SessionInfo info;
    private JSplitPane verticalSplitter, horizontalSplitter;
    private FileComponentHolder fileComponentHolder;
    private ExternalEditor externalEditor;


    //private FileStore fileStore;

    public SessionContent(SessionInfo info, ExternalEditor externalEditor) {
        super(new BorderLayout());
        this.info = info;
        this.externalEditor = externalEditor;
        init();
    }

//    Box createTab(String text, boolean closable, String icon) {
//        Box pan = Box.createHorizontalBox();
//
//        JLabel btn2 = new JLabel();
//        btn2.setFont(App.getFontAwesomeFont());
//        btn2.setText(icon);
//        pan.add(btn2);
//        pan.add(Box.createHorizontalStrut(5));
//        pan.add(new JLabel(text));
//        pan.add(Box.createHorizontalStrut(5));
//        if (closable) {
//            JLabel btn1 = new JLabel();
//            btn1.setFont(App.getFontAwesomeFont());
//            btn1.setText("\uf00d");
//            pan.add(btn1);
//        }
//        return pan;
//    }


    public void init() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        this.fileComponentHolder = new FileComponentHolder(info, externalEditor);
        topPanel.add(fileComponentHolder);
        JTabbedPane bottomTabs = new JTabbedPane();
        TerminalHolder th = new TerminalHolder(info);
        //JToolBar toolBar = new JToolBar();
        JButton btn = new JButton();
        btn.setMargin(new Insets(5, 5, 5, 5));
        btn.setFont(App.getFontAwesomeFont());
        btn.setText("\uf120");
        //toolBar.add(btn);
        btn.addActionListener(e -> {
            th.createNewTerminal();
        });

        bottomTabs.addTab("Terminal", th);
        bottomTabs.addTab("Search", new JPanel());
        bottomTabs.addTab("System monitor", new TaskManager(this.info));
        bottomTabs.addTab("System load", new JPanel());
        bottomTabs.addTab("Process and port", new JPanel());


//        bottomTabs.setTabComponentAt(0, createTab("Terminal", false, "\uf120"));
//        bottomTabs.setTabComponentAt(1, createTab("Transfers", false, "\uf0ec"));

        //add(toolBar, BorderLayout.NORTH);
        verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitter.setBackground(Color.RED);
        verticalSplitter.setOpaque(false);
        verticalSplitter.setDividerSize(2);
        verticalSplitter.setResizeWeight(0.6);
        verticalSplitter.setBottomComponent(bottomTabs);
        add(verticalSplitter);
        verticalSplitter.setTopComponent(topPanel);
    }

    public FileComponentHolder getFileComponentHolder() {
        return fileComponentHolder;
    }


}
