/**
 * 
 */
package com.jediterm.terminal.model;

import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.charset.CharacterSet;
import com.jediterm.terminal.emulator.charset.GraphicSetState;


public class StoredCursor {

  //Cursor position
  private final int myCursorX;

  private final int myCursorY;

  //Character attributes set by the SGR command
  
  private final TextStyle myTextStyle;
  
  //Character sets (G0, G1, G2, or G3) currently in GL and GR
  private final int myGLMapping;
  private final int myGRMapping;

  //Wrap ﬂag (autowrap or no autowrap)
  private final boolean myAutoWrap;

  //State of origin mode (DECOM)
  private final boolean myOriginMode;
  
  //Selective erase attribute
  
  //Any single shift 2 (SS2) or single shift 3 (SS3) functions sent
  private final int myGLOverride;
  
  private final CharacterSet[] myDesignations = new CharacterSet[4];

  public StoredCursor(int cursorX,
                      int cursorY,
                       TextStyle textStyle,
                      boolean autoWrap,
                      boolean originMode,
                      GraphicSetState graphicSetState) {
    myCursorX = cursorX;
    myCursorY = cursorY;
    myTextStyle = textStyle;
    myAutoWrap = autoWrap;
    myOriginMode = originMode;
    myGLMapping = graphicSetState.getGL().getIndex();
    myGRMapping = graphicSetState.getGR().getIndex();
    myGLOverride = graphicSetState.getGLOverrideIndex();
    for (int i = 0; i<4; i++) {
      myDesignations[i] = graphicSetState.getGraphicSet(i).getDesignation();
    }
  }

  public int getCursorX() {
    return myCursorX;
  }

  public int getCursorY() {
    return myCursorY;
  }

  public TextStyle getTextStyle() {
    return myTextStyle;
  }

  public int getGLMapping() {
    return myGLMapping;
  }

  public int getGRMapping() {
    return myGRMapping;
  }

  public boolean isAutoWrap() {
    return myAutoWrap;
  }

  public boolean isOriginMode() {
    return myOriginMode;
  }

  public int getGLOverride() {
    return myGLOverride;
  }

  public CharacterSet[] getDesignations() {
    return myDesignations;
  }
}