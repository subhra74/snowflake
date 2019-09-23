package snowflake.components.diskusage;

import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.utils.ScriptLoader;
import snowflake.utils.SshCommandUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DiskAnalysisTask implements Runnable {
    private SshUserInteraction source;
    private SshClient client;
    private String folder;
    private AtomicBoolean stopFlag;
    private Consumer<DiskUsageEntry> callback;

    public DiskAnalysisTask(String folder, AtomicBoolean stopFlag,
                            Consumer<DiskUsageEntry> callback,
                            SshUserInteraction source) {
        this.source = source;
        this.callback = callback;
        this.folder = folder;
        this.stopFlag = stopFlag;
    }

    public void run() {
        DiskUsageEntry root = null;
        try {
            this.client = new SshClient(source);
            this.client.connect();
            StringBuilder scriptBuffer = new StringBuilder("export POSIXLY_CORRECT=1\n" +
                    "export FOLDER='" + folder + "'\n");
            scriptBuffer.append(ScriptLoader.loadShellScript("/du.sh"));
            StringBuilder output = new StringBuilder();
            if (SshCommandUtils.exec(client, scriptBuffer.toString(), stopFlag, output)) {
                System.out.println("output\n" + output);
                List<String> lines = Arrays.asList(output.toString().split("\n"));
                DuOutputParser duOutputParser = new DuOutputParser(folder);
                int prefixLen = folder.endsWith("/") ? folder.length() - 1 : folder.length();
                root = duOutputParser.parseList(lines, prefixLen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callback.accept(root);
            try {
                this.client.disconnect();
            } catch (Exception e) {
            }
        }
    }
}
