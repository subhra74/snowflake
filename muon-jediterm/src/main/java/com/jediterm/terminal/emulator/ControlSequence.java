/**
 *
 */
package com.jediterm.terminal.emulator;

//import com.google.common.collect.Lists;
import com.jediterm.terminal.util.CharUtils;
import com.jediterm.terminal.TerminalDataStream;

import java.io.IOException;
import java.util.ArrayList;

public class ControlSequence {
  private int myArgc;

  private int[] myArgv;

  private char myFinalChar;

  private ArrayList<Character> myUnhandledChars;

  private boolean myStartsWithQuestionMark = false; // true when CSI ?
  private boolean myStartsWithMoreMark = false; // true when CSI >

  private final StringBuilder mySequenceString = new StringBuilder();


  ControlSequence(final TerminalDataStream channel) throws IOException {
    myArgv = new int[5];
    myArgc = 0;

    readControlSequence(channel);
  }

  private void readControlSequence(final TerminalDataStream channel) throws IOException {
    myArgc = 0;
    // Read integer arguments
    int digit = 0;
    int seenDigit = 0;
    int pos = -1;

    while (true) {
      final char b = channel.getChar();
      mySequenceString.append(b);
      pos++;
      if (b == '?' && pos == 0) {
        myStartsWithQuestionMark = true;
      }
      else if (b == '>' && pos == 0) {
        myStartsWithMoreMark = true;
      }
      else if (b == ';') {
        if (digit > 0) {
          myArgc++;
          if (myArgc == myArgv.length) {
            int[] replacement = new int[myArgv.length * 2];
            System.arraycopy(myArgv, 0, replacement, 0, myArgv.length);
            myArgv = replacement;
          }
          myArgv[myArgc] = 0;
          digit = 0;
        }
      }
      else if ('0' <= b && b <= '9') {
        myArgv[myArgc] = myArgv[myArgc] * 10 + b - '0';
        digit++;
        seenDigit = 1;
      }
      else if (':' <= b && b <= '?') {
        addUnhandled(b);
      }
      else if (0x40 <= b && b <= 0x7E) {
        myFinalChar = b;
        break;
      }
      else {
        addUnhandled(b);
      }
    }
    myArgc += seenDigit;
  }

  private void addUnhandled(final char b) {
    if (myUnhandledChars == null) {
      myUnhandledChars = new ArrayList<>();
    }
    myUnhandledChars.add(b);
  }

  public boolean pushBackReordered(final TerminalDataStream channel) throws IOException {
    if (myUnhandledChars == null) return false;
    final char[] bytes = new char[1024]; // can't be more than the whole buffer...
    int i = 0;
    for (final char b : myUnhandledChars) {
      bytes[i++] = b;
    }
    bytes[i++] = (byte)CharUtils.ESC;
    bytes[i++] = (byte)'[';

    if (myStartsWithQuestionMark) {
      bytes[i++] = (byte)'?';
    }
    
    if (myStartsWithMoreMark) {
      bytes[i++] = (byte)'>';
    }

    for (int argi = 0; argi < myArgc; argi++) {
      if (argi != 0) bytes[i++] = ';';
      String s = Integer.toString(myArgv[argi]);
      for (int j = 0; j < s.length(); j++) {
        bytes[i++] = s.charAt(j);
      }
    }
    bytes[i++] = myFinalChar;
    channel.pushBackBuffer(bytes, i);
    return true;
  }

  int getCount() {
    return myArgc;
  }

  final int getArg(final int index, final int defaultValue) {
    if (index >= myArgc) {
      return defaultValue;
    }
    return myArgv[index];
  }

  public String appendTo(final String str) {
    StringBuilder sb = new StringBuilder(str);
    appendToBuffer(sb);
    return sb.toString();
  }

  public final void appendToBuffer(final StringBuilder sb) {
    sb.append("ESC[");

    if (myStartsWithQuestionMark) {
      sb.append("?");
    }

    if (myStartsWithMoreMark) {
      sb.append(">");
    }

    String sep = "";
    for (int i = 0; i < myArgc; i++) {
      sb.append(sep);
      sb.append(myArgv[i]);
      sep = ";";
    }
    sb.append(myFinalChar);

    if (myUnhandledChars != null) {
      sb.append(" Unhandled:");
      CharUtils.CharacterType last = CharUtils.CharacterType.NONE;
      for (final char b : myUnhandledChars) {
        last = CharUtils.appendChar(sb, last, (char)b);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    appendToBuffer(sb);
    return sb.toString();
  }

  public char getFinalChar() {
    return myFinalChar;
  }

  public boolean startsWithQuestionMark() {
    return myStartsWithQuestionMark;
  }

  public boolean startsWithMoreMark() {
    return myStartsWithMoreMark;
  }

  public String getSequenceString() {
    return mySequenceString.toString();
  }
}