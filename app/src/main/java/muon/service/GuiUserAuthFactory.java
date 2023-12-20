package muon.service;

import muon.App;
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

    public GuiUserAuthFactory(SessionInfo sessionInfo) {
        authPassword = new GuiUserAuthPassword(sessionInfo);
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
        private SessionInfo sessionInfo;
        private AtomicBoolean initialAttempt = new AtomicBoolean(true);

        public GuiUserAuthPassword(SessionInfo sessionInfo) {
            this.sessionInfo = sessionInfo;
        }

        @Override
        protected String resolveAttemptedPassword(ClientSession session, String service) throws Exception {
            System.out.println("resolveAttemptedPassword");
            if (initialAttempt.get()) {
                initialAttempt.set(false);

                var sessionPassword = AppUtils.getPassword(sessionInfo);
                if (!StringUtils.isEmpty(sessionPassword)) {
                    return sessionPassword;
                }
            }
            var password = App.getUserInputService().getPassword(sessionInfo.getHost(), sessionInfo.getUser());
            sessionInfo.setLastPassword(password);
            return password;
        }
    }
}
