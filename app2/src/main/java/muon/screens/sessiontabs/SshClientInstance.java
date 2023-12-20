package muon.screens.sessiontabs;

import muon.App;
import muon.model.SessionInfo;
import muon.util.AppUtils;
import org.apache.sshd.client.*;
import org.apache.sshd.client.session.*;
import org.apache.sshd.common.session.*;
import org.apache.sshd.core.CoreModuleProperties;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshClientInstance {
    private SessionInfo sessionInfo;
    private SshClient client;
    private ClientSession session;
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private AtomicBoolean closed = new AtomicBoolean(false);

    public SshClientInstance(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        client = SshClient.setUpDefaultClient();
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
        CoreModuleProperties.PASSWORD_PROMPTS.set(client, 10);

        var callback = App.getInputBlockerDialog();

        client.setUserInteraction(callback);
        client.setPasswordIdentityProvider(callback);
        client.setPasswordAuthenticationReporter(callback);
    }

    public void close(Boolean cancelled) {
        AppUtils.runAsync(() -> {
            closed.set(true);
            client.close(true);
        });
    }

    public boolean connect() {
        try {
            App.getInputBlockerDialog().showBlocker();
            client.start();
            App.getInputBlockerDialog().setCloseCallback(this::close);
            do {
                try {
                    isConnected.set(false);
                    session = client.connect(
                                    sessionInfo.getUser(),
                                    sessionInfo.getHost(),
                                    sessionInfo.getPort())
                            .verify().getSession();
                    System.out.println("Beginning auth");
                    session.auth().verify();
                    isConnected.set(true);
                    return true;
                } catch (Exception ex) {
                    System.out.println("Error encountered: " + Thread.currentThread());
                    ex.printStackTrace();
                    safelyCloseCurrentSession();
                    if (closed.get()) {
                        return false;
                    }
                }
            } while (App.getInputBlockerDialog().shouldRetry());
            return false;
        } finally {
            App.getInputBlockerDialog().setCloseCallback(null);
            App.getInputBlockerDialog().hideBlocker();
        }
    }

    private void safelyCloseCurrentSession() {
        if (Objects.nonNull(this.session)) {
            try {
                session.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return this.isConnected.get();
    }

    public ClientSession getSession() {
        return session;
    }
}
