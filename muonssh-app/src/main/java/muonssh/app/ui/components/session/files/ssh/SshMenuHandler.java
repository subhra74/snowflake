package muonssh.app.ui.components.session.files.ssh;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import muonssh.app.App;
import muonssh.app.common.FileInfo;
import muonssh.app.common.FileType;
import muonssh.app.common.local.LocalFileSystem;
import muonssh.app.ui.components.session.BookmarkManager;
import muonssh.app.ui.components.session.files.FileBrowser;
import muonssh.app.ui.components.session.files.remote2remote.LocalPipeTransfer;
import muonssh.app.ui.components.session.files.remote2remote.Remote2RemoteTransferDialog;
import muonssh.app.ui.components.session.files.view.DndTransferData;
import muonssh.app.ui.components.session.files.view.DndTransferHandler;
import muonssh.app.ui.components.session.files.view.FolderView;
import muonssh.app.ui.components.settings.EditorEntry;
import muonssh.app.ui.components.settings.SettingsPageName;
import util.PathUtils;

import static muonssh.app.App.bundle;

public class SshMenuHandler {
	private AbstractAction aOpenInTab, aOpen, aRename, aDelete, aNewFile, aNewFolder, aCopy, aPaste, aCut, aAddToFav,
			aChangePerm, aSendFiles, aUpload, aDownload, aCreateLink, aCopyPath;

	private KeyStroke ksOpenInTab, ksOpen, ksRename, ksDelete, ksNewFile, ksNewFolder, ksCopy, ksPaste, ksCut,
			ksAddToFav, ksChangePerm, ksSendFiles, ksUpload, ksDownload, ksCreateLink, ksCopyPath;

	private JMenuItem mOpenInTab, mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy, mPaste, mCut, mAddToFav,
			mChangePerm, mSendFiles, mUpload, mOpenWithDefApp, mOpenWthInternalEdit, mEditorConfig, mOpenWithLogView,
			mDownload, mCreateLink, mCopyPath, mOpenFolderInTerminal, mOpenTerminalHere, mRunScriptInTerminal,
			mRunScriptInBackground, mExtractHere, mExtractTo, mCreateArchive, mOpenWithMenu;

	private JMenu mEditWith, mSendTo;
	private Map<String, String> extractCommands;
	private final FileBrowser fileBrowser;
	private FolderView folderView;
	private final SshFileOperations fileOperations;
	private final SshFileBrowserView fileBrowserView;
	private final ArchiveOperation archiveOperation;

	public SshMenuHandler(FileBrowser fileBrowser, SshFileBrowserView fileBrowserView) {
		this.fileBrowser = fileBrowser;
		this.fileOperations = new SshFileOperations();
		this.fileBrowserView = fileBrowserView;
		this.archiveOperation = new ArchiveOperation();
	}

	public void initMenuHandler(FolderView folderView) {
		this.folderView = folderView;
		InputMap map = folderView.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = folderView.getActionMap();
		this.initMenuItems(map, act);
	}

	private void initMenuItems(InputMap map, ActionMap act) {
		ksOpenInTab = KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK);
		mOpenInTab = new JMenuItem(bundle.getString("open_in_tab"));
		mOpenInTab.setAccelerator(ksOpenInTab);
		aOpenInTab = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewTab();
			}
		};
		mOpenInTab.addActionListener(aOpenInTab);
		map.put(ksOpenInTab, "ksOpenInTab");
		act.put("ksOpenInTab", aOpenInTab);

		aOpen = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open app");
				FileInfo fileInfo = folderView.getSelectedFiles()[0];
				try {
					App.getExternalEditorHandler().openRemoteFile(fileInfo, fileBrowser.getSSHFileSystem(),
							fileBrowser.getActiveSessionId(), false, null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// holder.openWithDefaultApp(fileInfo);
			}
		};
		ksOpen = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		mOpen = new JMenuItem(bundle.getString("open"));
		mOpen.addActionListener(aOpen);
		map.put(ksOpen, "mOpen");
		act.put("mOpen", aOpen);
		mOpen.setAccelerator(ksOpen);

