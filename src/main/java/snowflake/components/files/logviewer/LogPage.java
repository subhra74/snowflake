package snowflake.components.files.logviewer;

import snowflake.App;
import snowflake.common.ssh.SshClient;
import snowflake.components.files.FileComponentHolder;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogPage extends JPanel {
    private long totalLines;
    private int linePerPage = 5;
    private String filePath;
    private long currentPage;
    private long pageCount;
    private FileComponentHolder holder;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private JButton nextPage, prevPage;
    private JTextArea txtPage;
    private JTextField txtCurrentPage;
    private JLabel lblTotalPages;
    private LogSearchPanel logSearchPanel;

    public LogPage(String filePath, FileComponentHolder holder) {
        super(new BorderLayout());
        this.filePath = filePath;
        this.holder = holder;
        txtCurrentPage = new JTextField();
        txtCurrentPage.setPreferredSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        txtCurrentPage.setMaximumSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        txtCurrentPage.setMinimumSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        txtPage = new JTextArea();
        lblTotalPages = new JLabel("Total 0 Pages");
        initPages();
        nextPage = new JButton();
        nextPage.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        nextPage.setFont(App.getFontAwesomeFont());
        nextPage.setText("\uf063");
        nextPage.addActionListener(e -> {
            nextPage();
        });
        prevPage = new JButton("");
        prevPage.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        prevPage.setFont(App.getFontAwesomeFont());
        prevPage.setText("\uf062");
        prevPage.addActionListener(e -> {
            previousPage();
        });
        Box box = Box.createHorizontalBox();
        box.setBorder(new EmptyBorder(5, 5, 5, 5));
        box.add(new JLabel("Page"));
        box.add(Box.createHorizontalStrut(5));
        box.add(txtCurrentPage);
        box.add(Box.createHorizontalStrut(5));
        box.add(lblTotalPages);
        box.add(prevPage);
        box.add(nextPage);
        box.add(Box.createHorizontalStrut(5));
        add(box, BorderLayout.NORTH);
        add(new JScrollPane(txtPage));
        logSearchPanel = new LogSearchPanel(new SearchListener() {
            @Override
            public void search(String text, boolean regex, boolean matchCase, boolean fullWord) {
                holder.disableUi(stopFlag);
                service.execute(() -> {
                    try {
                        List<Integer> list = LogPage.this.search(text);
                        SwingUtilities.invokeLater(() -> {
                            logSearchPanel.setResults(list);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        holder.enableUi();
                    }
                });
            }

            @Override
            public void select(int index) {
                int page = (int) Math.ceil((float) index / linePerPage);
                System.out.println("Found on page: " + page);
                currentPage = page;
                loadPage();
            }
        });
        add(logSearchPanel, BorderLayout.SOUTH);
    }

    private void nextPage() {
        if (currentPage < pageCount) {
            currentPage++;
            loadPage();
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadPage();
        }
    }

    public void initPages() {
        holder.disableUi(stopFlag);
        service.execute(() -> {
            try {
                this.totalLines = getLineCount();
                System.out.println("Total lines: " + this.totalLines);
                if (this.totalLines > 0) {
                    this.pageCount = (long) Math.ceil((double) totalLines / linePerPage);
                    System.out.println("Page count: " + this.pageCount);
                    if (this.currentPage > this.pageCount) {
                        this.currentPage = this.pageCount;
                    }
                    String pageText = getPageText(this.currentPage);
                    SwingUtilities.invokeLater(() -> {
                        txtPage.setText(pageText);
                        this.lblTotalPages.setText(String.format("/ %d ", this.pageCount));
                        this.txtCurrentPage.setText((this.currentPage + 1) + "");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.enableUi();
            }
        });
    }

    public void loadPage() {
        holder.disableUi(stopFlag);
        service.execute(() -> {
            try {
                String pageText = getPageText(this.currentPage);
                SwingUtilities.invokeLater(() -> {
                    txtPage.setText(pageText);
                    this.txtCurrentPage.setText((this.currentPage + 1) + "");
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.enableUi();
            }
        });
    }

    private long getLineCount() throws Exception {
        String command = "wc -l \"" + filePath + "\"";
        System.out.println("Command: " + command);
        StringBuilder output = new StringBuilder();
        SshClient client = holder.getSshFileSystem().getWrapper();
        if (!client.isConnected()) {
            client.connect();
        }
        if (SshCommandUtils.exec(client, command, stopFlag, output)) {
            System.out.println(output);
            int index = output.indexOf(" ");
            if (index > 0) {
                return Long.parseLong(output.substring(0, index));
            }
        }
        return -1;
    }

    private String getPageText(long page) throws Exception {
        long lineStart = page * linePerPage;
        long lineEnd = lineStart + linePerPage;
        String command = "sed -ne '" + (lineStart + 1) + "," + lineEnd + "p;" + (lineEnd + 1) + "q' \"" + filePath + "\"";
        System.out.println("Command: " + command);
        StringBuilder output = new StringBuilder();
        SshClient client = holder.getSshFileSystem().getWrapper();
        if (!client.isConnected()) {
            client.connect();
        }
        if (SshCommandUtils.exec(client, command, stopFlag, output)) {
            return output.toString();
        }
        return null;
    }

    private List<Integer> search(String text) throws Exception {
        List<Integer> list = new ArrayList<>();
        StringBuilder command = new StringBuilder();
        command.append("awk '{if(index(tolower($0),\"" +
                text.toLowerCase(Locale.ENGLISH) + "\")){ print NR}}' \"" + this.filePath + "\"");
        System.out.println("Command: " + command);
        StringBuilder output = new StringBuilder();
        SshClient client = holder.getSshFileSystem().getWrapper();
        if (!client.isConnected()) {
            client.connect();
        }
        if (SshCommandUtils.exec(client, command.toString(), stopFlag, output)) {
            for (String line : output.toString().split("\n")) {
                String n = line.trim();
                if (n.length() < 1) continue;
                list.add(Integer.parseInt(n));
            }
        }
        return list;
    }
}
