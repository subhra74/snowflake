package muonssh.app.ui.components.session.files.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.*;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileSystem;
import muonssh.app.common.FileType;
import muonssh.app.common.InputTransferChannel;
import muonssh.app.common.OutputTransferChannel;
import muonssh.app.ssh.RemoteSessionInstance;
import muonssh.app.ssh.SSHRemoteFileInputStream;
import muonssh.app.ssh.SSHRemoteFileOutputStream;
import muonssh.app.ssh.SshFileSystem;
import muonssh.app.ui.components.session.files.FileBrowser;
import util.Constants;
import util.Constants.*;
import util.PathUtils;
import util.SudoUtils;

public class FileTransfer implements Runnable, AutoCloseable {
	private final FileSystem sourceFs;
	private final FileSystem targetFs;
	private final FileInfo[] files;
	private final String targetFolder;
	private long totalSize;
	private final AtomicBoolean stopFlag = new AtomicBoolean(false);

	private FileTransferProgress callback;
	private long processedBytes;
	private int processedFilesCount;
	private long totalFiles;
	private Constants.ConflictAction conflictAction = ConflictAction.PROMPT; // 0 -> overwrite, 1 -> auto rename, 2
	// -> skip
	private static final int BUF_SIZE = Short.MAX_VALUE;

	private final RemoteSessionInstance instance;

	/*public enum ConflictAction {
		OverWrite, AutoRename, Skip, Prompt, Cancel
	}

	public enum TransferMode {
		Prompt, Background, Normal
	}*/

	/*public FileTransfer(FileSystem sourceFs, FileSystem targetFs, FileInfo[] files, String targetFolder,
						FileTransferProgress callback, Constants.ConflictAction defaultConflictAction) {
		this.sourceFs = sourceFs;
		this.targetFs = targetFs;
		this.files = files;
		this.targetFolder = targetFolder;
		this.callback = callback;
		this.conflictAction = defaultConflictAction;
		if (defaultConflictAction == Constants.ConflictAction.CANCEL) {
			throw new IllegalArgumentException("defaultConflictAction can not be ConflictAction.Cancel");
		}
	}
*/
	public FileTransfer(FileSystem sourceFs, FileSystem targetFs, FileInfo[] files, String targetFolder,
						FileTransferProgress callback, Constants.ConflictAction defaultConflictAction, RemoteSessionInstance instance) {
		this.sourceFs = sourceFs;
		this.targetFs = targetFs;
		this.files = files;
		this.targetFolder = targetFolder;
		this.callback = callback;
		this.conflictAction = defaultConflictAction;
		this.instance = instance;
		if (defaultConflictAction == Constants.ConflictAction.CANCEL) {
			throw new IllegalArgumentException("defaultConflictAction can not be ConflictAction.Cancel");
		}
	}

	private void transfer(String targetFolder, RemoteSessionInstance instance1) throws Exception {
		System.out.println("Copying to " + targetFolder);
		List<FileInfoHolder> fileList = new ArrayList<>();
		List<FileInfo> list = targetFs.list(targetFolder);
		List<FileInfo> dupList = new ArrayList<>();

		if (this.conflictAction == Constants.ConflictAction.PROMPT) {
			this.conflictAction = checkForConflict(dupList);
			if (dupList.size() > 0 && this.conflictAction == ConflictAction.CANCEL) {
				System.out.println("Operation cancelled by user");
				return;
			}
		}

		totalSize = 0;
		for (FileInfo file : files) {
			if (stopFlag.get()) {
				return;
			}

			String proposedName = null;
			if (isDuplicate(list, file.getName())) {
				if (this.conflictAction == ConflictAction.AUTORENAME) {
					proposedName = generateNewName(list, file.getName());
					System.out.println("new name: " + proposedName);
				} else if (this.conflictAction == ConflictAction.SKIP) {
					continue;
				}
			}

			if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
				fileList.addAll(createFileList(file, targetFolder, proposedName));
			} else {
				fileList.add(new FileInfoHolder(file, targetFolder, proposedName));
				totalSize += file.getSize();
			}
		}
		totalFiles = fileList.size();

