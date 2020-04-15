package com.jediterm.terminal;

//import com.google.common.base.Ascii;
import com.jediterm.terminal.ui.UIUtil;
import com.jediterm.terminal.util.CharUtils;
////import org.jetbrains.annotations.NotNull;

import muon.terminal.Ascii;

import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.awt.event.KeyEvent.*;

/**
 * @author traff
 */
public class TerminalKeyEncoder {
  private static final int ESC = Ascii.ESC;

  private final Map<KeyCodeAndModifier, byte[]> myKeyCodes = new HashMap<KeyCodeAndModifier, byte[]>();

  private boolean myAltSendsEscape = true;
  private boolean myMetaSendsEscape = false;

  public TerminalKeyEncoder() {
    setAutoNewLine(false);
    arrowKeysAnsiCursorSequences();
    keypadAnsiSequences();
    putCode(VK_BACK_SPACE, Ascii.DEL);
    putCode(VK_F1, ESC, 'O', 'P');
    putCode(VK_F2, ESC, 'O', 'Q');
    putCode(VK_F3, ESC, 'O', 'R');
    putCode(VK_F4, ESC, 'O', 'S');
    putCode(VK_F5, ESC, '[', '1', '5', '~');
    putCode(VK_F6, ESC, '[', '1', '7', '~');
    putCode(VK_F7, ESC, '[', '1', '8', '~');
    putCode(VK_F8, ESC, '[', '1', '9', '~');
    putCode(VK_F9, ESC, '[', '2', '0', '~');
    putCode(VK_F10, ESC, '[', '2', '1', '~');
    putCode(VK_F11, ESC, '[', '2', '3', '~', ESC);
    putCode(VK_F12, ESC, '[', '2', '4', '~', Ascii.BS);

    putCode(VK_INSERT, ESC, '[', '2', '~');
    putCode(VK_DELETE, ESC, '[', '3', '~');

    putCode(VK_PAGE_UP, ESC, '[', '5', '~');
    putCode(VK_PAGE_DOWN, ESC, '[', '6', '~');

    putCode(VK_HOME, ESC, '[', 'H');
    putCode(VK_END, ESC, '[', 'F');
  }

  public void arrowKeysApplicationSequences() {
    putCode(VK_UP, ESC, 'O', 'A');
    putCode(VK_DOWN, ESC, 'O', 'B');
    putCode(VK_RIGHT, ESC, 'O', 'C');
    putCode(VK_LEFT, ESC, 'O', 'D');

    if (UIUtil.isLinux) {
      putCode(new KeyCodeAndModifier(VK_RIGHT, InputEvent.CTRL_MASK), ESC, '[',  '1', ';', '5', 'C'); // ^[[1;5C
      putCode(new KeyCodeAndModifier(VK_LEFT, InputEvent.CTRL_MASK), ESC, '[',  '1', ';', '5', 'D'); // ^[[1;5D
    }
    else {
      putCode(new KeyCodeAndModifier(VK_RIGHT, InputEvent.ALT_MASK), ESC, 'f'); // ^[f
      putCode(new KeyCodeAndModifier(VK_LEFT, InputEvent.ALT_MASK), ESC, 'b'); // ^[b
    }
  }

  public void arrowKeysAnsiCursorSequences() {
    putCode(VK_UP, ESC, '[', 'A');
    putCode(VK_DOWN, ESC, '[', 'B');
    putCode(VK_RIGHT, ESC, '[', 'C');
    putCode(VK_LEFT, ESC, '[', 'D');
    if (UIUtil.isMac) {
      putCode(new KeyCodeAndModifier(VK_RIGHT, InputEvent.ALT_MASK), ESC, 'f'); // ^[f
      putCode(new KeyCodeAndModifier(VK_LEFT, InputEvent.ALT_MASK), ESC, 'b'); // ^[b
    }
  }

  public void keypadApplicationSequences() {
    putCode(VK_KP_DOWN, ESC, 'O', 'B'); //2
    putCode(VK_KP_LEFT, ESC, 'O', 'D'); //4
    putCode(VK_KP_RIGHT, ESC, 'O', 'C'); //6
    putCode(VK_KP_UP, ESC, 'O', 'A'); //8

    putCode(VK_HOME, ESC, 'O', 'H');
    putCode(VK_END, ESC, 'O', 'F');
  }

  public void keypadAnsiSequences() {
    putCode(VK_KP_DOWN, ESC, '[', 'B'); //2
    putCode(VK_KP_LEFT, ESC, '[', 'D'); //4
    putCode(VK_KP_RIGHT, ESC, '[', 'C'); //6
    putCode(VK_KP_UP, ESC, '[', 'A'); //8

    putCode(VK_HOME, ESC, '[', 'H');
    putCode(VK_END, ESC, '[', 'F');
  }

