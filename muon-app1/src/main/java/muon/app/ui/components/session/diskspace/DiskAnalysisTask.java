package muon.app.ui.components.session.diskspace;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import muon.app.ssh.RemoteSessionInstance;

//import snowflake.utils.SshCommandUtils;

public class DiskAnalysisTask implements Runnable {
	// private SshUserInteraction source;
	private RemoteSessionInstance client;
	private String folder;
	private AtomicBoolean stopFlag;
	private Consumer<DiskUsageEntry> callback;

	public DiskAnalysisTask(String folder, AtomicBoolean stopFlag,
			Consumer<DiskUsageEntry> callback, RemoteSessionInstance client) {
		this.callback = callback;
		this.folder = folder;
		this.stopFlag = stopFlag;
		this.client = client;
	}

	public void run() {
		DiskUsageEntry root = null;
		try {
			StringBuilder scriptBuffer = new StringBuilder(
					"export POSIXLY_CORRECT=1; " + "du '" + folder + "'");
			StringBuilder output = new StringBuilder();
			client.exec(scriptBuffer.toString(), stopFlag, output);
			List<String> lines = Arrays.asList(output.toString().split("\n"));
			DuOutputParser duOutputParser = new DuOutputParser(folder);
			int prefixLen = folder.endsWith("/") ? folder.length() - 1
					: folder.length();
			root = duOutputParser.parseList(lines, prefixLen);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			callback.accept(root);
		}
	}

	public void close() {
//		try {
//			client.close();
//		} catch (Exception e) {
//
//		}
	}
}
