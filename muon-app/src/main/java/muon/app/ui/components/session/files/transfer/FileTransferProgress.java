package muon.app.ui.components.session.files.transfer;

public interface FileTransferProgress {
    void init(long totalSize, long files, FileTransfer fileTransfer);

    void progress(long processedBytes, long totalBytes, long processedCount, long totalCount, FileTransfer fileTransfer);

    void error(String cause, FileTransfer fileTransfer);

    void done(FileTransfer fileTransfer);
}