  void putCode(final int code, final int... bytesAsInt) {
    myKeyCodes.put(new KeyCodeAndModifier(code, 0), CharUtils.makeCode(bytesAsInt));
  }

  private void putCode( KeyCodeAndModifier key, final int... bytesAsInt) {
    myKeyCodes.put(key, CharUtils.makeCode(bytesAsInt));
  }

  public byte[] getCode(final int key, int modifiers) {
    byte[] bytes = myKeyCodes.get(new KeyCodeAndModifier(key, modifiers));
    if (bytes != null) {
      return bytes;
    }
    bytes = myKeyCodes.get(new KeyCodeAndModifier(key, 0));
    if (bytes == null) {
      return null;
    }

    if ((myAltSendsEscape || alwaysSendEsc(key)) && (modifiers & InputEvent.ALT_MASK) != 0) {
      return insertCodeAt(bytes, CharUtils.makeCode(ESC), 0);
    }

    if ((myMetaSendsEscape || alwaysSendEsc(key)) && (modifiers & InputEvent.META_MASK) != 0) {
      return insertCodeAt(bytes, CharUtils.makeCode(ESC), 0);
    }

    if (isCursorKey(key)) {
      return getCodeWithModifiers(bytes, modifiers);
    }

    return bytes;
  }

  private boolean alwaysSendEsc(int key) {
    return isCursorKey(key) || key == '\b';
  }

  private boolean isCursorKey(int key) {
    return key == VK_DOWN || key == VK_UP || key == VK_LEFT || key == VK_RIGHT || key == VK_HOME || key == VK_END;
  }

  /**
   * Refer to section PC-Style Function Keys in http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
   */
  private byte[] getCodeWithModifiers(byte[] bytes, int modifiers) {
    int code = modifiersToCode(modifiers);

    if (code > 0) {
      return insertCodeAt(bytes, Integer.toString(code).getBytes(), bytes.length-1);
    }
    return bytes;
  }

  private static byte[] insertCodeAt(byte[] bytes, byte[] code, int at) {
    byte[] res = new byte[bytes.length+code.length];
    System.arraycopy(bytes, 0, res, 0, bytes.length);
    System.arraycopy(bytes, at, res, at + code.length, bytes.length - at);
    System.arraycopy(code, 0, res, at, code.length);
    return res;
  }

  /**
   *
   Code     Modifiers
   ------+--------------------------
   2     | Shift
   3     | Alt
   4     | Shift + Alt
   5     | Control
   6     | Shift + Control
   7     | Alt + Control
   8     | Shift + Alt + Control
   9     | Meta
   10    | Meta + Shift
   11    | Meta + Alt
   12    | Meta + Alt + Shift
   13    | Meta + Ctrl
   14    | Meta + Ctrl + Shift
   15    | Meta + Ctrl + Alt
   16    | Meta + Ctrl + Alt + Shift
   ------+--------------------------
   * @param modifiers
   * @return
   */
  private static int modifiersToCode(int modifiers) {
    int code = 0;
    if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
      code |= 1;
    }
    if ((modifiers & InputEvent.ALT_MASK) != 0) {
      code |= 2;
    }
    if ((modifiers & InputEvent.CTRL_MASK) != 0) {
      code |= 4;
    }
    if ((modifiers & InputEvent.META_MASK) != 0) {
      code |= 8;
    }
    return code != 0? code + 1: code;
  }

  public void setAutoNewLine(boolean enabled) {
    if (enabled) {
      putCode(VK_ENTER, Ascii.CR, Ascii.LF);
    }
    else {
      putCode(VK_ENTER, Ascii.CR);
    }
  }

  public void setAltSendsEscape(boolean altSendsEscape) {
    myAltSendsEscape = altSendsEscape;
  }

  public void setMetaSendsEscape(boolean metaSendsEscape) {
    myMetaSendsEscape = metaSendsEscape;
  }

  private static class KeyCodeAndModifier {
    private final int myCode;
    private final int myModifier;

    public KeyCodeAndModifier(int code, int modifier) {
      myCode = code;
      myModifier = modifier;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      KeyCodeAndModifier that = (KeyCodeAndModifier) o;
      return myCode == that.myCode && myModifier == that.myModifier;
    }

    @Override
    public int hashCode() {
      return Objects.hash(myCode, myModifier);
    }
  }
}
