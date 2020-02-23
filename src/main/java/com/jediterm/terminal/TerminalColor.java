package com.jediterm.terminal;

////import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author traff
 */
public class TerminalColor {
  public static final TerminalColor BLACK = index(0);
  public static final TerminalColor WHITE = index(15);

  private int myColorIndex;
  private Color myColor;

  public TerminalColor(int index) {
    myColorIndex = index;
    myColor = null;
  }

  public TerminalColor(int r, int g, int b) {
    myColorIndex = -1;
    myColor = new Color(r, g, b);
  }

  public static TerminalColor index(int index) {
    return new TerminalColor(index);
  }

  public static TerminalColor rgb(int r, int g, int b) {
    return new TerminalColor(r, g, b);
  }

  public boolean isIndexed() {
    return myColorIndex != -1;
  }

  public Color toAwtColor() {
    if (isIndexed()) {
      throw new IllegalArgumentException("Color is indexed color so a palette is needed");
    }

    return myColor;
  }

  public int getIndex() {
    return myColorIndex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TerminalColor that = (TerminalColor) o;

    if (isIndexed()) {
      if (!that.isIndexed()) return false;
      if (myColorIndex != that.myColorIndex) return false;
    } else {
      if (that.isIndexed()) {
        return false;
      }
      return myColor.equals(that.myColor);
    }
    return true;
  }

  @Override
  public int hashCode() {
    return myColor != null ? myColor.hashCode() : myColorIndex;
  }

  
  public static TerminalColor awt( Color color) {
    if (color == null) {
      return null;
    }
    return rgb(color.getRed(), color.getGreen(), color.getBlue());
  }
}
