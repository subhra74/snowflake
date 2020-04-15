package com.jediterm.terminal;

import com.jediterm.terminal.util.CharUtils;

import java.io.IOException;

/**
 * Takes data from underlying char array.
 *
 * @author traff
 */
public class ArrayTerminalDataStream implements TerminalDataStream {
  protected char[] myBuf;
  protected int myOffset;
  protected int myLength;

  public ArrayTerminalDataStream(char[] buf, int offset, int length) {
    myBuf = buf;
    myOffset = offset;
    myLength = length;
  }

  public ArrayTerminalDataStream(char[] buf) {
    this(buf, 0, buf.length);
  }

  public char getChar() throws IOException {
    if (myLength == 0) {
      throw new EOF();
    }

    myLength--;

    return myBuf[myOffset++];
  }

  public void pushChar(final char c) throws EOF {
    if (myOffset == 0) {
      // Pushed back too many... shift it up to the end.

      char[] newBuf;
      if (myBuf.length - myLength == 0) {
        newBuf = new char[myBuf.length + 1];
      }
      else {
        newBuf = myBuf;
      }
      myOffset = newBuf.length - myLength;
      System.arraycopy(myBuf, 0, newBuf, myOffset, myLength);
      myBuf = newBuf;
    }

    myLength++;
    myBuf[--myOffset] = c;
  }

  public String readNonControlCharacters(int maxChars) throws IOException {
    String nonControlCharacters = CharUtils.getNonControlCharacters(maxChars, myBuf, myOffset, myLength);

    myOffset += nonControlCharacters.length();
    myLength -= nonControlCharacters.length();

    return nonControlCharacters;
  }

  public void pushBackBuffer(final char[] bytes, final int length) throws EOF {
    for (int i = length - 1; i >= 0; i--) {
      pushChar(bytes[i]);
    }
  }
}
