package com.jediterm.terminal.ui;

import javax.swing.*;
import java.awt.*;

/**
 * @author traff
 */
public interface AbstractTabs<T extends Component> {

  int getTabCount();

  void addTab(String name, T terminal);

  String getTitleAt(int index);

  int getSelectedIndex();

  void setSelectedIndex(int index);

  void setTabComponentAt(int index, Component component);

  int indexOfComponent(Component component);

  int indexOfTabComponent(Component component);

  void removeAll();

  void remove(T terminal);

  void setTitleAt(int index, String name);

  void setSelectedComponent(T terminal);

  JComponent getComponent();

  T getComponentAt(int index);

  void addChangeListener(TabChangeListener listener);

  interface TabChangeListener {
    void tabRemoved();

    void selectionChanged();
  }
}
