package snowflake.components.files.search;

import snowflake.App;
import snowflake.common.local.files.LocalFileSystem;
import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;
import snowflake.utils.ScriptLoader;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileSearchPanel extends JPanel implements AutoCloseable {
    private JTextField txtName;
    private JComboBox<String> cmbSize, cmbSizeUnit;
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
    private JButton btnShowInBrowser;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private FileComponentHolder holder;

    public FileSearchPanel(FileComponentHolder holder) {
        setLayout(new BorderLayout());
        this.holder = holder;
        chkIncludeCompressed = new JCheckBox(
                "Look inside compressed files");
        chkIncludeCompressed.setAlignmentX(LEFT_ALIGNMENT);
        radFileName = new JRadioButton("In filename (like *.zip or R*ME.txt)");
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

//        b1.setBorder(new EmptyBorder(10, 10,
//                10, 10));

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


        txtSize = new JTextField();
        txtSize.setAlignmentX(LEFT_ALIGNMENT);
        Dimension txtSizeD = new Dimension(60, txtSize.getPreferredSize().height);
        txtSize.setPreferredSize(txtSizeD);
        txtSize.setMaximumSize(txtSizeD);

        cmbSizeUnit = new JComboBox<>(
                new String[]{"GB", "MB",
                        "KB", "B"});
        cmbSizeUnit.setMaximumSize(cmbSizeUnit.getPreferredSize());

        cmbSize = new JComboBox<>(
                new String[]{"=",
                        "<",
                        ">"});
        cmbSize.setMaximumSize(new Dimension(20, cmbSize.getPreferredSize().height));
        cmbSize.setAlignmentX(LEFT_ALIGNMENT);

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

        btnSearch = new JButton("Search");
        btnSearch.setAlignmentX(LEFT_ALIGNMENT);
        // btnSearch.setPreferredSize(pref);

        btnSearch.addActionListener(e -> {
            find();
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

        //b1.add(Box.createVerticalStrut(10));

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

        Box boxSize = Box.createHorizontalBox();
        boxSize.setAlignmentX(LEFT_ALIGNMENT);
        boxSize.add(lblSize);
        boxSize.add(Box.createHorizontalGlue());
        boxSize.add(cmbSize);
        boxSize.add(Box.createRigidArea(new Dimension(3, 0)));
        boxSize.add(txtSize);
        boxSize.add(Box.createRigidArea(new Dimension(3, 0)));
        boxSize.add(cmbSizeUnit);

        b1.add(boxSize);
//        b1.add(Box.createVerticalStrut(3));
//        b1.add(cmbSize);
//        b1.add(Box.createVerticalStrut(3));
//        b1.add(txtSize);

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

        b1.setMinimumSize(b1.getPreferredSize());
        b1.setMaximumSize(b1.getPreferredSize());

        b1.setBorder(new EmptyBorder(10, 10, 10, 10));

        //b1.add(btnSearch);

        Box statBox = Box.createHorizontalBox();
        statBox.setOpaque(true);
        statBox.add(Box.createRigidArea(new Dimension(10, 25)));
        statBox.add(lblStat);
        statBox.add(Box.createHorizontalGlue());
        statBox.add(lblCount);
        statBox.add(Box.createRigidArea(new Dimension(10, 25)));
        statBox.setBorder(new MatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));
        //statBox.setBackground(UIManager.getColor("Panel.background"));

        btnShowInBrowser = new JButton(
                "Show location");

        disableButtons();

        btnShowInBrowser.addActionListener(e -> {
            int index = table.getSelectedRow();
            if (index != -1) {
                SearchResult res = model.getItemAt(index);
                String path = res.getPath();
                path = PathUtils.getParent(path);
                if (path.length() > 0) {
                    holder.openInFileBrowser(path);
                }
            }
        });


        Box bActions = Box.createHorizontalBox();
        bActions.setOpaque(true);
        //bActions.setBackground(UIManager.getColor("Panel.background"));
        bActions.setBorder(new EmptyBorder(5,
                10, 5, 10));
        bActions.add(Box.createHorizontalGlue());
        bActions.add(btnShowInBrowser);

        JScrollPane jspB1 = new JScrollPane(b1,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jspB1.setBorder(null);

        //contentPane.add(jspB1, BorderLayout.WEST);

        //JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JPanel p = new JPanel(
                new BorderLayout(1, 1));
        //p.setBackground(UIManager.getColor("DefaultBorder.color"));

        p.add(jsp, BorderLayout.CENTER);
        p.add(bActions, BorderLayout.SOUTH);

        JPanel pp = new JPanel(new BorderLayout());
        pp.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 1,
                        new Color(240, 240, 240)),
                new EmptyBorder(5, 5, 5, 5)));
        pp.add(jspB1);

        JPanel buttonHolder = new JPanel(new BorderLayout());
        buttonHolder.setBorder(new EmptyBorder(5, 10, 5, 10));
        buttonHolder.add(btnSearch);
        pp.add(buttonHolder, BorderLayout.SOUTH);

        JPanel splitPane = new JPanel(new BorderLayout());
        splitPane.add(p);
        splitPane.add(pp, BorderLayout.WEST);

