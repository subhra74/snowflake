package snowflake.components.terminal.ssh;

import com.jcraft.jsch.Channel;
import com.jediterm.terminal.TtyConnector;

public interface DisposableTtyConnector extends TtyConnector {
    public void stop();

    public boolean isCancelled();

    public boolean isBusy();

    public boolean isRunning(Channel channel);

    public int getExitStatus();

    public boolean isInitialized();
}
