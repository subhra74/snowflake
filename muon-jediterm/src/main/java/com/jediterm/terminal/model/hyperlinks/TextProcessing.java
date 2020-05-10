package com.jediterm.terminal.model.hyperlinks;

//import com.google.common.collect.Lists;
import com.jediterm.terminal.HyperlinkStyle;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.CharBuffer;
import com.jediterm.terminal.model.LinesBuffer;
import com.jediterm.terminal.model.TerminalLine;
import com.jediterm.terminal.model.TerminalTextBuffer;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.util.CharUtils;
import org.apache.log4j.Logger;
//import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author traff
 */
public class TextProcessing {

  private static final Logger LOG = Logger.getLogger(TextProcessing.class);

  private final List<HyperlinkFilter> myHyperlinkFilter;
  private TextStyle myHyperlinkColor;
  private HyperlinkStyle.HighlightMode myHighlightMode;
  private TerminalTextBuffer myTerminalTextBuffer;

  public TextProcessing( TextStyle hyperlinkColor,
                         HyperlinkStyle.HighlightMode highlightMode) {
    myHyperlinkColor = hyperlinkColor;
    myHighlightMode = highlightMode;
    myHyperlinkFilter = new ArrayList<>();
  }

  public void setTerminalTextBuffer( TerminalTextBuffer terminalTextBuffer) {
    myTerminalTextBuffer = terminalTextBuffer;
  }

  public void processHyperlinks( LinesBuffer buffer,  TerminalLine updatedLine) {
    if (myHyperlinkFilter.isEmpty()) return;
    doProcessHyperlinks(buffer, updatedLine);
  }

  private void doProcessHyperlinks( LinesBuffer buffer,  TerminalLine updatedLine) {
    myTerminalTextBuffer.lock();
    try {
      int updatedLineInd = findLineInd(buffer, updatedLine);
      if (updatedLineInd == -1) {
        // When lines arrive fast enough, the line might be pushed to the history buffer already.
        updatedLineInd = findHistoryLineInd(myTerminalTextBuffer.getHistoryBuffer(), updatedLine);
        if (updatedLineInd == -1) {
          LOG.debug("Cannot find line for links processing");
          return;
        }
        buffer = myTerminalTextBuffer.getHistoryBuffer();
      }
      int startLineInd = updatedLineInd;
      while (startLineInd > 0 && buffer.getLine(startLineInd - 1).isWrapped()) {
        startLineInd--;
      }
      String lineStr = joinLines(buffer, startLineInd, updatedLineInd);
      for (HyperlinkFilter filter : myHyperlinkFilter) {
        LinkResult result = filter.apply(lineStr);
        if (result != null) {
          for (LinkResultItem item : result.getItems()) {
            TextStyle style = new HyperlinkStyle(myHyperlinkColor.getForeground(), myHyperlinkColor.getBackground(),
              item.getLinkInfo(), myHighlightMode, null);
            if (item.getStartOffset() < 0 || item.getEndOffset() > lineStr.length()) continue;

            int prevLinesLength = 0;
            for (int lineInd = startLineInd; lineInd <= updatedLineInd; lineInd++) {
              int startLineOffset = Math.max(prevLinesLength, item.getStartOffset());
              int endLineOffset = Math.min(prevLinesLength + myTerminalTextBuffer.getWidth(), item.getEndOffset());
              if (startLineOffset < endLineOffset) {
                buffer.getLine(lineInd).writeString(startLineOffset - prevLinesLength, new CharBuffer(lineStr.substring(startLineOffset, endLineOffset)), style);
              }
              prevLinesLength += myTerminalTextBuffer.getWidth();
            }
          }
        }
      }
    }
    finally {
      myTerminalTextBuffer.unlock();
    }
  }

  private int findHistoryLineInd( LinesBuffer historyBuffer,  TerminalLine line) {
    int lastLineInd = Math.max(0, historyBuffer.getLineCount() - 200); // check only last lines in history buffer
    for (int i = historyBuffer.getLineCount() - 1; i >= lastLineInd; i--) {
      if (historyBuffer.getLine(i) == line) {
        return i;
      }
    }
    return -1;
  }

  private static int findLineInd( LinesBuffer buffer,  TerminalLine line) {
    for (int i = 0; i < buffer.getLineCount(); i++) {
      TerminalLine l = buffer.getLine(i);
      if (l == line) {
        return i;
      }
    }
    return -1;
  }

  
  private String joinLines( LinesBuffer buffer, int startLineInd, int updatedLineInd) {
    StringBuilder result = new StringBuilder();
    for (int i = startLineInd; i <= updatedLineInd; i++) {
      String text = buffer.getLine(i).getText();
      if (i < updatedLineInd && text.length() < myTerminalTextBuffer.getWidth()) {
        text = text + new CharBuffer(CharUtils.NUL_CHAR, myTerminalTextBuffer.getWidth() - text.length());
      }
      result.append(text);
    }
    return result.toString();
  }

  public void addHyperlinkFilter( HyperlinkFilter filter) {
    myHyperlinkFilter.add(filter);
  }
}
