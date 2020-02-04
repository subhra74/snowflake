package snowflake.components.taskmgr;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.ssh.SshClient;

import java.io.ByteArrayOutputStream;

public class PlatformChecker {
    public static String getPlatformName(SshClient client) throws Exception {
        ChannelExec exec = client.getExecChannel();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        exec.setOutputStream(bout);
        exec.setCommand("uname");
        exec.connect();
        while (exec.isConnected()) {
            Thread.sleep(500);
        }
        int ret = exec.getExitStatus();
        exec.disconnect();
        if (ret != 0) throw new Exception("Error while running uname");
        return new String(bout.toByteArray(), "utf-8").replace("\n", "");
    }
}
