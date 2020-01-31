package snowflake.components.sysinfo;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.common.DisabledPanel;
import snowflake.components.common.StartPage;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.sysinfo.platforms.SystemInfo;
import snowflake.components.sysinfo.platforms.linux.LinuxSysInfo;
import snowflake.components.taskmgr.PlatformChecker;
import snowflake.utils.GraphicsUtils;
import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SystemInfoPanel extends JPanel implements AutoCloseable {
	private AtomicBoolean stopFlag;
	private ExecutorService threadPool = Executors.newSingleThreadExecutor();
	private SshUserInteraction userInteraction;
	private Box tabbedBox;
	private SessionInfo info;
	private JLabel labels[] = new JLabel[3];
	private String pages[] = { "System information", "Services (systemd)",
			"Process and Ports" };
	private Component pageComponent[];
	private CardLayout cardLayout;
	private JPanel cardPanel;
	private CardLayout mainCardLayout;
	private JRootPane rootPane;
	private JPanel contentPane;
	private SshClient client;
	private String platform;
	private SystemInfo systemInfo;
	private JTextArea txtSystemOverview;
	private ServicePanel servicePanel;
	private SocketPanel socketPanel;
	private DisabledPanel disabledPanel;

	public SystemInfoPanel(SessionInfo info) {
		super(new BorderLayout());
		mainCardLayout = new CardLayout();
		stopFlag = new AtomicBoolean(false);
		setLayout(mainCardLayout);
		this.info = info;
		contentPane = new JPanel(mainCardLayout);
		rootPane = new JRootPane();
		rootPane.setContentPane(contentPane);
		add(rootPane);
		userInteraction = new SshUserInteraction(info, rootPane);
		client = new SshClient(userInteraction);

		disabledPanel = new DisabledPanel();
		disabledPanel.startAnimation(null);
		rootPane.setGlassPane(disabledPanel);

		servicePanel = createServicePanel();
		socketPanel = createSocketPanel();

		pageComponent = new Component[] { createSystemOverviewPanel(),
				servicePanel, socketPanel };

		JPanel mainPanel = new JPanel(new BorderLayout());

		StartPage startPage = new StartPage("Linux tools",
				"A set of useful tools for Linux", "Open", e -> {
					client = new SshClient(userInteraction);
					mainCardLayout.show(contentPane, "Main");
					disableUi();
					threadPool.submit(() -> {
						try {
							getSysInfo();
							updateView();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null,
									ex.getMessage());
							SwingUtilities.invokeLater(() -> {
								enableUi();
								mainCardLayout.show(contentPane, "Start");
							});
						}
					});
				});

//        JPanel startPanel = new JPanel();
//        JButton btnStart = new JButton("Start");
//        btnStart.addActionListener(e -> {
//            client = new SshClient(userInteraction);
//            mainCardLayout.show(contentPane, "Wait");
//            threadPool.submit(() -> {
//                getSysInfo();
//                updateView();
//            });
//        });
//        startPanel.add(btnStart);

		contentPane.add(mainPanel, "Main");
		contentPane.add(startPage, "Start");
//        contentPane.add(new JPanel(), "Wait");

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		mainPanel.add(cardPanel);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(
				new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
		JLabel label32 = new JLabel("Linux tools");
		label32.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

		Box optionBox = Box.createHorizontalBox();
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(e -> {
			disableUi();
			threadPool.submit(() -> {
				try {
					getSysInfo();
					updateView();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage());
					SwingUtilities.invokeLater(() -> {
						enableUi();
						mainCardLayout.show(contentPane, "Start");
					});
				}
			});
		});
		JButton btnClose = new JButton("Done");
		btnClose.addActionListener(e -> {
			threadPool.submit(() -> {
				try {
					client.disconnect();
				} catch (Exception err) {

				}
			});
			mainCardLayout.show(contentPane, "Start");
		});
		optionBox.add(btnRefresh);
		optionBox.add(Box.createHorizontalStrut(5));
		optionBox.add(btnClose);

		topPanel.add(optionBox, BorderLayout.EAST);

		topPanel.add(label32);
		topPanel.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
				new EmptyBorder(5, 10, 5, 10)));
		mainPanel.add(topPanel, BorderLayout.NORTH);

		tabbedBox = Box.createVerticalBox();
		tabbedBox.setBackground(new Color(240, 240, 240));
		tabbedBox.setOpaque(true);

		int i = 0;
		Dimension maxDim = null;
		for (String string : pages) {
			JLabel label = new JLabel(string);
			label.setOpaque(true);
			label.setBorder(new EmptyBorder(10, 10, 10, 30));
			tabbedBox.add(label);
			labels[i] = label;
			tabbedBox.add(label);
			cardPanel.add(pageComponent[i], label.getText());
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					labelClicked(label);
				}
			});
			i++;
			if (maxDim == null) {
				maxDim = label.getPreferredSize();
			} else {
				Dimension dimension = label.getPreferredSize();
				if (maxDim.width < dimension.width) {
					maxDim.width = dimension.width;
				}
				if (maxDim.height < dimension.height) {
					maxDim.height = dimension.height;
				}
			}
		}

		for (JLabel label1 : labels) {
			label1.setPreferredSize(maxDim);
			label1.setMinimumSize(maxDim);
			label1.setMaximumSize(maxDim);
			label1.setAlignmentX(Box.LEFT_ALIGNMENT);
		}

		mainPanel.add(tabbedBox, BorderLayout.WEST);

		labelClicked(labels[0]);
		mainCardLayout.show(contentPane, "Start");
	}

	private void updateView() {
		SwingUtilities.invokeLater(() -> {
			if (systemInfo != null) {
				txtSystemOverview.setText(systemInfo.getSystemOverview());
				servicePanel.setServiceData(systemInfo.getServices());
				txtSystemOverview.setCaretPosition(0);
				socketPanel.setSocketData(systemInfo.getSockets());
			}
			enableUi();
		});
	}

	private void getListingSockets() {
		String cmd = SocketPanel.LSOF_COMMAND;

		disableUi();
//        mainCardLayout.show(contentPane, "Wait");

		boolean elevated = socketPanel.getUseSuperUser();
		if (cmd != null) {
			threadPool.submit(() -> {
				try {
					StringBuilder output = new StringBuilder();
					if (elevated) {
						try {
							if (SudoUtils.runSudo(cmd, client, output) == 0) {
								java.util.List<SocketEntry> list = SocketPanel
										.parseSocketList(output.toString());
								systemInfo.setSockets(list);
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "Operation failed");
					} else {
						System.out.println("Command was: " + cmd);
						try {
							if (SshCommandUtils.exec(client, cmd,
									new AtomicBoolean(false), output)) {
								System.out.println(
										"Command was: " + cmd + " " + output);
								java.util.List<SocketEntry> list = SocketPanel
										.parseSocketList(output.toString());
								systemInfo.setSockets(list);
								getSysInfo();
								return;
							}
							System.out.println("Error: " + output);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					updateView();
				}
			});
		}
	}

	private void performServiceAction(int option) {

		String cmd1 = null;

		switch (option) {
		case 1:
			cmd1 = servicePanel.getStartServiceCommand();
			break;
		case 2:
			cmd1 = servicePanel.getStopServiceCommand();
			break;
		case 3:
			cmd1 = servicePanel.getEnableServiceCommand();
			break;
		case 4:
			cmd1 = servicePanel.getDisableServiceCommand();
			break;
		case 5:
			cmd1 = servicePanel.getReloadServiceCommand();
			break;
		case 6:
			cmd1 = servicePanel.getRestartServiceCommand();
			break;
		}

		String cmd = cmd1;

		disableUi();
		// mainCardLayout.show(contentPane, "Wait");

		boolean elevated = servicePanel.getUseSuperUser();
		if (cmd != null) {
			threadPool.submit(() -> {
				try {
					if (elevated) {
						try {
							if (LinuxSysInfo.runCommandWithSudo(client,
									stopFlag, cmd)) {
								getSysInfo();
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "Operation failed");
					} else {
						try {
							if (LinuxSysInfo.runCommand(client, stopFlag,
									cmd)) {
								getSysInfo();
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					updateView();
				}
			});
		}
	}

	private ServicePanel createServicePanel() {
		ServicePanel servicePanel = new ServicePanel();
		servicePanel.setStartServiceActionListener(e -> {
			performServiceAction(1);
		});
		servicePanel.setStopServiceActionListener(e -> {
			performServiceAction(2);
		});
		servicePanel.setEnableServiceActionListener(e -> {
			performServiceAction(3);
		});
		servicePanel.setDisableServiceActionListener(e -> {
			performServiceAction(4);
		});
		servicePanel.setReloadServiceActionListener(e -> {
			performServiceAction(5);
		});
		servicePanel.setRestartServiceActionListener(e -> {
			performServiceAction(6);
		});
		return servicePanel;
	}

	private JPanel createSystemOverviewPanel() {
		txtSystemOverview = GraphicsUtils.createTextArea();
		txtSystemOverview.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		txtSystemOverview.setEditable(false);
		JScrollPane jScrollPane = new JScrollPane(txtSystemOverview);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(jScrollPane);
		return panel;
	}

	private SocketPanel createSocketPanel() {
		SocketPanel socketPanel = new SocketPanel();
		socketPanel.setRefreshActionListener(e -> {
			getListingSockets();
		});
		return socketPanel;
	}

	void labelClicked(JLabel label) {
		for (JLabel label1 : labels) {
			if (label == label1) {
				label1.setBackground(new Color(220, 220, 220));
				cardLayout.show(cardPanel, label.getText());
			} else {
				label1.setBackground(new Color(240, 240, 240));
			}
		}
	}

	private void getSysInfo() throws Exception {
		if (!client.isConnected()) {
			client.connect();
		}

		if (platform == null) {
			platform = PlatformChecker.getPlatformName(client);
		}

		if ("Linux".equals(platform)) {
			systemInfo = LinuxSysInfo.retrieveSystemInfo(client, stopFlag);
		} else {
			try {
				client.disconnect();
			} catch (Exception e) {
			}
			throw new Exception("Not supported on: " + platform);
		}
	}

	public void close() {
		client.disconnect();
		client = null;
	}

	private void disableUi() {
		disabledPanel.setVisible(true);
	}

	private void enableUi() {
		disabledPanel.setVisible(false);
	}
}
