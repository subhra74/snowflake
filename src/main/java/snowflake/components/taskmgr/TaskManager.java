package snowflake.components.taskmgr;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.main.ConnectedResource;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.taskmgr.plaformsupport.LinuxPlatformSupport;
import snowflake.components.taskmgr.plaformsupport.PlatformSupport;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskManager extends JPanel implements ConnectedResource {
    private JRootPane rootPane;
    private JPanel contentPane;
    private SshUserInteraction userInteraction;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SshClient client;
    private PlatformSupport nativePlatform;
    private boolean commandPending;
    private int stats_interval = 2, ps_interval = 5;
    private SystemLoadPanel systemLoadPanel;
    private ProcessListPanel processListPanel;
    private AtomicBoolean running = new AtomicBoolean(false);

    public TaskManager(SessionInfo info) {
        setLayout(new BorderLayout());
        contentPane = new JPanel(new BorderLayout());
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
        userInteraction = new SshUserInteraction(info, rootPane);
        client = new SshClient(userInteraction);
        systemLoadPanel = new SystemLoadPanel();
        processListPanel = new ProcessListPanel();
        contentPane.add(systemLoadPanel, BorderLayout.WEST);
        contentPane.add(processListPanel);
        init();
    }

    private void executeCommand() {

    }

    private void init() {
        running.set(true);
        executorService.submit(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }
                String platform = PlatformChecker.getPlatformName(client);
                System.out.println("'" + platform + "'");
                if ("Linux".equals(platform)) {
                    this.nativePlatform = new LinuxPlatformSupport();
                }

                if (this.nativePlatform == null) {
                    throw new Exception("Platform not supported: " + platform);
                }

                long lastStatsTime = 0;
                long lastPsTime = 0;

                while (true) {
                    if (commandPending) {
                        executeCommand();
                    }
                    long time = System.currentTimeMillis();
                    if (time - lastStatsTime > stats_interval * 1000) {
                        this.nativePlatform.updateMetrics(client);
                        // System.out.println("Cpu: " + this.nativePlatform.getCpuUsage() + " mem: " + this.nativePlatform.getMemoryUsage());
                        lastStatsTime = time;
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
                    if (commandPending) {
                        executeCommand();
                    }
                    if (time - lastPsTime > ps_interval * 1000) {
                        this.nativePlatform.updateProcessList(client);
                        lastPsTime = time;
                    }
                    SwingUtilities.invokeLater(() -> {
                        //update ui ps
                        processListPanel.setProcessList(this.nativePlatform.getProcessList());
                    });
                    Thread.sleep(1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
}
