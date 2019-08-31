package snowflake.components.taskmgr;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.taskmgr.plaformsupport.LinuxPlatformSupport;
import snowflake.components.taskmgr.plaformsupport.PlatformSupport;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager extends JPanel {
    private JRootPane rootPane;
    private JPanel contentPane;
    private SshUserInteraction userInteraction;
    private JTabbedPane tabs;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SshClient client;
    private PlatformSupport nativePlatform;
    private boolean commandPending;

    public TaskManager(SessionInfo info) {
        setLayout(new BorderLayout());
        userInteraction = new SshUserInteraction(info, rootPane);
        contentPane = new JPanel(new BorderLayout());
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
        client = new SshClient(userInteraction);
        tabs = new JTabbedPane();
        contentPane.add(tabs);
        init();
    }

    private void executeCommand() {

    }

    private void init() {
        executorService.submit(() -> {
            try {
                if (!client.isConnected()) {
                    client.connect();
                }
                String platform = PlatformChecker.getPlatformName(client);
                System.out.println(platform);
                if ("Linux".equals("platform")) {
                    this.nativePlatform = new LinuxPlatformSupport();
                }

                while (true) {
                    if (commandPending) {
                        executeCommand();
                    }
                    this.nativePlatform.updateMetrics(client);
                    SwingUtilities.invokeLater(()->{
                        //update ui
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
