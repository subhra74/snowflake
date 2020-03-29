package snowflake.components.sysinfo.platforms.linux;

import snowflake.common.ssh.RemoteSessionInstance;
//import snowflake.common.ssh.SshClient;
import snowflake.components.sysinfo.ServicePanel;
import snowflake.components.sysinfo.SocketPanel;
import snowflake.components.sysinfo.platforms.SystemInfo;
import snowflake.utils.ScriptLoader;
//import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class LinuxSysInfo {
	public static SystemInfo retrieveSystemInfo(RemoteSessionInstance client,
			AtomicBoolean stopFlag) throws Exception {
		SystemInfo systemInfo = new SystemInfo();
		StringBuilder output = new StringBuilder();
//        if (SshCommandUtils.exec(client, ScriptLoader.loadShellScript("/linux-system-info.sh"), stopFlag, output)) {
//            systemInfo.setSystemOverview(output.toString());
//        }
//        output = new StringBuilder();
//        if (SshCommandUtils.exec(client, ServicePanel.SYSTEMD_COMMAND, stopFlag, output)) {
//            systemInfo.setServices(ServicePanel.parseServiceEntries(output));
//        }
//        output = new StringBuilder();
//        if (SshCommandUtils.exec(client, SocketPanel.LSOF_COMMAND, stopFlag, output)) {
//            systemInfo.setSockets(SocketPanel.parseSocketList(output.toString()));
//        }
		return systemInfo;
	}

	public static boolean runCommandWithSudo(RemoteSessionInstance client,
			AtomicBoolean stopFlag, String command) throws Exception {
		// StringBuilder output = new StringBuilder();
		return SudoUtils.runSudo(command, client) == 0;
	}

	public static boolean runCommand(RemoteSessionInstance client,
			AtomicBoolean stopFlag, String command) throws Exception {
		StringBuilder output = new StringBuilder();
		return client.exec(command, new AtomicBoolean(false), output) == 0;
	}
}
