package muon.app.ui.components.session.terminal.ssh;

import com.jediterm.terminal.TtyConnector;

public interface DisposableTtyConnector extends TtyConnector {
	public void stop();

	public boolean isCancelled();

	public boolean isBusy();

	public boolean isRunning();

	public int getExitStatus();

	public boolean isInitialized();
}
