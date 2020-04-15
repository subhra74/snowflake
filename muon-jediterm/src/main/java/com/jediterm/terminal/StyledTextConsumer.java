package com.jediterm.terminal;

import com.jediterm.terminal.model.CharBuffer;
////import org.jetbrains.annotations.NotNull;

/**
 * General interface that obtains styled range of characters at coordinates (<b>x</b>, <b>y</b>) when the screen starts at <b>startRow</b>
 *
 * @author traff
 */
public interface StyledTextConsumer {
  /**
   *
   * @param x indicates starting column of the characters
   * @param y indicates row of the characters
   * @param style style of characters
   * @param characters text characters
   * @param startRow number of the first row.
   *                 It can be different for different buffers, e.g. backBuffer starts from 0, textBuffer and scrollBuffer from -count
   */
  void consume(int x, int y,  TextStyle style,  CharBuffer characters, int startRow);

  void consumeNul(int x, int y, int nulIndex,  TextStyle style,  CharBuffer characters, int startRow);

  void consumeQueue(int x, int y, int nulIndex, int startRow);

}
