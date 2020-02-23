package com.jediterm.terminal.emulator.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author traff
 */
public interface TerminalMouseListener {
  void mousePressed(int x, int y, MouseEvent event);
  void mouseReleased(int x, int y, MouseEvent event);
  void mouseMoved(int x, int y, MouseEvent event);
  void mouseDragged(int x, int y, MouseEvent event);
  void mouseWheelMoved(int x, int y, MouseWheelEvent event);
}
