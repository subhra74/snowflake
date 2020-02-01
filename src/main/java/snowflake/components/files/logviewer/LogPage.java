package
        snowflake.components.files.logviewer;

import com.google.common.primitives.Longs;
import org.tukaani.xz.XZInputStream;
import snowflake.App;
import snowflake.common.ssh.SshClient;
import snowflake.components.files.FileComponentHolder;
import snowflake.utils.FormatUtils;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.PathUtils;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

public class LogPage extends JPanel {
    private long totalLines;
    private int linePerPage = 50;
    private String filePath;
    private long currentPage;
    private long pageCount;
    private FileComponentHolder holder;
    private ExecutorService service = Executors.newSingleThreadExecutor();
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private JButton nextPage, prevPage;
    private DefaultListModel<String> lineModel;
    private JList<String> lineList;
    private JTextField txtCurrentPage;
    private JLabel lblTotalPages;
    private PagedLogSearchPanel logSearchPanel;
    private String indexFile;
    private RandomAccessFile raf;

    public LogPage(String filePath, FileComponentHolder holder) {
        super(new BorderLayout());
        this.filePath = filePath;
        this.holder = holder;
        txtCurrentPage = GraphicsUtils.createTextField();//new JTextField();
        txtCurrentPage.addActionListener(e -> {
            System.out.println("Called");
            int page = Integer.parseInt(txtCurrentPage.getText().trim());
            if (page > 0 && page <= this.pageCount) {
                this.currentPage = page - 1;
                loadPage();
            }
        });

        lblTotalPages = new JLabel("Total 0 Pages");

        txtCurrentPage.setPreferredSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        txtCurrentPage.setMaximumSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        txtCurrentPage.setMinimumSize(new Dimension(50, txtCurrentPage.getPreferredSize().height));
        lineModel = new DefaultListModel<>();
        lineList = new JList<>(lineModel);
        LogListRenderer renderer = new LogListRenderer();
        lineList.setFixedCellHeight(renderer.getPreferredSize().height);
        lineList.setCellRenderer(renderer);
        lineList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        initPages();
        nextPage = new JButton();
        nextPage.setToolTipText("Next page");
        nextPage.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        nextPage.setFont(App.getFontAwesomeFont());
        nextPage.setText("\uf063");
        nextPage.addActionListener(e -> {
            nextPage();
        });
        prevPage = new JButton("");
        prevPage.setToolTipText("Previous page");
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

        JButton btnReload = new JButton();
        btnReload.setToolTipText("Reload");
        btnReload.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnReload.setFont(App.getFontAwesomeFont());
        btnReload.setText("\uf0e2");
        btnReload.addActionListener(e -> {
            try {
                raf.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Files.delete(Paths.get(this.indexFile));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            initPages();
        });

        JButton btnCopy = new JButton();
        btnCopy.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnCopy.setFont(App.getFontAwesomeFont());
        btnCopy.setToolTipText("Copy selected text");
        btnCopy.setText("\uf0c5");
        btnCopy.addActionListener(e -> {
            if (lineList.getSelectedIndices().length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int index : lineList.getSelectedIndices()) {
                    sb.append(lineModel.get(index));
                    sb.append("\n");
                }
                Toolkit.getDefaultToolkit().getSystemClipboard().
                        setContents(new StringSelection(sb.toString()), null);
            }
        });

        box.add(Box.createHorizontalGlue());
        JLabel lblFont = new JLabel("Font size");
        box.add(Box.createHorizontalStrut(5));
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(12, 5, 72, 1);
        JSpinner spFontSize = new JSpinner(spinnerNumberModel);
        spFontSize.setMaximumSize(spFontSize.getPreferredSize());
        spFontSize.addChangeListener(e -> {
            int fontSize = (int) spinnerNumberModel.getValue();
            renderer.setFontSize(fontSize);
            lineList.setFixedCellHeight(renderer.getPreferredSize().height);
        });
        box.add(lblFont);
        box.add(Box.createHorizontalStrut(5));
        box.add(spFontSize);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnReload);
        box.add(Box.createHorizontalStrut(5));
        box.add(btnCopy);

