package muon.screens.appwin.tabs.terminal;

import com.jediterm.core.util.TermSize;
import com.jediterm.terminal.TtyConnector;
import muon.dto.session.SessionInfo;
import muon.exceptions.FSConnectException;
import muon.service.InputBlocker;
import muon.service.SshTerminalClient;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SshTtyConnector implements TtyConnector {
    private InputStreamReader myInputStreamReader;
    private PipedOutputStream myOutputStream;
    private AtomicBoolean isInitiated = new AtomicBoolean(false);
    private AtomicBoolean isCancelled = new AtomicBoolean(false);
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private SshTerminalClient wr;
    private String initialCommand;
    private AtomicBoolean isReady = new AtomicBoolean(false);

    public SshTtyConnector(String initialCommand, SessionInfo sessionInfo, InputBlocker inputBlocker) {
        this.initialCommand = initialCommand;

        try {
            this.myOutputStream = new PipedOutputStream();
            var pipeOut = new PipedOutputStream();
            this.myInputStreamReader = new InputStreamReader(new PipedInputStream(pipeOut), "utf-8");
            this.wr = new SshTerminalClient(sessionInfo, inputBlocker,
                    new PipedInputStream(myOutputStream), pipeOut);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        isInitiated.set(true);
    }

    public void start() throws FSConnectException {
        System.out.println("Start tty connector");
        wr.start();
    }

    @Override
    public void close() {
        System.err.println(Thread.currentThread());
        try {
            stopFlag.set(true);
            System.out.println("Terminal wrapper disconnecting");
            wr.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void resize(@NotNull TermSize termSize) {
        System.out.println(Thread.currentThread() + " " + SwingUtilities.isEventDispatchThread());
        wr.resize(termSize.getRows(), termSize.getColumns());
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
        return wr.isConnected();
    }

    @Override
    public void write(String string) throws IOException {
        write(string.getBytes("utf-8"));
    }

    @Override
    public int waitFor() throws InterruptedException {
        System.out.println("Waiting for session to end: " + Thread.currentThread());
        try{
            return wr.waitFor();
        }finally {
            System.out.println("Session end: " + Thread.currentThread());
        }
    }

    @Override
    public boolean ready() throws IOException {
        return isReady.get();
    }

}