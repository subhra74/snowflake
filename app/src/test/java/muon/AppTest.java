package muon;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import muon.dto.file.FileInfo;
import muon.dto.file.FileType;
import muon.dto.session.SessionInfo;
import muon.exceptions.FSAccessException;
import muon.exceptions.FSConnectException;
import muon.service.InputBlocker;
import muon.service.SftpFileSystem;
import muon.service.SftpUploader;
import muon.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
        System.out.println(PathUtils.getParentDir("c:\\users\\Desjtop"));
        System.out.println(new File("c:\\users\\Desjtop").getParent());
        System.out.println(PathUtils.getFileName("/Users/Abc/"));
        var si = new SessionInfo();
        si.setHost("localhost");
        si.setPort(22);
        si.setUser("subhro");
        si.setPassword("Soundwave@64".toCharArray());
        var fs = new SftpFileSystem(si, new InputBlocker() {
            @Override
            public void blockInput() {

            }

            @Override
            public void unblockInput() {

            }

            @Override
            public String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo) {
                System.out.println("prompt");
                return new String[0];
            }

            @Override
            public void showBanner(String message) {

            }

            @Override
            public void showConnectionInProgress() {

            }

            @Override
            public void showRetryOption() {

            }

            @Override
            public void showError() {

            }
        });
//        var home = fs.getHome();
//        System.out.println(home);
//        fs.list(home).getFiles().stream().forEach(
//                f -> System.out.println(f.getName() + " : " + f.getPath()));
//        fs.list(home).getFolders().stream().forEach(
//                f -> System.out.println(f.getName() + " : " + f.getPath()));
        //fs.mkdir("/Users/subhro/Documents/aaa");

        var u = new SftpUploader(fs);
        u.uploadInForeground("/Users/subhro/Downloads",
                List.of(new FileInfo("/Users/subhro/Downloads/layout-CustomLayoutDemoProject",
                        "layout-CustomLayoutDemoProject", 0, LocalDateTime.now(), FileType.Directory, null)),
                "/Users/subhro/Documents/aaa", null);
    }
}
