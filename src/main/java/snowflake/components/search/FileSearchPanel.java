package snowflake.components.search;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class FileSearchPanel extends JPanel {
    private JTextField txtName;
    private JComboBox<String> cmbSize;
    private JTextField txtSize;
    private JRadioButton radAny, radWeek, radCust;
    private JRadioButton radBoth, radFile, radFolder;
    private JSpinner spDate1, spDate2;
    private JTextField txtFolder;
    private JButton btnSearch;
    private SearchTableModel model;
    private JTable table;
    private JLabel lblStat, lblCount;
    private ExecutorService threadPool;
    private static final String lsRegex1 = "([dflo])\\|(.*)";
    private Pattern pattern;
    private JRadioButton radFileName, radFileContents;
    private JCheckBox chkIncludeCompressed;
    private String searchScript;
    private JButton btnShowInBrowser, btnDelete, btnDownload;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
}
