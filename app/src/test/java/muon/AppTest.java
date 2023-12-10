package muon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import muon.dto.file.FileInfo;
import muon.dto.file.FileType;
import muon.dto.session.SavedSessionTree;
import muon.dto.session.SessionFolder;
import muon.dto.session.SessionInfo;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.service.*;
import muon.util.AESUtils;
import muon.util.PathUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.UserAuth;
import org.apache.sshd.client.auth.UserAuthFactory;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordAuthenticationReporter;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.auth.password.UserAuthPassword;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.contrib.client.auth.password.InteractivePasswordIdentityProvider;
import org.apache.sshd.core.CoreModuleProperties;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

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

    public void testEncryptDecrypt() throws Exception {
        SessionInfo info = new SessionInfo();
        info.setId(UUID.randomUUID().toString());
        info.setHost("localhost");
        info.setPort(22);
        info.setUser("user");
        info.setPassword("password");

        SessionFolder folder = new SessionFolder();
        folder.setId(UUID.randomUUID().toString());
        folder.setName("Sessions");
        folder.setItems(List.of(info));

        SavedSessionTree tree = new SavedSessionTree();
        tree.setFolder(folder);

        var temp = File.createTempFile("aaa", "bbb");

        AESUtils.encrypt(temp, "abcd1234", tree);
        var ret = AESUtils.decrypt(temp, "abcd1234");
        System.out.println(ret.getFolder().getName());
    }

