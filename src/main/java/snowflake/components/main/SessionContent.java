package snowflake.components.main;

import snowflake.App;
import snowflake.common.SnowFlakePanel;
import snowflake.components.diskusage.DiskUsageAnalyzer;
import snowflake.components.files.FileComponentHolder;
import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.files.transfer.BackgroundTransferPanel;
import snowflake.components.files.transfer.FileTransfer;
import snowflake.components.keymanager.KeyManagerPanel;
import snowflake.components.networktools.NetworkToolsPanel;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.sysinfo.SystemInfoPanel;
import snowflake.components.taskmgr.TaskManager;
import snowflake.components.terminal.TerminalHolder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SessionContent extends JPanel {
	private static Color bg = new Color(33, 36, 43), sg = new Color(62, 68, 81);
	private SessionInfo info;
	// private JSplitPane verticalSplitter, horizontalSplitter;
	private CardLayout mainCard;
	private JPanel mainPanel;
	private JPanel panels[];
	private TaskManager taskManager;
	private DiskUsageAnalyzer diskUsageAnalyzer;
	private SystemInfoPanel systemInfoPanel;
	private KeyManagerPanel keyManagerPanel;
	private FileComponentHolder fileComponentHolder;
	private TerminalHolder terminalHolder;
	private ExternalEditor externalEditor;
	private BackgroundTransferPanel backgroundTransferPanel;
	private NetworkToolsPanel networkToolsPanel;
	private JLabel lblProgressCount = new JLabel("");

	private MatteBorder matteBorder = new MatteBorder(0, 5, 0, 5,
			Color.DARK_GRAY);
	private EmptyBorder emptyBorder = new EmptyBorder(0, 5, 0, 5);

	// private FileStore fileStore;

	public SessionContent(SessionInfo info, ExternalEditor externalEditor) {
		super(new BorderLayout(0, 0));
		this.info = info;
		this.externalEditor = externalEditor;
		lblProgressCount.setBackground(Color.DARK_GRAY);
		lblProgressCount.setOpaque(false);
		lblProgressCount.setBorder(emptyBorder);
		lblProgressCount.setForeground(Color.BLACK);
		init();
	}

//    Box createTab(String text, boolean closable, String icon) {
//        Box pan = Box.createHorizontalBox();
//
//        JLabel btn2 = new JLabel();
//        btn2.setFont(App.getFontAwesomeFont());
//        btn2.setText(icon);
//        pan.add(btn2);
//        pan.add(Box.createHorizontalStrut(5));
//        pan.add(new JLabel(text));
//        pan.add(Box.createHorizontalStrut(5));
//        if (closable) {
//            JLabel btn1 = new JLabel();
//            btn1.setFont(App.getFontAwesomeFont());
//            btn1.setText("\uf00d");
//            pan.add(btn1);
//        }
//        return pan;
//    }

	public void init() {
		this.mainCard = new CardLayout();
		this.mainPanel = new JPanel(this.mainCard);
		this.mainPanel.setOpaque(false);
		this.fileComponentHolder = new FileComponentHolder(info, externalEditor,
				this);
//        TabbedPanel bottomTabs = new TabbedPanel();
		terminalHolder = new TerminalHolder(info);
		backgroundTransferPanel = new BackgroundTransferPanel((count) -> {
			lblProgressCount.setText(count > 0 ? count * 25 + "" : "");
			lblProgressCount.setOpaque(count > 0);
			lblProgressCount.setBorder(count > 0 ? matteBorder : emptyBorder);
		});
		taskManager = new TaskManager(this.info);
		diskUsageAnalyzer = new DiskUsageAnalyzer(this.info);
		systemInfoPanel = new SystemInfoPanel(this.info);
		keyManagerPanel = new KeyManagerPanel(this.info);
		networkToolsPanel = new NetworkToolsPanel(this.info);

		// JToolBar toolBar = new JToolBar();
		JButton btn = new JButton();
		btn.setMargin(new Insets(5, 5, 5, 5));
		btn.setFont(App.getFontAwesomeFont());
		btn.setText("\uf120");
		// toolBar.add(btn);
		btn.addActionListener(e -> {
			terminalHolder.createNewTerminal();
		});

		mainPanel.add(fileComponentHolder, SnowFlakePanel.FILES.getName());
		mainPanel.add(terminalHolder, SnowFlakePanel.TERMINAL.getName());
//        mainPanel.add(fileSearchPanel, "Search");
		mainPanel.add(taskManager, SnowFlakePanel.SYSTEM_MONITOR.getName());
		mainPanel.add(diskUsageAnalyzer, SnowFlakePanel.DISK_SPACE_ANALYZER.getName());
		mainPanel.add(backgroundTransferPanel, SnowFlakePanel.ACTIVE_TRANSFERS.getName());
		mainPanel.add(systemInfoPanel, SnowFlakePanel.LINUX_TOOLS.getName());
		mainPanel.add(keyManagerPanel, SnowFlakePanel.SSH_KEYS.getName());
		mainPanel.add(networkToolsPanel, SnowFlakePanel.NETWORK_TOOLS.getName());

		panels = new JPanel[SnowFlakePanel.values().length];
		Dimension maxDim = null;
		for (SnowFlakePanel snowFlakePanel : SnowFlakePanel.values()) {
			JPanel panel = new JPanel(new BorderLayout(10, 10));

			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					panelClicked(panel);
				}
			};

			if (snowFlakePanel.getName().equals(SnowFlakePanel.ACTIVE_TRANSFERS.getName())) {
				panel.add(lblProgressCount, BorderLayout.EAST);
			}

			panel.setName(snowFlakePanel.getName());
			panel.addMouseListener(adapter);
			panel.setBackground(bg);
			// panel.setBackground(new Color(20, 23, 41));
			JLabel iconLabel = new JLabel();
			iconLabel.addMouseListener(adapter);
			iconLabel.setFont(App.getFontAwesomeFont());
			iconLabel.setForeground(Color.GRAY);
			iconLabel.setText(snowFlakePanel.getIcon());
			JLabel textLabel = new JLabel(snowFlakePanel.getName());
			textLabel.addMouseListener(adapter);
			textLabel.setForeground(Color.GRAY);
			panel.add(textLabel);
			panel.add(iconLabel, BorderLayout.WEST);
			panel.setBorder(new EmptyBorder(10, 15, 10, 15));
			panels[snowFlakePanel.ordinal()] = panel;

			if (maxDim == null) {
				maxDim = panel.getPreferredSize();
			} else {
				Dimension dimension = panel.getPreferredSize();
				if (maxDim.width < dimension.width) {
					maxDim.width = dimension.width;
				}
				if (maxDim.height < dimension.height) {
					maxDim.height = dimension.height;
				}
			}
		}

