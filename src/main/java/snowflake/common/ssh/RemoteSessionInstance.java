/**
 * 
 */
package snowflake.common.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import net.schmizz.sshj.connection.channel.direct.PTYMode;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import snowflake.components.newsession.SessionInfo;

/**
 * @author subhro
 *
 */
public class RemoteSessionInstance {
	private SshClient2 ssh;

	public RemoteSessionInstance(SessionInfo info) {
		this.ssh = new SshClient2(info);
	}

	public synchronized int exec(String command,
			Function<Command, Integer> callback, boolean pty) {
		try {
			if (!ssh.isConnected()) {
				ssh.connect();
			}
			try (Session session = ssh.openSession()) {
				session.setAutoExpand(true);
				if (pty) {
					session.allocatePTY("vt100", 80, 24, 0, 0,
							Collections.<PTYMode, Integer>emptyMap());
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

	public synchronized int exec(String command, AtomicBoolean stopFlag,
			StringBuilder output, StringBuilder error) {
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

					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					ByteArrayOutputStream berr = new ByteArrayOutputStream();

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
								int x = in.read(b, 0,
										m > b.length ? b.length : m);
								if (x == -1) {
									break;
								}
								m -= x;
								bout.write(b, 0, x);
							}
						}

						if (err.available() > 0) {
							int m = err.available();
							while (m > 0) {
								int x = err.read(b, 0,
										m > b.length ? b.length : m);
								if (x == -1) {
									break;
								}
								m -= x;
								berr.write(b, 0, x);
							}
						}

//						x = err.read(b);
//						if (x > 0) {
//							berr.write(b, 0, x);
//						}

						// Thread.sleep(500);
					} while (cmd.isOpen());

					System.out.println(cmd.isOpen() + " " + cmd.isEOF() + " "
							+ cmd.getExitStatus());
					// System.out.println((char)in.read());

					output.append(new String(bout.toByteArray(), "utf-8"));
					error.append(new String(berr.toByteArray(), "utf-8"));

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

	/**
	 * 
	 */
	public void close() {
		try {
			this.ssh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
