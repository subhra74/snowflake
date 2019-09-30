package snowflake.utils;

import com.jcraft.jsch.ChannelExec;
import snowflake.common.ssh.SshClient;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;

public class SudoUtils {
    private static JPasswordField passwordField = new JPasswordField(30);

    public static int runSudo(String command, SshClient sshClient) {
        StringBuilder output = new StringBuilder();
        return runSudo(command, sshClient, output);
    }

    public static int runSudo(String command, SshClient sshClient, StringBuilder output) {
        String prompt = UUID.randomUUID().toString();
        try {
            ChannelExec exec = sshClient.getExecChannel();
            String fullCommand = "sudo -S -p '" + prompt + "' " + command;
            System.out.println("Full sudo: " + fullCommand + " prompt: " + prompt);
            exec.setCommand(fullCommand);
            exec.setPty(true);
            PipedInputStream pin = new PipedInputStream();
            PipedOutputStream put = new PipedOutputStream(pin);
            //InputStream in = exec.getInputStream();
            OutputStream out = exec.getOutputStream();
            exec.setErrStream(put);
            exec.setOutputStream(put);
            exec.connect();
            StringBuilder sb = new StringBuilder();
            while (true) {
                int x = pin.read();
                if (x == -1) break;
                char ch = (char) x;
                sb.append(ch);
                output.append(ch);
                //System.out.println(sb);
                if (sb.toString().contains(prompt)) {
                    if (JOptionPane.showOptionDialog(null, new Object[]{"Root password", passwordField},
                            "Authentication", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {
                        sb = new StringBuilder();
                        out.write((new String(passwordField.getPassword()) + "\n").getBytes());
                        out.flush();
                    } else {
                        exec.disconnect();
                        pin.close();
                        out.close();
                        return -2;
                    }
                }
            }
            if (exec.getExitStatus() == -1) {
                while (exec.isConnected()) {
                    Thread.sleep(500);
                }
            }
            pin.close();
            out.close();
            return exec.getExitStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
