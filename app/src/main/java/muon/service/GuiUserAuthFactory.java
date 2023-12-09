package muon.service;

import muon.dto.session.SessionInfo;
import muon.util.AppUtils;
import muon.util.StringUtils;
import org.apache.sshd.client.auth.UserAuth;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.password.UserAuthPassword;
import org.apache.sshd.client.session.ClientSession;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuiUserAuthFactory implements UserAuthFactory {
    private GuiUserAuthPassword authPassword;

    public GuiUserAuthFactory(InputBlocker inputBlocker, SessionInfo sessionInfo) {
        authPassword = new GuiUserAuthPassword(inputBlocker, sessionInfo);
    }

    public void setInputBlocker(InputBlocker inputBlocker) {
        authPassword.setInputBlocker(inputBlocker);
    }

    @Override
    public UserAuth createUserAuth(ClientSession session) throws IOException {
        return authPassword;
    }

    @Override
    public String getName() {
        return "password";
    }

    class GuiUserAuthPassword extends UserAuthPassword {
        private InputBlocker inputBlocker;
        private SessionInfo sessionInfo;
        private AtomicBoolean initialAttempt = new AtomicBoolean(true);

        public GuiUserAuthPassword(InputBlocker inputBlocker, SessionInfo sessionInfo) {
            this.inputBlocker = inputBlocker;
            this.sessionInfo = sessionInfo;
        }

        public void setInputBlocker(InputBlocker inputBlocker) {
            this.setInputBlocker(inputBlocker);
        }

        @Override
        protected String resolveAttemptedPassword(ClientSession session, String service) throws Exception {
            System.out.println("resolveAttemptedPassword");
            if (initialAttempt.get()) {
                initialAttempt.set(false);
                var sessionPassword = sessionInfo.getPassword();
                if (!StringUtils.isEmpty(sessionPassword)) {
                    return sessionPassword;
                }
            }
            return inputBlocker.getPassword(sessionInfo.getHost(), sessionInfo.getUser());
        }
    }
}
