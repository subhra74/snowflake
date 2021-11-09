/**
 * 
 */
package muonssh.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileSystem;
import muonssh.app.common.local.LocalFileSystem;
import muonssh.app.ssh.CachedCredentialProvider;
import muonssh.app.ssh.PortForwardingSession;
import muonssh.app.ssh.RemoteSessionInstance;
import muonssh.app.ssh.SshFileSystem;
import muonssh.app.ui.components.DisabledPanel;
import muonssh.app.ui.components.session.diskspace.DiskspaceAnalyzer;
import muonssh.app.ui.components.session.files.FileBrowser;
import muonssh.app.ui.components.session.files.transfer.BackgroundFileTransfer;
import muonssh.app.ui.components.session.files.transfer.FileTransfer;
import muonssh.app.ui.components.session.files.transfer.TransferProgressPanel;
import muonssh.app.ui.components.session.logviewer.LogViewer;
import muonssh.app.ui.components.session.processview.ProcessViewer;
import muonssh.app.ui.components.session.search.SearchPanel;
import muonssh.app.ui.components.session.terminal.TerminalHolder;
import muonssh.app.ui.components.session.utilpage.UtilityPage;
import util.Constants;
import util.LayoutUtilities;

/**
 * @author subhro
 *
 */
public class SessionContentPanel extends JPanel implements PageHolder, CachedCredentialProvider {
	private final SessionInfo info;
	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private RemoteSessionInstance remoteSessionInstance;
	private final JRootPane rootPane;
	private final JPanel contentPane;
	private final DisabledPanel disabledPanel;
	private final TransferProgressPanel progressPanel = new TransferProgressPanel();
	public final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
	private final TabbedPage[] pages;
	private final FileBrowser fileBrowser;
	private final LogViewer logViewer;
	private final TerminalHolder terminalHolder;
	private final DiskspaceAnalyzer diskspaceAnalyzer;
	private final SearchPanel searchPanel;
	private final ProcessViewer processViewer;
	private final UtilityPage utilityPage;
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final Deque<RemoteSessionInstance> cachedSessions = new LinkedList<>();
	private ThreadPoolExecutor backgroundTransferPool;
	private char[] cachedPassword;
	private char[] cachedPassPhrase;
	private String cachedUser;
	private PortForwardingSession pfSession;

	/**
	 * 
	 */
	public SessionContentPanel(SessionInfo info) {
		super(new BorderLayout());
		this.info = info;
		this.disabledPanel = new DisabledPanel();
		this.remoteSessionInstance = new RemoteSessionInstance(info, App.getInputBlocker(), this);
		Box contentTabs = Box.createHorizontalBox();
		contentTabs.setBorder(new MatteBorder(0, 0, 1, 0, App.SKIN.getDefaultBorderColor()));

//		String names[] = { "File browser", "Log viewer", "Terminal",
//				"File search", "Utilities" };

//		String icons[] = { FontAwesomeContants.FA_FOLDER_OPEN_O,
//				FontAwesomeContants.FA_STICKY_NOTE_O,
//				FontAwesomeContants.FA_TELEVISION,
//				FontAwesomeContants.FA_SEARCH, FontAwesomeContants.FA_SLIDERS };
		fileBrowser = new FileBrowser(info, this, null, this.hashCode());
		logViewer = new LogViewer(this);
		terminalHolder = new TerminalHolder(info, this);
		diskspaceAnalyzer = new DiskspaceAnalyzer(this);
		searchPanel = new SearchPanel(this);
		processViewer = new ProcessViewer(this);
		utilityPage = new UtilityPage(this);

		Page[] pageArr = null;
		if (App.getGlobalSettings().isFirstFileBrowserView()){
			pageArr = new Page[] { fileBrowser, terminalHolder , logViewer, searchPanel, diskspaceAnalyzer,
					processViewer, utilityPage };
		} else {
			pageArr = new Page[] { terminalHolder, fileBrowser , logViewer, searchPanel, diskspaceAnalyzer,
					processViewer, utilityPage };
		}



//		JPanel[] panels = {
//				new FileBrowser(info, new AtomicBoolean(), this, null,
//						this.hashCode()),
//				new JPanel(), new JPanel(), new JPanel(), new JPanel() };

		this.cardLayout = new CardLayout();
		this.cardPanel = new JPanel(this.cardLayout);

		this.pages = new TabbedPage[pageArr.length];
		for (int i = 0; i < pageArr.length; i++) {
			TabbedPage tabbedPage = new TabbedPage(pageArr[i], this);
			this.pages[i] = tabbedPage;
			this.cardPanel.add(tabbedPage.getPage(), tabbedPage.getId());
			pageArr[i].putClientProperty("pageId", tabbedPage.getId());
		}

		LayoutUtilities.equalizeSize(this.pages);

		for (TabbedPage item : this.pages) {
			contentTabs.add(item);
		}

		contentTabs.add(Box.createHorizontalGlue());

		this.contentPane = new JPanel(new BorderLayout(), true);
		this.contentPane.add(contentTabs, BorderLayout.NORTH);
		this.contentPane.add(this.cardPanel);

		this.rootPane = new JRootPane();
		this.rootPane.setContentPane(this.contentPane);

		this.add(this.rootPane);

		showPage(this.pages[0].getId());

		if (info.getPortForwardingRules() != null && info.getPortForwardingRules().size() > 0) {
			this.pfSession = new PortForwardingSession(info, App.getInputBlocker(), this);
			this.pfSession.start();
		}
	}
	
