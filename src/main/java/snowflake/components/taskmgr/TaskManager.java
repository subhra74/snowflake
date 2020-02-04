package snowflake.components.taskmgr;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.common.DisabledPanel;
import snowflake.components.common.StartPage;
import snowflake.components.common.TabbedPanel;
import snowflake.components.main.ConnectedResource;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.taskmgr.plaformsupport.*;
import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager extends JPanel implements ConnectedResource {
    private JRootPane rootPane;
    private JPanel contentPane;
    private SshUserInteraction userInteraction;
    //private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SshClient client;
    private PlatformSupport nativePlatform;
    private boolean commandPending;
    private int stats_interval = 2, ps_interval = 5;
    private SystemLoadPanel systemLoadPanel;
    private ProcessListPanel processListPanel;
    private AtomicBoolean running = new AtomicBoolean(false);
    private String commandToExecute;
    private boolean runCommandAsRoot;
    private Cursor DEF_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR), WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
    private Thread t;
    private CardLayout cardLayout;
    private JSpinner spInterval;
    private AtomicInteger sleepInterval = new AtomicInteger(3);
    private DisabledPanel disabledPanel;

    public TaskManager(SessionInfo info) {
        cardLayout = new CardLayout();
        setLayout(new BorderLayout());
        contentPane = new JPanel(cardLayout);
        //contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
        userInteraction = new SshUserInteraction(info, rootPane);
        systemLoadPanel = new SystemLoadPanel();
        processListPanel = new ProcessListPanel((cmd, root) -> {
            disableUi();
            this.commandPending = true;
            this.commandToExecute = cmd;
            this.runCommandAsRoot = root;
            t.interrupt();
        });

        processListPanel.setMinimumSize(new Dimension(10, 10));

        JPanel panel = new JPanel(new BorderLayout());

        TabbedPanel tabbedPanel = new TabbedPanel();
        tabbedPanel.addTab("System performance", systemLoadPanel);
        tabbedPanel.addTab("Processes", processListPanel);
        tabbedPanel.setSelectedIndex(0);

//        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        jSplitPane.putClientProperty("Nimbus.Overrides", App.splitPaneSkin2);
//        jSplitPane.setLeftComponent(systemLoadPanel);
//        jSplitPane.setRightComponent(processListPanel);
//        jSplitPane.setDividerSize(5);


        //panel.add(systemLoadPanel, BorderLayout.WEST);
        //panel.add(processListPanel);

        spInterval = new JSpinner(new SpinnerNumberModel(100, 1, 100, 1));
        spInterval.setValue(sleepInterval.get());
        spInterval.setMaximumSize(spInterval.getPreferredSize());
        spInterval.addChangeListener(e -> {
            int interval = (Integer) spInterval.getValue();
            System.out.println("New interval: " + interval);
            this.sleepInterval.set(interval);
            this.t.interrupt();
        });

        JButton btnClose = new JButton("Disconnect");
        btnClose.addActionListener(e -> {
            new Thread(() -> {
                try {
                    running.set(false);
                    t.interrupt();
                    client.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        Box topPanel = Box.createHorizontalBox();
//        topPanel.setOpaque(true);
//        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0,
                        new Color(240, 240, 240)),
                new EmptyBorder(5, 10, 5, 10)));

        JLabel titleLabel = new JLabel("System Monitor");
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));

        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(new JLabel("Refresh interval"));
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(spInterval);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(new JLabel("Sec"));
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(btnClose);

        panel.add(topPanel, BorderLayout.NORTH);
        //panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(tabbedPanel);
//        panel.add(jSplitPane);

        contentPane.add(panel, "content");

        StartPage startPage = new StartPage("System Monitor",
                "Monitor resource usage and manage processes",
                "Start monitoring", e -> {
//            processListPanel.enableStop();
            init();
        });

        Box box1 = Box.createVerticalBox();
        box1.add(Box.createVerticalGlue());

        JButton btnStart = new JButton("Start monitoring");
        btnStart.setAlignmentX(Box.CENTER_ALIGNMENT);
        box1.add(btnStart);

        btnStart.addActionListener(e -> {
//            processListPanel.enableStop();
            init();
        });

        box1.add(Box.createVerticalGlue());

        contentPane.add(startPage, "start");
        cardLayout.show(contentPane, "start");

        disabledPanel = new DisabledPanel();
        disabledPanel.startAnimation(null);
        rootPane.setGlassPane(disabledPanel);

        //jSplitPane.setDividerLocation(0.5f);
    }

    private void init() {
        running.set(true);
        cardLayout.show(contentPane, "content");
        disableUi();
        t = new Thread(() -> {
            try {
                client = new SshClient(userInteraction);
                client.connect();
                String platform = PlatformChecker.getPlatformName(client);
                System.out.println("'" + platform + "'");
                if (!running.get()) {
                    throw new Exception("Stopped by user");
                }

//                if ("Linux".equals(platform)) {
//                    this.nativePlatform = new LinuxPlatformSupport();
//                }
//
//                if ("FreeBSD".equals(platform)) {
//                    this.nativePlatform = new FreeBSDPlatformSupport();
//                }
//
//                if ("OpenBSD".equals(platform)) {
//                    this.nativePlatform = new OpenBSDPlatformSupport();
//                }
//
//                if ("NetBSD".equals(platform)) {
//                    this.nativePlatform = new NetBSDPlatformSupport();
//                }
//
//                if ("HP-UX".equals(platform)) {
//                    this.nativePlatform = new HpUxPlatformSupport();
//                }

                switch (platform) {
                    case "Linux":
                        this.nativePlatform = new LinuxPlatformSupport();
                        break;
                    case "FreeBSD":
                        this.nativePlatform = new FreeBSDPlatformSupport();
                        break;
                    case "OpenBSD":
                        this.nativePlatform = new OpenBSDPlatformSupport();
                        break;
                    case "NetBSD":
                        this.nativePlatform = new NetBSDPlatformSupport();
                        break;
                    case "HP-UX":
                        this.nativePlatform = new HpUxPlatformSupport();
                        break;
                }

                if (!running.get()) {
                    throw new Exception("Stopped by user");
                }
                if (this.nativePlatform == null) {
                    JOptionPane.showMessageDialog(null, "Platform " + platform + " is not supported");
                    throw new Exception("Platform not supported: " + platform);
                }

                long lastStatsTime = 0;
                long lastPsTime = 0;

                while (running.get()) {
                    if (commandPending) {
                        executeCommand();
                        lastPsTime = 0;
                    }
                    if (!running.get()) {
                        throw new Exception("Stopped by user");
                    }
                    long time = System.currentTimeMillis();
                    this.nativePlatform.updateMetrics(client);
                    // System.out.println("Cpu: " + this.nativePlatform.getCpuUsage() + " mem: " + this.nativePlatform.getMemoryUsage());
                    //lastStatsTime = time;
//                    if (time - lastStatsTime > stats_interval * 1000) {
//                        this.nativePlatform.updateMetrics(client);
//                        // System.out.println("Cpu: " + this.nativePlatform.getCpuUsage() + " mem: " + this.nativePlatform.getMemoryUsage());
//                        lastStatsTime = time;
//                    }
                    if (!running.get()) {
                        throw new Exception("Stopped by user");
                    }
                    SwingUtilities.invokeLater(() -> {
                        //update ui stat
                        systemLoadPanel.setCpuUsage(this.nativePlatform.getCpuUsage());
                        systemLoadPanel.setMemoryUsage(this.nativePlatform.getMemoryUsage());
                        systemLoadPanel.setSwapUsage(this.nativePlatform.getSwapUsage());
                        systemLoadPanel.setTotalMemory(this.nativePlatform.getTotalMemory());
                        systemLoadPanel.setUsedMemory(this.nativePlatform.getUsedMemory());
                        systemLoadPanel.setTotalSwap(this.nativePlatform.getTotalSwap());
                        systemLoadPanel.setUsedSwap(this.nativePlatform.getUsedSwap());
                        systemLoadPanel.refreshUi();

                    });
                    if (!running.get()) {
                        throw new Exception("Stopped by user");
                    }
                    if (commandPending) {
                        executeCommand();
                    }
                    if (!running.get()) {
                        throw new Exception("Stopped by user");
                    }
                    this.nativePlatform.updateProcessList(client);
//                    if (time - lastPsTime > ps_interval * 1000) {
//                        this.nativePlatform.updateProcessList(client);
//                        lastPsTime = time;
//                    }
                    SwingUtilities.invokeLater(() -> {
                        //update ui ps
                        processListPanel.setProcessList(this.nativePlatform.getProcessList());
                        enableUi();
                    });
                    if (!running.get()) {
                        throw new Exception("Stopped by user");
                    }
                    try {
                        Thread.sleep(this.sleepInterval.get() * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Running: " + running.get());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
                SwingUtilities.invokeLater(() -> {
                    enableUi();
                    cardLayout.show(contentPane, "start");
                });
            }
        });
        t.start();
    }

    @Override
    public boolean isInitiated() {
        return running.get();
    }

    @Override
    public boolean isConnected() {
        return !(client == null || client.isConnected());
    }

    @Override
    public void close() {
        running.set(false);
        try {
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeCommand() {
        if (commandToExecute != null) {
            try {
                t.interrupt();
                if (runCommandAsRoot) {
                    if (SudoUtils.runSudo(commandToExecute, client) != 0) {
                        JOptionPane.showMessageDialog(this, "Operation failed");
                    } else {
                        this.nativePlatform.updateProcessList(client);
                    }
                } else {
                    StringBuilder buf = new StringBuilder();
                    if (!SshCommandUtils.exec(client, commandToExecute, new AtomicBoolean(false), buf)) {
                        JOptionPane.showMessageDialog(this, "Operation failed");
                    } else {
                        this.nativePlatform.updateProcessList(client);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                commandPending = false;
                commandToExecute = null;
                runCommandAsRoot = false;
                processListPanel.activateProcessListPanel();
                SwingUtilities.invokeLater(() -> {
                    processListPanel.setProcessList(this.nativePlatform.getProcessList());
                    enableUi();
                });
            }
        }
    }

    private void disableUi() {
        disabledPanel.setVisible(true);
    }

    private void enableUi() {
        disabledPanel.setVisible(false);
    }
}
