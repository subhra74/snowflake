package com.jediterm.terminal;

////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.Objects;
import java.util.WeakHashMap;

public class TextStyle {
  private static final EnumSet<Option> NO_OPTIONS = EnumSet.noneOf(Option.class);

  public static final TextStyle EMPTY = new TextStyle();

  private static final WeakHashMap<TextStyle, WeakReference<TextStyle>> styles = new WeakHashMap<>();

  private final TerminalColor myForeground;
  private final TerminalColor myBackground;
  private final EnumSet<Option> myOptions;

  public TextStyle() {
    this(null, null, NO_OPTIONS);
  }

  public TextStyle( TerminalColor foreground,  TerminalColor background) {
    this(foreground, background, NO_OPTIONS);
  }

  public TextStyle( TerminalColor foreground,  TerminalColor background,  EnumSet<Option> options) {
    myForeground = foreground;
    myBackground = background;
    myOptions = options.clone();
  }

  
  public static TextStyle getCanonicalStyle(TextStyle currentStyle) {
    if (currentStyle instanceof HyperlinkStyle) {
      return currentStyle;
    }
    final WeakReference<TextStyle> canonRef = styles.get(currentStyle);
    if (canonRef != null) {
      final TextStyle canonStyle = canonRef.get();
      if (canonStyle != null) {
        return canonStyle;
      }
    }
    styles.put(currentStyle, new WeakReference<>(currentStyle));
    return currentStyle;
  }

  
  public TerminalColor getForeground() {
    return myForeground;
  }

  
  public TerminalColor getBackground() {
    return myBackground;
  }

  public TextStyle createEmptyWithColors() {
    return new TextStyle(myForeground, myBackground);
  }

  public int getId() {
    return hashCode();
  }

  public boolean hasOption(final Option option) {
    return myOptions.contains(option);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TextStyle textStyle = (TextStyle) o;
    return Objects.equals(myForeground, textStyle.myForeground) &&
      Objects.equals(myBackground, textStyle.myBackground) &&
      myOptions.equals(textStyle.myOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myForeground, myBackground, myOptions);
  }

  public TerminalColor getBackgroundForRun() {
    return myOptions.contains(Option.INVERSE) ? myForeground : myBackground;
  }

  public TerminalColor getForegroundForRun() {
    return myOptions.contains(Option.INVERSE) ? myBackground : myForeground;
  }

  
  public Builder toBuilder() {
    return new Builder(this);
  }

  public enum Option {
    BOLD,
    ITALIC,
    BLINK,
    DIM,
    INVERSE,
    UNDERLINED,
    HIDDEN;

    private void set( EnumSet<Option> options, boolean val) {
      if (val) {
        options.add(this);
      }
      else {
        options.remove(this);
      }
    }
  }

  public static class Builder {
    private TerminalColor myForeground;
    private TerminalColor myBackground;
    private EnumSet<Option> myOptions;

    public Builder( TextStyle textStyle) {
      myForeground = textStyle.myForeground;
      myBackground = textStyle.myBackground;
      myOptions = textStyle.myOptions.clone();
    }

    public Builder() {
      myForeground = null;
      myBackground = null;
      myOptions = EnumSet.noneOf(Option.class);
    }

    
    public Builder setForeground( TerminalColor foreground) {
      myForeground = foreground;
      return this;
    }

    
    public Builder setBackground( TerminalColor background) {
      myBackground = background;
      return this;
    }

    
    public Builder setOption( Option option, boolean val) {
      option.set(myOptions, val);
      return this;
    }

    
    public TextStyle build() {
      return new TextStyle(myForeground, myBackground, myOptions);
    }
  }
}
