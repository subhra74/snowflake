/**
 *
 */
package com.jediterm.terminal;

import org.apache.log4j.Logger;

import java.awt.*;

public enum TerminalMode {
  Null,
  CursorKey {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setApplicationArrowKeys(enabled);
    }
  },
  ANSI,
  WideColumn {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      int h = terminal.getTerminalHeight();
      Dimension d = enabled ? new Dimension(132, h) : new Dimension(80, h);

      terminal.resize(d, RequestOrigin.Remote);
      terminal.clearScreen();
      terminal.resetScrollRegions();
    }
  },
  CursorVisible {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setCursorVisible(enabled);
    }
  },
  AlternateBuffer {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.useAlternateBuffer(enabled);
    }
  },
  SmoothScroll,
  ReverseVideo,
  OriginMode {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
    }
  },
  AutoWrap {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      //we do nothing just switching the mode
    }
  },
  AutoRepeatKeys,
  Interlace,
  Keypad {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setApplicationKeypad(enabled);
    }
  },
  StoreCursor {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      if (enabled) {
        terminal.saveCursor();
      }
      else {
        terminal.restoreCursor();
      }
    }
  }, 
  CursorBlinking {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setBlinkingCursor(enabled);
    }
  }, 
  AllowWideColumn, 
  ReverseWrapAround, 
  AutoNewLine {
    @Override
    public void setEnabled(Terminal terminal, boolean enabled) {
      terminal.setAutoNewLine(enabled);
    }
  }, 
  KeyboardAction, 
  InsertMode,
  SendReceive,
  EightBitInput, //Interpret "meta" key, sets eighth bit. (enables the eightBitInput resource).
               // http://www.leonerd.org.uk/hacks/hints/xterm-8bit.html

  AltSendsEscape //See section Alt and Meta Keys in http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
          {
            @Override
            public void setEnabled(Terminal terminal, boolean enabled) {
              terminal.setAltSendsEscape(enabled);
            }
          }
  ;

  private static final Logger LOG = Logger.getLogger(TerminalMode.class);
  
  public void setEnabled(Terminal terminal, boolean enabled) {
    LOG.error("Mode " + name() + " is not implemented, setting to " + enabled);
  }
}