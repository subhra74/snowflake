package com.jediterm.terminal.ui;

import java.util.List;

/**
 * @author traff
 */
public interface TerminalActionProvider {
  List<TerminalAction> getActions();
  TerminalActionProvider getNextProvider();
  void setNextProvider(TerminalActionProvider provider);
}
