package snowflake.components.files;

import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.local.files.LocalFileSystem;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.common.DisabledPanel;
import snowflake.components.common.TabbedPanel;
import snowflake.components.files.browser.AbstractFileBrowserView;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.files.browser.ssh.SshFileBrowserView;
import snowflake.components.files.editor.ExternalEditor;
import snowflake.components.files.editor.TextEditor;
//import snowflake.components.files.logviewer.LogViewerComponent;
import snowflake.components.files.logviewer.LogViewerPanel;
import snowflake.components.files.search.FileSearchPanel;
import snowflake.components.files.transfer.FileTransfer;
import snowflake.components.files.transfer.FileTransferProgress;
import snowflake.components.files.transfer.TransferProgressPanel;
import snowflake.components.main.ConnectedResource;
import snowflake.components.main.SessionContent;

import snowflake.components.main.LazyInitComponent;

import snowflake.components.newsession.SessionInfo;
import snowflake.components.newsession.SessionStore;
import snowflake.utils.PathUtils;
import snowflake.utils.PlatformAppLauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;


public class FileComponentHolder extends JPanel implements FileTransferProgress, ConnectedResource, LazyInitComponent {
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private static final int DEFAULT_APP = 10, DEFAULT_EDITOR = 20;
    private JRootPane rootPane;
    private JPanel contentPane;
    private SessionInfo info;
    private SshUserInteraction source;
    private Map<SessionInfo, FileSystem> fileSystemMap = new ConcurrentHashMap<>();
    private Map<FileSystem, Integer> fileViewMap = new ConcurrentHashMap<>();
    private AtomicBoolean closeRequested = new AtomicBoolean(false);
    private DisabledPanel disabledPanel;
    private FileTransfer ongoingFileTransfer;
    private TransferProgressPanel progressPanel;
    private TabbedPanel tabs;
    private FileBrowser fileBrowser;
    private SshFileSystem fs;
    private String tempFolder;
    private TextEditor editor;
    private ExternalEditor externalEditor;
    private List<ExternalEditor.FileModificationInfo> pendingTransfers = Collections.synchronizedList(new ArrayList<>());
    //private LogViewerComponent logViewerComponent;
    private LogViewerPanel logViewerComponent;
    private FileSearchPanel fileSearchPanel;
    private SessionContent sessionContent;
    private Map<String, List<FileInfo>> directoryCache = new ConcurrentHashMap<>();
    private AtomicBoolean init=new AtomicBoolean(false);

    public FileComponentHolder(SessionInfo info, ExternalEditor externalEditor, SessionContent sessionContent) {
        super(new BorderLayout());
       
        this.sessionContent = sessionContent;
        //setBorder(new LineBorder(new Color(200, 200, 200), 1));
        this.externalEditor = externalEditor;
        this.info = info;
       
    }

    @Override
    public void lazyInit() {
        if(init.get()){
          return;
        }
        init.set(true);
        setOpaque(true);
        contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        rootPane = new JRootPane();
        rootPane.setBorder(null);
        contentPane.setBorder(null);
        rootPane.setOpaque(true);
        rootPane.setContentPane(contentPane);
        add(rootPane);
        this.source = new SshUserInteraction(info, rootPane);
        this.disabledPanel = new DisabledPanel();

        try {
            this.tempFolder = Files.createTempDirectory(UUID.randomUUID().toString()).toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.disabledPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }
        });

        logViewerComponent = new LogViewerPanel(this);
        fileSearchPanel = new FileSearchPanel(this);
        fileBrowser = new FileBrowser(info, source, fileSystemMap, fileViewMap, closeRequested, this, rootPane);
        editor = new TextEditor(this);

        //tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs = new TabbedPanel();
        //tabs.setFont(App.getFontAwesomeFont());
