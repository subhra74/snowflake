package snowflake.components.files.logviewer;

import snowflake.App;
import snowflake.common.FileInfo;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.List;

public class LogViewerItem extends JPanel implements SearchListener {
    private String localTempFile;
    private FileInfo fileInfo;
    private DiskTableModel model;
    private JTable table;
    private int maxLine;
    private LogViewerComponent logViewerComponent;
    private LogSearchPanel logSearchPanel;

    public LogViewerItem(LogViewerComponent logViewerComponent,
                         FileInfo fileInfo, String localTempFile,
                         LineIndexer.IndexLines lines) throws FileNotFoundException {
        super(new BorderLayout());
        this.logViewerComponent = logViewerComponent;
        this.localTempFile = localTempFile;
        this.fileInfo = fileInfo;
        this.model = new DiskTableModel(localTempFile, lines.lines);
        this.maxLine = lines.maxLen;
        this.table = new JTable(model);
        this.table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.table.setTableHeader(null);
        this.logSearchPanel = new LogSearchPanel(this);
        this.add(logSearchPanel, BorderLayout.SOUTH);
        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(new JScrollPane(table));
        Box b1 = Box.createHorizontalBox();
        b1.add(new JTextField(fileInfo.getPath()));
        JButton btnReload = new JButton();
        btnReload.setFont(App.getFontAwesomeFont());
        btnReload.setText("\uf021");
        btnReload.addActionListener(e -> {
            logViewerComponent.getLatestLog();
        });
        b1.add(btnReload);
        add(b1, BorderLayout.NORTH);
        adjustColumns();
    }

    public void setMaxLine(int maxLine) {
        if (this.maxLine < maxLine) {
            this.maxLine = maxLine;
        }
    }

    public void addLines(List<LineEntry> lines) {
        this.model.addLines(lines);
    }

    public String getLocalTempFile() {
        return localTempFile;
    }

    public void setLocalTempFile(String localTempFile) {
        this.localTempFile = localTempFile;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    private void adjustColumns() {
        int charWidth = SwingUtilities.computeStringWidth(table.getFontMetrics(table.getFont()), "W");
        int totalWidth = charWidth * maxLine;
        table.getColumnModel().getColumn(0).setPreferredWidth(totalWidth * 2);
    }

    @Override
    public void search(String text, boolean regex, boolean matchCase, boolean fullWord) {
        table.clearSelection();
        logViewerComponent.initSearch(this, model.getList(), this.localTempFile, text, regex, matchCase, fullWord);
    }

    @Override
    public void select(int index) {
        table.setRowSelectionInterval(index, index);
        table.scrollRectToVisible(new Rectangle(table.getCellRect(index, 0, true)));
    }

    public void setSearchResult(List<Integer> list) {
        this.logSearchPanel.setResults(list);
    }
}
