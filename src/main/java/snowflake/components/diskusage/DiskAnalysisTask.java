package snowflake.components.diskusage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import snowflake.common.ssh.RemoteSessionInstance;
import snowflake.utils.ScriptLoader;
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
					"export POSIXLY_CORRECT=1\n" + "export FOLDER='" + folder
							+ "'\n");
			scriptBuffer.append(ScriptLoader.loadShellScript("/du.sh"));
			StringBuilder output = new StringBuilder();
			if (client.exec(scriptBuffer.toString(), stopFlag, output) == 0) {
				System.out.println("output\n" + output);
				List<String> lines = Arrays
						.asList(output.toString().split("\n"));
				DuOutputParser duOutputParser = new DuOutputParser(folder);
				int prefixLen = folder.endsWith("/") ? folder.length() - 1
						: folder.length();
				root = duOutputParser.parseList(lines, prefixLen);
			}
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
