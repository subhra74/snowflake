package snowflake.components.files;

import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.FileType;
import snowflake.common.local.files.LocalFileSystem;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.components.files.browser.FileBrowser;
import snowflake.components.files.editor.TextEditor;
import snowflake.components.files.transfer.FileTransfer;
import snowflake.components.files.transfer.FileTransferProgress;
import snowflake.components.files.transfer.TransferProgressPanel;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileComponentHolder extends JPanel implements FileTransferProgress {
    private JRootPane rootPane;
    private JPanel contentPane;
    private SessionInfo info;
    private SshUserInteraction source;
    private Map<SessionInfo, FileSystem> fileSystemMap = new ConcurrentHashMap<>();
    private Map<FileSystem, Integer> fileViewMap = new ConcurrentHashMap<>();
    private AtomicBoolean closeRequested = new AtomicBoolean(false);
    private JPanel disabledPanel;
    private FileTransfer fileTransfer;
    private TransferProgressPanel progressPanel;
    private JTabbedPane tabs;
    private FileBrowser fileBrowser;
    private SshFileSystem fs;
    private String tempFolder;
    private TextEditor editor;

    public FileComponentHolder(SessionInfo info) {
        super(new BorderLayout());
        this.info = info;
        contentPane = new JPanel(new BorderLayout());
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);
        this.source = new SshUserInteraction(info, rootPane);
        this.disabledPanel = new JPanel();
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

        fileBrowser = new FileBrowser(info, source, fileSystemMap, fileViewMap, closeRequested, this, rootPane);
        editor = new TextEditor(this);
        JPanel panelHolder = new JPanel(new BorderLayout());
        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(App.getFontAwesomeFont());
        tabs.addTab("\uf114", fileBrowser);
        tabs.addTab("\uf0f6", editor);
        tabs.addTab("\uf002", new JPanel());
        panelHolder.add(tabs);
        contentPane.add(panelHolder);
    }

    @Override
    public void init(String sourceName, String targetName, long totalSize, long files) {

    }

    @Override
    public void progress(long processedBytes, long totalBytes, long processedCount, long totalCount) {
        progressPanel.setProgress((int) ((processedBytes * 100) / totalBytes));
    }

    @Override
    public void error(String cause) {
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(false);
            if (tabs.getSelectedIndex() == 0) {
                fileBrowser.requestReload(progressPanel.getSource());
            }
        });
    }

    @Override
    public void done() {
        SwingUtilities.invokeLater(() -> {
            progressPanel.setVisible(false);
            if (tabs.getSelectedIndex() == 0) {
                fileBrowser.requestReload(progressPanel.getSource());
            } else if (tabs.getSelectedIndex() == 1) {
                if (progressPanel.getSource() == editor.hashCode()) {
                    if (editor.isSavingFile()) {
                        System.out.println("Saved");
                        editor.fileSaved();
                    } else {
                        editor.openRemoteFile(fileTransfer.getFiles()[0],
                                PathUtils.combine(tempFolder, fileTransfer.getFiles()[0].getName(),
                                        File.separator), fileTransfer.getOutPrefix());
                    }
                }
            }
        });
    }

    public void disableUi() {
        SwingUtilities.invokeLater(() -> {
            this.rootPane.setGlassPane(this.disabledPanel);
            this.disabledPanel.setVisible(true);
        });
    }

    public void enableUi() {
        SwingUtilities.invokeLater(() -> {
            this.disabledPanel.setVisible(false);
        });
    }

    public void newFileTransfer(FileSystem sourceFs,
                                FileSystem targetFs,
                                FileInfo[] files,
                                String sourceFolder,
                                String targetFolder,
                                int dragsource) {
        newFileTransfer(sourceFs, targetFs, files, sourceFolder, targetFolder, dragsource, null, null);
    }

    public void newFileTransfer(FileSystem sourceFs,
                                FileSystem targetFs,
                                FileInfo[] files,
                                String sourceFolder,
                                String targetFolder,
                                int dragsource,
                                String inPrefix, String outPrefix) {
        this.fileTransfer = new FileTransfer(sourceFs, targetFs, files, sourceFolder, targetFolder, this);
        this.fileTransfer.setInPrefix(inPrefix);
        this.fileTransfer.setOutPrefix(outPrefix);
        if (progressPanel == null) {
            progressPanel = new TransferProgressPanel(this.fileTransfer, dragsource);
        }
        progressPanel.clear();
        progressPanel.setSource(dragsource);
        rootPane.setGlassPane(progressPanel);
        progressPanel.setVisible(true);
        this.fileTransfer.start();
    }

    public void reloadRemoteFile(FileInfo fileInfo, String prefix) {
        newFileTransfer(this.fs,
                new LocalFileSystem(),
                new FileInfo[]{fileInfo},
                PathUtils.getParent(fileInfo.getPath()),
                tempFolder,
                editor.hashCode(), null, prefix);
    }

    public void editRemoteFileInternal(FileInfo fileInfo) {
        if (!editor.isAlreadyOpened(fileInfo.getPath())) {
            tabs.setSelectedIndex(1);
            editor.setSavingFile(false);
            newFileTransfer(this.fs,
                    new LocalFileSystem(),
                    new FileInfo[]{fileInfo},
                    PathUtils.getParent(fileInfo.getPath()),
                    tempFolder,
                    editor.hashCode(), null, UUID.randomUUID().toString());
        }
    }

    public void saveRemoteFile(String localFile, FileInfo fileInfo, String prefix) throws IOException {
        String path = PathUtils.combine(PathUtils.getParent(localFile), prefix + PathUtils.getFileName(localFile), File.separator);
        System.out.println("Saving file from: " + path + " to: " + fileInfo.getPath());
        editor.setSavingFile(true);
        //LocalFileSystem localFileSystem = new LocalFileSystem();
        FileInfo localFileInfo = new FileInfo(PathUtils.getFileName(localFile),
                path, new File(path).length(), FileType.File, 0, 0, null,
                null, 0, null);
        newFileTransfer(new LocalFileSystem(), this.fs,
                new FileInfo[]{localFileInfo}, this.tempFolder,
                PathUtils.getParent(fileInfo.getPath()), editor.hashCode(), prefix, null);
    }

    public synchronized SshFileSystem getSshFileSystem() {
        if (fs == null) {
            fs = new SshFileSystem(source);
            try {
                fs.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fs;
    }

    public SessionInfo getInfo() {
        return this.info;
    }
}