//		mOpenWithDefApp = new JMenuItem("Default application");
//		mOpenWithDefApp.addActionListener(e -> openDefaultApp());

		if (App.IS_WINDOWS) {
			mOpenWithMenu = new JMenuItem(bundle.getString("open_with"));
			mOpenWithMenu.addActionListener(e -> {
				FileInfo fileInfo = folderView.getSelectedFiles()[0];
				try {
					System.out.println("Called open with");
					App.getExternalEditorHandler().openRemoteFile(fileInfo, fileBrowser.getSSHFileSystem(),
							fileBrowser.getActiveSessionId(), true, null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
		}

//		mOpenWthInternalEdit = new JMenuItem("Internal editor");
//		mOpenWthInternalEdit.addActionListener(e -> openWithInternalEditor());

		mEditorConfig = new JMenuItem(bundle.getString("configure_editor"));
		mEditorConfig.addActionListener(e -> openEditorConfig());

		mOpenWithLogView = new JMenuItem(bundle.getString("open_log_viewer"));
		mOpenWithLogView.addActionListener(e -> openLogViewer());

		mEditWith = new JMenu(bundle.getString("edit_with"));
//		for (EditorEntry ent : App.getGlobalSettings().getEditors()) {
//			JMenuItem mEditorItem = new JMenuItem(ent.getName());
//			mEditorItem.addActionListener(e -> openWithEditor(ent.getPath()));
//			mEditWith.add(mEditorItem);
//		}
//		mEditWith.add(mEditorConfig);

		mSendTo = new JMenu(bundle.getString("send_another_server"));

		JMenuItem mSendViaSSH = new JMenuItem(bundle.getString("send_over_ftp"));
		mSendViaSSH.addActionListener(e -> {
			this.sendFilesViaSSH();
		});
		JMenuItem mSendViaLocal = new JMenuItem(bundle.getString("send_this_computer"));
		mSendViaLocal.addActionListener(e -> {
			this.sendFilesViaLocal();
		});

		mSendTo.add(mSendViaSSH);
		mSendTo.add(mSendViaLocal);

//		mEditWith.add(mOpenWithDefApp);
//		mEditWith.add(mOpenWithCustom);
//		mEditWith.add(mOpenWthInternalEdit);
//		mEditWith.add(mOpenWithLogView);

		mRunScriptInTerminal = new JMenuItem(bundle.getString("run_in_terminal"));
		mRunScriptInTerminal.addActionListener(e -> {

		});

		mOpenFolderInTerminal = new JMenuItem(bundle.getString("open_folder_terminal"));
		mOpenFolderInTerminal.addActionListener(e -> {
			openFolderInTerminal(folderView.getSelectedFiles()[0].getPath());
		});

		mOpenTerminalHere = new JMenuItem(bundle.getString("open_terminal_here"));
		mOpenTerminalHere.addActionListener(e -> {
			openFolderInTerminal(fileBrowserView.getCurrentDirectory());
		});

		mRunScriptInTerminal = new JMenuItem(bundle.getString("run_file_in_terminal"));
		mRunScriptInTerminal.addActionListener(e -> {
			openRunInTerminal(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles()[0].getPath());
		});

		mRunScriptInBackground = new JMenuItem(bundle.getString("run_file_in_background"));
		mRunScriptInBackground.addActionListener(e -> {
			openRunInBackground(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles()[0].getPath());
		});

		aRename = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rename(folderView.getSelectedFiles()[0], fileBrowserView.getCurrentDirectory());
			}
		};
		ksRename = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
		mRename = new JMenuItem(bundle.getString("rename"));
		mRename.addActionListener(aRename);
		map.put(ksRename, "mRename");
		act.put("mRename", aRename);
		mRename.setAccelerator(ksRename);

		ksDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		aDelete = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete(folderView.getSelectedFiles(), fileBrowserView.getCurrentDirectory());
			}
		};
		mDelete = new JMenuItem(bundle.getString("delete"));
		mDelete.addActionListener(aDelete);
		map.put(ksDelete, "ksDelete");
		act.put("ksDelete", aDelete);
		mDelete.setAccelerator(ksDelete);

		ksNewFile = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aNewFile = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile(fileBrowserView.getCurrentDirectory(), folderView.getFiles());
			}
		};
		mNewFile = new JMenuItem(bundle.getString("new_file"));
		mNewFile.addActionListener(aNewFile);
		map.put(ksNewFile, "ksNewFile");
		act.put("ksNewFile", aNewFile);
		mNewFile.setAccelerator(ksNewFile);

		ksNewFolder = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
		aNewFolder = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFolder(fileBrowserView.getCurrentDirectory(), folderView.getFiles());
			}
		};
		mNewFolder = new JMenuItem(bundle.getString("new_folder"));
		mNewFolder.addActionListener(aNewFolder);
		mNewFolder.setAccelerator(ksNewFolder);
		map.put(ksNewFolder, "ksNewFolder");
		act.put("ksNewFolder", aNewFolder);

		ksCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
		aCopy = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(false);
			}
		};
		mCopy = new JMenuItem(bundle.getString("copy"));
		mCopy.addActionListener(aCopy);
		map.put(ksCopy, "ksCopy");
		act.put("ksCopy", aCopy);
		mCopy.setAccelerator(ksCopy);

		ksCopyPath = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aCopyPath = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyPathToClipboard();
			}
		};
		mCopyPath = new JMenuItem(bundle.getString("copy_path"));
		mCopyPath.addActionListener(aCopyPath);
		map.put(ksCopyPath, "ksCopyPath");
		act.put("ksCopyPath", aCopyPath);
		mCopyPath.setAccelerator(ksCopyPath);

		ksPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
		aPaste = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handlePaste();
			}
		};
		mPaste = new JMenuItem(bundle.getString("paste"));
		mPaste.addActionListener(aPaste);
		map.put(ksPaste, "ksPaste");
		act.put("ksPaste", aPaste);
		mPaste.setAccelerator(ksPaste);

		ksCut = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK);
		aCut = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(true);
			}
		};
		mCut = new JMenuItem(bundle.getString("cut"));
		mCut.addActionListener(aCut);
		map.put(ksCut, "ksCut");
		act.put("ksCut", aCut);
		mCut.setAccelerator(ksCut);

		ksAddToFav = KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aAddToFav = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addToFavourites();
			}
		};
		mAddToFav = new JMenuItem(bundle.getString("bookmark"));
		mAddToFav.addActionListener(aAddToFav);
		map.put(ksAddToFav, "ksAddToFav");
		act.put("ksAddToFav", aAddToFav);
		mAddToFav.setAccelerator(ksAddToFav);

		ksChangePerm = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);
		aChangePerm = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePermission(folderView.getSelectedFiles(), fileBrowserView.getCurrentDirectory());
			}
		};
		mChangePerm = new JMenuItem(bundle.getString("properties"));
		mChangePerm.addActionListener(aChangePerm);
		map.put(ksChangePerm, "ksChangePerm");
		act.put("ksChangePerm", aChangePerm);
		mChangePerm.setAccelerator(ksChangePerm);

		ksCreateLink = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
		aCreateLink = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createLink(fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles());
			}
		};
		mCreateLink = new JMenuItem(bundle.getString("create_link"));
		mCreateLink.addActionListener(aCreateLink);
		map.put(ksCreateLink, "ksCreateLink");
		act.put("ksCreateLink", aCreateLink);
		mCreateLink.setAccelerator(ksCreateLink);

		mExtractHere = new JMenuItem(bundle.getString("extract_here"));
		mExtractHere.addActionListener(e -> {
			extractArchive(folderView.getSelectedFiles()[0].getPath(), fileBrowserView.getCurrentDirectory(),
					fileBrowserView.getCurrentDirectory());
			// openFolderInTerminal(folderView.getSelectedFiles()[0].getPath());
		});

		mExtractTo = new JMenuItem(bundle.getString("extract_to"));
		mExtractTo.addActionListener(e -> {
			String text = JOptionPane.showInputDialog(bundle.getString("selec_target"),
					fileBrowserView.getCurrentDirectory());
			if (text == null || text.length() < 1) {
				return;
			}
			extractArchive(folderView.getSelectedFiles()[0].getPath(), text, fileBrowserView.getCurrentDirectory());
			// openFolderInTerminal(folderView.getSelectedFiles()[0].getPath());
		});

		mCreateArchive = new JMenuItem(bundle.getString("create_archive"));
		mCreateArchive.addActionListener(e -> {
			List<String> files = new ArrayList<>();
			for (FileInfo fileInfo : folderView.getSelectedFiles()) {
				files.add(fileInfo.getName());
			}
			createArchive(files, fileBrowserView.getCurrentDirectory(), fileBrowserView.getCurrentDirectory());
		});

		mDownload = new JMenuItem(bundle.getString("download_files"));
		mDownload.addActionListener(e -> {
			downloadFiles(folderView.getSelectedFiles(), fileBrowserView.getCurrentDirectory());
		});

		mUpload = new JMenuItem(bundle.getString("upload_here"));
		mUpload.addActionListener(e -> {
			try {
				uploadFiles();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	private void changePermission(FileInfo[] selectedFiles, String currentDirectory) {
		System.out.println("Showing property of: " + selectedFiles.length);
		PropertiesDialog propertiesDialog = new PropertiesDialog(fileBrowser,
				SwingUtilities.windowForComponent(fileBrowserView), selectedFiles.length > 1);
		if (selectedFiles.length > 1) {
			propertiesDialog.setMultipleDetails(selectedFiles);
		} else if (selectedFiles.length == 1) {
			propertiesDialog.setDetails(selectedFiles[0]);
		} else {
			return;
		}
		propertiesDialog.setVisible(true);
	}

	private void copyToClipboard(boolean cut) {
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		DndTransferData transferData = new DndTransferData(fileBrowser.getInfo().hashCode(), selectedFiles,
				fileBrowserView.getCurrentDirectory(), fileBrowserView.hashCode(), DndTransferData.DndSourceType.SSH);
		transferData.setTransferAction(cut ? DndTransferData.TransferAction.Cut : DndTransferData.TransferAction.Copy);

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DndTransferHandler.DATA_FLAVOR_DATA_FILE };
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals(DndTransferHandler.DATA_FLAVOR_DATA_FILE);
			}

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				return transferData;
			}
		}, (a, b) -> {
		});
	}

	private void copyPathToClipboard() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (FileInfo f : folderView.getSelectedFiles()) {
			if (!first) {
				sb.append("\n");
			}
			sb.append(f.getPath());
			if (first) {
				first = false;
			}
		}
		if (sb.length() > 0) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
		}
	}

	private void openLogViewer() {
		fileBrowser.getHolder().openLog(folderView.getSelectedFiles()[0]);
	}

	public boolean createMenu(JPopupMenu popup, FileInfo[] files) {
		popup.removeAll();
		int selectionCount = files.length;
		int count = 0;
		count += createBuiltInItems1(selectionCount, popup, files);
		count += createBuiltInItems2(selectionCount, popup, files);
		return count > 0;
	}

	private int createBuiltInItems1(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
		int count = 0;
		if (selectionCount == 1) {
			if (selectedFiles[0].getType() == FileType.Directory || selectedFiles[0].getType() == FileType.DirLink) {
				popup.add(mOpenInTab);
				count++;
				popup.add(mOpenFolderInTerminal);
				count++;
			}

			if ((selectedFiles[0].getType() == FileType.File || selectedFiles[0].getType() == FileType.FileLink)) {
				popup.add(mOpen);
				count++;

				if (App.IS_WINDOWS) {
					popup.add(mOpenWithMenu);
					count++;
				}
				
				loadEditors();
				popup.add(mEditWith);
				count++;

				popup.add(mRunScriptInTerminal);
				count++;

				popup.add(mRunScriptInBackground);
				count++;

				popup.add(mOpenWithLogView);
				count++;
			}
		}

		if (selectionCount > 0) {
			popup.add(mCut);
			popup.add(mCopy);
			popup.add(mCopyPath);
			popup.add(mDownload);
			count += 3;
		}

		if (hasSupportedContentOnClipboard()) {
			popup.add(mPaste);
		}

		if (selectionCount == 1) {
			popup.add(mRename);
			count++;
		}

		return count;
	}

	private int createBuiltInItems2(int selectionCount, JPopupMenu popup, FileInfo[] selectedFiles) {
		int count = 0;
		if (selectionCount > 0) {
			popup.add(mDelete);
			popup.add(mCreateArchive);
			popup.add(mSendTo);
			// popup.add(mSendFiles);
			count += 3;
		}

		if (selectionCount == 1) {
			FileInfo fileInfo = selectedFiles[0];
			if ((selectedFiles[0].getType() == FileType.File || selectedFiles[0].getType() == FileType.FileLink)
					&& this.archiveOperation.isSupportedArchive(fileInfo.getName())) {
				popup.add(mExtractHere);
				popup.add(mExtractTo);
			}
			count += 2;
		}

		if (selectionCount < 1) {
			popup.add(mNewFolder);
			popup.add(mNewFile);
			popup.add(mOpenTerminalHere);
			count += 2;
		}

		if (selectionCount < 1 || (selectionCount == 1
				&& (selectedFiles[0].getType() == FileType.File || selectedFiles[0].getType() == FileType.FileLink))) {
			popup.add(mUpload);
			count += 1;
		}

		// check only if folder is selected
		boolean allFolder = true;
		for (FileInfo f : selectedFiles) {
			if (f.getType() != FileType.Directory && f.getType() != FileType.DirLink) {
				allFolder = false;
				break;
			}
		}

		popup.add(mAddToFav);
		count++;

//        if (selectionCount == 0) {
//            popup.add(mUpload);
//            count++;
//        }
//
//        if (selectionCount > 0) {
//            popup.add(mDownload);
//            count++;
//        }

		if (selectionCount <= 1) {
			popup.add(mCreateLink);
			count++;
		}

		if (selectionCount >= 1) {
			popup.add(mChangePerm);
			count++;
		}
		return count;
	}

	public void openNewTab() {
		FileInfo[] files = folderView.getSelectedFiles();
		if (files.length == 1) {
			FileInfo file = files[0];
			if (file.getType() == FileType.Directory || file.getType() == FileType.DirLink) {
				fileBrowser.openSshFileBrowserView(file.getPath(), this.fileBrowserView.getOrientation());
			} else {

			}
		}
	}

	private void rename(FileInfo info, String baseFolder) {
		String text = JOptionPane.showInputDialog(bundle.getString("please_new_name"), info.getName());
		if (text != null && text.length() > 0) {
			renameAsync(info.getPath(), PathUtils.combineUnix(PathUtils.getParent(info.getPath()), text), baseFolder);
		}
	}

	private void renameAsync(String oldName, String newName, String baseFolder) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.rename(oldName, newName, fileBrowserView.getFileSystem(),
						fileBrowserView.getSshClient(),fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(baseFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileBrowser.enableUi();
			}
		});
	}

	private void delete(FileInfo[] targetList, String baseFolder) {
		boolean delete = true;
		if (App.getGlobalSettings().isConfirmBeforeDelete()) {
			delete = JOptionPane.showConfirmDialog(null, "Delete selected files?") == JOptionPane.YES_OPTION;
		}
		if (!delete)
			return;
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.delete(targetList, fileBrowserView.getFileSystem(),
						fileBrowserView.getSshClient(), fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(baseFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileBrowser.enableUi();
			}

		});
	}

	public void newFile(String baseFolder, FileInfo[] files) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.newFile(files, fileBrowserView.getFileSystem(), baseFolder,
						fileBrowserView.getSshClient(), fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(baseFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileBrowser.enableUi();
			}

		});
	}

	public void newFolder(String baseFolder, FileInfo[] files) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.newFolder(files, baseFolder, fileBrowserView.getFileSystem(),
						fileBrowserView.getSshClient(), fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(baseFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileBrowser.enableUi();
			}

		});
	}

	public void createLink(String baseFolder, FileInfo[] files) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.createLink(files, fileBrowserView.getFileSystem(), fileBrowserView.getSshClient())) {
					fileBrowserView.render(baseFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fileBrowser.enableUi();
			}
		});
	}

	private void handlePaste() {
		if (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DndTransferHandler.DATA_FLAVOR_DATA_FILE)) {
			try {
				DndTransferData transferData = (DndTransferData) Toolkit.getDefaultToolkit().getSystemClipboard()
						.getData(DndTransferHandler.DATA_FLAVOR_DATA_FILE);
//                for(DataFlavor df:Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors()){
//                    Object obj=Toolkit.getDefaultToolkit().getSystemClipboard().getData(df);
//                    System.out.println(obj);
//                }
				if (transferData != null) {
					fileBrowserView.handleDrop(transferData);
				}
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			DataFlavor[] flavors = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
			for (DataFlavor flavor : flavors) {
				if (flavor.isFlavorJavaFileListType()) {

				}
			}
		}
	}

	private boolean hasSupportedContentOnClipboard() {
		boolean ret = (Toolkit.getDefaultToolkit().getSystemClipboard()
				.isDataFlavorAvailable(DndTransferHandler.DATA_FLAVOR_DATA_FILE)
				|| Toolkit.getDefaultToolkit().getSystemClipboard()
						.isDataFlavorAvailable(DataFlavor.javaFileListFlavor));
		if (!ret)
			System.out.println("Nothing on clipboard");
		return ret;
	}

	public void copy(List<FileInfo> files, String targetFolder) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.copyTo(fileBrowserView.getSshClient(), files, targetFolder,
						fileBrowserView.getFileSystem(), fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(targetFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void move(List<FileInfo> files, String targetFolder) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.moveTo(fileBrowserView.getSshClient(), files, targetFolder,
						fileBrowserView.getFileSystem(), fileBrowser.getInfo().getPassword())) {
					fileBrowserView.render(targetFolder);
				} else {
					fileBrowser.enableUi();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void addToFavourites() {
		FileInfo[] arr = folderView.getSelectedFiles();

		if (arr.length > 0) {
			BookmarkManager.addEntry(fileBrowser.getInfo().getId(),
					Arrays.asList(arr).stream()
							.filter(a -> a.getType() == FileType.DirLink || a.getType() == FileType.Directory)
							.map(a -> a.getPath()).collect(Collectors.toList()));
		} else if (arr.length == 0) {
			BookmarkManager.addEntry(fileBrowser.getInfo().getId(), fileBrowserView.getCurrentDirectory());
		}

		this.fileBrowserView.getOverflowMenuHandler().loadFavourites();
	}

	public JPopupMenu createAddressPopup() {
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem mOpenInNewTab = new JMenuItem(bundle.getString("open_new_tab"));
		JMenuItem mCopyPath = new JMenuItem(bundle.getString("copy_path"));
		JMenuItem mOpenInTerminal = new JMenuItem(bundle.getString("open_in_terminal"));
		JMenuItem mBookmark = new JMenuItem(bundle.getString("bookmark"));
		popupMenu.add(mOpenInNewTab);
		popupMenu.add(mCopyPath);
		popupMenu.add(mOpenInTerminal);
		popupMenu.add(mBookmark);

		mOpenInNewTab.addActionListener(e -> {
			String path = popupMenu.getName();
			fileBrowser.openSshFileBrowserView(path, this.fileBrowserView.getOrientation());
		});

		mOpenInTerminal.addActionListener(e -> {
			String path = popupMenu.getName();
			this.openFolderInTerminal(path);
		});

		mCopyPath.addActionListener(e -> {
			String path = popupMenu.getName();
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(path), null);
		});

		mBookmark.addActionListener(e -> {
			addToFavourites();
		});
		return popupMenu;
	}

	private void openFolderInTerminal(String folder) {
		fileBrowser.getHolder().openTerminal("cd \"" + folder + "\"");
	}

	private void openRunInTerminal(String folder, String file) {
		fileBrowser.getHolder().openTerminal("cd \"" + folder + "\"; \"" + file + "\"");
	}

	private void openRunInBackground(String folder, String file) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			fileBrowser.disableUi();
			try {
				if (fileOperations.runScriptInBackground(fileBrowserView.getSshClient(),
						"cd \"" + folder + "\"; nohup \"" + file + "\" &", new AtomicBoolean())) {
				}
				fileBrowser.enableUi();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void extractArchive(String archive, String folder, String currentFolder) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			AtomicBoolean stopFlag = new AtomicBoolean(false);
			fileBrowser.disableUi(stopFlag);
			try {
				if (!archiveOperation.extractArchive(fileBrowserView.getSshClient(), archive, folder, stopFlag)) {
					if (!fileBrowser.isSessionClosed()) {
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
				}
				fileBrowserView.render(currentFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void createArchive(List<String> files, String folder, String currentFolder) {
		fileBrowser.getHolder().EXECUTOR.submit(() -> {
			AtomicBoolean stopFlag = new AtomicBoolean(false);
			fileBrowser.disableUi(stopFlag);
			try {
				if (!archiveOperation.createArchive(fileBrowserView.getSshClient(), files, folder, stopFlag)) {
					if (!fileBrowser.isSessionClosed()) {
						JOptionPane.showMessageDialog(null, "Operation failed");
					}
				}
				fileBrowserView.render(currentFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void downloadFiles(FileInfo[] files, String currentDirectory) {
		throw new RuntimeException("Not implemented");
//		try {
//			JFileChooser jfc = new JFileChooser();
//			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//			if (jfc.showSaveDialog(SwingUtilities.getWindowAncestor(
//					fileBrowserView)) == JFileChooser.APPROVE_OPTION) {
//				File file = jfc.getSelectedFile();
//
//				JComboBox<String> cmbOptions = new JComboBox<>(new String[] {
//						"Transfer normally", "Transfer in background" });
//				if (JOptionPane.showOptionDialog(holder,
//						new Object[] { "Please select a transfer mode",
//								cmbOptions },
//						"Transfer options", JOptionPane.OK_CANCEL_OPTION,
//						JOptionPane.PLAIN_MESSAGE, null, null,
//						null) != JOptionPane.OK_OPTION) {
//					return;
//				}
//				boolean backgroundTransfer = cmbOptions.getSelectedIndex() == 1;
//
//				if (backgroundTransfer) {
//					FileSystem sourceFs = holder.getSshFileSystem();
//					FileSystem targetFs = new LocalFileSystem();
//					holder.newFileTransfer(sourceFs, targetFs, files,
//							currentDirectory, file.getAbsolutePath(),
//							this.hashCode(), -1, true);
//					return;
//				}
//				FileSystem targetFs = new LocalFileSystem();
//				FileSystem sourceFs = holder.getSshFileSystem();
//				holder.newFileTransfer(sourceFs, targetFs, files,
//						currentDirectory, file.getAbsolutePath(),
//						this.hashCode(), -1, false);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
	}

	private void uploadFiles() throws IOException {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.setMultiSelectionEnabled(true);
		if (jfc.showOpenDialog(SwingUtilities.getWindowAncestor(fileBrowser)) == JFileChooser.APPROVE_OPTION) {
			System.out.println("After file selection");
			File[] files = jfc.getSelectedFiles();
			if (files.length > 0) {
				List<FileInfo> list = new ArrayList<>();

				try (LocalFileSystem localFileSystem = new LocalFileSystem()) {
					for (File file : files) {
						FileInfo fileInfo = localFileSystem.getInfo(file.getAbsolutePath());
						list.add(fileInfo);
					}
				}
				DndTransferData uploadData = new DndTransferData(0, list.toArray(new FileInfo[0]), files[0].getParent(),
						0, DndTransferData.DndSourceType.LOCAL);
				fileBrowserView.handleDrop(uploadData);
			}
		}
	}

	private void openWithEditor(String path) {
		FileInfo fileInfo = folderView.getSelectedFiles()[0];
		try {
			App.getExternalEditorHandler().openRemoteFile(fileInfo, fileBrowser.getSSHFileSystem(),
					fileBrowser.getActiveSessionId(), false, path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void openEditorConfig() {
		App.openSettings(SettingsPageName.Editor);
	}

	private void sendFilesViaLocal() {
		LocalPipeTransfer pipTransfer = new LocalPipeTransfer();
		pipTransfer.transferFiles(fileBrowser, fileBrowserView.getCurrentDirectory(), folderView.getSelectedFiles());
	}

	private void sendFilesViaSSH() {
		Remote2RemoteTransferDialog r2rt = new Remote2RemoteTransferDialog(App.getAppWindow(),
				this.fileBrowser.getHolder(), folderView.getSelectedFiles(), fileBrowserView.getCurrentDirectory());
		r2rt.setLocationRelativeTo(App.getAppWindow());
		r2rt.setVisible(true);
	}

	private void loadEditors() {
		mEditWith.removeAll();
		for (EditorEntry ent : App.getGlobalSettings().getEditors()) {
			JMenuItem mEditorItem = new JMenuItem(ent.getName());
			mEditorItem.addActionListener(e -> openWithEditor(ent.getPath()));
			mEditWith.add(mEditorItem);
		}
		mEditWith.add(mEditorConfig);
	}

}
