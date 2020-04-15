package util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import muon.app.ssh.RemoteSessionInstance;

//import snowflake.common.ssh.SshClient;

public class SudoUtils {
	private static JPasswordField passwordField = new JPasswordField(30);

//	public static int runSudo(String command, SshClient sshClient) {
//		StringBuilder output = new StringBuilder();
//		return runSudo(command, sshClient, output);
//	}
//
//	public static int runSudo(String command, SshClient sshClient,
//			StringBuilder output) {
//		String prompt = UUID.randomUUID().toString();
//		try {
//			ChannelExec exec = sshClient.getExecChannel();
//			String fullCommand = "sudo -S -p '" + prompt + "' " + command;
//			System.out.println(
//					"Full sudo: " + fullCommand + " prompt: " + prompt);
//			exec.setCommand(fullCommand);
//			exec.setPty(true);
//			PipedInputStream pin = new PipedInputStream();
//			PipedOutputStream put = new PipedOutputStream(pin);
//			// InputStream in = exec.getInputStream();
//			OutputStream out = exec.getOutputStream();
//			exec.setErrStream(put);
//			exec.setOutputStream(put);
//			exec.connect();
//			StringBuilder sb = new StringBuilder();
//			while (true) {
//				int x = pin.read();
//				if (x == -1)
//					break;
//				char ch = (char) x;
//				sb.append(ch);
//				output.append(ch);
//				// System.out.println(sb);
//				if (sb.toString().contains(prompt)) {
//					if (JOptionPane.showOptionDialog(null,
//							new Object[] { "Root password", passwordField },
//							"Authentication", JOptionPane.OK_CANCEL_OPTION,
//							JOptionPane.PLAIN_MESSAGE, null, null,
//							null) == JOptionPane.OK_OPTION) {
//						sb = new StringBuilder();
//						out.write(
//								(new String(passwordField.getPassword()) + "\n")
//										.getBytes());
//						out.flush();
//					} else {
//						exec.disconnect();
//						pin.close();
//						out.close();
//						return -2;
//					}
//				}
//			}
//			if (exec.getExitStatus() == -1) {
//				while (exec.isConnected()) {
//					Thread.sleep(500);
//				}
//			}
//			pin.close();
//			out.close();
//			return exec.getExitStatus();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return -1;
//		}
//	}

//	public static int runSudo(String command, RemoteSessionInstance instance) {
//		return runSudo(command, instance, new StringBuilder(),
//				new StringBuilder());
//	}

	public static int runSudo(String command, RemoteSessionInstance instance) {
		String prompt = UUID.randomUUID().toString();
		try {
			String fullCommand = "sudo -S -p '" + prompt + "' " + command;
			System.out.println(
					"Full sudo: " + fullCommand + "\nprompt: " + prompt);
			int ret = instance.exec(fullCommand, cmd -> {
				try {
					InputStream in = cmd.getInputStream();
					OutputStream out = cmd.getOutputStream();
					StringBuilder sb = new StringBuilder();
					Reader r = new InputStreamReader(in,
							Charset.forName("utf-8"));

					char[] b = new char[8192];

					while (cmd.isOpen()) {
						int x = r.read(b);
						if (x > 0) {
							sb.append(b, 0, x);
						}

						System.out.println("buffer: " + sb);
						if (sb.indexOf(prompt) != -1) {
							if (JOptionPane.showOptionDialog(null,
									new Object[] { "Root password",
											passwordField },
									"Authentication",
									JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, null,
									null) == JOptionPane.OK_OPTION) {
								sb = new StringBuilder();
								out.write(
										(new String(passwordField.getPassword())
												+ "\n").getBytes());
								out.flush();
							} else {
								cmd.close();
								return -2;
							}
						}
						Thread.sleep(50);
					}
					cmd.join();
					cmd.close();
					return cmd.getExitStatus();
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			}, true);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int runSudoWithOutput(String command,
			RemoteSessionInstance instance, StringBuilder output,
			StringBuilder error) {
		String prompt = UUID.randomUUID().toString();
		try {
			String fullCommand = "sudo -S -p '" + prompt + "' " + command;
			System.out.println(
					"Full sudo: " + fullCommand + "\nprompt: " + prompt);
			int ret = instance.exec(fullCommand, cmd -> {
				try {
					InputStream in = cmd.getInputStream();
					// InputStream err = cmd.getErrorStream();
					OutputStream out = cmd.getOutputStream();
					StringBuilder sb = new StringBuilder();
//					System.out.println(
//							"Window buffer: " + cmd.getRemoteWinSize());

					// byte[] b = new byte[(int) cmd.getRemoteMaxPacketSize()];

					Reader r = new InputStreamReader(in,
							Charset.forName("utf-8"));

					while (true) {
						int ch = r.read();
						if (ch == -1)
							break;
						sb.append((char) ch);
						output.append((char) ch);

						System.out.println("buffer: " + sb);
						if (sb.indexOf(prompt) != -1) {
							if (JOptionPane.showOptionDialog(null,
									new Object[] { "Root password",
											passwordField },
									"Authentication",
									JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.PLAIN_MESSAGE, null, null,
									null) == JOptionPane.OK_OPTION) {
								sb = new StringBuilder();
								out.write(
										(new String(passwordField.getPassword())
												+ "\n").getBytes());
								out.flush();
							} else {
								cmd.close();
								return -2;
							}
						}

						// Thread.sleep(50);
					}
					cmd.join();
					cmd.close();
//					if (in.available() > 0) {
//						int x = r.read(b);
//						if (x > 0) {
//							output.append(b, 0, x);
//						}
//					}
					return cmd.getExitStatus();
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			}, true);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
