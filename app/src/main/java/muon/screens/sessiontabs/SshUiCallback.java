package muon.screens.sessiontabs;

import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public interface SshUiCallback extends
        PasswordIdentityProvider,
        PasswordAuthenticationReporter,
        UserInteraction {
    void showBlocker();
    void hideBlocker();
    boolean shouldRetry();
    void setCloseCallback(Consumer<Boolean> callback);
}
