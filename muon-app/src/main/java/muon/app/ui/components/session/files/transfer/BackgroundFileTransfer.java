package muon.app.ui.components.session.files.transfer;

import muon.app.ssh.RemoteSessionInstance;
import muon.app.ui.components.session.SessionContentPanel;

public class BackgroundFileTransfer {
	private FileTransfer fileTransfer;
	private RemoteSessionInstance instance;
	private SessionContentPanel session;

	public BackgroundFileTransfer(FileTransfer fileTransfer, RemoteSessionInstance instance,
			SessionContentPanel session) {
		super();
		this.fileTransfer = fileTransfer;
		this.instance = instance;
		this.session = session;
	}

	public FileTransfer getFileTransfer() {
		return fileTransfer;
	}

	public void setFileTransfer(FileTransfer fileTransfer) {
		this.fileTransfer = fileTransfer;
	}

	public RemoteSessionInstance getInstance() {
		return instance;
	}

	public void setInstance(RemoteSessionInstance instance) {
		this.instance = instance;
	}

	public SessionContentPanel getSession() {
		return session;
	}

	public void setSession(SessionContentPanel session) {
		this.session = session;
	}
}
