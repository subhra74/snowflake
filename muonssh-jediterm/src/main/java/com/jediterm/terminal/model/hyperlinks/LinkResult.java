package com.jediterm.terminal.model.hyperlinks;

import java.util.ArrayList;
import java.util.Arrays;

//import com.google.common.collect.Lists;
//import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author traff
 */
public class LinkResult {
  private final LinkResultItem myItem;
  private List<LinkResultItem> myItemList;

  public LinkResult( LinkResultItem item) {
    myItem = item;
    myItemList = null;
  }

  public LinkResult( List<LinkResultItem> itemList) {
    myItemList = itemList;
    myItem = null;
  }

  public List<LinkResultItem> getItems() {
    if (myItemList == null) {
      myItemList = new ArrayList<>(Arrays.asList(myItem));
    }
    return myItemList;
  }
}
