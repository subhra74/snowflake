package com.jediterm.terminal.model;

import com.jediterm.terminal.util.Pair;
////import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author traff
 */
public class TerminalSelection {
  private final Point myStart;

  private Point myEnd;

  public TerminalSelection(Point start) {
    myStart = start;
  }

  public TerminalSelection(Point start, Point end) {
    myStart = start;
    myEnd = end;
  }

  public Point getStart() {
    return myStart;
  }

  public Point getEnd() {
    return myEnd;
  }

  public void updateEnd(Point end) {
    myEnd = end;
  }

  public Pair<Point, Point> pointsForRun(int width) {
    Pair<Point, Point> p = SelectionUtil.sortPoints(new Point(myStart), new Point(myEnd));
    p.second.x = Math.min(p.second.x + 1, width);
    return p;
  }

  public boolean contains(Point toTest) {
    return intersects(toTest.x, toTest.y, 1);
  }

  public void shiftY(int dy) {
    myStart.y += dy;
    myEnd.y += dy;
  }

  public boolean intersects(int x, int row, int length) {
    return null != intersect(x, row, length);
  }


  public Pair<Integer, Integer> intersect(int x, int row, int length) {
    int newX = x;
    int newLength;

    Pair<Point, Point> p = SelectionUtil.sortPoints(new Point(myStart), new Point(myEnd));

    if (p.first.y == row) {
      newX = Math.max(x, p.first.x);
    }

    if (p.second.y == row) {
      newLength = Math.min(p.second.x, x + length - 1) - newX + 1;
    } else {
      newLength = length - newX + x;
    }

    if (newLength<=0 || row < p.first.y || row > p.second.y) {
      return null;
    } else
      return Pair.create(newX, newLength);
  }

  @Override
  public String toString() {
    return "[x=" + myStart.x + ",y=" + myStart.y + "]" + " -> [x=" + myEnd.x + ",y=" + myEnd.y + "]";
  }

}
