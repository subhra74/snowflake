package com.jediterm.terminal.ui;

import com.jediterm.terminal.TerminalDisplay;
import com.jediterm.terminal.TtyConnector;

import javax.swing.*;
import java.awt.*;

/**
 * @author traff
 */
public interface TerminalWidget {
  JediTermWidget createTerminalSession(TtyConnector ttyConnector);

  JComponent getComponent();

  default JComponent getPreferredFocusableComponent() {
    return getComponent();
  }

  boolean canOpenSession();

  void setTerminalPanelListener(TerminalPanelListener terminalPanelListener);

  Dimension getPreferredSize();

  TerminalSession getCurrentSession();

  TerminalDisplay getTerminalDisplay();

  void addListener(TerminalWidgetListener listener);
  void removeListener(TerminalWidgetListener listener);
}
