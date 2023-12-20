package muon.screens.sessiontabs.filebrowser;

import muon.dto.file.FileInfo;
import muon.dto.file.FileType;
import muon.screens.sessiontabs.SshClientInstance;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class SftpClientInstance {
    private SshClientInstance clientInstance;
    private SftpClientFactory factory;
    private SftpClient sftpClient;

    public SftpClientInstance(SshClientInstance instance) {
        this.clientInstance = instance;
        this.factory = DefaultSftpClientFactory.INSTANCE;
    }

    public void connect() throws Exception {
        this.sftpClient = this.factory.createSftpClient(clientInstance.getSession());
    }

    public String getInitialPath() throws IOException {
        return this.sftpClient.canonicalPath(".");
    }

    public List<FileInfo> ls(String path) throws Exception {
        Iterable<SftpClient.DirEntry> iterator;
        var result = new ArrayList<FileInfo>();
        iterator = this.sftpClient.readDir(path);
        for (var dir : iterator) {
            var info = new FileInfo(
                    dir.getLongFilename(),
                    dir.getFilename(),
                    dir.getAttributes().isDirectory() ? 0 : dir.getAttributes().getSize(),
                    LocalDateTime.ofInstant(
                            dir.getAttributes().getModifyTime().toInstant(),
                            ZoneId.systemDefault()),
                    dir.getAttributes().isDirectory() ? FileType.Directory : FileType.File,
                    dir.getAttributes().getOwner()
            );
            result.add(info);
        }
        return result;
    }
}
