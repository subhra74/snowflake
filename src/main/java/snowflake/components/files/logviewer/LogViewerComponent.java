package snowflake.components.files.logviewer;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.InputTransferChannel;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.TabHeader;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogViewerComponent extends JPanel {
    private JTabbedPane tabs;
    private FileComponentHolder holder;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private CardLayout cardLayout;

    public LogViewerComponent(FileComponentHolder holder) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        this.holder = holder;
        tabs = new JTabbedPane();
        add(tabs, "Tabs");

        JPanel msgPanel = new JPanel(new BorderLayout());
        JLabel noTabMsg = new JLabel("No files opened, please open a file from file browser");
        noTabMsg.setHorizontalAlignment(JLabel.CENTER);
        msgPanel.add(noTabMsg);
        add(msgPanel, "Labels");

        cardLayout.show(this, "Labels");

        tabs.addChangeListener(e -> {
            System.out.println("Tab changed");
            if (tabs.getTabCount() == 0) {
                cardLayout.show(this, "Labels");
            } else {
                cardLayout.show(this, "Tabs");
            }
        });
    }

    public void getLatestLog() {
        int index = tabs.getSelectedIndex();
        executorService.submit(() -> {
            holder.disableUi();
            LogViewerItem item = (LogViewerItem) tabs.getSelectedComponent();
            try {
                FileInfo fileInfo1 = this.holder.getSshFileSystem().getInfo(item.getFileInfo().getPath());
                if (fileInfo1.getSize() > item.getFileInfo().getSize()) {
                    try (InputTransferChannel ch = this.holder.getSshFileSystem().inputTransferChannel();
                         InputStream in = ch.getInputStream(item.getFileInfo().getPath(), item.getFileInfo().getSize());
                         OutputStream out = new FileOutputStream(item.getLocalTempFile(), true)) {
                        byte[] buf = new byte[8192];
                        int c = 0;
                        while (!stopFlag.get()) {
                            int x = in.read(buf);
                            if (x == -1) break;
                            out.write(buf, 0, x);
                            c += x;
                            out.flush();
                        }
                        System.out.println("New file with extra byte: " + c);
                        SwingUtilities.invokeLater(() -> {
                            LineEntry lastLine = item.getLastLine();
                            long offset = item.getFileInfo().getSize();
                            if (lastLine != null) {
                                offset -= lastLine.length;
                                item.removeLastLine();
                            }
                            LineIndexer.IndexLines lines = LineIndexer.indexLines(item.getLocalTempFile(), offset, stopFlag);
                            item.addLines(lines.lines);
                            item.setMaxLine(lines.maxLen);
                            item.setFileInfo(fileInfo1);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (fileInfo1.getSize() < item.getFileInfo().getSize()) {
                    System.out.println("Old size: " + fileInfo1.getSize() + " new size: " + item.getFileInfo().getSize());
                    if (JOptionPane.showConfirmDialog(this,
                            "File has been truncated, would you like to reload the file?",
                            "Reload?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        closeTab(index);
                        openLog2(fileInfo1, holder.getTempFolder());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.enableUi();
            }
        });
    }

    private void openLog2(FileInfo fileInfo, String tempFolder) {
        try {
            SshFileSystem fs = holder.getSshFileSystem();
            String localTempFile = PathUtils.combine(tempFolder, UUID.randomUUID() + fileInfo.getName(), File.separator);

            try (InputTransferChannel ch = fs.inputTransferChannel(); InputStream in = ch.getInputStream(fileInfo.getPath()); OutputStream out = new FileOutputStream(localTempFile)) {
                byte[] buf = new byte[8192];
                while (!stopFlag.get()) {
                    int x = in.read(buf);
                    if (x == -1) break;
                    out.write(buf, 0, x);
                }
            }
            if (stopFlag.get()) {
                return;
            }
            LineIndexer.IndexLines lines = LineIndexer.indexLines(localTempFile, 0, stopFlag);
            SwingUtilities.invokeLater(() -> {
                try {
                    LogViewerItem item = new LogViewerItem(this, fileInfo, localTempFile, lines);
                    TabHeader tabHeader = new TabHeader(fileInfo.getName());
//                    JPanel pan = new JPanel(new BorderLayout());
//                    pan.add(new JLabel(fileInfo.getName()));
//                    JLabel btnClose = new JLabel();
//                    btnClose.setFont(App.getFontAwesomeFont());
//                    btnClose.setText("\uf2d3");
                    int count = tabs.getTabCount();
                    tabHeader.getBtnClose().addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int index = tabs.indexOfTabComponent(tabHeader);
                            System.out.println("Closing tab at: " + index);
                            closeTab(index);
                        }
                    });
//                    pan.add(btnClose, BorderLayout.EAST);
                    tabs.addTab(null, item);
                    tabs.setTabComponentAt(count, tabHeader);
                    tabs.setSelectedIndex(count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openLog(FileInfo fileInfo, String tempFolder) {
        holder.disableUi();
        executorService.submit(() -> {
            openLog2(fileInfo, tempFolder);
            holder.enableUi();
        });
    }

    public void closeTab(int index) {
        tabs.removeTabAt(index);
    }

    public void initSearch(LogViewerItem item, List<LineEntry> lines, String localFile,
                           String searchText, boolean regex,
                           boolean caseSensitive, boolean fullWord) {
        executorService.submit(() -> {
            holder.disableUi();
            List<Integer> list = LogSearch.search(lines, localFile, stopFlag, searchText, regex, caseSensitive, fullWord);
            holder.enableUi();
            SwingUtilities.invokeLater(() -> {
                item.setSearchResult(list);
            });
        });
    }
}
