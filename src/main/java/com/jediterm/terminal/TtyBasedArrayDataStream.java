/**
 *
 */
package com.jediterm.terminal;

import java.io.IOException;

/**
 * Takes data from and sends it back to TTY input and output streams via {@link TtyConnector}
 */
public class TtyBasedArrayDataStream extends ArrayTerminalDataStream {
  private TtyConnector myTtyConnector;

  public TtyBasedArrayDataStream(final TtyConnector ttyConnector) {
    super(new char[1024], 0, 0);
    myTtyConnector = ttyConnector;
  }

  private void fillBuf() throws IOException {
    myOffset = 0;
    myLength = myTtyConnector.read(myBuf, myOffset, myBuf.length);

    if (myLength <= 0) {
      myLength = 0;
      throw new EOF();
    }
  }

  public char getChar() throws IOException {
    if (myLength == 0) {
      fillBuf();
    }
    return super.getChar();
  }

  public String readNonControlCharacters(int maxChars) throws IOException {
    if (myLength == 0) {
      fillBuf();
    }

    return super.readNonControlCharacters(maxChars);
  }
}