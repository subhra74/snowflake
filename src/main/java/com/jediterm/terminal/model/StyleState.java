package com.jediterm.terminal.model;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
////import org.jetbrains.annotations.NotNull;

public class StyleState {
  private TextStyle myCurrentStyle = TextStyle.EMPTY;
  private TextStyle myDefaultStyle = TextStyle.EMPTY;
  
  private TextStyle myMergedStyle = null;

  public StyleState() {
  }

  public TextStyle getCurrent() {
    return TextStyle.getCanonicalStyle(getMergedStyle());
  }


  private static TextStyle merge( TextStyle style,  TextStyle defaultStyle) {
    TextStyle.Builder builder = style.toBuilder();
    if (style.getBackground() == null && defaultStyle.getBackground() != null) {
      builder.setBackground(defaultStyle.getBackground());
    }
    if (style.getForeground() == null && defaultStyle.getForeground() != null) {
      builder.setForeground(defaultStyle.getForeground());
    }
    return builder.build();
  }

  public void reset() {
    myCurrentStyle = myDefaultStyle;
    myMergedStyle = null;
  }

  public void set(StyleState styleState) {
    setCurrent(styleState.getCurrent());
  }

  public void setDefaultStyle(TextStyle defaultStyle) {
    myDefaultStyle = defaultStyle;
    myMergedStyle = null;
  }

  public TerminalColor getBackground() {
    return getBackground(null);
  }

  public TerminalColor getBackground(TerminalColor color) {
    return color != null ? color : myDefaultStyle.getBackground();
  }

  public TerminalColor getForeground() {
    return getForeground(null);
  }

  public TerminalColor getForeground(TerminalColor color) {
    return color != null ? color : myDefaultStyle.getForeground();
  }

  public void setCurrent(TextStyle current) {
    myCurrentStyle = current;
    myMergedStyle = null;
  }

  private TextStyle getMergedStyle() {
    if (myMergedStyle == null) {
      myMergedStyle = merge(myCurrentStyle, myDefaultStyle);
    }
    return myMergedStyle;
  }
}
