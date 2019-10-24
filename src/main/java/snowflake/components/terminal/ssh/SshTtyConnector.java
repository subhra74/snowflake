package snowflake.components.terminal.ssh;

import java.awt.*;
import java.io.*;
import java.util.concurrent.atomic.*;

import com.jcraft.jsch.*;
import com.jediterm.terminal.*;
import snowflake.App;
import snowflake.common.ssh.SshClient;
import snowflake.common.ssh.SshUserInteraction;

public class SshTtyConnector implements DisposableTtyConnector {
    private InputStreamReader myInputStreamReader;
    private InputStream myInputStream = null;
    private OutputStream myOutputStream = null;
    private ChannelShell channel;
    private AtomicBoolean isInitiated = new AtomicBoolean(false);
    private SshUserInteraction source;
    private AtomicBoolean isCancelled = new AtomicBoolean(false);
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private Dimension myPendingTermSize;
    private Dimension myPendingPixelSize;
    private SshClient wr;
    private String initialCommand;

    public SshTtyConnector(SshUserInteraction source, String initialCommand) {
        this.initialCommand = initialCommand;
        this.source = source;
    }

    public SshTtyConnector(SshUserInteraction source) {
        this(source, null);
    }

    @Override
    public boolean init(Questioner q) {
        try {
            this.wr = new SshClient(source);
            this.wr.connect();
            this.channel = wr.getShellChannel();

            String lang = System.getenv().get("LANG");
            channel.setEnv("LANG", lang != null ? lang : "en_US.UTF-8");
            channel.setPtyType(App.getGlobalSettings().getTerminalType());
            //channel.setPtyType("xterm-256color");

            PipedOutputStream pout1 = new PipedOutputStream();
            PipedInputStream pin1 = new PipedInputStream(pout1);
            channel.setOutputStream(pout1);

            PipedOutputStream pout2 = new PipedOutputStream();
            PipedInputStream pin2 = new PipedInputStream(pout2);
            channel.setInputStream(pin2);

            myInputStream = pin1;// channel.getInputStream();
            myOutputStream = pout2;// channel.getOutputStream();
            myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
            channel.connect();

            resizeImmediately();
            System.out.println("Initiated");

            if (initialCommand != null) {
                pout2.write((initialCommand + "\n").getBytes("utf-8"));
            }

            // resize(termSize, pixelSize);
            isInitiated.set(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            isInitiated.set(false);
            isCancelled.set(true);
            return false;
        }
    }

    @Override
    public void close() {
        try {
            stopFlag.set(true);
            System.out.println("Terminal wrapper disconnecting");
            wr.disconnect();
        } catch (Exception e) {
        }
    }

    @Override
    public void resize(Dimension termSize, Dimension pixelSize) {
        myPendingTermSize = termSize;
        myPendingPixelSize = pixelSize;
        if (channel != null) {
            resizeImmediately();
        }

//		if (channel == null) {
//			return;
//		}
//		System.out.println("Terminal resized");
//		channel.setPtySize(termSize.width, termSize.height, pixelSize.width, pixelSize.height);
    }

    @Override
    public String getName() {
        return "Remote";
    }

    @Override
    public int read(char[] buf, int offset, int length) throws IOException {
        return myInputStreamReader.read(buf, offset, length);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        myOutputStream.write(bytes);
        myOutputStream.flush();
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isConnected() && isInitiated.get();
    }

    @Override
    public void write(String string) throws IOException {
        write(string.getBytes("utf-8"));
    }

    @Override
    public int waitFor() throws InterruptedException {
        System.out.println("Start waiting...");
        while (!isInitiated.get() || isRunning(channel)) {
            System.out.println("waiting");
            Thread.sleep(100); // TODO: remove busy wait
        }
        System.out.println("waiting exit");
        return channel.getExitStatus();
    }

    public boolean isRunning(Channel channel) {
        return channel != null && channel.getExitStatus() < 0
                && channel.isConnected();
    }

    public boolean isBusy() {
        return channel.getExitStatus() < 0 && channel.isConnected();
    }

    public boolean isCancelled() {
        return isCancelled.get();
    }

    public void stop() {
        stopFlag.set(true);
        close();
    }

    public int getExitStatus() {
        if (channel != null) {
            return channel.getExitStatus();
        }
        return -2;
    }

    private void resizeImmediately() {
        if (myPendingTermSize != null && myPendingPixelSize != null) {
            setPtySize(channel, myPendingTermSize.width,
                    myPendingTermSize.height, myPendingPixelSize.width,
                    myPendingPixelSize.height);
            myPendingTermSize = null;
            myPendingPixelSize = null;
        }
    }

    private void setPtySize(ChannelShell channel, int col, int row, int wp,
                            int hp) {
        System.out.println("Exec pty resized:- col: " + col + " row: " + row
                + " wp: " + wp + " hp: " + hp);
        channel.setPtySize(col, row, wp, hp);
    }

    @Override
    public boolean isInitialized() {
        return isInitiated.get();
    }

}