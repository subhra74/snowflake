package snowflake.components.diskusage;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.common.DisabledPanel;
import snowflake.components.common.StartPage;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiskUsageAnalyzer extends JPanel implements AutoCloseable {
    private CardLayout cardLayout;
    private JRootPane rootPane;
    private JPanel contentPane;
    private SessionInfo info;
    private ExecutorService threadPool;
    private SshUserInteraction source;
    private SshClient client;
    //private JPanel waitPanel;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private PartitionTableModel model;
    private JPanel resultPanel;
    private JTextField textField;
    private JTable table;
    private Box resultBox;
    private JTree resultTree;
    private DefaultTreeModel treeModel;
    private DiskAnalysisTask task;
    private DisabledPanel disabledPanel;

    public DiskUsageAnalyzer(SessionInfo info) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        resultPanel = new JPanel(new BorderLayout(10, 10));
        JButton btnNext = new JButton("Analyze another folder/partition");
        btnNext.addActionListener(e -> {
            cardLayout.show(contentPane, "Partitions");
        });
        JButton btnExit = new JButton("Finish analysis");
        btnExit.addActionListener(e -> {
            cardLayout.show(contentPane, "Welcome");
        });

        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("results"));
        resultTree = new JTree(treeModel);
        resultTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        resultBox = Box.createHorizontalBox();
        resultBox.add(Box.createHorizontalGlue());
        resultBox.add(btnNext);
        resultBox.add(Box.createHorizontalStrut(10));
        resultBox.add(btnExit);
        resultPanel.add(resultBox, BorderLayout.SOUTH);
        resultPanel.add(new JScrollPane(resultTree));
        resultPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        disabledPanel = new DisabledPanel();
        disabledPanel.startAnimation(stopFlag);


//        waitPanel = new JPanel();
//        JButton btnStop = new JButton("Stop");
//        waitPanel.add(btnStop);

        this.source = new SshUserInteraction(info, rootPane);
        this.threadPool = Executors.newSingleThreadExecutor();

        StartPage startPage = new StartPage("Disk Space Analyzer",
                "Analyze disk space usage for a partition or folder", "Open", e -> {
            stopFlag.set(false);
            cardLayout.show(contentPane, "Partitions");
            disableUi();
            threadPool.submit(() -> {
                listPartitions();
            });
        });

//        JPanel welcomePanel = new JPanel();
//        JButton btnWelcome = new JButton("Start");
//        btnWelcome.addActionListener(e -> {
//            cardLayout.show(contentPane, "Wait");
//            threadPool.submit(() -> {
//                listPartitions();
//            });
//        });
//        welcomePanel.add(btnWelcome);

        contentPane.add(createSelectionPanel(), "Partitions");
        contentPane.add(startPage, "Welcome");
        contentPane.add(resultPanel, "Results");
        //contentPane.add(waitPanel, "Wait");

        cardLayout.show(contentPane, "Welcome");

        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);

        rootPane.setGlassPane(disabledPanel);

        source = new SshUserInteraction(info, rootPane);
    }

    private void listPartitions() {
        try {
            StringBuilder output = new StringBuilder();
            this.client = new SshClient(source);
            if (SshCommandUtils.exec(client, "export POSIXLY_CORRECT=1;df -P -k", stopFlag, output)) {
                List<PartitionEntry> list = new ArrayList<>();
                boolean first = true;
                for (String line : output.toString().split("\n")) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    if (!line.trim().startsWith("/dev/")) {
                        continue;
                    }
                    String[] arr = line.split("\\s+");
                    if (arr.length < 6) continue;
                    PartitionEntry ent = new PartitionEntry(arr[0], arr[5],
                            Long.parseLong(arr[1].trim()) * 1024,
                            Long.parseLong(arr[2].trim()) * 1024,
                            Long.parseLong(arr[3].trim()) * 1024,
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
                enableUi();
            });
        }
    }

    private void createTree(DefaultMutableTreeNode treeNode, DiskUsageEntry entry) {
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
        Collections.sort(entry.getChildren(), (a, b) -> {
            return a.getSize() < b.getSize() ? 1 : (a.getSize() > b.getSize() ? -1 : 0);
        });
        for (DiskUsageEntry ent : entry.getChildren()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(ent);
            child.setAllowsChildren(ent.isDirectory());
            treeNode.add(child);
            createTree(child, ent);
        }
    }

    private void analyze(String path) {
        System.out.println("Analyzing path: " + path);
        task = new DiskAnalysisTask(path, stopFlag, res -> {
            SwingUtilities.invokeLater(() -> {
                if (res != null) {
                    System.out.println("Result found");
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(res);
                    root.setAllowsChildren(true);
                    createTree(root, res);
                    treeModel.setRoot(root);
                }
                enableUi();
            });
        }, source);
        cardLayout.show(contentPane, "Results");
        disableUi();
        threadPool.submit(task);
    }

    private Component createFolderBox() {
        JLabel folderLabel = new JLabel("Enter the folder path to analyze");
        textField = GraphicsUtils.createTextField(30);//new JTextField(30);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        folderLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        textField.setAlignmentX(Box.LEFT_ALIGNMENT);

        Box folderBox = Box.createVerticalBox();
        folderBox.add(folderLabel);
        folderBox.add(textField);
        return folderBox;
    }

    private JPanel createPartitionOptions() {
        JLabel lbl = new JLabel("Please select a mounted partition to analyze");

        Box btop = Box.createHorizontalBox();
        btop.add(lbl);
        btop.add(Box.createHorizontalGlue());
        JButton btnRefresh = new JButton("Refresh partitions");
        btnRefresh.addActionListener(e -> {
            disableUi();
            threadPool.submit(() -> {
                listPartitions();
            });
        });
        btop.add(btnRefresh);

        PartitionRenderer r1 = new PartitionRenderer();
        UsagePercentageRenderer r2 = new UsagePercentageRenderer();

        model = new PartitionTableModel();
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setDefaultRenderer(Object.class, r1);
        table.setDefaultRenderer(Double.class, r2);
        table.setRowHeight(Math.max(r1.getPreferredSize().height, r2.getPreferredSize().height));
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
            stopFlag.set(false);
            if (radPartition.isSelected()) {
                int x = table.getSelectedRow();
                if (x != -1) {
                    int r = table.convertRowIndexToModel(x);
                    analyze(model.get(r).getMountPoint());
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a partition");
                    return;
                }
            } else {
                if (!textField.getText().startsWith("/")) {
                    JOptionPane.showMessageDialog(this, "Please enter absolute path");
                    return;
                }
                analyze(textField.getText());
            }
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

    public void close() {
        try {
            this.client.disconnect();
        } catch (Exception e) {

        }

        try {
            task.close();
        } catch (Exception e) {

        }
    }

    private void disableUi() {
        disabledPanel.setVisible(true);
    }

    private void enableUi() {
        disabledPanel.setVisible(false);
    }
}
