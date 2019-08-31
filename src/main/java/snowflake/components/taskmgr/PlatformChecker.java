package snowflake.components.taskmgr;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.ssh.SshClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

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
        exec.disconnect();
        return new String(bout.toByteArray(), "utf-8");
    }
}
