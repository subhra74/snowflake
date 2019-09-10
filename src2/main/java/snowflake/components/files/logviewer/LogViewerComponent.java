package snowflake.components.files.logviewer;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.InputTransferChannel;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.files.FileComponentHolder;
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

    public LogViewerComponent(FileComponentHolder holder) {
        super(new BorderLayout());
        this.holder = holder;
        tabs = new JTabbedPane();
        add(tabs);
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
                        while (!stopFlag.get()) {
                            int x = in.read(buf);
                            if (x == -1) break;
                            out.write(buf, 0, x);
                        }
                        SwingUtilities.invokeLater(() -> {
                            LineIndexer.IndexLines lines = LineIndexer.indexLines(item.getLocalTempFile(), item.getFileInfo().getSize() + 1, stopFlag);
                            item.addLines(lines.lines);
                            item.setMaxLine(lines.maxLen);
                            item.setFileInfo(fileInfo1);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (fileInfo1.getSize() < item.getFileInfo().getSize()) {
                    if (JOptionPane.showConfirmDialog(this,
                            "File has been truncated, would you like to reload the file?",
                            "Reload?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        LogViewerItem item2 = (LogViewerItem) tabs.getSelectedComponent();
                        closeTab(index);
                        openLog2(item2.getFileInfo(), holder.getTempFolder());
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
                    JPanel pan = new JPanel(new BorderLayout());
                    pan.add(new JLabel(fileInfo.getName()));
                    JLabel btnClose = new JLabel();
                    btnClose.setFont(App.getFontAwesomeFont());
                    btnClose.setText("\uf2d3");
                    int count = tabs.getTabCount();
                    btnClose.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int index = tabs.indexOfTabComponent(pan);
                            System.out.println("Closing tab at: " + index);
                            closeTab(index);
                        }
                    });
                    pan.add(btnClose, BorderLayout.EAST);
                    tabs.addTab(null, item);
                    tabs.setTabComponentAt(count, pan);
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
