package muon.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;
import org.apache.sshd.core.CoreModuleProperties;

import java.io.IOException;
import java.security.KeyPair;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws IOException {
        var client = SshClient.setUpDefaultClient();
        CoreModuleProperties.PASSWORD_PROMPTS.set(client, 10);
        client.addSessionListener(new SessionListener() {
            @Override
            public void sessionDisconnect(Session session, int reason, String msg, String language, boolean initiator) {
                System.out.println("sessionDisconnect");
            }

            @Override
            public void sessionClosed(Session session) {
                System.out.println("sessionClosed");
            }

            @Override
            public void sessionException(Session session, Throwable t) {
                System.err.println("sessionException");
            }
        });
        client.setUserInteraction(new UserInteraction() {
            @Override
            public KeyPair resolveAuthPublicKeyIdentityAttempt(ClientSession session) throws Exception {
                System.out.println("resolveAuthPublicKeyIdentityAttempt");
                return null;
            }

            @Override
            public String resolveAuthPasswordAttempt(ClientSession session) throws Exception {
                System.out.println("resolveAuthPasswordAttempt");
                return "null";
            }

            @Override
            public void welcome(ClientSession session, String banner, String lang) {
                System.out.println("welcome");
            }

            @Override
            public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
                System.out.println("getUpdatedPassword");
                return null;
            }

            @Override
            public boolean isInteractionAllowed(ClientSession session) {
                return true;
            }

            @Override
            public void serverVersionInfo(ClientSession session, List<String> lines) {
                System.out.println("serverVersionInfo " + String.join(" ", lines));
                // do nothing
            }

            @Override
            public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
                System.out.println("interactive----");
                System.out.println("name " + name);
                System.out.println("instruction " + instruction);
                System.out.println("lang " + lang);
                System.out.println("prompt " + String.join(" ", prompt));
                return new String[]{"sdfsdf"};
            }
        });
        client.setPasswordAuthenticationReporter(new PasswordAuthenticationReporter() {
            @Override
            public void signalAuthenticationAttempt(ClientSession session, String service, String oldPassword, boolean modified, String newPassword) throws Exception {
                System.out.println("signalAuthenticationAttempt");
                System.out.println("service: " + service);
                System.out.println("oldPassword: " + oldPassword);
                System.out.println("modified: " + modified);
                System.out.println("newPassword: " + newPassword);
            }

            @Override
            public void signalAuthenticationExhausted(ClientSession session, String service) throws Exception {
                System.out.println("signalAuthenticationExhausted");
                System.out.println("service: " + service);
            }

            @Override
            public void signalAuthenticationSuccess(ClientSession session, String service, String password) throws Exception {
                System.out.println("signalAuthenticationSuccess");
                System.out.println("service: " + service);
                System.out.println("password: " + password);
            }

            @Override
            public void signalAuthenticationFailure(ClientSession session, String service, String password, boolean partial, List<String> serverMethods) throws Exception {
                System.out.println("signalAuthenticationFailure");
                System.out.println("service: " + service);
                System.out.println("password: " + password);
                System.out.println("partial: " + partial);
                System.out.println("serverMethods: " + String.join(" ", serverMethods));
            }
        });

        try{
            client.start();
            System.out.println(client.getUserAuthFactories());
            var session = client.connect(
                            "subhro",
                            "localhost",
                            22)
                    .verify().getSession();
            session.auth().verify();


            System.out.println("Retry");


            client.start();
            System.out.println(client.getUserAuthFactories());
             session = client.connect(
                            "subhro",
                            "localhost",
                            22)
                    .verify().getSession();
            session.auth().verify();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try{
            System.out.println("Retry");


            //client.start();
            System.out.println(client.getUserAuthFactories());
            var session = client.connect(
                            "subhro",
                            "localhost",
                            22)
                    .verify().getSession();
            session.auth().verify();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
