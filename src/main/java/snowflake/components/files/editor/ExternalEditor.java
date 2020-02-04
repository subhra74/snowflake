package snowflake.components.files.editor;

import snowflake.common.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ExternalEditor  implements AutoCloseable{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<FileModificationInfo> filesToWatch = new ArrayList<>();
    private AtomicBoolean skipMonitoring = new AtomicBoolean(false);
    private Consumer<List<FileModificationInfo>> callback;
    private long interval;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);

    public ExternalEditor(Consumer<List<FileModificationInfo>> callback, long interval) {
        this.callback = callback;
        this.interval = interval;
    }

    public void addForMonitoring(FileInfo fileInfo, String localFile, int activeSessionId) {
        FileModificationInfo item = new FileModificationInfo();
        item.fileInfo = fileInfo;
        item.file = new File(localFile);
        item.lastModified = item.file.lastModified();
        item.activeSessionId = activeSessionId;
        filesToWatch.add(item);
    }

    public void startWatchingForChanges() {
        monitorFileChanges();
    }

    private void monitorFileChanges() {
        executor.submit(() -> {
            while (!stopFlag.get()) {
                if (!skipMonitoring.get()) {
                    List<FileModificationInfo> list = new ArrayList<>();
                    for (FileModificationInfo info : filesToWatch) {
                        File f = info.file;
                        long modified = f.lastModified();
                        if (modified > info.lastModified) {
                            list.add(info);
                            info.lastModified = modified;
                        }
                    }
                    if (list.size() > 0) {
                        callback.accept(list);
                    }
                }
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class FileModificationInfo {
        public FileInfo fileInfo;
        public File file;
        public int activeSessionId;
        public long lastModified;
    }

    public void setSkipMonitoring() {
        this.skipMonitoring.set(true);
    }

    public void resumeMonitoring() {
        this.skipMonitoring.set(false);
    }

    public void close() {
        stopFlag.set(true);
    }
}
