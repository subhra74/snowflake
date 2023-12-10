package muon.service;

import muon.constants.AppConstant;
import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.dto.file.FileType;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.exceptions.FSException;
import muon.dto.session.SessionInfo;
import muon.util.AppUtils;
import muon.util.IdentityManager;
import muon.util.PathUtils;
import muon.util.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.keyboard.UserAuthKeyboardInteractiveFactory;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;
import org.apache.sshd.sftp.common.SftpConstants;
import org.apache.sshd.sftp.common.SftpException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SftpFileSystem implements FileSystem {
    private InputBlocker inputBlocker;
    private SessionInfo sessionInfo;
    private SshCallback callback;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private SshClient client;
    private ClientSession session;
    private SftpClient sftpClient;
    private String homePath;
    private GuiUserAuthFactory passwordUserAuthFactory;

    public SftpFileSystem(SessionInfo sessionInfo, InputBlocker inputBlocker) {
        this.sessionInfo = sessionInfo;
        this.inputBlocker = inputBlocker;
        this.passwordUserAuthFactory = new GuiUserAuthFactory(inputBlocker, sessionInfo);
        this.callback = new SshCallback(inputBlocker, sessionInfo);
    }

    private boolean exists(String path) throws FSAccessException, FSConnectException {
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
            if (Objects.nonNull(sftpClient) && sftpClient.isOpen()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public boolean mkdir(String path) throws FSConnectException, FSAccessException {
        ensureConnected();

        if (exists(path)) {
            return false;
        }

        try {
            this.sftpClient.mkdir(path);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (Objects.nonNull(sftpClient) && sftpClient.isOpen()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public FileList list(String folder) throws FSConnectException, FSAccessException {
        ensureConnected();
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
            if (Objects.nonNull(sftpClient) && sftpClient.isOpen()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public OutputStream createOutputStream(String path) throws FSAccessException, FSConnectException {
        ensureConnected();
        try {
            return sftpClient.write(path,
                    List.of(SftpClient.OpenMode.Create, SftpClient.OpenMode.Write));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (Objects.nonNull(sftpClient) && sftpClient.isOpen()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    public OutputStream appendOutputStream(String path) throws FSAccessException, FSConnectException {
        ensureConnected();
        try {
            return sftpClient.write(path,
                    List.of(SftpClient.OpenMode.Append, SftpClient.OpenMode.Write));
        } catch (Exception ex) {
            ex.printStackTrace();
            if (Objects.nonNull(sftpClient) && sftpClient.isOpen()) {
                throw new FSAccessException(ex.getMessage(), ex);
            } else {
                throw new FSConnectException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String getHome() throws FSConnectException {
        ensureConnected();
        return homePath;
    }

    private void ensureConnected() throws FSConnectException {
        if (!(connected.get() && sftpClient.isOpen())) {
            connect();
        }
    }

    @Override
    public void close() throws Exception {
        closeImpl();
    }

    private void connect() throws FSConnectException {
        inputBlocker.showConnectionInProgress();
        if (Objects.isNull(client)) {
            setupSshClient();
            client.start();
        }
        System.out.println("Connecting...");
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

    private void setupSshClient() {
        client = SshClient.setUpDefaultClient();
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
