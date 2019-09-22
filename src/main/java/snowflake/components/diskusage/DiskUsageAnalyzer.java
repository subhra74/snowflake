package snowflake.components.diskusage;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.diskusage.treetable.JTreeTable;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
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
    private JPanel resultPanel;
    private JTextField textField;

    public DiskUsageAnalyzer(SessionInfo info) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        resultPanel = new JPanel(new BorderLayout());

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

        contentPane.add(createSelectionPanel(), "Partitions");
        contentPane.add(welcomePanel, "Welcome");
        contentPane.add(resultPanel, "Results");
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

    private void createTree(DefaultMutableTreeNode treeNode, DiskUsageEntry entry) {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
        for (DiskUsageEntry ent : entry.getChildren()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(ent);
            treeNode.add(child);
            createTree(child, ent);
        }
    }

    private void analyze(String path) {
        DiskAnalysisTask task = new DiskAnalysisTask(path, stopFlag, res -> {
            SwingUtilities.invokeLater(() -> {
                if (res != null) {
                    System.out.println("Result found");



                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(res);
                    createTree(root, res);
                    resultPanel.add(new JScrollPane(new JTree(root)));
                    cardLayout.show(contentPane, "Results");
                }
            });
        }, source);
        cardLayout.show(contentPane, "Wait");
        threadPool.submit(task);
    }

    private Component createFolderBox() {
        JLabel folderLabel = new JLabel("Enter the folder path to analyze");
        textField = new JTextField(30);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        folderLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        textField.setAlignmentX(Box.LEFT_ALIGNMENT);

        Box folderBox = Box.createVerticalBox();
        folderBox.add(folderLabel);
        folderBox.add(textField);
        return folderBox;
    }

    private JPanel createPartitionOptions() {
        JLabel lbl = new JLabel("Please select a partition to analyze");

        Box btop = Box.createHorizontalBox();
        btop.add(lbl);
        btop.add(Box.createHorizontalGlue());
        JButton btnRefresh = new JButton("Refresh partitions");
        btop.add(btnRefresh);

        model = new PartitionTableModel();
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, new PartitionRenderer());
        JScrollPane jsp = new JScrollPane(table);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(btop, BorderLayout.NORTH);
        panel.add(jsp);

        return panel;
    }

    private JPanel createSelectionPanel() {
        JPanel partitionPanel = new JPanel(new BorderLayout(10, 10));

        CardLayout card1 = new CardLayout();
        JPanel panel = new JPanel(card1);

        panel.add(createFolderBox(), "Folder");
        panel.add(createPartitionOptions(), "Partitions");

        card1.show(panel, "Partitions");

        ButtonGroup bg = new ButtonGroup();
        JRadioButton radPartition = new JRadioButton("Analyze a disk Partition");
        JRadioButton radFolder = new JRadioButton("Analyze a folder");
        bg.add(radFolder);
        bg.add(radPartition);

        radPartition.addActionListener(e -> {
            card1.show(panel, "Partitions");
        });

        radFolder.addActionListener(e -> {
            card1.show(panel, "Folder");
        });

        radPartition.setSelected(true);

        JButton btnNext = new JButton("Next");
        btnNext.addActionListener(e -> {
            analyze(textField.getText());
        });

        partitionPanel.add(panel);
        Box b2 = Box.createHorizontalBox();
        b2.add(Box.createHorizontalGlue());
        b2.add(btnNext);
        partitionPanel.add(b2, BorderLayout.SOUTH);

        Box b3 = Box.createHorizontalBox();
        b3.add(Box.createHorizontalGlue());
        b3.add(radPartition);
        b3.add(Box.createHorizontalStrut(10));
        b3.add(radFolder);
        b3.add(Box.createHorizontalGlue());
        partitionPanel.add(b3, BorderLayout.NORTH);
        partitionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return partitionPanel;
    }
}
