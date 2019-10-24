package snowflake.components.files.browser.sftp;

import snowflake.common.ssh.files.SshFileSystem;
import snowflake.utils.PathUtils;

import javax.swing.*;
import java.io.File;

public class SftpFileOperations {
    public boolean rename(SshFileSystem fs, String oldName, String newName) {
        try {
            fs.rename(oldName, newName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean newFile(SshFileSystem fs, String folder) {
        String text = JOptionPane.showInputDialog("New file");
        if (text == null || text.length() < 1) {
            return false;
        }
        try {
            fs.createFile(PathUtils.combine(folder, text, File.separator));
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to create new file");
        }
        return false;
    }

    public boolean newFolder(SshFileSystem fs, String folder) {
        String text = JOptionPane.showInputDialog("New folder name");
        if (text == null || text.length() < 1) {
            return false;
        }
        try {
            fs.mkdir(PathUtils.combine(folder, text, fs.getSeparator()));
            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to create new folder");
        }
        return false;
    }
}
