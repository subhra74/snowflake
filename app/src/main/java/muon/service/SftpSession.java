package muon.service;

import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.dto.file.FileType;
import muon.dto.session.SessionInfo;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.util.AppUtils;
import muon.util.PathUtils;
import muon.util.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.keyboard.UserAuthKeyboardInteractiveFactory;
import org.apache.sshd.client.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;
import org.apache.sshd.sftp.common.SftpConstants;
import org.apache.sshd.sftp.common.SftpException;

import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SftpSession implements AutoCloseable {
    private SessionInfo sessionInfo;
    private SshClient client;
    private ClientSession session;
    private SftpClient sftpClient;
    private String homePath;
    private AtomicBoolean connected = new AtomicBoolean(false);

    public SftpSession(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public FileList list(String folder) throws FSConnectException, FSAccessException {
        try {
            var folders = new ArrayList<FileInfo>();
            var files = new ArrayList<FileInfo>();
            for (var dir : this.sftpClient.readDir(folder)) {
                if ((StringUtils.equalsIgnoreCase(dir.getFilename(), ".") ||
                        StringUtils.equalsIgnoreCase(dir.getFilename(), ".."))
                        && dir.getAttributes().isDirectory()) {
                    continue;
                }
                var info = new FileInfo(
                        Paths.get(folder, dir.getFilename()).toAbsolutePath().toString(),
                        dir.getFilename(),
                        dir.getAttributes().isDirectory() ? 0 : dir.getAttributes().getSize(),
                        LocalDateTime.ofInstant(
                                dir.getAttributes().getModifyTime().toInstant(),
                                ZoneId.systemDefault()),
                        dir.getAttributes().isDirectory() ? FileType.Directory : FileType.File,
                        dir.getAttributes().getOwner()
                );
                if (dir.getAttributes().isDirectory()) {
                    folders.add(info);
                } else {
                    files.add(info);
                }
            }

            var name = PathUtils.getFileName(folder);
            if (Objects.isNull(name)) {
                name = "";
            }

            return new FileList(
                    files,
                    folders,
                    folder,
                    name);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isConnected()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public boolean exists(String path) throws FSAccessException, FSConnectException {
        try {
            this.sftpClient.stat(path);
            return true;
        } catch (Exception ex) {
            if (ex instanceof SftpException) {
                var ex2 = (SftpException) ex;
                if (ex2.getStatus() == SftpConstants.SSH_FX_NO_SUCH_FILE) {
                    return false;
                }
            }
            if (isConnected()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public String getHomePath() {
        return homePath;
    }

    public boolean isConnected() {
        return connected.get() && sftpClient.isOpen();
    }

    public void connect(SshCallback callback, GuiUserAuthFactory passwordUserAuthFactory) throws FSConnectException {
        System.out.println("Connecting...");
        if (Objects.isNull(client)) {
            client = create(callback, passwordUserAuthFactory);
            client.start();
        }
        try {
            session = client.connect(
                            AppUtils.getUser(sessionInfo),
                            sessionInfo.getHost(),
                            sessionInfo.getPort())
                    .verify().getSession();
            session.auth().verify();
            sftpClient = DefaultSftpClientFactory.INSTANCE.createSftpClient(
                    session);
            homePath = sftpClient.canonicalPath(".");
            connected.set(true);
        } catch (Exception ex) {
            closeImpl();
            throw new FSConnectException(ex.getMessage(), ex);
        }
    }

    @Override
    public void close() {
        closeImpl();
    }

    public OutputStream createOutputStream(String path) throws FSAccessException, FSConnectException {
        try {
            return sftpClient.write(path,
                    List.of(SftpClient.OpenMode.Create, SftpClient.OpenMode.Write));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isConnected()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public OutputStream appendOutputStream(String path) throws FSAccessException, FSConnectException {
        try {
            return sftpClient.write(path,
                    List.of(SftpClient.OpenMode.Append, SftpClient.OpenMode.Write));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isConnected()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public boolean mkdir(String path) throws FSConnectException, FSAccessException {
        if (exists(path)) {
            return false;
        }

        try {
            this.sftpClient.mkdir(path);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (isConnected()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    private static SshClient create(SshCallback callback, GuiUserAuthFactory passwordUserAuthFactory) {
        var client = SshClient.setUpDefaultClient();
        CoreModuleProperties.PASSWORD_PROMPTS.set(client, 3);
        CoreModuleProperties.CHANNEL_OPEN_TIMEOUT.set(client, Duration.ZERO);
        client.setUserInteraction(callback);
        client.setPasswordIdentityProvider(callback);
        client.setPasswordAuthenticationReporter(callback);
        client.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, Duration.ofMinutes(1));

        List<UserAuthFactory> userAuthFactories = List.of(
                passwordUserAuthFactory,
                UserAuthPublicKeyFactory.INSTANCE,
                UserAuthKeyboardInteractiveFactory.INSTANCE);
        client.setUserAuthFactories(userAuthFactories);
        client.addSessionListener(new SessionListener() {
            @Override
            public void sessionDisconnect(Session session, int reason, String msg, String language, boolean initiator) {
                System.err.println("sessionDisconnect");
            }

            @Override
            public void sessionClosed(Session session) {
                System.err.println("sessionClosed");
            }

            @Override
            public void sessionException(Session session, Throwable t) {
                System.err.println("sessionException");
            }
        });
        return client;
    }

    private void closeImpl() {
        if (Objects.nonNull(sftpClient)) {
            try {
                sftpClient.close();
            } catch (Exception exx) {
            }
            sftpClient = null;
        }
        if (Objects.nonNull(session)) {
            try {
                session.close(true);
            } catch (Exception exx) {
            }
            session = null;
        }
        if (Objects.nonNull(client)) {
            try {
                client.close(true);
            } catch (Exception exx) {
            }
            client = null;
        }
        connected.set(false);
    }
}