//    public void testApacheSshClient() throws Exception {
//        var ui = new UserInteraction() {
//            @Override
//            public KeyPair resolveAuthPublicKeyIdentityAttempt(ClientSession session) throws Exception {
//                System.out.println("resolveAuthPublicKeyIdentityAttempt");
//                return null;
//            }
//
//            @Override
//            public String resolveAuthPasswordAttempt(ClientSession session) throws Exception {
//                System.out.println("resolveAuthPasswordAttempt");
//                return "null1";
//            }
//
//            @Override
//            public void welcome(ClientSession session, String banner, String lang) {
//                System.out.println("welcome");
//            }
//
//            @Override
//            public String getUpdatedPassword(ClientSession session, String prompt, String lang) {
//                System.out.println("getUpdatedPassword");
//                return "null2";
//            }
//
//            @Override
//            public boolean isInteractionAllowed(ClientSession session) {
//                return true;
//            }
//
//            @Override
//            public void serverVersionInfo(ClientSession session, List<String> lines) {
//                System.out.println("serverVersionInfo " + String.join(" ", lines));
//                // do nothing
//            }
//
//            @Override
//            public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo) {
//                System.out.println("interactive----");
//                System.out.println("name " + name);
//                System.out.println("instruction " + instruction);
//                System.out.println("lang " + lang);
//                System.out.println("prompt " + String.join(" ", prompt));
//                return new String[]{"sdfsdf"};
//            }
//        };
//
//
//        var client = SshClient.setUpDefaultClient();
//
////        var list2 = new ArrayList<UserAuthFactory>();
////        list2.add(new UserAuthFactory() {
////            @Override
////            public UserAuth createUserAuth(ClientSession session) throws IOException {
////                return new UserAuthPassword() {
////                    @Override
////                    protected String resolveAttemptedPassword(ClientSession session, String service) throws Exception {
////                        System.out.println("resolveAttemptedPassword");
////                        return "some new value";
////                    }
////                };
////            }
////
////            @Override
////            public String getName() {
////                return "password";
////            }
////        });
////        client.setUserAuthFactories(list2);
//        client.start();
//        CoreModuleProperties.PASSWORD_PROMPTS.set(client, 3);
//        CoreModuleProperties.CHANNEL_OPEN_TIMEOUT.set(client, Duration.ZERO);
//        var session = client.connect(
//                        "subhro",
//                        "localhost",
//                        22)
//                .verify().getSession();
//        session.setPasswordAuthenticationReporter(new PasswordAuthenticationReporter() {
//            @Override
//            public void signalAuthenticationAttempt(ClientSession session, String service, String oldPassword, boolean modified, String newPassword) throws Exception {
//                System.out.println("signalAuthenticationAttempt: " + newPassword);
//            }
//
//            @Override
//            public void signalAuthenticationFailure(ClientSession session, String service, String password, boolean partial, List<String> serverMethods) throws Exception {
//                System.out.println("signalAuthenticationFailure: " + serverMethods);
//            }
//
//            @Override
//            public void signalAuthenticationExhausted(ClientSession session, String service) throws Exception {
//                System.out.println("signalAuthenticationExhausted: " + service);
//            }
//        });
////        session.setPasswordIdentityProvider(new PasswordIdentityProvider() {
////            @Override
////            public Iterable<String> loadPasswords(SessionContext session) throws IOException, GeneralSecurityException {
////                return List.of("some value");
////            }
////        });
//
//        PasswordIdentityProvider passwordIdentityProvider =
//                InteractivePasswordIdentityProvider.providerOf(session, ui, "My prompt");
//        session.setPasswordIdentityProvider(passwordIdentityProvider);
//
//        var factList = session.getUserAuthFactories();
//        System.out.println(factList);
//
//        session.auth().verify();
//    }
//
//    public void testApp2() throws IOException {
//        var si = new SessionInfo();
//        si.setHost("192.168.29.169");
//        si.setPort(22);
//        si.setUser("subhro");
//        si.setPassword("suo");
//
//        var out = new ByteArrayOutputStream();
//
//        var terminal = new SshTerminalClient(si, new InputBlocker() {
//            @Override
//            public String getPassword(String host, String user) {
//                return null;
//            }
//
//            @Override
//            public void blockInput() {
//
//            }
//
//            @Override
//            public void unblockInput() {
//
//            }
//
//            @Override
//            public String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo) {
//                System.out.println("prompt");
//                return new String[0];
//            }
//
//            @Override
//            public void showBanner(String message) {
//
//            }
//
//            @Override
//            public void showConnectionInProgress() {
//
//            }
//
//            @Override
//            public void showRetryOption() {
//
//            }
//
//            @Override
//            public void showError() {
//
//            }
//        }, new ByteArrayInputStream(new byte[0]), out);
//        terminal.start();
//        terminal.resize(32, 132);
//        System.out.println(out.toString("utf-8"));
//    }
//
//    /**
//     * Rigourous Test :-)
//     */
//    public void testApp() throws IOException {
//        System.out.println(PathUtils.getParentDir("c:\\users\\Desjtop"));
//        System.out.println(new File("c:\\users\\Desjtop").getParent());
//        System.out.println(PathUtils.getFileName("/Users/Abc/"));
//        var si = new SessionInfo();
//        si.setHost("localhost");
//        si.setPort(22);
//        si.setUser("subhro");
//        si.setPassword("Soundwave@64");
//        var fs = new SftpFileSystem(si, new InputBlocker() {
//            @Override
//            public String getPassword(String host, String user) {
//                return null;
//            }
//
//            @Override
//            public void blockInput() {
//
//            }
//
//            @Override
//            public void unblockInput() {
//
//            }
//
//            @Override
//            public String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo) {
//                System.out.println("prompt");
//                return new String[0];
//            }
//
//            @Override
//            public void showBanner(String message) {
//
//            }
//
//            @Override
//            public void showConnectionInProgress() {
//
//            }
//
//            @Override
//            public void showRetryOption() {
//
//            }
//
//            @Override
//            public void showError() {
//
//            }
//        });
////        var home = fs.getHome();
////        System.out.println(home);
////        fs.list(home).getFiles().stream().forEach(
////                f -> System.out.println(f.getName() + " : " + f.getPath()));
////        fs.list(home).getFolders().stream().forEach(
////                f -> System.out.println(f.getName() + " : " + f.getPath()));
//        //fs.mkdir("/Users/subhro/Documents/aaa");
//
//        var u = new SftpUploader(fs);
//        u.uploadInForeground("/Users/subhro/Downloads",
//                List.of(new FileInfo("/Users/subhro/Downloads/layout-CustomLayoutDemoProject",
//                        "layout-CustomLayoutDemoProject", 0, LocalDateTime.now(), FileType.Directory, null)),
//                "/Users/subhro/Documents/aaa", null);
//    }
}
