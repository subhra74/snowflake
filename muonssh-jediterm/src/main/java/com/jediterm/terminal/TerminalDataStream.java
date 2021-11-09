package com.jediterm.terminal;

import java.io.IOException;

/**
 * Represents data communication interface for terminal.
 * It allows to {@link #getChar()} by one and {@link #pushChar(char)} back as well as requesting a chunk of plain ASCII
 * characters ({@link #readNonControlCharacters(int)} - for faster processing from buffer in the size <=<b>maxChars</b>).
 *
 *
 * @author traff
 */
public interface TerminalDataStream {
  char getChar() throws IOException;

  void pushChar(char c) throws IOException;

  String readNonControlCharacters(int maxChars) throws IOException;

  void pushBackBuffer(char[] bytes, int length) throws IOException;

  class EOF extends IOException {
    public EOF() {
      super("EOF: There is no more data or connection is lost");
    }
  }
}
