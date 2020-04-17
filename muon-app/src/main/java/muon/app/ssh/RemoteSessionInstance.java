/**
 * 
 */
package muon.app.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import muon.app.ui.components.session.SessionInfo;
import net.schmizz.sshj.connection.channel.direct.PTYMode;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

/**
 * @author subhro
 *
 */
public class RemoteSessionInstance {
	private SshClient2 ssh;
	private SshFileSystem sshFs;
	private AtomicBoolean closed = new AtomicBoolean(false);

	public RemoteSessionInstance(SessionInfo info, InputBlocker inputBlocker) {
		this.ssh = new SshClient2(info, inputBlocker);
		this.sshFs = new SshFileSystem(this.ssh);
	}

	public int exec(String command, Function<Command, Integer> callback, boolean pty) throws Exception {
		synchronized (this.ssh) {
			if (this.closed.get()) {
				throw new OperationCancelledException();
			}
			try {
				if (!ssh.isConnected()) {
					ssh.connect();
				}
				try (Session session = ssh.openSession()) {
					session.setAutoExpand(true);
					if (pty) {
						session.allocatePTY("vt100", 80, 24, 0, 0, Collections.<PTYMode, Integer>emptyMap());
					}
					try (final Command cmd = session.exec(command)) {
						return callback.apply(cmd);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return 1;
		}
	}

	public int exec(String command, AtomicBoolean stopFlag) throws Exception {
		return exec(command, stopFlag, null, null);
	}

	public int exec(String command, AtomicBoolean stopFlag, StringBuilder output) throws Exception {
		return exec(command, stopFlag, output, null);
	}

	public int exec(String command, AtomicBoolean stopFlag, StringBuilder output, StringBuilder error)
			throws Exception {
		ByteArrayOutputStream bout = output == null ? null : new ByteArrayOutputStream();
		ByteArrayOutputStream berr = error == null ? null : new ByteArrayOutputStream();
		int ret = execBin(command, stopFlag, bout, berr);
		if (output != null) {
			try {
				output.append(new String(bout.toByteArray(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (error != null) {
			try {
				error.append(new String(berr.toByteArray(), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public int execBin(String command, AtomicBoolean stopFlag, OutputStream bout, OutputStream berr) throws Exception {
		synchronized (this.ssh) {
			if (this.closed.get()) {
				throw new OperationCancelledException();
			}
			System.out.println(Thread.currentThread().getName());
			System.out.println(command);
			if (stopFlag.get()) {
				return -1;
			}
			try {
				if (!ssh.isConnected()) {
					ssh.connect();
				}
				try (Session session = ssh.openSession()) {
					session.setAutoExpand(true);
//				session.allocatePTY(App.getGlobalSettings().getTerminalType(),
//						80, 24, 0, 0, Collections.<PTYMode, Integer>emptyMap());
					try (final Command cmd = session.exec(command)) {
						System.out.println("Command and Session started");

						InputStream in = cmd.getInputStream();
						InputStream err = cmd.getErrorStream();

						byte[] b = new byte[8192];

						do {
							if (stopFlag.get()) {
								System.out.println("stopflag");
								break;
							}

							// System.out.println(in.available() + " " +
							// err.available());
							if (in.available() > 0) {
								int m = in.available();
								while (m > 0) {
									int x = in.read(b, 0, m > b.length ? b.length : m);
									if (x == -1) {
										break;
									}
									m -= x;
									if (bout != null) {
										bout.write(b, 0, x);
									}

								}
							}

							if (err.available() > 0) {
								int m = err.available();
								while (m > 0) {
									int x = err.read(b, 0, m > b.length ? b.length : m);
									if (x == -1) {
										break;
									}
									m -= x;
									if (berr != null) {
										berr.write(b, 0, x);
									}

								}
							}

//						x = err.read(b);
//						if (x > 0) {
//							berr.write(b, 0, x);
//						}

							// Thread.sleep(500);
						} while (cmd.isOpen());

						System.out.println(cmd.isOpen() + " " + cmd.isEOF() + " " + cmd.getExitStatus());
						// System.out.println((char)in.read());

						// System.out.println(output + " " + error);

						System.out.println("Command and Session closed");

						cmd.close();
						return cmd.getExitStatus();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 1;
		}
	}

	/**
	 * 
	 */
	public void close() {
		try {
			this.closed.set(true);
			try {
				this.sshFs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.ssh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the sshFs
	 */
	public SshFileSystem getSshFs() {
		return sshFs;

	}

	public boolean isSessionClosed() {
		return closed.get();
	}

}
