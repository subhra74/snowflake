package com.jediterm.terminal;

////import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author traff
 */
public abstract class ProcessTtyConnector implements TtyConnector {
  protected final InputStream myInputStream;
  protected final OutputStream myOutputStream;
  protected final InputStreamReader myReader;
  protected Charset myCharset;
  private Dimension myPendingTermSize;
  private Dimension myPendingPixelSize;
  private Process myProcess;

  public ProcessTtyConnector( Process process, Charset charset) {
    myOutputStream = process.getOutputStream();
    myCharset = charset;
    myInputStream = process.getInputStream();
    myReader = new InputStreamReader(myInputStream, charset);
    myProcess = process;
  }

  
  public Process getProcess() {
    return myProcess;
  }

  @Override
  public void resize(Dimension termSize, Dimension pixelSize) {
    setPendingTermSize(termSize);
    setPendingPixelSize(pixelSize);
    if (isConnected()) {
      resizeImmediately();
      setPendingTermSize(null);
      setPendingPixelSize(null);
    }
  }

  protected abstract void resizeImmediately();

  @Override
  public abstract String getName();

  public int read(char[] buf, int offset, int length) throws IOException {
    return myReader.read(buf, offset, length);
    //return myInputStream.read(buf, offset, length);
  }

  public void write(byte[] bytes) throws IOException {
    myOutputStream.write(bytes);
    myOutputStream.flush();
  }

  @Override
  public abstract boolean isConnected();

  @Override
  public void write(String string) throws IOException {
    write(string.getBytes(myCharset));
  }

  protected void setPendingTermSize(Dimension pendingTermSize) {
    this.myPendingTermSize = pendingTermSize;
  }

  protected void setPendingPixelSize(Dimension pendingPixelSize) {
    this.myPendingPixelSize = pendingPixelSize;
  }

  protected Dimension getPendingTermSize() {
    return myPendingTermSize;
  }

  protected Dimension getPendingPixelSize() {
    return myPendingPixelSize;
  }

  @Override
  public boolean init(Questioner q) {
    return isConnected();
  }

  @Override
  public void close() {
    myProcess.destroy();
    try {
      myOutputStream.close();
    }
    catch (IOException ignored) { }
    try {
      myInputStream.close();
    }
    catch (IOException ignored) { }
  }

  @Override
  public int waitFor() throws InterruptedException {
    return myProcess.waitFor();
  }
}
