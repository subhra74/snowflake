package snowflake.components.files.ssh;

import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.ssh.SshClient;
import snowflake.utils.PathUtils;
import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;

public class SftpFileOperations {

    public SftpFileOperations() {
    }

    public boolean rename(String oldName, String newName, FileSystem fs, SshClient client) {
        try {
            fs.rename(oldName, newName);
            return true;
        } catch (AccessDeniedException e) {
            e.printStackTrace();
            if (JOptionPane.showConfirmDialog(null, "Access denied, rename using sudo?", "Use sudo?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                return renameWithPrivilege(oldName, newName, client);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Operation failed");
            return false;
        }
    }

    private boolean renameWithPrivilege(String oldName, String newName, SshClient client) {
        StringBuilder command = new StringBuilder();
        command.append("mv \"" + oldName + "\" \"" + newName + "\"");
        System.out.println("Invoke sudo: " + command);
        int ret = SudoUtils.runSudo(command.toString(), client);
        if (ret == -1) {
            JOptionPane.showMessageDialog(null, "Operation failed");
        }
        return ret == 0;
    }

    public boolean delete(FileInfo[] targetList, FileSystem fs, SshClient client) {
        try {
            try {
                SshCommandUtils.delete(Arrays.asList(targetList),
                        client);
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("delete: file not found");
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                for (FileInfo s : targetList) {
                    fs.delete(s);
                }
                return true;
            }
        } catch (FileNotFoundException | AccessDeniedException e) {
            e.printStackTrace();
            return deletePrivilege(targetList, client);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting file");
            return false;
        }
    }

    private boolean deletePrivilege(FileInfo[] targetList, SshClient client) {
        StringBuilder sb = new StringBuilder("rm -rf ");
        for (FileInfo file : targetList) {
            sb.append("\"" + file.getPath() + "\" ");
        }

        System.out.println("Invoke sudo: " + sb.toString());
        int ret = SudoUtils.runSudo(sb.toString(), client);
        if (ret == -1) {
            JOptionPane.showMessageDialog(null, "Operation failed");
        }
        return ret == 0;
    }

    public boolean newFile(FileInfo[] files, FileSystem fs, String folder, SshClient client) {
        String text = JOptionPane.showInputDialog("New file");
        if (text == null || text.length() < 1) {
            return false;
        }
        boolean alreadyExists = false;
        for (FileInfo f : files) {
            if (f.getName().equals(text)) {
                alreadyExists = true;
                break;
            }
        }
        if (alreadyExists) {
            JOptionPane.showMessageDialog(null, "File with same name already exists");
            return false;
        }
        try {
            fs.createFile(PathUtils.combineUnix(folder, text));
            return true;
        } catch (AccessDeniedException e1) {
            e1.printStackTrace();
            if (!touchWithPrivilege(folder, text, client)) {
                JOptionPane.showMessageDialog(null, "Unable to create new file");
                return false;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to create new file");
        }
        return false;
    }

    private boolean touchWithPrivilege(String path, String newFile, SshClient client) {
        String file = PathUtils.combineUnix(path, newFile);
        StringBuilder command = new StringBuilder();
        command.append("touch \"" + file + "\"");
        System.out.println("Invoke sudo: " + command);
        int ret = SudoUtils.runSudo(command.toString(), client);
        if (ret == -1) {
            JOptionPane.showMessageDialog(null, "Operation failed");
        }
        return ret == 0;
    }

    public boolean newFolder(FileInfo[] files, String folder, FileSystem fs, SshClient client) {
        String text = JOptionPane.showInputDialog("New folder name");
        if (text == null || text.length() < 1) {
            return false;
        }
        boolean alreadyExists = false;
        for (FileInfo f : files) {
            if (f.getName().equals(text)) {
                alreadyExists = true;
                break;
            }
        }
        if (alreadyExists) {
            JOptionPane.showMessageDialog(null, "File with same name already exists");
            return false;
        }
        try {
            fs.mkdir(PathUtils.combineUnix(folder, text));
            return true;
        } catch (AccessDeniedException e1) {
            e1.printStackTrace();
            if (!mkdirWithPrivilege(folder, text, client)) {
                JOptionPane.showMessageDialog(null, "Unable to create new folder");
                return false;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Unable to create new folder");
        }
        return false;
    }

    private boolean mkdirWithPrivilege(String path, String newFolder, SshClient client) {
        String file = PathUtils.combineUnix(path, newFolder);
        StringBuilder command = new StringBuilder();
        command.append("mkdir \"" + file + "\"");
        System.out.println("Invoke sudo: " + command);
        int ret = SudoUtils.runSudo(command.toString(), client);
        if (ret == -1) {
            JOptionPane.showMessageDialog(null, "Operation failed");
        }
        return ret == 0;
    }

    public boolean createLink(FileInfo[] files, FileSystem fs, SshClient client) {
        JTextField txtLinkName = new JTextField(30);
        JTextField txtFileName = new JTextField(30);
        JCheckBox chkHardLink = new JCheckBox("Hardlink");

        if (files.length > 0) {
            FileInfo info = files[0];
            txtFileName.setText(info.getPath());
        }

        if (JOptionPane.showOptionDialog(null,
                new Object[]{"Create link", txtLinkName,
                        "File name", txtFileName, chkHardLink},
                "Create link", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, null, null) == JOptionPane.OK_OPTION) {
            if (txtLinkName.getText().length() > 0 && txtFileName.getText().length() > 0) {
                return createLinkAsync(txtFileName.getText(), txtLinkName.getText(), chkHardLink.isSelected(), fs);
            }
        }
        return false;
    }

    private boolean createLinkAsync(String src, String dst, boolean hardLink, FileSystem fs) {
        try {
            fs.createLink(src, dst, hardLink);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
