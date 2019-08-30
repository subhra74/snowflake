package snowflake.components.files.editor;

import snowflake.common.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ExternalEditor {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Map<FileInfo, File> fileMap = new ConcurrentHashMap<>();
    private Map<FileInfo, Long> fileUpdateMap = new ConcurrentHashMap<>();
    private AtomicBoolean skipMonitoring = new AtomicBoolean(false);
    private Consumer<List<FileModificationInfo>> callback;
    private long interval;

    public ExternalEditor(Consumer<List<FileModificationInfo>> callback, long interval) {
        this.callback = callback;
        this.interval = interval;
    }

    public void startWatchingForChanges() {
        monitorFileChanges();
    }

    private void monitorFileChanges() {
        executor.submit(() -> {
            while (true) {
                if (!skipMonitoring.get()) {
                    List<FileModificationInfo> list = new ArrayList<>();
                    for (FileInfo info : fileMap.keySet()) {
                        File f = fileMap.get(info);
                        long modified = f.lastModified();
                        long lastModified = fileUpdateMap.get(info);
                        if (modified > lastModified) {
                            FileModificationInfo fileModificationInfo = new FileModificationInfo();
                            fileModificationInfo.fileInfo = info;
                            fileModificationInfo.file = f;
                            list.add(fileModificationInfo);
                            fileUpdateMap.put(info, modified);
                        }
                    }
                    if (list.size() > 0) {
                        callback.accept(list);
                    }
                }
                Thread.sleep(interval);
            }
        });
    }

    public static class FileModificationInfo {
        public FileInfo fileInfo;
        public File file;
    }
}
