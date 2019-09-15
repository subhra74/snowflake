package snowflake.components.taskmgr;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.main.ConnectedResource;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.taskmgr.plaformsupport.LinuxPlatformSupport;
import snowflake.components.taskmgr.plaformsupport.PlatformSupport;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public TaskManager(SessionInfo info) {
        cardLayout = new CardLayout();
        setLayout(new BorderLayout());
        contentPane = new JPanel(cardLayout);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
        userInteraction = new SshUserInteraction(info, rootPane);
        client = new SshClient(userInteraction);
        systemLoadPanel = new SystemLoadPanel();
        processListPanel = new ProcessListPanel((cmd, root) -> {
            setCursor(WAIT_CURSOR);
            this.commandPending = true;
            this.commandToExecute = cmd;
            this.runCommandAsRoot = root;
            t.interrupt();
        }, (Boolean b) -> {
            processListPanel.disableStop();
            new Thread(() -> {
                try {
                    running.set(false);
                    t.interrupt();
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(systemLoadPanel, BorderLayout.WEST);
        panel.add(processListPanel);

        contentPane.add(panel, "content");

        Box box1 = Box.createVerticalBox();
        box1.add(Box.createVerticalGlue());

        JButton btnStart = GraphicsUtils.createButton("Start monitoring");
        btnStart.setAlignmentX(Box.CENTER_ALIGNMENT);
        box1.add(btnStart);

        btnStart.addActionListener(e -> {
            processListPanel.enableStop();
            init();
        });

        box1.add(Box.createVerticalGlue());

        contentPane.add(box1, "start");
        cardLayout.show(contentPane, "start");
    }

    private void init() {
        running.set(true);
        cardLayout.show(contentPane, "content");
        t = new Thread(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }
                String platform = PlatformChecker.getPlatformName(client);
                System.out.println("'" + platform + "'");
                if(!running.get()){
                    throw new Exception("Stopped by user");
                }
                if ("Linux".equals(platform)) {
                    this.nativePlatform = new LinuxPlatformSupport();
                }
                if(!running.get()){
                    throw new Exception("Stopped by user");
                }
                if (this.nativePlatform == null) {
                    throw new Exception("Platform not supported: " + platform);
                }

                long lastStatsTime = 0;
                long lastPsTime = 0;

                while (running.get()) {
                    if (commandPending) {
                        executeCommand();
                    }
                    if(!running.get()){
                        throw new Exception("Stopped by user");
                    }
                    long time = System.currentTimeMillis();
                    if (time - lastStatsTime > stats_interval * 1000) {
                        this.nativePlatform.updateMetrics(client);
                        // System.out.println("Cpu: " + this.nativePlatform.getCpuUsage() + " mem: " + this.nativePlatform.getMemoryUsage());
                        lastStatsTime = time;
                    }
                    if(!running.get()){
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
                    if(!running.get()){
                        throw new Exception("Stopped by user");
                    }
                    if (commandPending) {
                        executeCommand();
                    }
                    if(!running.get()){
                        throw new Exception("Stopped by user");
                    }
                    if (time - lastPsTime > ps_interval * 1000) {
                        this.nativePlatform.updateProcessList(client);
                        lastPsTime = time;
                    }
                    SwingUtilities.invokeLater(() -> {
                        //update ui ps
                        processListPanel.setProcessList(this.nativePlatform.getProcessList());
                    });
                    if(!running.get()){
                        throw new Exception("Stopped by user");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
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
        try {
            client.close();
        } catch (IOException e) {
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
                    }
                } else {
                    StringBuilder buf = new StringBuilder();
                    if (!SshCommandUtils.exec(client, commandToExecute, new AtomicBoolean(false), buf)) {
                        JOptionPane.showMessageDialog(this, "Operation failed");
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
                    setCursor(DEF_CURSOR);
                });
            }
        }
    }
}
