package muon.screens.appwin.tabs.filebrowser.transfer.foreground;

import muon.dto.file.FileInfo;
import muon.service.SftpUploadTask;
import muon.util.AppUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TransferDialog extends JDialog {
    private TransferConfirmPanel transferConfirmPanel;
    private TransferProgressPanel transferProgressPanel;
    private TransferRetryPanel transferRetryPanel;
    private Consumer<Boolean> result;
    private SftpUploadTask sftpUploadTask;

    public TransferDialog(SftpUploadTask sftpUploadTask, Consumer<Boolean> result, Window window) {
        super(window);
        this.result = result;
        setModal(true);
        setTitle("File transfer");
        setSize(420, 300);
        setLocationRelativeTo(getOwner());
        getContentPane().setLayout(new CardLayout());
        createUI();
        setSftpUploadTask(sftpUploadTask);
        showConfirm();
    }

    private void setSftpUploadTask(SftpUploadTask sftpUploadTask) {
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

    private void showConfirm() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "TransferConfirmPanel");
        transferConfirmPanel.setFocus();
    }

    private void showProgress() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "TransferProgressPanel");
        setSize(320, 200);
        setLocationRelativeTo(getOwner());
    }

    private void showError() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "TransferRetryPanel");
        setSize(320, 300);
        setLocationRelativeTo(getOwner());
    }

    private void transfer() {
        AppUtils.runAsync(() -> {
            try {
                if (!sftpUploadTask.isConnected()) {
                    sftpUploadTask.connect();
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
                showError();
            }
        });
    }

    private void createUI() {
        transferConfirmPanel = new TransferConfirmPanel(e -> transfer(), e -> result.accept(false));
        transferRetryPanel = new TransferRetryPanel(null, null);
        transferProgressPanel = new TransferProgressPanel(null);
        getContentPane().add(transferConfirmPanel, "TransferConfirmPanel");
        getContentPane().add(transferProgressPanel, "TransferProgressPanel");
        getContentPane().add(transferRetryPanel, "TransferRetryPanel");
    }


}
