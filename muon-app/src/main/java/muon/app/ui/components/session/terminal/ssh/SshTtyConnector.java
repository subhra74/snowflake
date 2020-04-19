package muon.app.ui.components.session.terminal.ssh;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jediterm.terminal.Questioner;

import muon.app.App;
import muon.app.ssh.SshClient2;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.PTYMode;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.connection.channel.direct.SessionChannel;
import net.schmizz.sshj.transport.TransportException;

public class SshTtyConnector implements DisposableTtyConnector {
	private InputStreamReader myInputStreamReader;
	private InputStream myInputStream = null;
	private OutputStream myOutputStream = null;
	private SessionChannel shell;
	private Session channel;
	private AtomicBoolean isInitiated = new AtomicBoolean(false);
	private AtomicBoolean isCancelled = new AtomicBoolean(false);
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private Dimension myPendingTermSize;
	private Dimension myPendingPixelSize;
	private SshClient2 wr;
	private String initialCommand;
	private SessionInfo info;
	private SessionContentPanel sessionContentPanel;

	public SshTtyConnector(SessionInfo info, String initialCommand, SessionContentPanel sessionContentPanel) {
		this.initialCommand = initialCommand;
		this.info = info;
		this.sessionContentPanel = sessionContentPanel;
	}

	public SshTtyConnector(SessionInfo info, SessionContentPanel sessionContentPanel) {
		this(info, null, sessionContentPanel);
	}

	@Override
	public boolean init(Questioner q) {
		try {
			this.wr = new SshClient2(this.info, App.getInputBlocker(), sessionContentPanel);
			this.wr.connect();
			this.channel = wr.openSession();
			this.channel.setAutoExpand(true);
			this.channel.allocatePTY(App.getGlobalSettings().getTerminalType(), App.getGlobalSettings().getTermWidth(),
					App.getGlobalSettings().getTermHeight(), 0, 0, Collections.<PTYMode, Integer>emptyMap());
			this.shell = (SessionChannel) this.channel.startShell();

			// String lang = System.getenv().get("LANG");

			// this.channel.setEnvVar("LANG", lang != null ? lang :
			// "en_US.UTF-8");
			// this.channel.
			// channel.setEnv("LANG", lang != null ? lang : "en_US.UTF-8");
			// channel.setPtyType(App.getGlobalSettings().getTerminalType());
			// channel.setPtyType("xterm-256color");

//			PipedOutputStream pout1 = new PipedOutputStream();
//			PipedInputStream pin1 = new PipedInputStream();
//			channel.setOutputStream(pout1);
//
//			PipedOutputStream pout2 = new PipedOutputStream();
//			PipedInputStream pin2 = new PipedInputStream(pout2);
//			channel.setInputStream(pin2);

			myInputStream = shell.getInputStream();// channel.getInputStream();
			myOutputStream = shell.getOutputStream();// channel.getOutputStream();
			myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
			// channel.connect();

			resizeImmediately();
			System.out.println("Initiated");

			if (initialCommand != null) {
				myOutputStream.write((initialCommand + "\n").getBytes("utf-8"));
				myOutputStream.flush();
			}

			// resize(termSize, pixelSize);
			isInitiated.set(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			isInitiated.set(false);
			isCancelled.set(true);
			return false;
		}
	}

	@Override
	public void close() {
		try {
			stopFlag.set(true);
			System.out.println("Terminal wrapper disconnecting");
			wr.disconnect();
		} catch (Exception e) {
		}
	}

	@Override
	public void resize(Dimension termSize, Dimension pixelSize) {
		myPendingTermSize = termSize;
		myPendingPixelSize = pixelSize;
		if (channel != null) {
			resizeImmediately();
		}

//		if (channel == null) {
//			return;
//		}
//		System.out.println("Terminal resized");
//		channel.setPtySize(termSize.width, termSize.height, pixelSize.width, pixelSize.height);
	}

	@Override
	public String getName() {
		return "Remote";
	}

	@Override
	public int read(char[] buf, int offset, int length) throws IOException {
		return myInputStreamReader.read(buf, offset, length);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		myOutputStream.write(bytes);
		myOutputStream.flush();
	}

	@Override
	public boolean isConnected() {
		return channel != null && channel.isOpen() && isInitiated.get();
	}

	@Override
	public void write(String string) throws IOException {
		write(string.getBytes("utf-8"));
	}

	@Override
	public int waitFor() throws InterruptedException {
		System.out.println("Start waiting...");
		while (!isInitiated.get() || isRunning()) {
			System.out.println("waiting");
			Thread.sleep(100); // TODO: remove busy wait
		}
		System.out.println("waiting exit");
		try {
			shell.join();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shell.getExitStatus();
	}

	public boolean isRunning() {
		return shell != null && shell.isOpen();
	}

	public boolean isBusy() {
		return channel.isOpen();
	}

	public boolean isCancelled() {
		return isCancelled.get();
	}

	public void stop() {
		stopFlag.set(true);
		close();
	}

	public int getExitStatus() {
		if (shell != null) {
			Integer exit = shell.getExitStatus();
			return exit == null ? -1 : exit;
		}
		return -2;
	}

	private void resizeImmediately() {
		if (myPendingTermSize != null && myPendingPixelSize != null) {
			setPtySize(shell, myPendingTermSize.width, myPendingTermSize.height, myPendingPixelSize.width,
					myPendingPixelSize.height);
			myPendingTermSize = null;
			myPendingPixelSize = null;
		}
	}

	private void setPtySize(Shell shell, int col, int row, int wp, int hp) {
		System.out.println("Exec pty resized:- col: " + col + " row: " + row + " wp: " + wp + " hp: " + hp);
		if (shell != null) {
			try {
				shell.changeWindowDimensions(col, row, wp, hp);
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// channel.setPtySize(col, row, wp, hp);
	}

	@Override
	public boolean isInitialized() {
		return isInitiated.get();
	}

}