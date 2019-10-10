package snowflake.utils;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.FileInfo;
import snowflake.common.ssh.SshClient;
import snowflake.components.taskmgr.TaskManager;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

public class SshCommandUtils {

    public static final String SCRIPT_START_TAG = "#----------SCRIPT START----------#";

    public static boolean exec(SshClient client,
                               String command,
                               AtomicBoolean stopFlag,
                               OutputStream outputStream,
                               StringBuilder error) {
        if (stopFlag.get()) {
            return false;
        }
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            ChannelExec exec = client.getExecChannel();
            exec.setCommand(command);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            exec.setOutputStream(outputStream);
            exec.setErrStream(bout);
            exec.connect();
            while (exec.isConnected()) {
                if (stopFlag.get()) {
                    exec.disconnect();
                    break;
                }
                Thread.sleep(500);
            }
            error.append(new String(bout.toByteArray(), "utf-8"));
            return exec.getExitStatus() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean exec(SshClient client, String command, AtomicBoolean stopFlag, StringBuilder output) {
        if (stopFlag.get()) {
            return false;
        }
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            ChannelExec exec = client.getExecChannel();
            exec.setCommand(command);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            exec.setOutputStream(bout);
            exec.setErrStream(bout);
            exec.connect();
            while (exec.isConnected()) {
                if (stopFlag.get()) {
                    exec.disconnect();
                    break;
                }
                Thread.sleep(500);
            }
            output.append(new String(bout.toByteArray(), "utf-8"));
            return exec.getExitStatus() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static final int executeCommand(SshClient wrapper, String command,
                                           boolean compressed, List<String> output) {
        System.out.println("Executing: " + command);
        ChannelExec exec = null;
        List<String> buffer = new ArrayList<>();
        try {
            exec = wrapper.getExecChannel();
            InputStream in = exec.getInputStream();
            exec.setCommand(command);
            exec.connect();

            InputStream inStream = compressed ? new GZIPInputStream(in) : in;
            StringBuilder sb = new StringBuilder();
            while (true) {
                int x = inStream.read();

                if (x == '\n') {
                    buffer.add(sb.toString());
                    sb = new StringBuilder();
                }
                if (x != -1 && x != '\n')
                    sb.append((char) x);

                if (exec.getExitStatus() != -1 && x == -1) {
                    break;
                }
            }

            in.close();
            int ret = exec.getExitStatus();
            System.err.println("Exit code: " + ret);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            boolean scriptStart = false;
            for (String str : buffer) {
                if (scriptStart) {
                    output.add(str);
                }
                if (!scriptStart && str.equals(SCRIPT_START_TAG)) {
                    scriptStart = true;
                }
            }
            if (exec != null) {
                exec.disconnect();
            }
        }
    }

    public static void delete(List<FileInfo> files, SshClient sshClient)
            throws Exception {
        ChannelExec exec = null;
        try {
            exec = sshClient.getExecChannel();
            StringBuilder sb = new StringBuilder();

            for (FileInfo file : files) {
                sb.append("\"" + file.getPath() + "\" ");
            }

            System.out.println("Delete command1: rm -rf " + sb);

            exec.setCommand("rm -rf " + sb);
            exec.connect();
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(exec.getInputStream()));
            while (true) {
                String s = r.readLine();
                if (s == null && exec.getExitStatus() >= 0) {
                    break;
                }
            }
            r.close();
            int exit = exec.getExitStatus();
            System.out.println("exit: " + exit);
            if (exit != 0) {
                throw new FileNotFoundException();
            }
        } finally {
            try {
                exec.disconnect();
            } catch (Exception e2) {
            }
        }
    }
}
