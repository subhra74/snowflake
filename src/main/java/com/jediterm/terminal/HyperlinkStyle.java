package com.jediterm.terminal;

import com.jediterm.terminal.model.hyperlinks.LinkInfo;
////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;

/**
 * @author traff
 */
public class HyperlinkStyle extends TextStyle implements Runnable {
  
  private final LinkInfo myLinkInfo;

  
  private final TextStyle myHighlightStyle;

  
  private final TextStyle myPrevTextStyle;

  
  private final HighlightMode myHighlightMode;

  public HyperlinkStyle( TextStyle prevTextStyle,  LinkInfo hyperlinkInfo) {
    this(prevTextStyle.getForeground(), prevTextStyle.getBackground(), hyperlinkInfo, HighlightMode.HOVER, prevTextStyle);
  }

  public HyperlinkStyle( TerminalColor foreground,
                         TerminalColor background,
                         LinkInfo hyperlinkInfo,
                         HighlightMode mode,
                         TextStyle prevTextStyle) {
    this(false, foreground, background, hyperlinkInfo, mode, prevTextStyle);
  }

  private HyperlinkStyle(boolean keepColors,
                          TerminalColor foreground,
                          TerminalColor background,
                          LinkInfo hyperlinkInfo,
                          HighlightMode mode,
                          TextStyle prevTextStyle) {
    super(keepColors ? foreground : null, keepColors ? background : null);
    myHighlightStyle = new TextStyle.Builder()
        .setBackground(background)
        .setForeground(foreground)
        .setOption(Option.UNDERLINED, true)
        .build();
    myLinkInfo = hyperlinkInfo;
    myHighlightMode = mode;
    myPrevTextStyle = prevTextStyle;
  }

  
  public TextStyle getPrevTextStyle() {
    return myPrevTextStyle;
  }

  @Override
  public void run() {
    myLinkInfo.navigate();
  }

  
  public TextStyle getHighlightStyle() {
    return myHighlightStyle;
  }

  
  public LinkInfo getLinkInfo() {
    return myLinkInfo;
  }

  
  public HighlightMode getHighlightMode() {
    return myHighlightMode;
  }

  
  @Override
  public Builder toBuilder() {
    return new Builder(this);
  }

  public enum HighlightMode {
    ALWAYS, NEVER, HOVER
  }

  public static class Builder extends TextStyle.Builder {

    
    private LinkInfo myLinkInfo;

    
    private TextStyle myHighlightStyle;

    
    private TextStyle myPrevTextStyle;

    
    private HighlightMode myHighlightMode;

    private Builder( HyperlinkStyle style) {
      myLinkInfo = style.myLinkInfo;
      myHighlightStyle = style.myHighlightStyle;
      myPrevTextStyle = style.myPrevTextStyle;
      myHighlightMode = style.myHighlightMode;
    }

    
    public HyperlinkStyle build() {
      return build(false);
    }

    
    public HyperlinkStyle build(boolean keepColors) {
      TerminalColor foreground = myHighlightStyle.getForeground();
      TerminalColor background = myHighlightStyle.getBackground();
      if (keepColors) {
        TextStyle style = super.build();
        foreground = style.getForeground() != null ? style.getForeground() : myHighlightStyle.getForeground();
        background = style.getBackground() != null ? style.getBackground() : myHighlightStyle.getBackground();
      }
      return new HyperlinkStyle(keepColors, foreground, background, myLinkInfo, myHighlightMode, myPrevTextStyle);
    }
  }
}
