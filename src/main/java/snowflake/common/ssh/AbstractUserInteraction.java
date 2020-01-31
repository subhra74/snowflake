package snowflake.common.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import snowflake.App;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.GraphicsUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractUserInteraction
		implements UserInfo, UIKeyboardInteractive {
	private static Map<String, String> passwordMap = new ConcurrentHashMap<>();
	private static Map<String, String> passphraseMap = new ConcurrentHashMap<>();
	protected SessionInfo info;
	private JPasswordField password = new JPasswordField(30);
	private static AtomicBoolean confirmYes = new AtomicBoolean(false);
	private static AtomicBoolean suppressMessage = new AtomicBoolean(false);
	private AtomicLong attempt = new AtomicLong(0);
	private JPanel panel;

	public AbstractUserInteraction(SessionInfo info) {
		this.info = info;
	}

	public SessionInfo getInfo() {
		return this.info;
	}

	@Override
	public String[] promptKeyboardInteractive(String destination, String name,
			String instruction, String[] prompt, boolean[] echo) {
		if (attempt.get() == 0) {
			if (prompt.length == 1 && prompt[0] != null
					&& prompt[0].toLowerCase().startsWith("password")
					&& info.getPassword() != null) {
				System.out.println(
						"Keyboard interactive - Assuming password is being asked for");
				return new String[] { info.getPassword() };
			}
		}

		attempt.incrementAndGet();

		List<JComponent> list = new ArrayList<>();
		list.add(new JLabel(destination));
		list.add(new JLabel(name));
		list.add(new JLabel(instruction));

		int i = 0;
		for (String s : Arrays.asList(prompt)) {
			System.out.println(s);
			list.add(new JLabel(s));
			if (echo[i++]) {
				JTextField txt = GraphicsUtils.createTextField(30);// new
																	// JTextField(30);
				list.add(txt);
			} else {
				JPasswordField pass = new JPasswordField(30);
				list.add(pass);
			}
		}

		if (showModal(list, true)) {
			List<String> responses = new ArrayList<>();
			for (Object obj : list) {
				if (obj instanceof JPasswordField) {
					responses.add(
							new String(((JPasswordField) obj).getPassword()));
				} else if (obj instanceof JTextField) {
					responses.add(((JTextField) obj).getText());
				}
			}

			String[] arr1 = new String[responses.size()];
			responses.toArray(arr1);
			return arr1;
		}

		return null;
	}

	@Override
	public void showMessage(String message) {
		System.out.println("showMessage: " + message);
		if (!App.getGlobalSettings().isShowMessagePrompt()) {
			return;
		}
		if (!suppressMessage.get()) {
			JCheckBox chkHideWarn = new JCheckBox("Hide warnings");
			chkHideWarn.setSelected(true);
			JTextArea txtMsg = GraphicsUtils.createTextArea();
			txtMsg.setEditable(false);
			txtMsg.setText(message);
			JScrollPane jsp = new JScrollPane(txtMsg);
			jsp.setPreferredSize(new Dimension(600, 300));
			jsp.setBorder(new LineBorder(
					UIManager.getColor("DefaultBorder.color"), 1));

			List<JComponent> list = new ArrayList<>();
			list.add(jsp);
			list.add(chkHideWarn);

			showModal(list, false);

			if (chkHideWarn.isSelected()) {
				suppressMessage.set(true);
				App.getGlobalSettings().setShowMessagePrompt(false);
				App.saveSettings();
			}
		}
	}

	@Override
	public boolean promptYesNo(String message) {
		System.out.println("promptYesNo: " + message);
		if (confirmYes.get()) {
			return true;
		}

		if (showModal(Arrays.asList(new JLabel(message)), true)) {
			if (!confirmYes.get()) {
				confirmYes.set(true);
			}
			return true;
		}
		return false;
		// return true;
	}

	@Override
	public boolean promptPassword(String message) {
		System.out.println("promptPassword: " + message);
		if (attempt.get() == 0
				&& SshUserInteraction
						.getPreEnteredPassword(info.getId()) != null
				&& SshUserInteraction.getPreEnteredPassword(info.getId())
						.length() > 0) {
			return true;
		}

		attempt.getAndIncrement();
		password.setText("");

		System.out.println("Showing modal for password");

		if (showModal(Arrays.asList(new JLabel(message), password), true)) {
			SshUserInteraction.setPreEnteredPassword(info.getId(),
					new String(password.getPassword()));
			return true;
		}

		return false;
	}

	@Override
	public boolean promptPassphrase(String message) {
		System.out.println("prompt Passphrase: " + message);
		if (attempt.get() == 0
				&& SshUserInteraction
						.getPreEnteredPassphrase(info.getId()) != null
				&& SshUserInteraction.getPreEnteredPassphrase(info.getId())
						.length() > 0) {
			return true;
		}
		attempt.getAndIncrement();
		password.setText("");

		if (showModal(Arrays.asList(new JLabel(message), password), true)) {
			SshUserInteraction.setPreEnteredPassphrase(info.getId(),
					new String(password.getPassword()));
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		System.out.println("getPassword");
		return SshUserInteraction.getPreEnteredPassword(info.getId());
	}

	@Override
	public String getPassphrase() {
		System.out.println("getPassphrase");
		return SshUserInteraction.getPreEnteredPassphrase(info.getId());
	}

	public static synchronized String getPreEnteredPassword(String id) {
		return passwordMap.get(id);
	}

	public static synchronized void setPreEnteredPassword(String id,
			String preEnteredPassword) {
		passwordMap.put(id, preEnteredPassword);
	}

	public static synchronized String getPreEnteredPassphrase(String id) {
		return passphraseMap.get(id);
	}

	public static synchronized void setPreEnteredPassphrase(String id,
			String preEnteredPassphrase) {
		passphraseMap.put(id, preEnteredPassphrase);
	}

	protected abstract boolean showModal(List<JComponent> components,
			boolean yesNo);
}
