package com.jediterm.terminal.model.hyperlinks;

////import org.jetbrains.annotations.Nullable;

/**
 * @author traff
 */
public interface HyperlinkFilter {

  
  LinkResult apply(String line);
}
