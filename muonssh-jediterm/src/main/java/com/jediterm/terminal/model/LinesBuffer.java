package com.jediterm.terminal.model;

//import com.google.common.collect.Lists;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.TerminalLine.TextEntry;
import com.jediterm.terminal.model.hyperlinks.TextProcessing;
import org.apache.log4j.Logger;
////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds styled characters lines
 */
public class LinesBuffer {
  private static final Logger LOG = Logger.getLogger(LinesBuffer.class);

  public static final int DEFAULT_MAX_LINES_COUNT = 5000;

  // negative number means no limit
  private int myBufferMaxLinesCount = DEFAULT_MAX_LINES_COUNT;

  private ArrayList<TerminalLine> myLines = new ArrayList<>();

  
  private final TextProcessing myTextProcessing;

  public LinesBuffer( TextProcessing textProcessing) {
    myTextProcessing = textProcessing;
  }

  public LinesBuffer(int bufferMaxLinesCount,  TextProcessing textProcessing) {
    myBufferMaxLinesCount = bufferMaxLinesCount;
    myTextProcessing = textProcessing;
  }

  public synchronized String getLines() {
    final StringBuilder sb = new StringBuilder();

    boolean first = true;

    for (TerminalLine line : myLines) {
      if (!first) {
        sb.append("\n");
      }

      sb.append(line.getText());
      first = false;
    }

    return sb.toString();
  }


  public synchronized void addNewLine( TextStyle style,  CharBuffer characters) {
    addNewLine(new TerminalLine.TextEntry(style, characters));
  }


  private synchronized void addNewLine( TerminalLine.TextEntry entry) {
    addLine(new TerminalLine(entry));
  }

  private synchronized void addLine( TerminalLine line) {
    if (myBufferMaxLinesCount > 0 && myLines.size() >= myBufferMaxLinesCount) {
      removeTopLines(1);
    }

    myLines.add(line);
  }

  public synchronized int getLineCount() {
    return myLines.size();
  }

  public synchronized void removeTopLines(int count) {
    if (count >= myLines.size()) { // remove all lines
      myLines = new ArrayList<>();
    } else {
      myLines = new ArrayList<>(myLines.subList(count, myLines.size()));
    }
  }

  public String getLineText(int row) {
    TerminalLine line = getLine(row);

    return line.getText();
  }

  public synchronized void insertLines(int y, int count, int lastLine,  TextEntry filler) {
    LinesBuffer tail = new LinesBuffer(myTextProcessing);

    if (lastLine < getLineCount() - 1) {
      moveBottomLinesTo(getLineCount() - lastLine - 1, tail);
    }

    LinesBuffer head = new LinesBuffer(myTextProcessing);
    if (y > 0) {
      moveTopLinesTo(y, head);
    }

    for (int i = 0; i < count; i++) {
      head.addNewLine(filler);
    }

    head.moveBottomLinesTo(head.getLineCount(), this);

    removeBottomLines(count);

    tail.moveTopLinesTo(tail.getLineCount(), this);
  }

  public synchronized LinesBuffer deleteLines(int y, int count, int lastLine,  TextEntry filler) {
    LinesBuffer tail = new LinesBuffer(myTextProcessing);

    if (lastLine < getLineCount() - 1) {
      moveBottomLinesTo(getLineCount() - lastLine - 1, tail);
    }

    LinesBuffer head = new LinesBuffer(myTextProcessing);
    if (y > 0) {
      moveTopLinesTo(y, head);
    }

    int toRemove = Math.min(count, getLineCount());

    LinesBuffer removed = new LinesBuffer(myTextProcessing);
    moveTopLinesTo(toRemove, removed);

    head.moveBottomLinesTo(head.getLineCount(), this);

    for (int i = 0; i < toRemove; i++) {
      addNewLine(filler);
    }

    tail.moveTopLinesTo(tail.getLineCount(), this);

    return removed;
  }

