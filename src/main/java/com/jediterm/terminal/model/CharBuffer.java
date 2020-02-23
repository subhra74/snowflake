package com.jediterm.terminal.model;

import com.jediterm.terminal.util.CharUtils;
import com.jediterm.terminal.util.Pair;
////import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author traff
 */
public class CharBuffer implements Iterable<Character>, CharSequence {

  public final static CharBuffer EMPTY = new CharBuffer(new char[0], 0, 0);

  private final char[] myBuf;
  private final int myStart;
  private final int myLength;

  public CharBuffer( char[] buf, int start, int length) {
    if (start + length > buf.length) {
      throw new IllegalArgumentException(String.format("Out ouf bounds %d+%d>%d", start, length, buf.length));
    }
    myBuf = buf;
    myStart = start;
    myLength = length;
    
    if (myLength < 0) {
      throw new IllegalStateException("Length can't be negative: " + myLength);
    }

    if (myStart < 0) {
      throw new IllegalStateException("Start position can't be negative: " + myStart);
    }

    if (myStart + myLength > myBuf.length) {
      throw new IllegalStateException(String.format("Interval is out of array bounds: %d+%d>%d", myStart, myLength, myBuf.length));
    }
  }

  public CharBuffer(char c, int count) {
    this(new char[count], 0, count);
    assert !CharUtils.isDoubleWidthCharacter(c, false);
    Arrays.fill(myBuf, c);
  }

  public CharBuffer( String str) {
    this(str.toCharArray(), 0, str.length());
  }

  @Override
  public Iterator<Character> iterator() {
    return new Iterator<Character>() {
      private int myCurPosition = myStart;

      @Override
      public boolean hasNext() {
        return myCurPosition < myBuf.length && myCurPosition < myStart + myLength;
      }

      @Override
      public Character next() {
        return myBuf[myCurPosition];
      }

      @Override
      public void remove() {
        throw new IllegalStateException("Can't remove from buffer");
      }
    };
  }

  public char[] getBuf() {
    return myBuf;
  }

  public int getStart() {
    return myStart;
  }

  public CharBuffer subBuffer(int start, int length) {
    return new CharBuffer(myBuf, getStart() + start, length);
  }

  public CharBuffer subBuffer(Pair<Integer, Integer> range) {
    return new CharBuffer(myBuf, getStart() + range.first, range.second - range.first);
  }

  public boolean isNul() {
    return myLength > 0 && myBuf[0] == CharUtils.NUL_CHAR;
  }

  public void unNullify() {
    Arrays.fill(myBuf, CharUtils.EMPTY_CHAR);
  }

  @Override
  public int length() {
    return myLength;
  }

  @Override
  public char charAt(int index) {
    return myBuf[myStart + index];
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return new CharBuffer(myBuf, myStart + start, end - start);
  }

  @Override
  public String toString() {
    return new String(myBuf, myStart, myLength);
  }

  public CharBuffer clone() {
    char[] newBuf = Arrays.copyOfRange(myBuf, myStart, myStart + myLength);

    return new CharBuffer(newBuf, 0, myLength);
  }
}
