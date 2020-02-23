package com.jediterm.terminal.emulator;

import java.io.IOException;

/**
 * @author traff
 */
public interface Emulator {
  boolean hasNext();

  void next() throws IOException;

  void resetEof();
}
