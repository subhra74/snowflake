package com.jediterm.terminal.ui.settings;

import javax.swing.*;

public interface SystemSettingsProvider {
  KeyStroke[] getCopyKeyStrokes();

  KeyStroke[] getPasteKeyStrokes();

  KeyStroke[] getClearBufferKeyStrokes();

  KeyStroke[] getNewSessionKeyStrokes();

  KeyStroke[] getCloseSessionKeyStrokes();

  KeyStroke[] getFindKeyStrokes();

  KeyStroke[] getPageUpKeyStrokes();

  KeyStroke[] getPageDownKeyStrokes();

  KeyStroke[] getLineUpKeyStrokes();

  KeyStroke[] getLineDownKeyStrokes();
}
