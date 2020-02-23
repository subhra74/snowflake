package com.jediterm.terminal.model.hyperlinks;

//import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public class LinkInfo {
  private final Runnable myNavigateCallback;

  public LinkInfo( Runnable navigateCallback) {
    myNavigateCallback = navigateCallback;
  }

  public void navigate() {
    myNavigateCallback.run();
  }
}
