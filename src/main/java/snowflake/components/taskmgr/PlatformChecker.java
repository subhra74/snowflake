package snowflake.components.taskmgr;

import java.util.concurrent.atomic.AtomicBoolean;

import snowflake.common.ssh.RemoteSessionInstance;

public class PlatformChecker {
	public static String getPlatformName(RemoteSessionInstance instance)
			throws Exception {
		StringBuilder out = new StringBuilder(), err = new StringBuilder();
		int ret = instance.exec("uname", new AtomicBoolean(false), out, err);
		if (ret != 0)
			throw new Exception("Error while running uname");
		return out.toString().trim();
	}
}
