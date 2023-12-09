package muon.service;

import muon.dto.session.SessionInfo;
import muon.util.StringUtils;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SshCallback implements PasswordIdentityProvider,
        PasswordAuthenticationReporter,
        UserInteraction {
    private InputBlocker inputBlocker;
    private SessionInfo sessionInfo;

    public SshCallback(InputBlocker inputBlocker, SessionInfo sessionInfo) {
        this.inputBlocker = inputBlocker;
        this.sessionInfo = sessionInfo;
    }

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
        System.out.println("getUpdatedPassword");
        return null;
    }

    @Override
    public KeyPair resolveAuthPublicKeyIdentityAttempt(ClientSession session) throws Exception {
        System.out.println("resolveAuthPublicKeyIdentityAttempt");
        return null;
    }

    @Override
    public Iterable<String> loadPasswords(SessionContext session) throws IOException, GeneralSecurityException {
        System.out.println("loadPasswords");
        if (!StringUtils.isEmpty(sessionInfo.getPassword())) {
            return List.of(sessionInfo.getPassword());
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
