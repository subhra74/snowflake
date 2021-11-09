package muonssh.app.ui.components.session.terminal.ssh;

import com.jediterm.terminal.TtyConnector;

public interface DisposableTtyConnector extends TtyConnector {
	void stop();

	boolean isCancelled();

	boolean isBusy();

	boolean isRunning();

	int getExitStatus();

	boolean isInitialized();
}
