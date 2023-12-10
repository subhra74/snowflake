package muon.service;

import muon.dto.session.SessionInfo;
import muon.exceptions.FSConnectException;
import muon.util.AppUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.keyboard.UserAuthKeyboardInteractive;
import org.apache.sshd.client.auth.keyboard.UserAuthKeyboardInteractiveFactory;
import org.apache.sshd.client.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.client.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.PtyChannelConfiguration;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.core.CoreModuleProperties;
import org.apache.sshd.sftp.client.impl.DefaultSftpClientFactory;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshTerminalClient implements AutoCloseable {
    private InputBlocker inputBlocker;
    private SessionInfo sessionInfo;
    private SshCallback callback;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private SshClient client;
    private ClientSession session;
    private ChannelShell channelShell;
    private InputStream in;
    private OutputStream out;
    private GuiUserAuthFactory passwordUserAuthFactory;

    public SshTerminalClient(SessionInfo sessionInfo, InputBlocker inputBlocker, InputStream in, OutputStream out) {
        this.sessionInfo = sessionInfo;
        this.inputBlocker = inputBlocker;
        this.passwordUserAuthFactory = new GuiUserAuthFactory(inputBlocker, sessionInfo);
        this.callback = new SshCallback(inputBlocker, sessionInfo);
        this.in = in;
        this.out = out;
    }

    public void start() throws FSConnectException {
        connect();
    }

    private void ensureConnected() throws FSConnectException {
        if (!(connected.get() && channelShell.isOpen())) {
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

            var ptyConf = new PtyChannelConfiguration();
            ptyConf.setPtyType("xterm-256color");

            channelShell = session.createShellChannel(ptyConf, new HashMap<>());
            channelShell.setOut(this.out);
            channelShell.setIn(this.in);
            channelShell.open().verify();

            connected.set(true);
            System.out.println(channelShell);
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
        if (Objects.nonNull(channelShell)) {
            try {
                channelShell.close();
            } catch (Exception exx) {
            }
            channelShell = null;
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

    public int waitFor() {
        channelShell.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
        return 0;
    }

    public void resize(int row, int cols) {
        System.out.println("Row: " + row + " Col: " + cols);
        try {
            channelShell.sendWindowChange(cols, row);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        if (Objects.nonNull(channelShell)) {
            try {
                return channelShell.isOpen();
            } catch (Exception exx) {
            }
        }
        return false;
    }
}
