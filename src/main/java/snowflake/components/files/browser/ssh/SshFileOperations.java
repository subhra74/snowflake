package snowflake.components.files.browser.ssh;

import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.common.FileType;
import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.files.SshFileSystem;
import snowflake.utils.PathUtils;
import snowflake.utils.SshCommandUtils;
import snowflake.utils.SudoUtils;
import snowflake.utils.TimeUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshFileOperations {

    public SshFileOperations() {
    }

    public boolean runScriptInBackground(SshClient client, String command, AtomicBoolean stopFlag) {
        System.out.println("Invoke command: " + command);
        StringBuilder output = new StringBuilder();
        boolean ret = SshCommandUtils.exec(client, command, stopFlag, output);
        System.out.println("output: " + output.toString());
        return ret;
    }

    public boolean moveTo(SshClient client, List<FileInfo> files, String targetFolder, FileSystem fs) throws Exception {
        List<FileInfo> fileList = fs.list(targetFolder);
        List<FileInfo> dupList = new ArrayList<>();
        for (FileInfo file : files) {
            for (FileInfo file1 : fileList) {
                if (file.getName().equals(file1.getName())) {
                    dupList.add(file);
                }
            }
        }

        int action = -1;
        if (dupList.size() > 0) {
            JComboBox<String> cmbs = new JComboBox<>(new String[]{"Auto rename", "Overwrite"});
            if (JOptionPane.showOptionDialog(null, new Object[]{"Some file with the same name already exists. Please choose an action",
                    cmbs}, "Action required", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                action = cmbs.getSelectedIndex();
            } else {
                return false;
            }
        }

        StringBuilder command = new StringBuilder();
        for (FileInfo fileInfo : files) {
            if (fileInfo.getType() == FileType.DirLink || fileInfo.getType() == FileType.Directory) {
                command.append("mv ");
            } else {
                command.append("mv -T ");
            }
            command.append("\"" + fileInfo.getPath() + "\" ");
            if (dupList.contains(fileInfo) && action == 0) {
                command.append("\"" + PathUtils.combineUnix(targetFolder, getUniqueName(fileList, fileInfo.getName())) + "\"; ");
            } else {
                command.append("\"" + PathUtils.combineUnix(targetFolder, fileInfo.getName()) + "\"; ");
            }
        }

        System.out.println("Move: " + command);
        if (!SshCommandUtils.exec(client, command.toString(), new AtomicBoolean(false), new StringBuilder())) {
            int ret = SudoUtils.runSudo(command.toString(), client);
            if (ret == -1) {
                JOptionPane.showMessageDialog(null, "Operation failed");
            } else {
                return ret == 0;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean copyTo(SshClient client, List<FileInfo> files, String targetFolder, FileSystem fs) throws Exception {
        List<FileInfo> fileList = fs.list(targetFolder);
        List<FileInfo> dupList = new ArrayList<>();
        for (FileInfo file : files) {
            for (FileInfo file1 : fileList) {
                if (file.getName().equals(file1.getName())) {
                    dupList.add(file);
                }
            }
        }

        int action = -1;
        if (dupList.size() > 0) {
            JComboBox<String> cmbs = new JComboBox<>(new String[]{"Auto rename", "Overwrite"});
            if (JOptionPane.showOptionDialog(null, new Object[]{"Some file with the same name already exists. Please choose an action",
                    cmbs}, "Action required", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
                action = cmbs.getSelectedIndex();
            } else {
                return false;
            }
        }

        StringBuilder command = new StringBuilder();
        for (FileInfo fileInfo : files) {
            if (fileInfo.getType() == FileType.DirLink || fileInfo.getType() == FileType.Directory) {
                command.append("cp -rf ");
            } else {
                command.append("cp -Tf ");
            }
            command.append("\"" + fileInfo.getPath() + "\" ");
            if (dupList.contains(fileInfo) && action == 0) {
                command.append("\"" + PathUtils.combineUnix(targetFolder, getUniqueName(fileList, fileInfo.getName())) + "\"; ");
            } else {
                command.append("\"" + PathUtils.combineUnix(targetFolder, fileInfo.getName()) + "\"; ");
            }
        }

        System.out.println("Copy: " + command);
        if (!SshCommandUtils.exec(client, command.toString(), new AtomicBoolean(false), new StringBuilder())) {
            int ret = SudoUtils.runSudo(command.toString(), client);
            if (ret == -1) {
                JOptionPane.showMessageDialog(null, "Operation failed");
            } else {
                return ret == 0;
            }
        } else {
            return true;
        }
        return false;
    }

    private String getUniqueName(List<FileInfo> list, String name) {
        while (true) {
            boolean found = false;
            for (FileInfo f : list) {
                if (name.equals(f.getName())) {
                    name = "Copy of " + name;
                    found = true;
                    break;
                }
            }
            if (!found) break;
        }
        return name;
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
