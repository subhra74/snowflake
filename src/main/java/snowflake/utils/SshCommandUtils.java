package snowflake.utils;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.FileInfo;
import snowflake.common.ssh.SshClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class SshCommandUtils {
    public static final String SCRIPT_START_TAG = "#----------SCRIPT START----------#";

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