	public void reconnect() {
		this.remoteSessionInstance.close();
		this.remoteSessionInstance = new RemoteSessionInstance(info, App.getInputBlocker(), this);
	}

	@Override
	public void showPage(String pageId) {
		TabbedPage selectedPage = null;
		for (TabbedPage item : this.pages) {
			if (pageId.equals(item.getId())) {
				selectedPage = item;
			}
			item.setSelected(false);
		}
		selectedPage.setSelected(true);
		this.cardLayout.show(this.cardPanel, pageId);
		this.revalidate();
		this.repaint();
		selectedPage.getPage().onLoad();
	}

	/**
	 * @return the info
	 */
	public SessionInfo getInfo() {
		return info;
	}

	/**
	 * @return the remoteSessionInstance
	 */
	public RemoteSessionInstance getRemoteSessionInstance() {
		return remoteSessionInstance;
	}

	public void disableUi() {
		SwingUtilities.invokeLater(() -> {
			this.disabledPanel.startAnimation(null);
			// this.disabledPanel.btn.setVisible(false);
			this.rootPane.setGlassPane(this.disabledPanel);
			this.disabledPanel.setVisible(true);
		});
	}

	public void disableUi(AtomicBoolean stopFlag) {
		SwingUtilities.invokeLater(() -> {
			this.disabledPanel.startAnimation(stopFlag);
			// this.disabledPanel.btn.setVisible(true);
			this.rootPane.setGlassPane(this.disabledPanel);
			System.out.println("Showing disable panel");
			this.disabledPanel.setVisible(true);
		});
	}

	public void enableUi() {
		SwingUtilities.invokeLater(() -> {
			this.disabledPanel.stopAnimation();
			System.out.println("Hiding disable panel");
			this.disabledPanel.setVisible(false);
		});
	}

	public void startFileTransferModal(Consumer<Boolean> stopCallback) {
		progressPanel.setStopCallback(stopCallback);
		progressPanel.clear();
		this.rootPane.setGlassPane(this.progressPanel);
		progressPanel.setVisible(true);
		this.revalidate();
		this.repaint();
	}

	public void setTransferProgress(int progress) {
		progressPanel.setProgress(progress);
	}

	public void endFileTransfer() {
		progressPanel.setVisible(false);
		this.revalidate();
		this.repaint();
	}

	public int getActiveSessionId() {
		return this.hashCode();
	}

	public void downloadFileToLocal(FileInfo remoteFile, Consumer<File> callback) {

	}

