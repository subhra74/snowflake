package snowflake.components.diskusage;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiskUsageAnalyzer extends JPanel {
    private CardLayout cardLayout;
    private JRootPane rootPane;
    private JPanel contentPane;
    private SessionInfo info;
    private ExecutorService threadPool;
    private SshUserInteraction source;
    private SshClient client;
    private JPanel waitPanel;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private PartitionTableModel model;

    public DiskUsageAnalyzer(SessionInfo info) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        waitPanel = new JPanel();
        JButton btnStop = new JButton("Stop");
        waitPanel.add(btnStop);

        this.source = new SshUserInteraction(info, rootPane);
        this.threadPool = Executors.newSingleThreadExecutor();

        JPanel welcomePanel = new JPanel();
        JButton btnWelcome = new JButton("Start");
        btnWelcome.addActionListener(e -> {
            cardLayout.show(contentPane, "Wait");
            threadPool.submit(() -> {
                listPartitions();
            });
        });
        welcomePanel.add(btnWelcome);

        JPanel partitionPanel = new JPanel(new BorderLayout());
        JComboBox<String> cmbSelection = new JComboBox<>(new String[]{"Analyze disk space usage of a Partition",
                "Analyze disk space usage of a Folder"});

        model = new PartitionTableModel();
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new PartitionRenderer());
        JScrollPane jsp = new JScrollPane(table);

        Box b1 = Box.createHorizontalBox();
        b1.add(new JLabel("Folder to analyze"));
        b1.add(Box.createHorizontalStrut(10));

        JTextField textField = new JTextField(30);
        b1.add(textField);

        partitionPanel.add(cmbSelection, BorderLayout.NORTH);
        partitionPanel.add(jsp);
        partitionPanel.add(b1, BorderLayout.SOUTH);

        JPanel pp = new JPanel(new BorderLayout());
        JButton btnNext = new JButton("Analyze");
        JButton btnRefresh = new JButton("Refresh partitions");

        Box b2 = Box.createHorizontalBox();
        b2.add(Box.createHorizontalGlue());
        b2.add(btnRefresh);
        b2.add(Box.createHorizontalStrut(10));
        b2.add(btnNext);

        pp.add(partitionPanel);
        pp.add(b2, BorderLayout.SOUTH);

        contentPane.add(pp, "Partitions");
        contentPane.add(welcomePanel, "Welcome");
        contentPane.add(waitPanel, "Wait");

        cardLayout.show(contentPane, "Welcome");

        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);

        source = new SshUserInteraction(info, rootPane);
    }

    private void listPartitions() {
        try {
            StringBuilder output = new StringBuilder();
            this.client = new SshClient(source);
            if (SshCommandUtils.exec(client, "export POSIXLY_CORRECT=1;df", stopFlag, output)) {
                List<PartitionEntry> list = new ArrayList<>();
                boolean first = true;
                for (String line : output.toString().split("\n")) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    String[] arr = line.split("\\s+");
                    if (arr.length < 6) continue;
                    PartitionEntry ent = new PartitionEntry(arr[0], arr[5],
                            Long.parseLong(arr[1].trim()) * 512,
                            Long.parseLong(arr[2].trim()) * 512,
                            Long.parseLong(arr[3].trim()) * 512,
                            Double.parseDouble(arr[4].replace("%", "").trim()));
                    list.add(ent);
                }
                SwingUtilities.invokeLater(() -> {
                    model.clear();
                    model.add(list);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.disconnect();
            SwingUtilities.invokeLater(() -> {
                cardLayout.show(contentPane, "Partitions");
            });
        }
    }
}
