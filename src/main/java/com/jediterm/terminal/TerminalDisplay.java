package com.jediterm.terminal;

import com.jediterm.terminal.model.JediTerminal;
import com.jediterm.terminal.model.TerminalSelection;
import com.jediterm.terminal.emulator.mouse.MouseMode;

import java.awt.*;

public interface TerminalDisplay {
  // Size information
  int getRowCount();

  int getColumnCount();

  void setCursor(int x, int y);

  void setCursorShape(CursorShape shape);

  void beep();

  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  Dimension requestResize(Dimension pendingResize, RequestOrigin origin, int cursorY, JediTerminal.ResizeHandler resizeHandler);

  default Dimension requestResize(Dimension pendingResize, RequestOrigin origin, int cursorX, int cursorY,
                                  JediTerminal.ResizeHandler resizeHandler) {
    return requestResize(pendingResize, origin, cursorY, resizeHandler);
  }

  void scrollArea(final int scrollRegionTop, final int scrollRegionSize, int dy);

  void setCursorVisible(boolean shouldDrawCursor);

  void setScrollingEnabled(boolean enabled);

  void setBlinkingCursor(boolean enabled);

  void setWindowTitle(String name);

  void setCurrentPath(String path);

  void terminalMouseModeSet(MouseMode mode);

  TerminalSelection getSelection();
  
  boolean ambiguousCharsAreDoubleWidth();
}
