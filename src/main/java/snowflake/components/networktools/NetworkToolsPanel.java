package snowflake.components.networktools;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshModalUserInteraction;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.common.DisabledPanel;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.SshCommandUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkToolsPanel extends JPanel {
    private SessionInfo info;
    private JRootPane rootPane;
    private JPanel contentPane;
    private ExecutorService threadPool;
    private SshUserInteraction source;
    private SshClient client;
    private DisabledPanel disabledPanel;
    private JTextArea txtOutput;
    private DefaultComboBoxModel<String> modelHost, modelPort;
    private JComboBox<String> cmbHost, cmbPort, cmbDNSTool;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private JButton btn1, btn2, btn3, btn4;

    public NetworkToolsPanel(SessionInfo info) {
        super(new BorderLayout());
        this.info = info;

        modelHost = new DefaultComboBoxModel<String>();
        modelPort = new DefaultComboBoxModel<String>();

        cmbHost = new JComboBox<String>(modelHost);
        cmbPort = new JComboBox<String>(modelPort);
        cmbHost.setEditable(true);
        cmbPort.setEditable(true);

        cmbDNSTool = new JComboBox<String>(new String[]{"nslookup", "dig",
                "dig +short", "host", "getent ahostsv4"});

        JPanel grid = new JPanel(new GridLayout(1, 4, 10, 10));
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        btn1 = new JButton("Ping");
        btn2 = new JButton("Port check");
        btn3 = new JButton("Traceroute");
        btn4 = new JButton("DNS lookup");

        btn1.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[]{"Host to ping", cmbHost}, "Ping",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                executeAsync(
                        "ping -c 4 " + cmbHost.getSelectedItem());
            }
        });

        btn2.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[]{"Host name", cmbHost, "Port number",
                            cmbPort},
                    "Port check", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null,
                    null) == JOptionPane.OK_OPTION) {
                executeAsync("bash -c 'test cat</dev/tcp/"
                        + cmbHost.getSelectedItem() + "/"
                        + cmbPort.getSelectedItem()
                        + " && echo \"Port Reachable\" || echo \"Port Not reachable\"'");
            }
        });

        btn3.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[]{"Host name", cmbHost}, "Traceroute",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                executeAsync(
                        "traceroute " + cmbHost.getSelectedItem());
            }
        });

        btn4.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[]{"Host name", cmbHost, "Tool to use",
                            cmbDNSTool},
                    "DNS lookup", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null,
                    null) == JOptionPane.OK_OPTION) {
                executeAsync(cmbDNSTool.getSelectedItem() + " "
                        + cmbHost.getSelectedItem());
            }
        });

        grid.add(btn1);
        grid.add(btn2);
        grid.add(btn3);
        grid.add(btn4);

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        disabledPanel = new DisabledPanel();
        disabledPanel.startAnimation(stopFlag);

        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);

        rootPane.setGlassPane(disabledPanel);

        contentPane.add(grid, BorderLayout.NORTH);

        txtOutput = GraphicsUtils.createTextArea();
        txtOutput.setEditable(false);
        JScrollPane jsp = new JScrollPane(txtOutput);
        jsp.setBorder(new LineBorder(new Color(240, 240, 240), 1));
        contentPane.add(jsp);
    }

    private void disableUI() {
        disabledPanel.setVisible(true);
    }

    private void enableUI() {
        disabledPanel.setVisible(false);
    }

    private void executeAsync(String cmd) {
        txtOutput.setText("");
        disableUI();
        new Thread(() -> {
            executeCommand(cmd);
        }).start();
    }

    private void executeCommand(String cmd) {
        StringBuilder outText = new StringBuilder();
        if (stopFlag.get()) {
            return;
        }
        try {
            if (this.client == null) {
                this.client = new SshClient(new SshModalUserInteraction(info));
            }

            StringBuilder output = new StringBuilder();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            if (SshCommandUtils.exec(client, cmd, stopFlag, bout, output)) {
                outText.append(new String(bout.toByteArray(), "utf-8") + "\n");
                System.out.println("Command stdout: " + outText);
            } else {
                JOptionPane.showMessageDialog(this, "Error executed with errors");
                outText.append(output.toString() + "\n");
                System.out.println("Command stdout: " + output.toString());
            }
            System.out.println("Done");
            System.out.println("Command output: " + outText);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                this.txtOutput.setText(outText.toString());
                enableUI();
            });
        }
    }

    public void close() {
        stopFlag.set(true);
        if (this.client != null) {
            this.client.disconnect();
        }
    }


}
