package com.jediterm.terminal.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

/**
 * @author traff
 */
public class TerminalTabsImpl implements AbstractTabs<JediTermWidget> {
  protected JTabbedPane myTabbedPane = new JTabbedPane();

  @Override
  public int getTabCount() {
    return myTabbedPane.getTabCount();
  }

  @Override
  public void addTab(String name, JediTermWidget terminal) {
    myTabbedPane.addTab(name, terminal);
  }

  @Override
  public String getTitleAt(int index) {
    return myTabbedPane.getTitleAt(index);
  }

  @Override
  public int getSelectedIndex() {
    return myTabbedPane.getSelectedIndex();
  }

  @Override
  public void setSelectedIndex(int index) {
    myTabbedPane.setSelectedIndex(index);
  }

  @Override
  public void setTabComponentAt(int index, Component component) {
    myTabbedPane.setTabComponentAt(index, component);
  }

  @Override
  public int indexOfComponent(Component component) {
    return myTabbedPane.indexOfComponent(component);
  }

  @Override
  public int indexOfTabComponent(Component component) {
    return myTabbedPane.indexOfTabComponent(component);
  }

  @Override
  public void removeAll() {
    myTabbedPane.removeAll();
  }

  @Override
  public void remove(JediTermWidget terminal) {
    myTabbedPane.remove(terminal);
  }

  @Override
  public void setTitleAt(int index, String name) {
    myTabbedPane.setTitleAt(index, name);
  }

  @Override
  public void setSelectedComponent(JediTermWidget terminal) {
    myTabbedPane.setSelectedComponent(terminal);
  }

  @Override
  public JComponent getComponent() {
    return myTabbedPane;
  }

  @Override
  public JediTermWidget getComponentAt(int index) {
    return (JediTermWidget)myTabbedPane.getComponentAt(index);
  }

  @Override
  public void addChangeListener(final TabChangeListener listener) {
    myTabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        listener.selectionChanged();
      }
    });
    
    myTabbedPane.addContainerListener(new ContainerListener() {
      @Override
      public void componentAdded(ContainerEvent e) {
                
      }

      @Override
      public void componentRemoved(ContainerEvent e) {
        if (e.getSource() == myTabbedPane) {
          listener.tabRemoved();
        }
      }
    });
    
  }
}
