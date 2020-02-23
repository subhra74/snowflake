package com.jediterm.terminal.emulator.mouse;

/**
 * @author traff
 */
public class MouseButtonCodes {
  // X11 button number
  public static final int NONE = -1;       // no button
  public static final int LEFT = 0;       // left button
  public static final int MIDDLE = 1;     // middle button
  public static final int RIGHT = 2;      // right button
  public static final int RELEASE = 3;    // release - for 1000/1005/1015 mode
  public static final int SCROLLDOWN = 4; // scroll down
  public static final int SCROLLUP = 5;    // scroll up
}
