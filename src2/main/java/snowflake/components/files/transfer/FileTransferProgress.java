package snowflake.components.files.transfer;

public interface FileTransferProgress {
    void init(String sourceName, String targetName, long totalSize, long files);

    void progress(long processedBytes, long totalBytes, long processedCount, long totalCount);

    void error(String cause);

    void done();
}