//        tabs.addTab("\uf114", fileBrowser);
//        tabs.addTab("\uf0f6", editor);
//        tabs.addTab("\uf022", logViewerComponent);

        tabs.addTab("File browser", fileBrowser);
        tabs.addTab("Text editor", editor);
        tabs.addTab("Log viewer", logViewerComponent);
        tabs.addTab("Search", fileSearchPanel);
        tabs.setSelectedIndex(0);


        contentPane.add(tabs);

    }

    @Override
    public void init(long totalSize, long files, FileTransfer fileTransfer) {

    }

    @Override
    public void progress(long processedBytes, long totalBytes, long processedCount, long totalCount, FileTransfer fileTransfer) {
        if (totalBytes == 0) {
            progressPanel.setProgress(0);
        } else {
            progressPanel.setProgress((int) ((processedBytes * 100) / totalBytes));
        }
    }

    @Override
    public void error(String cause, FileTransfer fileTransfer) {
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(false);
            JOptionPane.showMessageDialog(null, "Operation failed");
//            if (tabs.getSelectedIndex() == 0) {
//                JOptionPane.showMessageDialog(null, "Operation failed");
//            } else if (tabs.getSelectedIndex() == 1) {
//                if (progressPanel.getSource() == editor.hashCode()) {
//                    if (editor.isSavingFile()) {
//                        System.out.println("Saved with error");
//                        editor.fileSavedWithError();
//                    } else {
//                        JOptionPane.showMessageDialog(null, "Unable to open file");
//                    }
//                }
//            }
        });
    }

    @Override
    public void done(FileTransfer fileTransfer) {
        if (isCloseRequested().get()) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(false);
            if (tabs.getSelectedIndex() == 0) {
                if (progressPanel.getSource() == DEFAULT_APP || progressPanel.getSource() == DEFAULT_EDITOR) {
                    String localFile = PathUtils.combine(ongoingFileTransfer.getTargetFolder(), ongoingFileTransfer.getFiles()[0].getName(),
                            File.separator);
                    if (progressPanel.getSource() == DEFAULT_APP ?
                            PlatformAppLauncher.shellLaunch(localFile) : PlatformAppLauncher.shellEdit(localFile)) {
                        externalEditor.addForMonitoring(ongoingFileTransfer.getFiles()[0],
                                localFile,
                                this.hashCode());
                    }
                } else {
                    fileBrowser.requestReload(progressPanel.getSource());
                }
            } else if (tabs.getSelectedIndex() == 1) {
                if (progressPanel.getSource() == editor.hashCode()) {
                    if (editor.isSavingFile()) {
                        System.out.println("Saved");
                        editor.fileSaved();
                    } else {
                        editor.openRemoteFile(ongoingFileTransfer.getFiles()[0],
                                PathUtils.combine(ongoingFileTransfer.getTargetFolder(), ongoingFileTransfer.getFiles()[0].getName(),
                                        File.separator));
                    }
                }
            }

            if (this.pendingTransfers.size() > 0) {
                processQueue();
            }
        });
    }

    private void processQueue() {
        ExternalEditor.FileModificationInfo fmi = pendingTransfers.remove(0);
        try {
            saveRemoteFile(fmi.file.getAbsolutePath(), fmi.fileInfo, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disableUi() {
        SwingUtilities.invokeLater(() -> {
            this.disabledPanel.startAnimation(null);
            //this.disabledPanel.btn.setVisible(false);
            this.rootPane.setGlassPane(this.disabledPanel);
            this.disabledPanel.setVisible(true);
        });
    }

    public void disableUi(AtomicBoolean stopFlag) {
        SwingUtilities.invokeLater(() -> {
            this.disabledPanel.startAnimation(stopFlag);
            //this.disabledPanel.btn.setVisible(true);
            this.rootPane.setGlassPane(this.disabledPanel);
            this.disabledPanel.setVisible(true);
        });
    }

    public void enableUi() {
        SwingUtilities.invokeLater(() -> {
            this.disabledPanel.stopAnimation();
            this.disabledPanel.setVisible(false);
        });
    }

    public void newFileTransfer(FileSystem sourceFs,
                                FileSystem targetFs,
                                FileInfo[] files,
                                String sourceFolder,
                                String targetFolder,
                                int dragsource,
                                int defaultOverwriteAction,
                                boolean backgroundTransfer) {
        if (backgroundTransfer) {
            sessionContent.transferInBackground(new FileTransfer(sourceFs, targetFs, files,
                    sourceFolder, targetFolder, null, defaultOverwriteAction));
            return;
        }
        this.ongoingFileTransfer = new FileTransfer(sourceFs, targetFs, files,
                sourceFolder, targetFolder, this, defaultOverwriteAction);
        if (progressPanel == null) {
            progressPanel = new TransferProgressPanel(this.ongoingFileTransfer, dragsource);
        }
        progressPanel.clear();
        progressPanel.setSource(dragsource);
        rootPane.setGlassPane(progressPanel);
        progressPanel.setVisible(true);
        this.threadPool.submit(this.ongoingFileTransfer);
    }

    public void reloadRemoteFile(FileInfo fileInfo) {
        newFileTransfer(this.fs,
                new LocalFileSystem(),
                new FileInfo[]{fileInfo},
                PathUtils.getParent(fileInfo.getPath()),
                tempFolder,
                editor.hashCode(), 0, false);
    }

    public void editRemoteFileInternal(FileInfo fileInfo) {
        if (!editor.isAlreadyOpened(fileInfo.getPath())) {
            tabs.setSelectedIndex(1);
            editor.setSavingFile(false);
            downloadFileToTempFolder(fileInfo, editor.hashCode());
        }else {
        	tabs.setSelectedIndex(1);
        	editor.showTab(fileInfo);
        }
    }

    private void downloadFileToTempFolder(FileInfo fileInfo, int hashcode) {
        String tempFolder = PathUtils.combine(this.tempFolder, UUID.randomUUID().toString(), File.separator);
        Path tempFolderPath = Path.of(tempFolder);
        if (!Files.exists(tempFolderPath)) {
            try {
                Files.createDirectories(tempFolderPath);
                newFileTransfer(this.fs,
                        new LocalFileSystem(),
                        new FileInfo[]{fileInfo},
                        PathUtils.getParent(fileInfo.getPath()),
                        tempFolder,
                        hashcode,
                        0,
                        false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveRemoteFile(String localFile, FileInfo fileInfo, int hashCode) throws IOException {
        System.out.println("Saving file from: " + localFile + " to: " + fileInfo.getPath());
        editor.setSavingFile(true);
        newFileTransfer(new LocalFileSystem(), this.fs,
                new FileInfo[]{new LocalFileSystem().getInfo(localFile)}, PathUtils.getParent(localFile),
                PathUtils.getParent(fileInfo.getPath()), hashCode, 0, false);
    }

    public SshFileSystem getSshFileSystem() throws Exception {
        if (isCloseRequested().get()) {
            System.out.println("Close requested...");
            return null;
        }
        if (fs == null) {
            System.out.println("Creating file system from thread " + Thread.currentThread().getName());
            fs = new SshFileSystem(source);
            fs.connect();
        }
        return fs;
    }

    public SessionInfo getInfo() {
        return this.info;
    }

    public void filesChanged(List<ExternalEditor.FileModificationInfo> list) {
        this.pendingTransfers.addAll(list);
        if (!this.progressPanel.isVisible()) {
            processQueue();
        }
    }

    public void openWithDefaultApp(FileInfo fileInfo) {
        downloadFileToTempFolder(fileInfo, DEFAULT_APP);
    }

    public void openWithDefaultEditor(FileInfo fileInfo) {
        downloadFileToTempFolder(fileInfo, DEFAULT_EDITOR);
    }

    public void openWithLogViewer(FileInfo fileInfo) {
        this.tabs.setSelectedIndex(2);
        this.logViewerComponent.openLog(fileInfo);
    }

    public String getTempFolder() {
        return this.tempFolder;
    }

    @Override
    public boolean isInitiated() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return !(fs == null || fs.isConnected());
    }

    @Override
    public void close() {
        this.closeRequested.set(true);
        this.fileSearchPanel.close();
        if (fs != null) {
            try {
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getFavouriteLocations(AbstractFileBrowserView fileBrowserView) {
        if (fileBrowserView instanceof SshFileBrowserView) {
            return info.getFavouriteRemoteFolders();
        } else {
            return info.getFavouriteLocalFolders();
        }
    }

    public void addFavouriteLocation(AbstractFileBrowserView fileBrowserView, String path) {
        if (fileBrowserView instanceof SshFileBrowserView) {
            info.getFavouriteRemoteFolders().add(path);
        } else {
            info.getFavouriteLocalFolders().add(path);
        }
        System.out.println(info.getId() + " --- " + info.getFavouriteLocalFolders() + " " + info.getFavouriteRemoteFolders());
        SessionStore.updateFavourites(info.getId(),
                info.getFavouriteLocalFolders(),
                info.getFavouriteRemoteFolders());
    }

    public void openTerminal(String command) {
        this.sessionContent.openTerminal(command);
    }

    public AtomicBoolean isCloseRequested() {
        return this.closeRequested;
    }

    public Map<String, List<FileInfo>> getDirectoryCache() {
        return directoryCache;
    }

    public void statAsync(String path, BiConsumer<FileInfo, Boolean> biConsumer) {
        this.threadPool.submit(() -> {
            disableUi();
            try {
                FileInfo fileInfo = this.fs.getInfo(path);
                biConsumer.accept(fileInfo, Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
                biConsumer.accept(null, Boolean.FALSE);
            } finally {
                enableUi();
            }
        });
    }

    public void showPage(String name) {
        sessionContent.showPage(name);
    }

    public void openInFileBrowser(String path) {
        this.tabs.setSelectedIndex(0);
        this.fileBrowser.openSshFileBrowserView(path, AbstractFileBrowserView.PanelOrientation.Left);
    }
}
