package snowflake.components.search;

import snowflake.App;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
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

    private JRootPane rootPane;
    private JPanel contentPane;

    private SessionInfo info;

    public FileSearchPanel(SessionInfo info) {
        this.info = info;
        setLayout(new BorderLayout());
        contentPane = new JPanel(new BorderLayout());


        chkIncludeCompressed = new JCheckBox(
                "Look inside compressed files");
        chkIncludeCompressed.setAlignmentX(LEFT_ALIGNMENT);
        radFileName = new JRadioButton("In filename");
        radFileName.setAlignmentX(LEFT_ALIGNMENT);
        radFileContents = new JRadioButton("In file content");
        radFileContents.setAlignmentX(LEFT_ALIGNMENT);

        ButtonGroup bg = new ButtonGroup();
        bg.add(radFileName);
        bg.add(radFileContents);

        radFileName.setSelected(true);

        //setBackground(UIManager.getColor("DefaultBorder.color"));

        setLayout(new BorderLayout(1, 1));
        Box b1 = Box.createVerticalBox();
        b1.setOpaque(true);
        //b1.setBackground(UIManager.getColor("Panel.background"));

        b1.setBorder(new EmptyBorder(10, 10,
                10, 10));

        JLabel lblName = new JLabel(
                "Search for");
        lblName.setAlignmentX(LEFT_ALIGNMENT);
        txtName = new JTextField(20);
        Dimension pref = txtName.getPreferredSize();
        txtName.setMaximumSize(pref);
        txtName.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblFolder = new JLabel("Search in");
        lblFolder.setAlignmentX(LEFT_ALIGNMENT);
        txtFolder = new JTextField(20);
        txtFolder.setPreferredSize(pref);
        txtFolder.setMaximumSize(pref);
        txtFolder.setAlignmentX(LEFT_ALIGNMENT);

        txtFolder.setText("$HOME");

//        if (args == null || args.length < 1) {
//            txtFolder.setText("$HOME");
//        } else {
//            txtFolder.setText(args[0]);
//        }

        JLabel lblSize = new JLabel("Size");
        lblSize.setAlignmentX(LEFT_ALIGNMENT);
        cmbSize = new JComboBox<>(
                new String[]{"Equal to",
                        "Less than",
                        "More than"});
        cmbSize.setMaximumSize(pref);
        cmbSize.setAlignmentX(LEFT_ALIGNMENT);

        txtSize = new JTextField(10);
        txtSize.setPreferredSize(pref);
        txtSize.setAlignmentX(LEFT_ALIGNMENT);
        txtSize.setMaximumSize(pref);

        JLabel lblMtime = new JLabel("Modified");
        lblMtime.setAlignmentX(LEFT_ALIGNMENT);

        ButtonGroup btnGroup1 = new ButtonGroup();
        radAny = new JRadioButton("Any time");
        radAny.setAlignmentX(LEFT_ALIGNMENT);
        radWeek = new JRadioButton("This week");
        radWeek.setAlignmentX(LEFT_ALIGNMENT);
        radCust = new JRadioButton("Between");
        radCust.setAlignmentX(LEFT_ALIGNMENT);

        btnGroup1.add(radAny);
        btnGroup1.add(radWeek);
        btnGroup1.add(radCust);

        ActionListener radSelected = new ActionListener() {

            private void disableSpinners() {
                spDate1.setEnabled(false);
                spDate2.setEnabled(false);
            }

            private void enableSpinners() {
                spDate1.setEnabled(true);
                spDate2.setEnabled(true);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == radAny) {
                    disableSpinners();
                } else if (e.getSource() == radWeek) {
                    disableSpinners();
                } else {
                    enableSpinners();
                }
            }
        };

        radAny.addActionListener(radSelected);
        radWeek.addActionListener(radSelected);
        radCust.addActionListener(radSelected);

        radAny.setSelected(true);

        JLabel lblFrom = new JLabel("From");
        lblFrom.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lblTo = new JLabel("To");
        lblTo.setAlignmentX(LEFT_ALIGNMENT);

        SpinnerDateModel sm1 = new SpinnerDateModel();
        sm1.setEnd(new Date());
        spDate1 = new JSpinner(sm1);
        spDate1.setPreferredSize(pref);
        spDate1.setMaximumSize(pref);
        spDate1.setAlignmentX(LEFT_ALIGNMENT);
        spDate1.setEditor(new JSpinner.DateEditor(spDate1, "dd/MM/yyyy"));

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        spDate1.setValue(cal.getTime());
        spDate1.setEnabled(false);

        SpinnerDateModel sm2 = new SpinnerDateModel();
        sm2.setEnd(new Date());
        spDate2 = new JSpinner(sm2);
        spDate2.setMaximumSize(pref);
        spDate2.setPreferredSize(pref);
        spDate2.setAlignmentX(LEFT_ALIGNMENT);
        spDate2.setEditor(new JSpinner.DateEditor(spDate2, "dd/MM/yyyy"));
        spDate2.setEnabled(false);

        JLabel lblLookfor = new JLabel(
                "Look for");
        lblLookfor.setAlignmentX(LEFT_ALIGNMENT);

        ButtonGroup btnGroup2 = new ButtonGroup();
        radBoth = new JRadioButton("Both file and folder");
        radBoth.setAlignmentX(LEFT_ALIGNMENT);
        radFile = new JRadioButton("File only");
        radFile.setAlignmentX(LEFT_ALIGNMENT);
        radFolder = new JRadioButton(
                "Folder only");
        radFolder.setAlignmentX(LEFT_ALIGNMENT);

        btnGroup2.add(radBoth);
        btnGroup2.add(radFile);
        btnGroup2.add(radFolder);

        radBoth.setSelected(true);

        btnSearch = new JButton("Find");
        btnSearch.setAlignmentX(LEFT_ALIGNMENT);
        // btnSearch.setPreferredSize(pref);

        btnSearch.addActionListener(e -> {
            //find();
        });

        model = new SearchTableModel();

        table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            if (table.getSelectedRowCount() > 0) {
                enableButtons();
            } else {
                disableButtons();
            }
        });

        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(24);
        table.setShowGrid(false);
        resizeColumnWidth(table);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        SearchTableRenderer r = new SearchTableRenderer(model);
        table.setDefaultRenderer(String.class, r);
        table.setDefaultRenderer(Date.class, r);
        table.setDefaultRenderer(Integer.class, r);
        table.setDefaultRenderer(Long.class, r);

        JScrollPane jsp = new JScrollPane(table);
        jsp.setBorder(null);

        lblStat = new JLabel("Ready");
        lblCount = new JLabel("");
        lblCount.setHorizontalAlignment(JLabel.RIGHT);

        b1.add(Box.createVerticalStrut(10));

        b1.add(lblName);
        b1.add(Box.createVerticalStrut(3));
        b1.add(txtName);

        b1.add(Box.createVerticalStrut(10));

        b1.add(radFileName);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radFileContents);
        b1.add(Box.createVerticalStrut(3));
        b1.add(chkIncludeCompressed);

        b1.add(Box.createVerticalStrut(10));

        b1.add(lblFolder);
        b1.add(Box.createVerticalStrut(3));
        b1.add(txtFolder);

        b1.add(Box.createVerticalStrut(10));

        b1.add(lblSize);
        b1.add(Box.createVerticalStrut(3));
        b1.add(cmbSize);
        b1.add(Box.createVerticalStrut(3));
        b1.add(txtSize);

        b1.add(Box.createVerticalStrut(10));

        // b1.add(b2);
        b1.add(lblMtime);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radAny);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radWeek);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radCust);

        b1.add(Box.createVerticalStrut(10));

        b1.add(lblFrom);
        b1.add(Box.createVerticalStrut(3));
        b1.add(spDate1);
        b1.add(Box.createVerticalStrut(3));
        b1.add(lblTo);
        b1.add(Box.createVerticalStrut(3));
        b1.add(spDate2);

        b1.add(Box.createVerticalStrut(10));

        b1.add(lblLookfor);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radBoth);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radFile);
        b1.add(Box.createVerticalStrut(3));
        b1.add(radFolder);

        b1.add(Box.createVerticalStrut(10));

        b1.add(btnSearch);

        Box statBox = Box.createHorizontalBox();
        statBox.setOpaque(true);
        statBox.add(Box.createRigidArea(new Dimension(10,25)));
        statBox.add(lblStat);
        statBox.add(Box.createHorizontalGlue());
        statBox.add(lblCount);
        statBox.add(Box.createRigidArea(new Dimension(10,25)));
        statBox.setBorder(new MatteBorder(1,0,0,0,new Color(240,240,240)));
        //statBox.setBackground(UIManager.getColor("Panel.background"));

        btnShowInBrowser = new JButton(
                "Show location");
        btnDelete = new JButton("Delete");
        btnDownload = new JButton("Download");

        disableButtons();

        btnShowInBrowser.addActionListener(e -> {
//            int index = table.getSelectedRow();
//            if (index != -1) {
//                SearchResult res = model.getItemAt(index);
//                String path = res.getPath();
//                path = PathUtils.getParent(path);
//                if (path.length() > 0) {
//                    //appSession.createFolderView(path);
//                }
//            }
        });

        btnDelete.addActionListener(e -> {
//            if (table.getSelectedRowCount() > 0) {
//                if (t != null && t.isAlive()) {
//                    t.interrupt();
//                }
//                t = new Thread(() -> {
//                    deleteItems();
//                });
//                t.start();
//                if (t.isAlive()) {
//                    deleteWaitDialog.setVisible(true);
//                }
//            }
        });

        btnDownload.addActionListener(e -> {
//            int c = table.getSelectedRowCount();
//            if (c < 1) {
//                return;
//            }
//            JFileChooser jfc = new JFileChooser();
//            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            if (jfc.showSaveDialog(
//                    getWindow()) == JFileChooser.APPROVE_OPTION) {
//                String downloadFolder = jfc.getSelectedFile().getAbsolutePath();
//                downloadItems(downloadFolder);
//            }
        });

        Box bActions = Box.createHorizontalBox();
        bActions.setOpaque(true);
        //bActions.setBackground(UIManager.getColor("Panel.background"));
        bActions.setBorder(new EmptyBorder(5,
                10, 5, 10));
        bActions.add(Box.createHorizontalGlue());
        bActions.add(btnShowInBrowser);
        bActions.add(Box.createHorizontalStrut(10));
        bActions.add(btnDelete);
        bActions.add(Box.createHorizontalStrut(10));
        bActions.add(btnDownload);

        JScrollPane jspB1 = new JScrollPane(b1,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspB1.setBorder(new MatteBorder(0,0,0,1,new Color(240,240,240)));

        //contentPane.add(jspB1, BorderLayout.WEST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel p = new JPanel(
                new BorderLayout(1, 1));
        //p.setBackground(UIManager.getColor("DefaultBorder.color"));

        p.add(jsp, BorderLayout.CENTER);
        p.add(bActions, BorderLayout.SOUTH);

        splitPane.setRightComponent(p);
        splitPane.setLeftComponent(jspB1);

        splitPane.putClientProperty("Nimbus.Overrides", App.splitPaneSkin2);

        contentPane.add(statBox, BorderLayout.SOUTH);

        contentPane.add(splitPane, BorderLayout.CENTER);

        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
    }

    public void resizeColumnWidth(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn col = columnModel.getColumn(column);
            if (column == 0) {
                col.setPreferredWidth(300);
            } else if (column == 1) {
                col.setPreferredWidth(100);
            } else {
                col.setPreferredWidth(400);
            }
        }
    }

    private void disableButtons() {
        btnShowInBrowser.setEnabled(false);
        btnDelete.setEnabled(false);
        btnDownload.setEnabled(false);
    }

    private void enableButtons() {
        btnShowInBrowser.setEnabled(true);
        btnDelete.setEnabled(true);
        btnDownload.setEnabled(true);
    }

}
