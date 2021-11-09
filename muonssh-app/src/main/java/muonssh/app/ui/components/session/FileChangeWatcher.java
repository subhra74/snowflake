package muonssh.app.ui.components.session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import muonssh.app.common.FileInfo;

public class FileChangeWatcher {
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final List<FileModificationInfo> filesToWatch = new ArrayList<>();
	private final AtomicBoolean skipMonitoring = new AtomicBoolean(false);
	private final Consumer<List<FileModificationInfo>> callback;
	private final long interval;
	private final AtomicBoolean stopFlag = new AtomicBoolean(false);

	public FileChangeWatcher(Consumer<List<FileModificationInfo>> callback,
			long interval) {
		this.callback = callback;
		this.interval = interval;
	}

	public void addForMonitoring(FileInfo fileInfo, String localFile,
			int activeSessionId) {
		FileModificationInfo item = new FileModificationInfo();
		item.remoteFile = fileInfo;
		item.localFile = new File(localFile);
		item.lastModified = item.localFile.lastModified();
		item.activeSessionId = activeSessionId;
		filesToWatch.add(item);
	}

	private void monitorFileChanges() {
		executor.submit(() -> {
			while (!stopFlag.get()) {
				if (!skipMonitoring.get()) {
					List<FileModificationInfo> list = new ArrayList<>();
					//System.out.println("Watching for changes: " + list);
					for (FileModificationInfo info : filesToWatch) {
						File f = info.localFile;
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
		public FileInfo remoteFile;
		public File localFile;
		public int activeSessionId;
		public long lastModified;

		@Override
		public String toString() {
			if (remoteFile != null)
				return remoteFile.getName();
			return "";
		}
	}

	public void startWatching() {
		monitorFileChanges();
	}

	public void stopWatching() {
		this.skipMonitoring.set(true);
	}

	public void resumeWatching() {
		this.skipMonitoring.set(false);
	}

	public void destroy() {
		stopFlag.set(true);
	}
}
