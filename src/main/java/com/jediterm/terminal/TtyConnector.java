package com.jediterm.terminal;

import java.awt.*;
import java.io.IOException;

/**
 * Interface to tty.
 */
public interface TtyConnector {
  boolean init(Questioner q);

  void close();

  void resize(Dimension termSize, Dimension pixelSize);

  String getName();

  int read(char[] buf, int offset, int length) throws IOException;

  void write(byte[] bytes) throws IOException;

  boolean isConnected();

  void write(String string) throws IOException;

  int waitFor() throws InterruptedException;
}
