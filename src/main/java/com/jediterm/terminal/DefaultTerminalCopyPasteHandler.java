package com.jediterm.terminal;

import com.jediterm.terminal.ui.UIUtil;
import org.apache.log4j.Logger;
////import org.jetbrains.annotations.NotNull;
////import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.*;

public class DefaultTerminalCopyPasteHandler implements TerminalCopyPasteHandler, ClipboardOwner {
  private static final Logger LOG = Logger.getLogger(DefaultTerminalCopyPasteHandler.class);

  @Override
  public void setContents( String text, boolean useSystemSelectionClipboardIfAvailable) {
    if (useSystemSelectionClipboardIfAvailable) {
      Clipboard systemSelectionClipboard = getSystemSelectionClipboard();
      if (systemSelectionClipboard != null) {
        setClipboardContents(new StringSelection(text), systemSelectionClipboard);
        return;
      }
    }
    setSystemClipboardContents(text);
  }

  
  @Override
  public String getContents(boolean useSystemSelectionClipboardIfAvailable) {
    if (useSystemSelectionClipboardIfAvailable) {
      Clipboard systemSelectionClipboard = getSystemSelectionClipboard();
      if (systemSelectionClipboard != null) {
        return getClipboardContents(systemSelectionClipboard);
      }
    }
    return getSystemClipboardContents();
  }

  @SuppressWarnings("WeakerAccess")
  protected void setSystemClipboardContents( String text) {
    setClipboardContents(new StringSelection(text), getSystemClipboard());
  }

  
  private String getSystemClipboardContents() {
    return getClipboardContents(getSystemClipboard());
  }

  private void setClipboardContents( Transferable contents,  Clipboard clipboard) {
    if (clipboard != null) {
      try {
        clipboard.setContents(contents, this);
      }
      catch (IllegalStateException e) {
        logException("Cannot set contents", e);
      }
    }
  }

  
  private String getClipboardContents( Clipboard clipboard) {
    if (clipboard != null) {
      try {
        return (String) clipboard.getData(DataFlavor.stringFlavor);
      }
      catch (Exception e) {
        logException("Cannot get clipboard contents", e);
      }
    }
    return null;
  }

  
  private static Clipboard getSystemClipboard() {
    try {
      return Toolkit.getDefaultToolkit().getSystemClipboard();
    }
    catch (IllegalStateException e) {
      logException("Cannot get system clipboard", e);
      return null;
    }
  }

  
  private static Clipboard getSystemSelectionClipboard() {
    try {
      return Toolkit.getDefaultToolkit().getSystemSelection();
    }
    catch (IllegalStateException e) {
      logException("Cannot get system selection clipboard", e);
      return null;
    }
  }

  private static void logException( String message,  Exception e) {
    if (UIUtil.isWindows && e instanceof IllegalStateException) {
      LOG.debug(message, e);
    }
    else {
      LOG.warn(message, e);
    }
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
  }
}