  public synchronized void writeString(int x, int y, CharBuffer str,  TextStyle style) {
    TerminalLine line = getLine(y);

    line.writeString(x, str, style);

    if (myTextProcessing != null) {
      myTextProcessing.processHyperlinks(this, line);
    }
  }

  public synchronized void clearLines(int startRow, int endRow,  TextEntry filler) {
    for (int i = startRow; i <= endRow; i++) {
      getLine(i).clear(filler);
    }
  }

  // used for reset, style not needed here (reset as well)
  public synchronized void clearAll() {
    myLines.clear();
  }

  public synchronized void deleteCharacters(int x, int y, int count,  TextStyle style) {
    TerminalLine line = getLine(y);
    line.deleteCharacters(x, count, style);
  }

  public synchronized void insertBlankCharacters(final int x, final int y, final int count, final int maxLen,  TextStyle style) {
    TerminalLine line = getLine(y);
    line.insertBlankCharacters(x, count, maxLen, style);
  }

  public synchronized void clearArea(int leftX, int topY, int rightX, int bottomY,  TextStyle style) {
    for (int y = topY; y < bottomY; y++) {
      TerminalLine line = getLine(y);
      line.clearArea(leftX, rightX, style);
    }
  }

  public synchronized void processLines(final int yStart, final int yCount,  final StyledTextConsumer consumer) {
    processLines(yStart, yCount, consumer, -getLineCount());
  }

  public synchronized void processLines(final int firstLine,
                                        final int count,
                                         final StyledTextConsumer consumer,
                                        final int startRow) {
    if (firstLine<0) {
      throw new IllegalArgumentException("firstLine=" + firstLine + ", should be >0");
    }
    for (int y = firstLine; y < Math.min(firstLine + count, myLines.size()); y++) {
      myLines.get(y).process(y, consumer, startRow);
    }
  }

  public synchronized void moveTopLinesTo(int count, final  LinesBuffer buffer) {
    count = Math.min(count, getLineCount());
    buffer.addLines(myLines.subList(0, count));
    removeTopLines(count);
  }

  public synchronized void addLines( List<TerminalLine> lines) {
    if (myBufferMaxLinesCount > 0) {
      // adding more lines than max size
      if (lines.size() >= myBufferMaxLinesCount) {
        int index = lines.size() - myBufferMaxLinesCount;
        myLines = new ArrayList<>(lines.subList(index, lines.size()));
        return;
      }

      int count = myLines.size() + lines.size();
      if (count >= myBufferMaxLinesCount) {
        removeTopLines(count - myBufferMaxLinesCount);
      }
    }

    myLines.addAll(lines);
  }

  
  public synchronized TerminalLine getLine(int row) {
    if (row<0) {
      LOG.error("Negative line number: " + row);
      return TerminalLine.createEmpty();
    }

    for (int i = getLineCount(); i <= row; i++) {
      addLine(TerminalLine.createEmpty());
    }

    return myLines.get(row);
  }

  public synchronized void moveBottomLinesTo(int count, final  LinesBuffer buffer) {
    count = Math.min(count, getLineCount());
    buffer.addLinesFirst(myLines.subList(getLineCount() - count, getLineCount()));

    removeBottomLines(count);
  }

  private synchronized void addLinesFirst( List<TerminalLine> lines) {
    List<TerminalLine> list = new ArrayList<>(lines);
    list.addAll(myLines);
    myLines = new ArrayList<>(list);
  }

  private synchronized void removeBottomLines(int count) {
    myLines = new ArrayList<>(myLines.subList(0, getLineCount() - count));
  }

  public int removeBottomEmptyLines(int ind, int maxCount) {
    int i = 0;
    while ((maxCount - i) > 0 && (ind >= myLines.size() || myLines.get(ind).isNul())) {
      if (ind < myLines.size()) {
        myLines.remove(ind);
      }
      ind--;
      i++;
    }

    return i;
  }
}