//        bottomTabs.addTab("Terminal", terminalHolder);
//        bottomTabs.addTab("Search", new FileSearchPanel(this.info));
//        bottomTabs.addTab("System monitor", new TaskManager(this.info));
//        bottomTabs.addTab("Disk space analyzer", new DiskUsageAnalyzer(this.info));
//        bottomTabs.addTab("Active transfers", backgroundTransferPanel);
//        bottomTabs.addTab("Tools", new SystemInfoPanel(this.info));
//        bottomTabs.setSelectedIndex(0);
//        bottomTabs.setBorder(new LineBorder(new Color(200, 200, 200), 1));

//        bottomTabs.setTabComponentAt(0, createTab("Terminal", false, "\uf120"));
//        bottomTabs.setTabComponentAt(1, createTab("Transfers", false, "\uf0ec"));

		// add(toolBar, BorderLayout.NORTH);
//        verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        verticalSplitter.putClientProperty("Nimbus.Overrides", App.splitPaneSkin1);
//        verticalSplitter.setBackground(Color.RED);
//        verticalSplitter.setOpaque(false);
//        verticalSplitter.setDividerSize(10);
//        verticalSplitter.setResizeWeight(0.5);
//        verticalSplitter.setBottomComponent(bottomTabs);
//        verticalSplitter.setBorder(new EmptyBorder(0, 0, 0, 0));
//        add(verticalSplitter);
//        verticalSplitter.setTopComponent(fileComponentHolder);

		JPanel sidePanel = new JPanel(new BorderLayout());
		sidePanel.setBackground(bg);
//        sidePanel.setMinimumSize(new Dimension(170, 200));
//        sidePanel.setPreferredSize(new Dimension(170, 200));
//        sidePanel.setBackground(new Color(20, 23, 41));

		for (JPanel panel1 : panels) {
			panel1.setPreferredSize(maxDim);
			panel1.setMinimumSize(maxDim);
			panel1.setMaximumSize(maxDim);
			panel1.setAlignmentX(Box.LEFT_ALIGNMENT);
		}

		Box vbox = Box.createVerticalBox();
		// vbox.setBorder(new EmptyBorder(1, 0,0,0));

		for (JPanel panel1 : panels) {
			vbox.add(panel1);
		}

		// setBorder(new LineBorder(Color.black,1));
		sidePanel.add(vbox);
//
//        verticalSplitter.setBorder(new EmptyBorder(10, 10, 10, 10));
		// mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		add(mainPanel);

		// mainCard.show(mainPanel, "Files");
//
		add(sidePanel, BorderLayout.WEST);

		panelClicked(panels[SnowFlakePanel.fromName(App.getGlobalSettings().getDefaultPanel()).ordinal()]);
	}

	public void transferInBackground(FileTransfer transfer) {
		this.backgroundTransferPanel.addNewBackgroundTransfer(transfer);
	}

	public FileComponentHolder getFileComponentHolder() {
		return fileComponentHolder;
	}

	public void openTerminal(String command) {
		this.terminalHolder.createNewTerminal(command);
	}

	void panelClicked(JPanel panel) {
		for (JPanel panel1 : panels) {
			if (panel == panel1) {
//                panel1.setBackground(new Color(40, 43, 61));
				panel1.setBackground(sg);
				mainCard.show(mainPanel, panel.getName());
				for (Component child : panel1.getComponents()) {
					child.setForeground(Color.WHITE);
				}

if(panel.getName().equals(SnowFlakePanel.TERMINAL.getName())){
this.terminalHolder.lazyInit();
}else if(panel.getName().equals(SnowFlakePanel.FILES.getName())){
this.fileComponentHolder.lazyInit();
}


				//panel.requestFocusInWindow();
			} else {
				panel1.setBackground(bg);
//                panel1.setBackground(new Color(20, 23, 41));
				for (Component child : panel1.getComponents()) {
					child.setForeground(Color.GRAY);
				}
			}
		}
	}

	public void close() {
		new Thread(() -> {
			diskUsageAnalyzer.close();
			systemInfoPanel.close();
			// keyManagerPanel.close();
			fileComponentHolder.close();
			terminalHolder.close();
			externalEditor.close();
			backgroundTransferPanel.close();
		}).start();
	}

	public void showPage(String pageName) {
		JPanel panel = panels[SnowFlakePanel.fromName(pageName).ordinal()];
		panelClicked(panel);
	}
}
