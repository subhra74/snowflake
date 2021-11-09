package com.jediterm.terminal;

import com.jediterm.terminal.ui.*;
import com.jediterm.terminal.ui.settings.TabbedSettingsProvider;
////import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author traff
 */
public class TabbedTerminalWidget extends AbstractTabbedTerminalWidget<JediTermWidget> {
  public TabbedTerminalWidget( TabbedSettingsProvider settingsProvider,  Function<AbstractTabbedTerminalWidget, JediTermWidget> createNewSessionAction) {
    super(settingsProvider, createNewSessionAction::apply);
  }

  @Override
  public JediTermWidget createInnerTerminalWidget() {
    return new JediTermWidget(getSettingsProvider());
  }

  @Override
  protected AbstractTabs<JediTermWidget> createTabbedPane() {
    return new TerminalTabsImpl();
  }
}
