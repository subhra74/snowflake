package util;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.filechooser.FileSystemView;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.win32.W32FileMonitor;

public final class Win32DragHandler {
	private FileMonitor fileMonitor = new W32FileMonitor();

	public synchronized void listenForDrop(String keyToListen, Consumer<File> callback) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		for (File drive : File.listRoots()) {
			if (fsv.isDrive(drive)) {
				try {
					System.out.println("Adding to watch: " + drive.getAbsolutePath());
					fileMonitor.addWatch(drive, W32FileMonitor.FILE_RENAMED | W32FileMonitor.FILE_CREATED, true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		fileMonitor.addFileListener(e -> {
			File file = e.getFile();
			System.err.println(file);
			if (file.getName().startsWith(keyToListen)) {
				callback.accept(file);
			}
		});
	}

	public synchronized void dispose() {
		System.out.println("File watcher disposed");
		this.fileMonitor.dispose();
	}

//	public final static synchronized void initFSWatcher() {
//
//		FileMonitor fileMonitor = new W32FileMonitor();
//		fileMonitor.addFileListener(e -> {
//			System.out.println(e.getFile());
//		});
//		FileSystemView fsv = FileSystemView.getFileSystemView();
//		for (File drive : File.listRoots()) {
//			if (fsv.isDrive(drive)) {
//				try {
//					System.out.println("Adding to watch: " + drive.getAbsolutePath());
//					fileMonitor.addWatch(drive, W32FileMonitor.FILE_RENAMED, true);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}
