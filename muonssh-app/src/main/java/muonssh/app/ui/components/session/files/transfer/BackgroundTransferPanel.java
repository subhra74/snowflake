package muonssh.app.ui.components.session.files.transfer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import muonssh.app.App;
import util.FontAwesomeContants;

public class BackgroundTransferPanel extends JPanel {
	private final Box verticalBox;
	private final AtomicInteger transferCount = new AtomicInteger(0);
	private final Consumer<Integer> callback;

	/**
	 * @param callback callback for notifying number of active transfers
	 */
	public BackgroundTransferPanel(Consumer<Integer> callback) {
		super(new BorderLayout());
		this.callback = callback;
		verticalBox = Box.createVerticalBox();
		JScrollPane jsp = new JScrollPane(verticalBox);
		jsp.setBorder(null);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(jsp);
	}

	public void addNewBackgroundTransfer(BackgroundFileTransfer transfer) {
		TransferPanelItem item = new TransferPanelItem(transfer);
		item.setAlignmentX(Box.LEFT_ALIGNMENT);
		this.verticalBox.add(item);
		this.verticalBox.revalidate();
		this.verticalBox.repaint();
		item.handle = transfer.getSession().getBackgroundTransferPool().submit(() -> {
			try {
				transfer.getFileTransfer().run();
				transfer.getSession().addToSessionCache(transfer.getInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void removePendingTransfers(int sessionId) {
		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(() -> {
					stopSession(sessionId);
				});
			} catch (InvocationTargetException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			stopSession(sessionId);
		}
	}

	private void stopSession(int sessionId) {
		for (int i = 0; i < this.verticalBox.getComponentCount(); i++) {
			Component c = this.verticalBox.getComponent(i);
			if (c instanceof TransferPanelItem) {
				TransferPanelItem tpi = (TransferPanelItem) c;
				if (tpi.fileTransfer.getSession().getActiveSessionId() == sessionId) {
					tpi.stop();
				}
			}
		}
	}

//	public void close() {
//		for (Component c : this.verticalBox.getComponents()) {
//			if (c instanceof TransferPanelItem) {
//				try {
//					((TransferPanelItem) c).stop();
//				} catch (Exception e) {
//
//				}
//			}
//		}
//	}

	class TransferPanelItem extends JPanel implements FileTransferProgress {
		private final BackgroundFileTransfer fileTransfer;
		private final JProgressBar progressBar;
		private final JLabel progressLabel;
		private Future<?> handle;

		public void stop() {
			fileTransfer.getFileTransfer().stop();
			this.handle.cancel(false);
		}

		public TransferPanelItem(BackgroundFileTransfer transfer) {
			super(new BorderLayout());
			transferCount.incrementAndGet();
			callback.accept(transferCount.get());
			this.fileTransfer = transfer;
			transfer.getFileTransfer().setCallback(this);
			progressBar = new JProgressBar();
			progressLabel = new JLabel("Waiting...");
			progressLabel.setBorder(new EmptyBorder(5, 0, 5, 5));
			JLabel removeLabel = new JLabel();
			removeLabel.setFont(App.SKIN.getIconFont());
			removeLabel.setText(FontAwesomeContants.FA_TRASH);

			removeLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					fileTransfer.getFileTransfer().stop();
				}
			});

			setBorder(new EmptyBorder(10, 10, 10, 10));
			Box topBox = Box.createHorizontalBox();
			topBox.add(progressLabel);
			topBox.add(Box.createHorizontalGlue());
			topBox.add(removeLabel);

			add(topBox);
			add(progressBar, BorderLayout.SOUTH);

			setMaximumSize(new Dimension(getMaximumSize().width, getPreferredSize().height));
		}

		@Override
		public void init(long totalSize, long files, FileTransfer fileTransfer) {
			SwingUtilities.invokeLater(() -> {
				progressLabel.setText(
						String.format("Copying %s to %s", fileTransfer.getSourceName(), fileTransfer.getTargetName()));
				progressBar.setValue(0);
			});
		}

		@Override
		public void progress(long processedBytes, long totalBytes, long processedCount, long totalCount,
				FileTransfer fileTransfer) {
			SwingUtilities.invokeLater(() -> {
				progressBar.setValue(totalBytes > 0 ? ((int) ((processedBytes * 100) / totalBytes)) : 0);
			});
		}

		@Override
		public void error(String cause, FileTransfer fileTransfer) {
			transferCount.decrementAndGet();
			callback.accept(transferCount.get());
			SwingUtilities.invokeLater(() -> {
				progressLabel.setText(String.format("Error while copying from %s to %s", fileTransfer.getSourceName(),
						fileTransfer.getTargetName()));
			});
		}

		@Override
		public void done(FileTransfer fileTransfer) {
			transferCount.decrementAndGet();
			callback.accept(transferCount.get());
			this.fileTransfer.getSession().addToSessionCache(this.fileTransfer.getInstance());
			System.out.println("done transfer");
			SwingUtilities.invokeLater(() -> {
				BackgroundTransferPanel.this.verticalBox.remove(this);
				BackgroundTransferPanel.this.revalidate();
				BackgroundTransferPanel.this.repaint();
			});
		}
	}
}