//        splitPane.setRightComponent(p);
//        splitPane.setLeftComponent(pp);

        splitPane.putClientProperty("Nimbus.Overrides", App.splitPaneSkin2);

        add(splitPane);
        add(statBox, BorderLayout.SOUTH);
        this.threadPool = Executors.newSingleThreadExecutor();
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
    }

    private void enableButtons() {
        btnShowInBrowser.setEnabled(true);
    }

    private void find() {
        if (this.holder.isCloseRequested().get()) {
            return;
        }
        StringBuilder criteriaBuffer = new StringBuilder();

        String folder = txtFolder.getText();
//			if (folder.contains(" ")) {
//				folder = "\"" + folder + "\"";
//			}

        criteriaBuffer.append(" ");

        if (txtSize.getText().length() > 0) {
            criteriaBuffer.append("-size");
            switch (cmbSize.getSelectedIndex()) {
                case 1:
                    criteriaBuffer.append(" -");
                    break;
                case 2:
                    criteriaBuffer.append(" +");
                    break;
                default:
                    criteriaBuffer.append(" ");
            }
            long sizeFactor = 1;
            switch (cmbSizeUnit.getSelectedIndex()) {
                case 0:
                    sizeFactor = 1024 * 1024 * 1024;
                    break;
                case 1:
                    sizeFactor = 1024 * 1024;
                    break;
                case 2:
                    sizeFactor = 1024;
                    break;
            }
            try {
                long size = Long.parseLong(txtSize.getText()) * sizeFactor;
                criteriaBuffer.append(size + "c");
                criteriaBuffer.append(" ");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid size");
                return;
            }
        }

        if (radFile.isSelected() || radFileContents.isSelected()) {
            criteriaBuffer.append(" -type f");
        } else if (radFolder.isSelected()) {
            criteriaBuffer.append(" -type d");
        }

        if (radWeek.isSelected()) {
            criteriaBuffer.append(" -mtime -7");
        } else if (radCust.isSelected()) {
            Date d1 = (Date) spDate1.getValue();
            Date d2 = (Date) spDate2.getValue();

            LocalDate now = LocalDate.now();
            LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();

            long days1 = ChronoUnit.DAYS.between(date1, now);
            long days2 = ChronoUnit.DAYS.between(date2, now);

            criteriaBuffer
                    .append(" -mtime +" + days2 + " -a -mtime -" + days1);
        }

        StringBuilder scriptBuffer = new StringBuilder();

        if (txtName.getText().length() > 0 && radFileName.isSelected()) {
            scriptBuffer
                    .append("export NAME='" + txtName.getText() + "'\n");
        }

        scriptBuffer.append("export LOCATION=\"" + folder + "\"\n");
        scriptBuffer.append("export CRITERIA='" + criteriaBuffer + "'\n");
        if (radFileContents.isSelected()) {
            scriptBuffer.append("export CONTENT=1\n");
            scriptBuffer
                    .append("export PATTERN='" + txtName.getText() + "'\n");
            if (chkIncludeCompressed.isSelected()) {
                scriptBuffer.append("export UNCOMPRESS=1\n");
            }
        }
        this.stopFlag.set(false);
        this.holder.disableUi(stopFlag);
        this.threadPool.submit(() -> {
            findAsync(scriptBuffer);
        });
    }

    private void findAsync(StringBuilder scriptBuffer) {
        if (this.holder.isCloseRequested().get()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            model.clear();
            lblStat.setText("Searching");
            lblCount.setText(String.format("%d items", model.getRowCount()));
            disableButtons();
        });

        System.out.println("Starting search.. ");
        try {
            SshClient client = this.holder.getSshFileSystem().getWrapper();
            if (!client.isConnected()) {
                if (this.holder.isCloseRequested().get()) {
                    return;
                }
                client.connect();
            }

            if (searchScript == null) {
                searchScript = ScriptLoader.loadShellScript("/search.sh");
            }

            scriptBuffer.append(searchScript);

            String findCmd = scriptBuffer.toString();
            System.out.println(findCmd);

            StringBuilder output = new StringBuilder();

            if (!SshCommandUtils.exec(client, findCmd, stopFlag, output)) {
                System.out.println("Error in search");
            }

            System.out.println("search output\n" + output);

            String lines[] = output.toString().split("\n");
            SwingUtilities.invokeLater(() -> {
                for (String line : lines) {
                    if (line.length() > 0) {
                        SearchResult res = parseOutput(line);
                        if (res != null) {
                            model.add(res);
                        }
                    }
                }
                lblCount.setText(String.format("%d items", model.getRowCount()));
            });

            lblStat.setText("Idle");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                lblStat.setText("Idle");
                lblCount.setText(String.format(
                        "%d items",
                        model.getRowCount()));
                this.holder.enableUi();
            });
        }
    }

    private SearchResult parseOutput(String text) {
        if (this.pattern == null) {
            this.pattern = Pattern.compile(lsRegex1);
        }

        Matcher matcher = this.pattern.matcher(text);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String path = matcher.group(2);

            String fileType = "Other";

            switch (type) {
                case "d":
                    fileType = "Folder";
                    break;
                case "l":
                    fileType = "Link";
                    break;
                case "f":
                    fileType = "File";
                    break;
            }

            return new SearchResult(PathUtils.getFileName(path), path, fileType);
        }

        return null;
    }

    public void close() {
        stopFlag.set(true);
    }
}
