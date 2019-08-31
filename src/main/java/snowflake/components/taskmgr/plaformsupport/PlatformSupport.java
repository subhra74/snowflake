package snowflake.components.taskmgr.plaformsupport;

import snowflake.common.ssh.SshClient;

public interface PlatformSupport {
    public void updateMetrics(SshClient client);
}
