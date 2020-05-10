package com.jediterm.terminal.model;

import com.jediterm.terminal.util.CharUtils;
import com.jediterm.terminal.util.Pair;
import org.apache.log4j.Logger;
////import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author traff
 */
public class SelectionUtil {
  private static final Logger LOG = Logger.getLogger(SelectionUtil.class);
  
  private static final List<Character> SEPARATORS = new ArrayList<Character>();
  static {
    SEPARATORS.add(' ');
    SEPARATORS.add('\u00A0'); // NO-BREAK SPACE
    SEPARATORS.add('\t');
    SEPARATORS.add('\'');
    SEPARATORS.add('"');
    SEPARATORS.add('$');
    SEPARATORS.add('(');
    SEPARATORS.add(')');
    SEPARATORS.add('[');
    SEPARATORS.add(']');
    SEPARATORS.add('{');
    SEPARATORS.add('}');
    SEPARATORS.add('<');
    SEPARATORS.add('>');
  }

  public static List<Character> getDefaultSeparators() {
    return new ArrayList<Character>(SEPARATORS);
  }
  
  public static Pair<Point, Point> sortPoints(Point a, Point b) {
    if (a.y == b.y) { /* same line */
      return Pair.create(a.x <= b.x ? a : b, a.x > b.x ? a : b);
    }
    else {
      return Pair.create(a.y < b.y ? a : b, a.y > b.y ? a : b);
    }
  }

  public static String getSelectionText(TerminalSelection selection, TerminalTextBuffer terminalTextBuffer) {
    return getSelectionText(selection.getStart(), selection.getEnd(), terminalTextBuffer);
  }

  
  public static String getSelectionText( Point selectionStart,
                                         Point selectionEnd,
                                         TerminalTextBuffer terminalTextBuffer) {

    Pair<Point, Point> pair = sortPoints(selectionStart, selectionEnd);
    pair.first.y = Math.max(pair.first.y, - terminalTextBuffer.getHistoryLinesCount());
    pair = sortPoints(pair.first, pair.second); // previous line may have change the order

    Point top = pair.first;
    Point bottom = pair.second;

    final StringBuilder selectionText = new StringBuilder();

    for (int i = top.y; i <= bottom.y; i++) {
      TerminalLine line = terminalTextBuffer.getLine(i);
      String text = line.getText();
      if (i == top.y) {
        if (i == bottom.y) {
          selectionText.append(processForSelection(text.substring(Math.min(text.length(), top.x), Math.min(text.length(), bottom.x))));
        } else {
          selectionText.append(processForSelection(text.substring(Math.min(text.length(), top.x))));
        }
      }
      else if (i == bottom.y) {
        selectionText.append(processForSelection(text.substring(0, Math.min(text.length(), bottom.x))));
      }
      else {
        selectionText.append(processForSelection(line.getText()));
      }
      if ((!line.isWrapped() && i < bottom.y) || bottom.x > text.length()) {
        selectionText.append("\n");
      }
    }

    return selectionText.toString();
  }

  private static String processForSelection(String text) {
    if (text.indexOf(CharUtils.DWC) != 0) {
      // remove dwc second chars
      StringBuilder sb = new StringBuilder();
      for (char c : text.toCharArray()) {
        if (c != CharUtils.DWC) {
          sb.append(c);
        }
      }
      return sb.toString();
    } else {
      return text;
    }
  }

  public static Point getPreviousSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer) {
    return getPreviousSeparator(charCoords, terminalTextBuffer, SEPARATORS);
  }

  public static Point getPreviousSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer,  List<Character> separators) {
    int x = charCoords.x;
    int y = charCoords.y;
    int terminalWidth = terminalTextBuffer.getWidth();

    if (separators.contains(terminalTextBuffer.getBuffersCharAt(x, y))) {
      return new Point(x, y);
    }

    String line = terminalTextBuffer.getLine(y).getText();
    while (x < line.length() && !separators.contains(line.charAt(x))) {
      x--;
      if (x < 0) {
        if (y <= - terminalTextBuffer.getHistoryLinesCount()) {
          return new Point(0, y);
        }
        y--;
        x = terminalWidth - 1;

        line = terminalTextBuffer.getLine(y).getText();
      }
    }

    x++;
    if (x >= terminalWidth) {
      y++;
      x = 0;
    }

    return new Point(x, y);
  }

  public static Point getNextSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer) {
    return getNextSeparator(charCoords, terminalTextBuffer, SEPARATORS);
  }

  public static Point getNextSeparator(Point charCoords, TerminalTextBuffer terminalTextBuffer,  List<Character> separators) {
    int x = charCoords.x;
    int y = charCoords.y;
    int terminalWidth = terminalTextBuffer.getWidth();
    int terminalHeight = terminalTextBuffer.getHeight();

    if (separators.contains(terminalTextBuffer.getBuffersCharAt(x, y))) {
      return new Point(x, y);
    }

    String line = terminalTextBuffer.getLine(y).getText();
    while (x < line.length() && !separators.contains(line.charAt(x))) {
      x++;
      if (x >= terminalWidth) {
        if (y >= terminalHeight - 1) {
          return new Point(terminalWidth - 1, terminalHeight - 1);
        }
        y++;
        x = 0;
        
        line = terminalTextBuffer.getLine(y).getText();
      }
    }

    x--;
    if (x < 0) {
      y--;
      x = terminalWidth - 1;
    }

    return new Point(x, y);
  }

}
