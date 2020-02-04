//package snowflake.components.files.logviewer;
//
//import snowflake.App;
//import snowflake.common.FileInfo;
//import snowflake.components.files.FileComponentHolder;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.TableCellRenderer;
//import java.awt.*;
//import java.awt.datatransfer.StringSelection;
//import java.io.FileNotFoundException;
//import java.util.List;
//
//public class LogViewerItem extends JPanel implements SearchListener {
//    private String localTempFile;
//    private FileInfo fileInfo;
//    private DiskTableModel model;
//    private JTable table;
//    private int maxLine;
//    private LogViewerComponent logViewerComponent;
//    private LogSearchPanel logSearchPanel;
//
//    public LogViewerItem(LogViewerComponent logViewerComponent,
//                         FileInfo fileInfo, String localTempFile,
//                         LineIndexer.IndexLines lines,
//                         FileComponentHolder holder) throws FileNotFoundException {
//        super(new BorderLayout());
//        setBorder(new EmptyBorder(5, 5, 5, 5));
//        this.logViewerComponent = logViewerComponent;
//        this.localTempFile = localTempFile;
//        this.fileInfo = fileInfo;
//        this.model = new DiskTableModel(localTempFile, lines.lines);
//        this.maxLine = lines.maxLen;
//        this.table = new JTable(model);
//        LogViewerRenderer renderer = new LogViewerRenderer();
//        this.table.setDefaultRenderer(Object.class, renderer);
//        this.table.setTableHeader(null);
//        this.logSearchPanel = new LogSearchPanel(this);
//        this.add(logSearchPanel, BorderLayout.SOUTH);
//        this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        this.table.setRowHeight(renderer.getPreferredSize().height);
//        add(new JScrollPane(table));
//        Box b1 = Box.createHorizontalBox();
//        b1.add(Box.createHorizontalStrut(5));
//        JTextField textField = new JTextField(fileInfo.getPath());
//        textField.setBorder(null);
//        textField.setEditable(false);
//        b1.add(textField);
//
//        JButton btnOpen = new JButton();
//        btnOpen.setFont(App.getFontAwesomeFont());
//        btnOpen.setText("\uf115");
//        btnOpen.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
//        btnOpen.setToolTipText("Open another log file");
//        btnOpen.addActionListener(e -> {
//            String text = JOptionPane.showInputDialog("Please enter full path of the file to be opened");
//            if (text.trim().length() < 1) {
//                JOptionPane.showMessageDialog(null,
//                        "Please enter full path of the file to be opened");
//                return;
//            }
//            holder.statAsync(text, (a, b) -> {
//                if (!b) {
//                    JOptionPane.showMessageDialog(null, "Unable to open file");
//                    return;
//                }
//                SwingUtilities.invokeLater(() -> {
//                    holder.openWithLogViewer(a);
//                });
//            });
//        });
//        b1.add(btnOpen);
//
//        JButton btnReload = new JButton();
//        btnReload.setFont(App.getFontAwesomeFont());
//        btnReload.setToolTipText("Check and load new log entries for this log file");
//        btnReload.setText("\uf021");
//        btnReload.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
//        btnReload.addActionListener(e -> {
//            logViewerComponent.getLatestLog();
//        });
//        b1.add(btnReload);
//
//        JButton btnCopy = new JButton();
//        btnCopy.setFont(App.getFontAwesomeFont());
//        btnCopy.setToolTipText("Copy selected text");
//        btnCopy.setText("\uf0c5");
//        btnCopy.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
//        btnCopy.addActionListener(e -> {
//            StringBuilder sb = new StringBuilder();
//            for (int r : table.getSelectedRows()) {
//                String str = (String) table.getValueAt(r, 0);
//                sb.append(str);
//            }
//            Toolkit.getDefaultToolkit().getSystemClipboard()
//                    .setContents(new StringSelection(sb.toString()), null);
//        });
//        b1.add(btnCopy);
//
//        add(b1, BorderLayout.NORTH);
//    }
//
//    public void setMaxLine(int maxLine) {
//        if (this.maxLine < maxLine) {
//            this.maxLine = maxLine;
//            adjustColumns();
//        }
//    }
//
//    public void addLines(List<LineEntry> lines) {
//        this.model.addLines(lines);
//    }
//
//    public int getLineCount() {
//        return this.model.getRowCount();
//    }
//
//    public LineEntry getLastLine() {
//        return this.model.getLastLine();
//    }
//
//    public void removeLastLine() {
//        model.removeLastLine();
//    }
//
//    public String getLocalTempFile() {
//        return localTempFile;
//    }
//
//    public void setLocalTempFile(String localTempFile) {
//        this.localTempFile = localTempFile;
//    }
//
//    public FileInfo getFileInfo() {
//        return fileInfo;
//    }
//
//    public void setFileInfo(FileInfo fileInfo) {
//        this.fileInfo = fileInfo;
//    }
//
//    public void adjustColumns() {
//        System.out.println("Adjusting columns");
//        int charWidth = SwingUtilities.computeStringWidth(table.getFontMetrics(table.getFont()), "W");
//        int totalWidth = charWidth * maxLine;
//        int width = getWidth();
//        System.out.println("Width: " + width + " w: " + totalWidth * 2);
//        int w = Math.max(totalWidth * 2, width);
//        table.getColumnModel().getColumn(0).setPreferredWidth(w);
//    }
//
//    @Override
//    public void search(String text, boolean regex, boolean matchCase, boolean fullWord) {
//        table.clearSelection();
//        logViewerComponent.initSearch(this, model.getList(), this.localTempFile, text, regex, matchCase, fullWord);
//    }
//
//    @Override
//    public void select(long index) {
//        table.setRowSelectionInterval((int) index, (int) index);
//        table.scrollRectToVisible(new Rectangle(table.getCellRect((int) index, 0, true)));
//    }
//
//    public void setSearchResult(List<Integer> list) {
//        this.logSearchPanel.setResults(list);
//    }
//}
