package snowflake.components.files.transfer;

import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTransferPanel extends JPanel  implements AutoCloseable{
    private ExecutorService executorService =
            Executors.newFixedThreadPool(App.getGlobalSettings().getNumberOfSimultaneousConnection());
    private Box verticalBox;

    public BackgroundTransferPanel() {
        super(new BorderLayout());
        verticalBox = Box.createVerticalBox();
        JScrollPane jsp = new JScrollPane(verticalBox);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(jsp);
    }

    public void addNewBackgroundTransfer(FileTransfer transfer) {
        TransferPanelItem item = new TransferPanelItem(transfer);
        item.setAlignmentX(Box.LEFT_ALIGNMENT);
        this.verticalBox.add(item);
        this.verticalBox.revalidate();
        this.verticalBox.repaint();
        executorService.submit(() -> {
            try (transfer) {
                transfer.run();
            }
        });
    }

    public void close() {
        for (Component c : this.verticalBox.getComponents()) {
            if (c instanceof TransferPanelItem) {
                try {
                    ((TransferPanelItem) c).stop();
                } catch (Exception e) {

                }
            }
        }
    }

    class TransferPanelItem extends JPanel implements FileTransferProgress {
        private FileTransfer fileTransfer;
        private JProgressBar progressBar;
        private JLabel progressLabel;
        private JLabel removeLabel;

        public void stop() {
            fileTransfer.stop();
        }

        public TransferPanelItem(FileTransfer transfer) {
            super(new BorderLayout());
            this.fileTransfer = transfer;
            transfer.setCallback(this);
            progressBar = new JProgressBar();
            progressLabel = new JLabel("Connecting...");
            removeLabel = new JLabel();
            removeLabel.setFont(App.getFontAwesomeFont());
            removeLabel.setText("\uf00d");

            removeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fileTransfer.stop();
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
        public void init(String sourceName, String targetName, long totalSize, long files, FileTransfer fileTransfer) {
            SwingUtilities.invokeLater(() -> {
                progressLabel.setText(String.format("Copying file from %s to %s",
                        fileTransfer.getSourceName(), fileTransfer.getTargetName()));
                progressBar.setValue(0);
            });
        }

        @Override
        public void progress(long processedBytes, long totalBytes, long processedCount, long totalCount, FileTransfer fileTransfer) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(totalBytes > 0 ? ((int) ((processedBytes * 100) / totalBytes)) : 0);
            });
        }

        @Override
        public void error(String cause, FileTransfer fileTransfer) {
            SwingUtilities.invokeLater(() -> {
                progressLabel.setText(String.format("Error while copying from %s to %s",
                        fileTransfer.getSourceName(), fileTransfer.getTargetName()));
            });
        }

        @Override
        public void done(FileTransfer fileTransfer) {
            System.out.println("done transfer");
            SwingUtilities.invokeLater(() -> {
                BackgroundTransferPanel.this.verticalBox.remove(this);
                BackgroundTransferPanel.this.revalidate();
                BackgroundTransferPanel.this.repaint();
            });
        }
    }
}
