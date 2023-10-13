package muon.service;

import muon.dto.file.FileInfo;
import muon.dto.file.FileList;
import muon.dto.file.FileType;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.exceptions.FSException;
import muon.dto.session.SessionInfo;
import muon.util.AppUtils;
import muon.util.PathUtils;
import muon.util.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionContext;
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

    public SftpFileSystem(SessionInfo sessionInfo, InputBlocker inputBlocker) {
        this.sessionInfo = sessionInfo;
        this.inputBlocker = inputBlocker;
        this.callback = new SshCallback();
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

    private void ensureConnected() throws FSConnectException{
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
                            sessionInfo.getUser(),
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
        CoreModuleProperties.PASSWORD_PROMPTS.set(client, 10);
        client.setUserInteraction(callback);
        client.setPasswordIdentityProvider(callback);
        client.setPasswordAuthenticationReporter(callback);
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

    class SshCallback implements PasswordIdentityProvider,
            PasswordAuthenticationReporter,
            UserInteraction {
        @Override
        public void serverVersionInfo(ClientSession session, List<String> lines) {
            System.out.println(String.join(" ", lines));
        }

        @Override
        public void welcome(ClientSession session, String banner, String lang) {
            inputBlocker.showBanner(banner);
        }

        @Override
        public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
            return inputBlocker.getUserInput(sessionInfo.getName(), sessionInfo.getUser(), prompt, echo);
        }

        @Override
        public boolean isInteractionAllowed(ClientSession session) {
            return true;
        }

        @Override
        public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
            return null;
        }

        @Override
        public KeyPair resolveAuthPublicKeyIdentityAttempt(ClientSession session) throws Exception {
            System.out.println("resolveAuthPublicKeyIdentityAttempt");
            return null;
        }

        @Override
        public Iterable<String> loadPasswords(SessionContext session) throws IOException, GeneralSecurityException {
            var sessionPassword = new String(sessionInfo.getPassword());
            if (!StringUtils.isEmpty(sessionPassword)) {
                return List.of(sessionPassword);
            }
            return null;
        }

        @Override
        public void signalAuthenticationAttempt(ClientSession session, String service, String oldPassword, boolean modified, String newPassword) throws Exception {
            System.out.println("signalAuthenticationAttempt");
        }

        @Override
        public void signalAuthenticationExhausted(ClientSession session, String service) throws Exception {
            System.out.println("signalAuthenticationExhausted");
        }

        @Override
        public void signalAuthenticationSuccess(ClientSession session, String service, String password) throws Exception {
            System.out.println("signalAuthenticationSuccess");
        }

        @Override
        public void signalAuthenticationFailure(ClientSession session, String service, String password, boolean partial, List<String> serverMethods) throws Exception {
            System.out.println("signalAuthenticationFailure");
        }
    }
}
