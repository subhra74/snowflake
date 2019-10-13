package snowflake.components.networktools;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.common.DisabledPanel;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;

public class NetworkToolsPanel extends JPanel {
    private SessionInfo info;
    private JRootPane rootPane;
    private JPanel contentPane;
    private ExecutorService threadPool;
    private SshUserInteraction source;
    private SshClient client;

    private DisabledPanel disabledPanel;

    public NetworkToolsPanel(SessionInfo info) {
        super(new BorderLayout());
        this.info = info;
    }

    private JTextArea txtOutput;
    private DefaultComboBoxModel<String> modelHost, modelPort;
    private JComboBox<String> cmbHost, cmbPort, cmbDNSTool;
    private SshWrapper wrapper;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private final Object lock = new Object();
    private SessionInfo info;

    private Cursor waitCursor, defaultCursor;

    private JButton btn1, btn2, btn3, btn4;

    public NetworkChecker(Window parent, SessionInfo info) {
        waitCursor = new Cursor(Cursor.WAIT_CURSOR);
        defaultCursor = getCursor();

        modelHost = new DefaultComboBoxModel<String>();
        modelPort = new DefaultComboBoxModel<String>();

        cmbHost = new JComboBox<String>(modelHost);
        cmbPort = new JComboBox<String>(modelPort);
        cmbHost.setEditable(true);
        cmbPort.setEditable(true);

        cmbDNSTool = new JComboBox<String>(new String[] { "nslookup", "dig",
                "dig +short", "host", "getent ahostsv4" });

        JPanel grid = new JPanel(new GridLayout());

        btn1 = new JButton("Ping");
        btn2 = new JButton("Port check");
        btn3 = new JButton("Traceroute");
        btn4 = new JButton("DNS lookup");

        btn1.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[] { "Host to ping", cmbHost }, "Ping",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                executeAsync(
                        "ping -c 4 " + cmbHost.getSelectedItem() + " 2>&1");
            }
        });

        btn2.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[] { "Host name", cmbHost, "Port number",
                            cmbPort },
                    "Port check", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null,
                    null) == JOptionPane.OK_OPTION) {
                executeAsync("bash -c 'test cat</dev/tcp/"
                        + cmbHost.getSelectedItem() + "/"
                        + cmbPort.getSelectedItem()
                        + " && echo \"Port Reachable\" || echo \"Port Not reachable\"' 2>/dev/null");
            }
        });

        btn3.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[] { "Host name", cmbHost }, "Traceroute",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, null, null) == JOptionPane.OK_OPTION) {
                executeAsync(
                        "traceroute " + cmbHost.getSelectedItem() + " 2>&1");
            }
        });

        btn4.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this,
                    new Object[] { "Host name", cmbHost, "Tool to use",
                            cmbDNSTool },
                    "DNS lookup", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, null,
                    null) == JOptionPane.OK_OPTION) {
                executeAsync(cmbDNSTool.getSelectedItem() + " "
                        + cmbHost.getSelectedItem() + " 2>&1");
            }
        });

        grid.add(btn1);
        grid.add(btn2);
        grid.add(btn3);
        grid.add(btn4);

        add(grid, BorderLayout.NORTH);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        JScrollPane jsp = new JScrollPane(txtOutput);
        add(jsp);

    }

    private void disableUI() {
        btn1.setEnabled(false);
        btn2.setEnabled(false);
        btn3.setEnabled(false);
        btn4.setEnabled(false);
    }

    private void enableUI() {
        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);
    }

    private void executeAsync(String cmd) {
        txtOutput.setText("");
        setCursor(waitCursor);
        disableUI();
        new Thread(() -> {
            executeCommand(cmd);
        }).start();
    }

    private void executeCommand(String cmd) {
        try {
            synchronized (lock) {
                if (wrapper == null || !wrapper.isConnected()) {
                    wrapper = SshUtility.connectWrapper(info, stopFlag);
                }
            }
            if (SshUtility.executeCommand(wrapper, cmd,
                    new ArrayList<String>() {
                        @Override
                        public boolean add(String e) {
                            SwingUtilities.invokeLater(() -> {
                                txtOutput.append(e + "\n");
                            });
                            return super.add(e);
                        }
                    }) != 0) {
                throw new Exception("Failed");
            }
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                setCursor(defaultCursor);
                enableUI();
            });
        }
    }

    private void cleanup() {
        stopFlag.set(true);
        new Thread(() -> {
            synchronized (lock) {
                if (wrapper != null) {
                    try {
                        wrapper.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).start();
    }
}