	public void openLog(FileInfo remoteFile) {
		showPage(this.logViewer.getClientProperty("pageId") + "");
		logViewer.openLog(remoteFile);
	}

	public void openFileInBrowser(String path) {
		showPage(this.fileBrowser.getClientProperty("pageId") + "");
		fileBrowser.openPath(path);
	}

	public void openTerminal(String command) {
		showPage(this.terminalHolder.getClientProperty("pageId") + "");
		this.terminalHolder.openNewTerminal(command);
	}

	/**
	 * @return the closed
	 */
	public boolean isSessionClosed() {
		return closed.get();
	}

	public void close() {
		this.closed.set(true);
		try {
			this.terminalHolder.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		App.removePendingTransfers(this.getActiveSessionId());
		if (this.backgroundTransferPool != null) {
			this.backgroundTransferPool.shutdownNow();
		}

		EXECUTOR.submit(() -> {
			try {
				this.backgroundTransferPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				this.remoteSessionInstance.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				this.cachedSessions.forEach(c -> c.close());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		});
		EXECUTOR.shutdown();
		
		if (this.pfSession != null) {
			this.pfSession.close();
		}
	}

	public void uploadInBackground(FileInfo[] localFiles, String targetRemoteDirectory, Constants.ConflictAction confiAction) {
		RemoteSessionInstance instance = createBackgroundSession();
		FileSystem sourceFs = new LocalFileSystem();
		FileSystem targetFs = instance.getSshFs();
		FileTransfer transfer = new FileTransfer(sourceFs, targetFs, localFiles, targetRemoteDirectory, null,
				confiAction,instance);
		App.addUpload(new BackgroundFileTransfer(transfer, instance, this));
	}

	public void downloadInBackground(FileInfo[] remoteFiles, String targetLocalDirectory, Constants.ConflictAction confiAction) {
		FileSystem targetFs = new LocalFileSystem();
		RemoteSessionInstance instance = createBackgroundSession();
		SshFileSystem sourceFs = instance.getSshFs();
		FileTransfer transfer = new FileTransfer(sourceFs, targetFs, remoteFiles, targetLocalDirectory, null,
				confiAction,instance);
		App.addDownload(new BackgroundFileTransfer(transfer, instance, this));
	}

	public synchronized ThreadPoolExecutor getBackgroundTransferPool() {
		if (this.backgroundTransferPool == null) {
			this.backgroundTransferPool = new ThreadPoolExecutor(
					App.getGlobalSettings().getBackgroundTransferQueueSize(),
					App.getGlobalSettings().getBackgroundTransferQueueSize(), 0, TimeUnit.NANOSECONDS,
					new LinkedBlockingQueue<Runnable>());
		} else {
			if (this.backgroundTransferPool.getMaximumPoolSize() != App.getGlobalSettings()
					.getBackgroundTransferQueueSize()) {
				this.backgroundTransferPool
						.setMaximumPoolSize(App.getGlobalSettings().getBackgroundTransferQueueSize());
			}
		}
		return this.backgroundTransferPool;
	}

	public synchronized RemoteSessionInstance createBackgroundSession() {
		if (this.cachedSessions.size() == 0) {
			return new RemoteSessionInstance(info, App.getInputBlocker(), this);
		}
		return this.cachedSessions.pop();
	}

	public synchronized void addToSessionCache(RemoteSessionInstance session) {
		this.cachedSessions.push(session);
	}

	@Override
	public synchronized char[] getCachedPassword() {
		return cachedPassword;
	}

	@Override
	public synchronized void cachePassword(char[] password) {
		this.cachedPassword = password;
	}

	@Override
	public synchronized char[] getCachedPassPhrase() {
		return cachedPassPhrase;
	}

	@Override
	public synchronized void setCachedPassPhrase(char[] cachedPassPhrase) {
		this.cachedPassPhrase = cachedPassPhrase;
	}

	@Override
	public synchronized String getCachedUser() {
		return cachedUser;
	}

	@Override
	public synchronized void setCachedUser(String cachedUser) {
		this.cachedUser = cachedUser;
	}

}
