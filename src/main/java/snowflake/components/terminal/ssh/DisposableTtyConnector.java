package snowflake.components.terminal.ssh;

import com.jediterm.terminal.TtyConnector;

import net.schmizz.sshj.connection.channel.direct.Session.Shell;

public interface DisposableTtyConnector extends TtyConnector {
	public void stop();

	public boolean isCancelled();

	public boolean isBusy();

	public boolean isRunning();

	public int getExitStatus();

	public boolean isInitialized();
}