		callback.init(totalSize, totalFiles, this);
		InputTransferChannel inc = sourceFs.inputTransferChannel();
		OutputTransferChannel outc = targetFs.outputTransferChannel();
		for (FileInfoHolder file : fileList) {
			System.out.println("Copying: " + file.info.getPath());
			if (stopFlag.get()) {
				System.out.println("Operation cancelled by user");
				return;
			}
			copyFile(file.info, file.targetPath, file.proposedName, inc, outc);
			System.out.println("Copying done: " + file.info.getPath());
			processedFilesCount++;
		}

	}

	public void run() {
		try {
			try {
				transfer(this.targetFolder,instance);
				callback.done(this);
			} catch (AccessDeniedException e) {
				if (targetFs instanceof SshFileSystem) {
					String tmpDir = "/tmp/" + UUID.randomUUID();
					if (App.getGlobalSettings().isTransferTemporaryDirectory()){
						targetFs.mkdir(tmpDir);
						transfer(tmpDir,instance);
						callback.done(this);
						JTextArea tmpFilePath = new JTextArea(5, 20);
						tmpFilePath.setText("Files copied in "+tmpDir + " due to permission issues");
						tmpFilePath.setEnabled(true);
						JOptionPane.showMessageDialog(null, tmpFilePath,"Copied to temp directory", JOptionPane.WARNING_MESSAGE);

						if (!App.getGlobalSettings().isPromptForSudo() ||
								JOptionPane.showConfirmDialog(null,
										"Permission denied, do you want to copy files from the temporary folder to destination with sudo?",
										"Insufficient permission", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							String command = "sh -c  \"cd '" + tmpDir + "'; cp -r * '" + this.targetFolder + "'\"";
							// String command = "sh -c cp -r \"" + tmpDir + "/*\" \"" +
							// this.targetFolder + "\"";
							System.out.println("Invoke sudo: " + command);
							int ret = SudoUtils.runSudo(command,instance);
							if (ret == 0) {
								callback.done(this);
								return;
							}
						}
					}


					throw e;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (stopFlag.get()) {
				System.out.println("Operation cancelled by user");
				callback.done(this);
				return;
			}
			callback.error("Error", this);
			return;
		}
	}

	private List<FileInfoHolder> createFileList(FileInfo folder, String target, String proposedName) throws Exception {
		if (stopFlag.get()) {
			throw new Exception("Interrupted");
		}
		String folderTarget = PathUtils.combineUnix(target, proposedName == null ? folder.getName() : proposedName);
		targetFs.mkdir(folderTarget);
		List<FileInfoHolder> fileInfoHolders = new ArrayList<>();
		List<FileInfo> list = sourceFs.list(folder.getPath());
		for (FileInfo file : list) {
			if (stopFlag.get()) {
				throw new Exception("Interrupted");
			}
			if (file.getType() == FileType.Directory) {
				fileInfoHolders.addAll(createFileList(file, folderTarget, null));
			} else if (file.getType() == FileType.File) {
				fileInfoHolders.add(new FileInfoHolder(file, folderTarget, null));
				totalSize += file.getSize();
			}
		}
		System.out.println("File list created");
		return fileInfoHolders;
	}

	private synchronized void copyFile(FileInfo file, String targetDirectory, String proposedName,
			InputTransferChannel inc, OutputTransferChannel outc) throws Exception {

		String outPath = PathUtils.combine(targetDirectory, proposedName == null ? file.getName() : proposedName,
				outc.getSeparator());
		String inPath = file.getPath();
		System.out.println("Copying -- " + inPath + " to " + outPath);
		try (InputStream in = inc.getInputStream(inPath); OutputStream out = outc.getOutputStream(outPath)) {
			long len = inc.getSize(inPath);
			System.out.println("Initiate write");

			int bufferCapacity = BUF_SIZE;
			if (in instanceof SSHRemoteFileInputStream && out instanceof SSHRemoteFileOutputStream) {
				bufferCapacity = Math.min(((SSHRemoteFileInputStream) in).getBufferCapacity(),
						((SSHRemoteFileOutputStream) out).getBufferCapacity());
			} else if (in instanceof SSHRemoteFileInputStream) {
				bufferCapacity = ((SSHRemoteFileInputStream) in).getBufferCapacity();
			} else if (out instanceof SSHRemoteFileOutputStream) {
				bufferCapacity = ((SSHRemoteFileOutputStream) out).getBufferCapacity();
			}

			byte[] buf = new byte[bufferCapacity];

			while (len > 0 && !stopFlag.get()) {
				int x = in.read(buf);
				if (x == -1)
					throw new IOException("Unexpected EOF");
				out.write(buf, 0, x);
				len -= x;
				processedBytes += x;
				callback.progress(processedBytes, totalSize, processedFilesCount, totalFiles, this);
				// Thread.sleep(500);
			}
			System.out.println("Copy done before stream closing");
			out.flush();
		}
		System.out.println("Copy done");
	}

	public void stop() {
		stopFlag.set(true);
	}

	@Override
	public void close() {
		stopFlag.set(true);
//		try {
//			this.sourceFs.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			this.targetFs.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	static class FileInfoHolder {
		FileInfo info;
		String targetPath;
		String proposedName;

		public FileInfoHolder(FileInfo info, String targetPath, String proposedName) {
			this.info = info;
			this.targetPath = targetPath;
			this.proposedName = proposedName;
		}
	}

	public FileInfo[] getFiles() {
		return files;
	}

	public String getTargetFolder() {
		return this.targetFolder;
	}

	private ConflictAction checkForConflict(List<FileInfo> dupList) throws Exception {
		List<FileInfo> fileList = targetFs.list(targetFolder);
		for (FileInfo file : files) {
			for (FileInfo file1 : fileList) {
				if (file.getName().equals(file1.getName())) {
					dupList.add(file);
				}
			}
		}

		ConflictAction action = ConflictAction.CANCEL;
		if (dupList.size() > 0) {

			DefaultComboBoxModel<Constants.ConflictAction> conflictOptionsCmb = new DefaultComboBoxModel<>(Constants.ConflictAction.values());
			conflictOptionsCmb.removeAllElements();
			for ( Constants.ConflictAction conflictActionCmb : Constants.ConflictAction.values()) {
				if (conflictActionCmb.getKey() <3 ) {
					conflictOptionsCmb.addElement(conflictActionCmb);
				}
			}
			JComboBox<Constants.ConflictAction> cmbs = new JComboBox<>(conflictOptionsCmb);

			if (JOptionPane.showOptionDialog(null,
					new Object[] { "Some file with the same name already exists. Please choose an action", cmbs },
					"Action required", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
					null) == JOptionPane.YES_OPTION) {
				action = (ConflictAction) cmbs.getSelectedItem();
			}
		}

		return action;
	}

	private boolean isDuplicate(List<FileInfo> list, String name) {
		for (FileInfo s : list) {
			System.out.println("Checking for duplicate: " + s.getName() + " --- " + name);
			if (s.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		System.out.println("Not duplicate: " + name);
		return false;
	}

	public String generateNewName(List<FileInfo> list, String name) {
		while (isDuplicate(list, name)) {
			name = "Copy-of-" + name;
		}
		return name;
	}

	public String getSourceName() {
		return this.sourceFs.getName();
	}

	public String getTargetName() {
		return this.targetFs.getName();
	}

	public void setCallback(FileTransferProgress callback) {
		this.callback = callback;
	}

	public FileSystem getSourceFs() {
		return sourceFs;
	}

	public FileSystem getTargetFs() {
		return targetFs;
	}

}
