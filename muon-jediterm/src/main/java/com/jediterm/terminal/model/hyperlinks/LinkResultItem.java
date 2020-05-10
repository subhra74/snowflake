package com.jediterm.terminal.model.hyperlinks;

//import org.jetbrains.annotations.NotNull;

/**
 * @author traff
 */
public class LinkResultItem {
  private int myStartOffset;
  private int myEndOffset;

  private LinkInfo myLinkInfo;

  public LinkResultItem(int startOffset, int endOffset,  LinkInfo linkInfo) {
    myStartOffset = startOffset;
    myEndOffset = endOffset;
    myLinkInfo = linkInfo;
  }

  public int getStartOffset() {
    return myStartOffset;
  }

  public int getEndOffset() {
    return myEndOffset;
  }

  public LinkInfo getLinkInfo() {
    return myLinkInfo;
  }
}