        add(box, BorderLayout.NORTH);
        add(new JScrollPane(lineList));
        logSearchPanel = new PagedLogSearchPanel(new SearchListener() {
            @Override
            public void search(String text) {
                holder.disableUi(stopFlag);
                service.execute(() -> {
                    try {
                        RandomAccessFile searchIndex = LogPage.this.search(text);
                        long len = searchIndex.length();
                        SwingUtilities.invokeLater(() -> {
                            logSearchPanel.setResults(searchIndex, len);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        holder.enableUi();
                    }
                });
            }

            @Override
            public void select(long index) {
                System.out.println("Search item found on line: " + index);
                int page = (int) index / linePerPage;
                int line = (int) (index % linePerPage);
                System.out.println("Found on page: " + page + " line: " + line);
                if (currentPage == page) {
                    if (line < lineModel.getSize() && line != -1) {
                        lineList.setSelectedIndex(line);
                        lineList.ensureIndexIsVisible(line);
                    }
                } else {
                    currentPage = page;
                    loadPage(line);
                }
            }
        });
        add(logSearchPanel, BorderLayout.SOUTH);
    }

    public static void toByteArray(long value, byte[] result) {
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
    }

    private void nextPage() {
        if (currentPage < pageCount - 1) {
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
                if ((indexFile(true)) || (indexFile(false))) {
                    this.totalLines = this.raf.length() / 16;
                    System.out.println("Total lines: " + this.totalLines);
                    if (this.totalLines > 0) {
                        this.pageCount = (long) Math.ceil((double) totalLines / linePerPage);
                        System.out.println("Page count: " + this.pageCount);
                        if (this.currentPage > this.pageCount) {
                            this.currentPage = this.pageCount;
                        }
                        String pageText = getPageText(this.currentPage);
                        SwingUtilities.invokeLater(() -> {
                            String lines[] = pageText.replace("\t", "    ").split("\n");
                            lineModel.clear();
                            lineModel.addAll(Arrays.asList(lines));
                            this.lblTotalPages.setText(String.format("/ %d ", this.pageCount));
                            this.txtCurrentPage.setText((this.currentPage + 1) + "");
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.enableUi();
            }
        });
    }

    public void loadPage() {
        loadPage(-1);
    }

    public void loadPage(int line) {
        holder.disableUi(stopFlag);
        service.execute(() -> {
            try {
                String pageText = getPageText(this.currentPage);
                SwingUtilities.invokeLater(() -> {
                    lineModel.clear();
                    String lines[] = pageText.replace("\t", "    ").split("\n");
                    lineModel.addAll(Arrays.asList(lines));
                    if (line < lineModel.getSize() && line != -1) {
                        lineList.setSelectedIndex(line);
                        lineList.ensureIndexIsVisible(line);
                    }
                    this.txtCurrentPage.setText((this.currentPage + 1) + "");
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.enableUi();
            }
        });
    }

// private long getLineCount() throws Exception {
// String command = "wc -l \"" + filePath + "\"";
// System.out.println("Command: " + command);
// StringBuilder output = new StringBuilder();
// SshClient client = holder.getSshFileSystem().getWrapper();
// if (!client.isConnected()) {
// client.connect();
// }
// if (SshCommandUtils.exec(client, command, stopFlag, output)) {
// System.out.println(output);
// int index = output.indexOf(" ");
// if (index > 0) {
// return Long.parseLong(output.substring(0, index));
// }
// }
// return -1;
// }

    private boolean indexFile(boolean xz) {
        try {
            String tempFile = PathUtils.combine(holder.getTempFolder(), UUID.randomUUID().toString(), File.separator);
            System.out.println("Temp file: " + tempFile);
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                String command = "LANG=C awk '{len=length($0); print len; }' \""
                        + filePath + "\" | " + (xz ? "xz" : "gzip") + " |cat";
                System.out.println("Command: " + command);
                StringBuilder error = new StringBuilder();
                SshClient client = holder.getSshFileSystem().getWrapper();
                if (!client.isConnected()) {
                    client.connect();
                }
                if (SshCommandUtils.exec(client, command, stopFlag, outputStream, error)) {
                    System.out.println(error);
                    try (InputStream inputStream = new FileInputStream(tempFile);
                         InputStream gzIn = xz ? new XZInputStream(inputStream) : new GZIPInputStream(inputStream)) {
                        this.indexFile = createIndexFile(gzIn);
                        this.raf = new RandomAccessFile(this.indexFile, "r");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createIndexFile(InputStream inputStream) throws Exception {
        byte[] longBytes = new byte[8];
        long offset = 0;
        String tempFile = PathUtils.combine(holder.getTempFolder(), UUID.randomUUID().toString(), File.separator);
        try (OutputStream outputStream = new FileOutputStream(tempFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             BufferedOutputStream bout = new BufferedOutputStream(outputStream)) {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.length() < 1) continue;
                toByteArray(offset, longBytes);
                bout.write(longBytes);
                long len = Long.parseLong(line);
                toByteArray(len, longBytes);
                bout.write(longBytes);
                offset += (len + 1);
            }
        }
        return tempFile;
    }

    private String getPageText(long page) throws Exception {
        long lineStart = page * linePerPage;
        long lineEnd = lineStart + linePerPage - 1;

        //System.out.println("Start line: " + lineStart + "\nEnd line: " + lineEnd);

        StringBuilder command = new StringBuilder();

        raf.seek(lineStart * 16);
        byte[] longBytes = new byte[8];
        if (raf.read(longBytes) != 8) {
            throw new Exception("EOF found");
        }

        long startOffset = Longs.fromByteArray(longBytes);

        //System.out.println("startOffset: " + startOffset);

        raf.seek(lineEnd * 16);
        if (raf.read(longBytes) != 8) {
            raf.seek(raf.length() - 16);
            raf.read(longBytes);
        }

        long endOffset = Longs.fromByteArray(longBytes);
        raf.seek(lineEnd * 16 + 8);
        if (raf.read(longBytes) != 8) {
            raf.seek(raf.length() - 8);
            raf.read(longBytes);
        }
        long lineLength = Longs.fromByteArray(longBytes);

        //System.out.println("endOffset: " + endOffset + " lineLength: " + lineLength);

        endOffset = endOffset + lineLength;

        long byteRange = endOffset - startOffset;

        if (startOffset < 8192) {
            command.append("dd if=\"" + this.filePath + "\" ibs=1 skip=" + startOffset
                    + " count=" + byteRange + " 2>/dev/null | sed -ne '1," +
                    linePerPage + "p;" + (linePerPage + 1) + "q'");
        } else {
            long blockToSkip = startOffset / 8192;
            long bytesToSkip = startOffset % 8192;
            int blocks = (int) Math.ceil((double) byteRange / 8192);

            System.out.println("Byte range: " + FormatUtils.humanReadableByteCount(byteRange, true));

            if (blocks * 8192 - bytesToSkip < byteRange) {
                blocks++;
            }
            command.append("dd if=\"" + this.filePath + "\" ibs=8192 skip=" + blockToSkip
                    + " count=" + blocks + " 2>/dev/null | dd bs=1 skip=" + bytesToSkip + " 2>/dev/null | sed -ne '1," +
                    linePerPage + "p;" + (linePerPage + 1) + "q'");
        }

        //String command = "sed -ne '" + (lineStart + 1) + "," + lineEnd + "p;" + (lineEnd + 1) + "q' \"" + filePath + "\"";
        System.out.println("Command: " + command);
        StringBuilder output = new StringBuilder();
        SshClient client = holder.getSshFileSystem().getWrapper();
        if (!client.isConnected()) {
            client.connect();
        }
        if (SshCommandUtils.exec(client, command.toString(), stopFlag, output)) {
            return output.toString();
        }
        return null;
    }

    private RandomAccessFile search(String text) throws Exception {
        byte[] longBytes = new byte[8];
        String tempFile = PathUtils.combine(holder.getTempFolder(), UUID.randomUUID().toString(), File.separator);
        //List<Integer> list = new ArrayList<>();
        StringBuilder command = new StringBuilder();
        command.append("awk '{if(index(tolower($0),\"" +
                text.toLowerCase(Locale.ENGLISH) + "\")){ print NR}}' \"" + this.filePath + "\"");
        System.out.println("Command: " + command);
        StringBuilder output = new StringBuilder();
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            SshClient client = holder.getSshFileSystem().getWrapper();
            if (!client.isConnected()) {
                client.connect();
            }
            String searchIndexes = PathUtils.combine(holder.getTempFolder(), UUID.randomUUID().toString(), File.separator);
            if (SshCommandUtils.exec(client, command.toString(), stopFlag, outputStream, output)) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
                     OutputStream out = new FileOutputStream(searchIndexes);
                     BufferedOutputStream bout = new BufferedOutputStream(out)) {
                    while (true) {
                        String line = br.readLine();
                        if (line == null) break;
                        line = line.trim();
                        if (line.length() < 1) continue;
                        long lineNo = Long.parseLong(line);
                        toByteArray(lineNo, longBytes);
                        bout.write(longBytes);
                    }
                    return new RandomAccessFile(searchIndexes, "r");
                }
            }
        }
        return null;
    }

    public void close() {
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            Files.delete(Paths.get(this.indexFile));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.logSearchPanel.close();
    }
}