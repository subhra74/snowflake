/**
 * 
 */
package muon.app.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import muon.app.App;
import muon.app.ui.components.session.NewSessionDlg;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.SessionListPanel;
import muon.app.ui.components.session.files.transfer.BackgroundFileTransfer;
import muon.app.ui.components.session.files.transfer.BackgroundTransferPanel;
import muon.app.ui.components.session.files.transfer.FileTransfer;
import muon.app.ui.components.settings.SettingsDialog;
import muon.app.ui.components.settings.SettingsPageName;
import util.FontAwesomeContants;

/**
 * @author subhro
 *
 */
public class AppWindow extends JFrame {
	private CardLayout sessionCard;
	private JPanel cardPanel;
	private SessionListPanel sessionListPanel;
	private BackgroundTransferPanel uploadPanel, downloadPanel;
	private JLabel lblUploadCount, lblDownloadCount;
	private JPopupMenu popup;
	private Component bottomPanel;

	/**
	 * 
	 */
	public AppWindow() {
		super("Muon SSH");
		try {
			this.setIconImage(ImageIO.read(AppWindow.class.getResource("/muon.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Insets inset = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

		Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();

		int screenWidth = screenD.width - inset.left - inset.right;
		int screenHeight = screenD.height - inset.top - inset.bottom;

		if (screenWidth < 1024 || screenHeight < 650) {
			setSize(screenWidth, screenHeight);
		} else {
			int width = (screenWidth * 80) / 100;
			int height = (screenHeight * 80) / 100;
			setSize(width, height);
		}

		// this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// mainWindow.add(new MainContent(mainWindow));
		this.setLocationRelativeTo(null);

		this.sessionCard = new CardLayout();
		this.cardPanel = new JPanel(this.sessionCard, true);
		this.cardPanel.setDoubleBuffered(true);

		this.add(createSessionPanel(), BorderLayout.WEST);
		this.add(this.cardPanel);
		this.bottomPanel = createBottomPanel();
		this.add(this.bottomPanel, BorderLayout.SOUTH);

		this.uploadPanel = new BackgroundTransferPanel(count -> {
			SwingUtilities.invokeLater(() -> {
				lblUploadCount.setText(count + "");
			});
		});

		this.downloadPanel = new BackgroundTransferPanel(count -> {
			SwingUtilities.invokeLater(() -> {
				lblDownloadCount.setText(count + "");
			});
		});
	}

	private JPanel createSessionPanel() {
		JLabel lblSession = new JLabel("Sessions");
		lblSession.setFont(App.SKIN.getDefaultFont().deriveFont(14.0f));
		JButton btnNew = new JButton("+ New");
		btnNew.setFont(App.SKIN.getDefaultFont().deriveFont(12.0f));
		btnNew.addActionListener(e -> {
			SessionInfo info = new NewSessionDlg(this).newSession();
			if (info != null) {
				sessionListPanel.createSession(info);
			}
		});

		JButton btnSettings = new JButton();
		btnSettings.setFont(App.SKIN.getIconFont().deriveFont(12.0f));
		btnSettings.setText(FontAwesomeContants.FA_COG);
		btnSettings.addActionListener(e -> {
			openSettings(null);
		});

		Box topBox = Box.createHorizontalBox();
		topBox.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBox.add(lblSession);
		topBox.add(Box.createRigidArea(new Dimension(50, 0)));
		topBox.add(Box.createHorizontalGlue());
		topBox.add(btnNew);
		topBox.add(Box.createRigidArea(new Dimension(5, 0)));
		topBox.add(btnSettings);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new MatteBorder(0, 0, 0, 1, App.SKIN.getDefaultBorderColor()));
		panel.add(topBox, BorderLayout.NORTH);

		sessionListPanel = new SessionListPanel(this);
		panel.add(sessionListPanel);

		return panel;

	}

	/**
	 * @param sessionContentPanel
	 */
	public void showSession(SessionContentPanel sessionContentPanel) {
		cardPanel.add(sessionContentPanel, sessionContentPanel.hashCode() + "");
		sessionCard.show(cardPanel, sessionContentPanel.hashCode() + "");
		revalidate();
		repaint();
	}

	/**
	 * @return the sessionListPanel
	 */
	public SessionListPanel getSessionListPanel() {
		return sessionListPanel;
	}

	/**
	 * @param sessionContentPanel
	 */
	public void removeSession(SessionContentPanel sessionContentPanel) {
		cardPanel.remove(sessionContentPanel);
		revalidate();
		repaint();
	}

	private Component createBottomPanel() {
		popup = new JPopupMenu();
		popup.setBorder(new LineBorder(App.SKIN.getDefaultBorderColor(), 1));
		popup.setPreferredSize(new Dimension(250, 300));

		Box b1 = Box.createHorizontalBox();
		b1.setOpaque(true);
		b1.setBackground(App.SKIN.getTableBackgroundColor());
		// b1.setBackground(App.SKIN.getDefaultSelectionBackground());
		b1.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, App.SKIN.getDefaultBorderColor()),
				new EmptyBorder(5, 5, 5, 5)));
		b1.add(Box.createRigidArea(new Dimension(10, 10)));
		// b1.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel lblBrand = new JLabel("Muon SSH 1.0.4");
		// lblBrand.setFont(App.SKIN.getDefaultFont().deriveFont(Font.PLAIN,
		// 14));
		// lblBrand.setForeground(Color.WHITE);
		lblBrand.setVerticalAlignment(JLabel.CENTER);
		b1.add(lblBrand);
		b1.add(Box.createRigidArea(new Dimension(10, 10)));

		JLabel lblUrl = new JLabel("https://github.com/subhra74/snowflake");
		// lblUrl.setFont(App.SKIN.getDefaultFont().deriveFont(Font.PLAIN, 14));
		// lblUrl.setForeground(Color.WHITE);
		// lblUrl.setForeground(App.SKIN.getDefaultSelectionForeground());
		b1.add(lblUrl);

		b1.add(Box.createHorizontalGlue());

		JLabel lblUpload = new JLabel();
		lblUpload.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		// lblUpload.setForeground(Color.WHITE);
		lblUpload.setText(FontAwesomeContants.FA_CLOUD_UPLOAD);
		b1.add(lblUpload);
		b1.add(Box.createRigidArea(new Dimension(5, 10)));
		lblUploadCount = new JLabel("0");
//		lblUploadCount
//				.setFont(App.SKIN.getDefaultFont().deriveFont(Font.PLAIN, 14));
		b1.add(lblUploadCount);

		lblUpload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showPopup(uploadPanel, lblUpload);
			}
		});

		lblUploadCount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showPopup(uploadPanel, lblUpload);
			}
		});

		b1.add(Box.createRigidArea(new Dimension(10, 10)));

		JLabel lblDownload = new JLabel();
		lblDownload.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		// lblDownload.setForeground(Color.WHITE);
		lblDownload.setText(FontAwesomeContants.FA_CLOUD_DOWNLOAD);
		b1.add(lblDownload);
		b1.add(Box.createRigidArea(new Dimension(5, 10)));
		lblDownloadCount = new JLabel("0");
