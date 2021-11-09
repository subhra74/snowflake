package muonssh.app.ui.components.session.files.ssh;

import javax.swing.*;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileSystem;
import muonssh.app.common.FileType;
import muonssh.app.ssh.RemoteSessionInstance;
import muonssh.app.ui.components.SkinnedTextField;
import util.PathUtils;
import util.SudoUtils;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshFileOperations {

	public SshFileOperations() {
	}

	public boolean runScriptInBackground(RemoteSessionInstance instance,
			String command, AtomicBoolean stopFlag) throws Exception {
		System.out.println("Invoke command: " + command);
		StringBuilder output = new StringBuilder();
		boolean ret = instance.exec(command, stopFlag, output,
				new StringBuilder()) == 0;
		System.out.println("output: " + output);
		return ret;
	}

	public boolean moveTo(RemoteSessionInstance instance, List<FileInfo> files,
			String targetFolder, FileSystem fs, String password) throws Exception {
		List<FileInfo> fileList = fs.list(targetFolder);
		List<FileInfo> dupList = new ArrayList<>();
		for (FileInfo file : files) {
			for (FileInfo file1 : fileList) {
				if (file.getName().equals(file1.getName())) {
					dupList.add(file);
				}
			}
		}

		int action = -1;
		if (dupList.size() > 0) {
			JComboBox<String> cmbs = new JComboBox<>(
					new String[] { "Auto rename", "Overwrite" });
			if (JOptionPane.showOptionDialog(null, new Object[] {
					"Some file with the same name already exists. Please choose an action",
					cmbs }, "Action required", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.YES_OPTION) {
				action = cmbs.getSelectedIndex();
			} else {
				return false;
			}
		}

		StringBuilder command = new StringBuilder();
		for (FileInfo fileInfo : files) {
			if (fileInfo.getType() == FileType.DirLink
					|| fileInfo.getType() == FileType.Directory) {
				command.append("mv ");
			} else {
				command.append("mv -T ");
			}
			command.append("\"" + fileInfo.getPath() + "\" ");
			if (dupList.contains(fileInfo) && action == 0) {
				command.append("\""
						+ PathUtils.combineUnix(targetFolder,
								getUniqueName(fileList, fileInfo.getName()))
						+ "\"; ");
			} else {
				command.append("\"" + PathUtils.combineUnix(targetFolder,
						fileInfo.getName()) + "\"; ");
			}
		}

		System.out.println("Move: " + command);
		if (instance.exec(command.toString(), new AtomicBoolean(false)) != 0) {
			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}

			if (!App.getGlobalSettings().isPromptForSudo()
					|| JOptionPane.showConfirmDialog(null,
					"Access denied, rename using sudo?", "Use sudo?",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				if (!instance.isSessionClosed()) {
					JOptionPane.showMessageDialog(null, "Operation failed");
				}
				return false;
			}

			int ret = SudoUtils.runSudo(command.toString(), instance, password);
			if (ret != -1) {
				return ret == 0;
			}

			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		} else {
			return true;
		}
		return false;
	}

	public boolean copyTo(RemoteSessionInstance instance, List<FileInfo> files,
			String targetFolder, FileSystem fs, String password) throws Exception {
		List<FileInfo> fileList = fs.list(targetFolder);
		List<FileInfo> dupList = new ArrayList<>();
		for (FileInfo file : files) {
			for (FileInfo file1 : fileList) {
				if (file.getName().equals(file1.getName())) {
					dupList.add(file);
				}
			}
		}

		int action = -1;
		if (dupList.size() > 0) {
			JComboBox<String> cmbs = new JComboBox<>(
					new String[] { "Auto rename", "Overwrite" });
			if (JOptionPane.showOptionDialog(null, new Object[] {
					"Some file with the same name already exists. Please choose an action",
					cmbs }, "Action required", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.YES_OPTION) {
				action = cmbs.getSelectedIndex();
			} else {
				return false;
			}
		}

		StringBuilder command = new StringBuilder();
		for (FileInfo fileInfo : files) {
			if (fileInfo.getType() == FileType.DirLink
					|| fileInfo.getType() == FileType.Directory) {
				command.append("cp -rf ");
			} else {
				command.append("cp -Tf ");
			}
			command.append("\"" + fileInfo.getPath() + "\" ");
			if (dupList.contains(fileInfo) && action == 0) {
				command.append("\""
						+ PathUtils.combineUnix(targetFolder,
								getUniqueName(fileList, fileInfo.getName()))
						+ "\"; ");
			} else {
				command.append("\"" + PathUtils.combineUnix(targetFolder,
						fileInfo.getName()) + "\"; ");
			}
		}

		System.out.println("Copy: " + command);
		if (instance.exec(command.toString(), new AtomicBoolean(false)) != 0) {
			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}
			if (!App.getGlobalSettings().isPromptForSudo()
					|| JOptionPane.showConfirmDialog(null,
					"Access denied, copy using sudo?", "Use sudo?",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				if (!instance.isSessionClosed()) {
					JOptionPane.showMessageDialog(null, "Operation failed");
				}
				return false;
			}

			int ret = SudoUtils.runSudo(command.toString(), instance, password);
			if (ret != -1) {
				return ret == 0;
			}

			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		} else {
			return true;
		}
		return false;
	}

	private String getUniqueName(List<FileInfo> list, String name) {
		while (true) {
			boolean found = false;
			for (FileInfo f : list) {
				if (name.equals(f.getName())) {
					name = "Copy of " + name;
					found = true;
					break;
				}
			}
			if (!found)
				break;
		}
		return name;
	}

	public boolean rename(String oldName, String newName, FileSystem fs,
			RemoteSessionInstance instance, String password) {
		try {
			fs.rename(oldName, newName);
			return true;
		} catch (AccessDeniedException e) {
			e.printStackTrace();

			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}

			if (!App.getGlobalSettings().isPromptForSudo()
					|| JOptionPane.showConfirmDialog(null,
					"Access denied, rename using sudo?", "Use sudo?",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				return renameWithPrivilege(oldName, newName, instance, password);
			}

			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
			return false;
		}
	}


	private boolean renameWithPrivilege(String oldName, String newName,
			RemoteSessionInstance instance, String password) {
		StringBuilder command = new StringBuilder();
		command.append("mv \"" + oldName + "\" \"" + newName + "\"");
		System.out.println("Invoke sudo: " + command);
		int ret = SudoUtils.runSudo(command.toString(), instance, password);
		if (ret == -1) {
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		}
		return ret == 0;
	}

	public boolean delete(FileInfo[] targetList, FileSystem fs,
			RemoteSessionInstance instance, String password) {
		try {
			try {
				delete(Arrays.asList(targetList), instance);
				return true;
			} catch (FileNotFoundException e) {
				System.out.println("delete: file not found");
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				for (FileInfo s : targetList) {
					fs.delete(s);
				}
				return true;
			}
		} catch (FileNotFoundException | AccessDeniedException e) {
			e.printStackTrace();
			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}
			if (!App.getGlobalSettings().isPromptForSudo()
					|| JOptionPane.showConfirmDialog(null,
						"Access denied, delete using sudo?", "Use sudo?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				return deletePrivilege(targetList, instance,password);
			}
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Error deleting file");// JOptionPane.showMessageDialog(null,
																			// "Operation
																			// failed");
			}

			return false;
		}
	}

	private boolean deletePrivilege(FileInfo[] targetList,
			RemoteSessionInstance instance, String password) {
		StringBuilder sb = new StringBuilder("rm -rf ");
		for (FileInfo file : targetList) {
			sb.append("\"" + file.getPath() + "\" ");
		}

		System.out.println("Invoke sudo: " + sb);
		int ret = SudoUtils.runSudo(sb.toString(), instance, password);
		if (ret == -1) {
			JOptionPane.showMessageDialog(null, "Operation failed");
		}
		return ret == 0;
	}

	public boolean newFile(FileInfo[] files, FileSystem fs, String folder,
			RemoteSessionInstance instance, String password) {
		String text = JOptionPane.showInputDialog("New file");
		if (text == null || text.length() < 1) {
			return false;
		}
		boolean alreadyExists = false;
		for (FileInfo f : files) {
			if (f.getName().equals(text)) {
				alreadyExists = true;
				break;
			}
		}
		if (alreadyExists) {
			JOptionPane.showMessageDialog(null,
					"File with same name already exists");
			return false;
		}
		try {
			fs.createFile(PathUtils.combineUnix(folder, text));
			return true;
		} catch (AccessDeniedException e1) {
			e1.printStackTrace();
			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}
			if (!App.getGlobalSettings().isPromptForSudo()
					|| JOptionPane.showConfirmDialog(null,
						"Access denied, new file using sudo?", "Use sudo?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (!touchWithPrivilege(folder, text, instance, password)) {
					if (!instance.isSessionClosed()) {
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
					return false;
				}
				return true;
			}
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}

			return false;
		} catch (Exception e1) {
			e1.printStackTrace();
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		}
		return false;
	}

	private boolean touchWithPrivilege(String path, String newFile,
			RemoteSessionInstance instance, String password) {
		String file = PathUtils.combineUnix(path, newFile);
		StringBuilder command = new StringBuilder();
		command.append("touch \"" + file + "\"");
		System.out.println("Invoke sudo: " + command);
		int ret = SudoUtils.runSudo(command.toString(), instance,password);
		if (ret == -1) {
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		}
		return ret == 0;
	}

	public boolean newFolder(FileInfo[] files, String folder, FileSystem fs,
			RemoteSessionInstance instance, String password) {
		String text = JOptionPane.showInputDialog("New folder name");
		if (text == null || text.length() < 1) {
			return false;
		}
		boolean alreadyExists = false;
		for (FileInfo f : files) {
			if (f.getName().equals(text)) {
				alreadyExists = true;
				break;
			}
		}
		if (alreadyExists) {
			JOptionPane.showMessageDialog(null,
					"File with same name already exists");
			return false;
		}
		try {
			fs.mkdir(PathUtils.combineUnix(folder, text));
			return true;
		} catch (AccessDeniedException e1) {
			e1.printStackTrace();
			if (!App.getGlobalSettings().isUseSudo()){
				JOptionPane.showMessageDialog(null, "Access denied");
				return false;
			}
			if (!App.getGlobalSettings().isPromptForSudo()
					||  JOptionPane.showConfirmDialog(null,
						"Access denied, try using sudo?", "Use sudo?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (!mkdirWithPrivilege(folder, text, instance, password)) {
					if (!instance.isSessionClosed()) {
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
					return false;
				}
				return true;
			}
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
			return false;

		} catch (Exception e1) {
			e1.printStackTrace();
			if (!instance.isSessionClosed()) {
				JOptionPane.showMessageDialog(null, "Operation failed");
			}
		}
		return false;
	}

	private boolean mkdirWithPrivilege(String path, String newFolder,
			RemoteSessionInstance instance, String password) {
		String file = PathUtils.combineUnix(path, newFolder);
		StringBuilder command = new StringBuilder();
		command.append("mkdir \"" + file + "\"");
		System.out.println("Invoke sudo: " + command);
		int ret = SudoUtils.runSudo(command.toString(), instance, password);
		if (ret == -1 && !instance.isSessionClosed()) {
			JOptionPane.showMessageDialog(null, "Operation failed");
		}
		return ret == 0;
	}

	public boolean createLink(FileInfo[] files, FileSystem fs,
			RemoteSessionInstance instance) {
		JTextField txtLinkName = new SkinnedTextField(30);// new
															// JTextField(30);
		JTextField txtFileName = new SkinnedTextField(30);// new
															// JTextField(30);
		JCheckBox chkHardLink = new JCheckBox("Hardlink");

		if (files.length > 0) {
			FileInfo info = files[0];
			txtLinkName.setText(
					PathUtils.combineUnix(PathUtils.getParent(info.getPath()),
							"Link to " + info.getName()));
			txtFileName.setText(info.getPath());
		}

		if (JOptionPane.showOptionDialog(null,
				new Object[] { "Create link", "Link path", txtLinkName,
						"File name", txtFileName, chkHardLink },
				"Create link", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null,
				null) == JOptionPane.OK_OPTION) {
			if (txtLinkName.getText().length() > 0
					&& txtFileName.getText().length() > 0) {
				return createLinkAsync(txtFileName.getText(),
						txtLinkName.getText(), chkHardLink.isSelected(), fs);
			}
		}
		return false;
	}

	private boolean createLinkAsync(String src, String dst, boolean hardLink,
			FileSystem fs) {
		try {
			fs.createLink(src, dst, hardLink);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void delete(List<FileInfo> files,
			RemoteSessionInstance instance) throws Exception {

		StringBuilder sb = new StringBuilder("rm -rf ");

		for (FileInfo file : files) {
			sb.append("\"" + file.getPath() + "\" ");
		}

		System.out.println("Delete command1: " + sb);

		if (instance.exec(sb.toString(), new AtomicBoolean(false)) != 0) {
			throw new FileNotFoundException("Operation failed");
		}
	}

}
