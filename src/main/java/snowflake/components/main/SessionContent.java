package snowflake.components.main;

import snowflake.App;
import snowflake.components.common.TabbedPanel;
import snowflake.components.diskusage.DiskUsageAnalyzer;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.files.transfer.BackgroundTransferPanel;
import snowflake.components.files.transfer.FileTransfer;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.search.FileSearchPanel;
import snowflake.components.taskmgr.TaskManager;
import snowflake.components.terminal.TerminalHolder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SessionContent extends JPanel {
    private SessionInfo info;
    private JSplitPane verticalSplitter, horizontalSplitter;
    private FileComponentHolder fileComponentHolder;
    private TerminalHolder terminalHolder;
    private ExternalEditor externalEditor;
    private BackgroundTransferPanel backgroundTransferPanel;

    //private FileStore fileStore;

    public SessionContent(SessionInfo info, ExternalEditor externalEditor) {
        super(new BorderLayout(0, 0));
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
        this.fileComponentHolder = new FileComponentHolder(info, externalEditor, this);
        TabbedPanel bottomTabs = new TabbedPanel();
        terminalHolder = new TerminalHolder(info);
        backgroundTransferPanel = new BackgroundTransferPanel();
        //JToolBar toolBar = new JToolBar();
        JButton btn = new JButton();
        btn.setMargin(new Insets(5, 5, 5, 5));
        btn.setFont(App.getFontAwesomeFont());
        btn.setText("\uf120");
        //toolBar.add(btn);
        btn.addActionListener(e -> {
            terminalHolder.createNewTerminal();
        });

        bottomTabs.addTab("Terminal", terminalHolder);
        bottomTabs.addTab("Search", new FileSearchPanel(this.info));
        bottomTabs.addTab("System monitor", new TaskManager(this.info));
        bottomTabs.addTab("Disk space analyzer", new DiskUsageAnalyzer(this.info));
        bottomTabs.addTab("Active transfers", backgroundTransferPanel);
        bottomTabs.addTab("Tools", new JPanel());
        bottomTabs.setSelectedIndex(0);
        bottomTabs.setBorder(new LineBorder(new Color(200, 200, 200), 1));


//        bottomTabs.setTabComponentAt(0, createTab("Terminal", false, "\uf120"));
//        bottomTabs.setTabComponentAt(1, createTab("Transfers", false, "\uf0ec"));

        //add(toolBar, BorderLayout.NORTH);
        verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitter.putClientProperty("Nimbus.Overrides", App.splitPaneSkin1);
        verticalSplitter.setBackground(Color.RED);
        verticalSplitter.setOpaque(false);
        verticalSplitter.setDividerSize(10);
        verticalSplitter.setResizeWeight(0.5);
        verticalSplitter.setBottomComponent(bottomTabs);
        verticalSplitter.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(verticalSplitter);
        verticalSplitter.setTopComponent(fileComponentHolder);
    }

    public void transferInBackground(FileTransfer transfer) {
        this.backgroundTransferPanel.addNewBackgroundTransfer(transfer);
    }

    public FileComponentHolder getFileComponentHolder() {
        return fileComponentHolder;
    }

    public void openTerminal(String command) {
        this.terminalHolder.createNewTerminal(command);
    }

}