//		lblDownloadCount
//				.setFont(App.SKIN.getDefaultFont().deriveFont(Font.PLAIN, 14));
		b1.add(lblDownloadCount);

		lblDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showPopup(downloadPanel, lblDownload);
			}
		});

		lblDownloadCount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showPopup(downloadPanel, lblDownload);
			}
		});

		b1.add(Box.createRigidArea(new Dimension(30, 10)));

//		JLabel lblBackgroundTransfer = new JLabel("Background transfer");
//		lblBackgroundTransfer
//				.setFont(App.SKIN.getDefaultFont().deriveFont(Font.PLAIN, 14));
//		b1.add(lblBackgroundTransfer);
//		b1.add(Box.createRigidArea(new Dimension(5, 10)));
//		JLabel lblBgToggle = new JLabel();
//		lblBgToggle.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
//		lblBgToggle.setForeground(Color.WHITE);
//		lblBgToggle.setText(FontAwesomeContants.FA_TOGGLE_OFF);
//		b1.add(lblBgToggle);
//		b1.add(Box.createRigidArea(new Dimension(20, 10)));

		JLabel lblHelp = new JLabel();
		lblHelp.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		// lblHelp.setForeground(Color.WHITE);
		lblHelp.setText(FontAwesomeContants.FA_QUESTION_CIRCLE);
		b1.add(lblHelp);
		b1.add(Box.createRigidArea(new Dimension(10, 10)));

		JLabel lblUpdate = new JLabel();
		lblUpdate.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		// lblUpdate.setForeground(Color.WHITE);
		lblUpdate.setText(FontAwesomeContants.FA_REFRESH);
		b1.add(lblUpdate);
		b1.add(Box.createRigidArea(new Dimension(10, 10)));

		return b1;
		// this.setVisible(true);
	}

	public void addUpload(BackgroundFileTransfer transfer) {
		this.uploadPanel.addNewBackgroundTransfer(transfer);
	}

	public void addDownload(BackgroundFileTransfer transfer) {
		this.downloadPanel.addNewBackgroundTransfer(transfer);
	}

	private void showPopup(Component panel, Component invoker) {
		popup.removeAll();
		popup.add(panel);
		popup.setInvoker(bottomPanel);

		// popup.pack();
		popup.show(bottomPanel, bottomPanel.getWidth() - popup.getPreferredSize().width,
				-popup.getPreferredSize().height);
	}

	public void removePendingTransfers(int sessionId) {
		this.uploadPanel.removePendingTransfers(sessionId);
		this.downloadPanel.removePendingTransfers(sessionId);
	}

	public void openSettings(SettingsPageName page) {
		SettingsDialog settingsDialog = new SettingsDialog(this);
		settingsDialog.showDialog(this, page);
	}
}
