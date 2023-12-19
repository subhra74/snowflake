package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.dto.file.FileInfo;
import muon.exceptions.FSAccessException;
import muon.service.GuiUserAuthFactory;
import muon.service.SftpUploadTask;
import muon.service.SshCallback;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.widgets.InputBlockerPanel;
import muon.widgets.InteractivePromptPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForegroundTransferProgressPanel extends JLayeredPane {
    private InputBlockerPanel inputBlockerPanel;
    private JPanel contentPanel;
    private TransferConfirmPanel transferConfirmPanel;
    private TransferProgressPanel transferProgressPanel;
    private TransferRetryPanel transferRetryPanel;
    private SftpUploadTask sftpUploadTask;
    private Consumer<Boolean> result;

    public ForegroundTransferProgressPanel(Consumer<Boolean> result) {
        this.result = result;

        inputBlockerPanel = new InputBlockerPanel(
                e -> {
                },
                e -> {
                    inputBlockerPanel.unblockInput();
                    result.accept(false);
                });
        inputBlockerPanel.setVisible(false);

        createContentPanel();

        this.add(contentPanel, Integer.valueOf(1));
        this.add(inputBlockerPanel, Integer.valueOf(2));

        setBackground(AppTheme.INSTANCE.getBackground());

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentPanel.setBounds(0, 0, getWidth(), getHeight());
                inputBlockerPanel.setBounds(0, 0, getWidth(), getHeight());
                revalidate();
                repaint();
            }
        });
    }

    public void setSftpUploadTask(SftpUploadTask sftpUploadTask) {
        this.sftpUploadTask = sftpUploadTask;
        this.transferProgressPanel.setProgress(0);
        var folderCount = this.sftpUploadTask.getLocalFiles().stream().filter(FileInfo::isDirectory).count();
        var fileCount = this.sftpUploadTask.getLocalFiles().size() - folderCount;
        var texts = new ArrayList<String>();
        texts.add("Transfer");
        if (folderCount > 0) {
            texts.add(folderCount + " folder" + (folderCount > 1 ? "s" : ""));
        }
        if (fileCount > 0) {
            if (folderCount > 0) {
                texts.add("and");
            }
            texts.add(fileCount + " file" + (fileCount > 1 ? "s" : ""));
        }
        var remoteFileNames = sftpUploadTask.getRemoteFiles().stream().map(FileInfo::getName).collect(Collectors.toSet());
        var conflictingFiles = sftpUploadTask.getLocalFiles().stream().filter(
                f -> remoteFileNames.contains(f.getName())).toList();
        this.transferConfirmPanel.setFileInfo(String.format(String.join(" ", texts)),
                this.sftpUploadTask.getRemoteFolder(), conflictingFiles);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new CardLayout());
        transferConfirmPanel = new TransferConfirmPanel(e -> transfer(), e -> result.accept(false));
        transferRetryPanel = new TransferRetryPanel(null, null);
        transferProgressPanel = new TransferProgressPanel(null);
        contentPanel.add(transferConfirmPanel, "TransferConfirmPanel");
        contentPanel.add(transferProgressPanel, "TransferProgressPanel");
        contentPanel.add(transferRetryPanel, "TransferRetryPanel");
    }

    public void showPrompt() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "InteractivePromptPanel");
    }

    public void showConfirm() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "TransferConfirmPanel");
        transferConfirmPanel.setFocus();
    }

    public void showProgress() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "TransferProgressPanel");
    }

    public void showRetry() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "TransferRetryPanel");
    }

    private void transfer() {
        AppUtils.runAsync(() -> {
            try {
                if (!sftpUploadTask.isConnected()) {
                    SwingUtilities.invokeAndWait(inputBlockerPanel::blockInput);
                    sftpUploadTask.connect(inputBlockerPanel);
                    SwingUtilities.invokeAndWait(inputBlockerPanel::unblockInput);
                }
                SwingUtilities.invokeAndWait(this::showProgress);
                sftpUploadTask.start(prg -> {
                    SwingUtilities.invokeLater(() -> {
                        transferProgressPanel.setProgress(prg);
                    });
                });
                result.accept(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                inputBlockerPanel.showRetryOption();
            }
        });
    }
}
