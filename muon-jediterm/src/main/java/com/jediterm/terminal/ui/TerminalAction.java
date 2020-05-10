package com.jediterm.terminal.ui;

//import com.google.common.base.Predicate;
//import com.google.common.base.Supplier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author traff
 */
public class TerminalAction {
  private final String myName;
  private final KeyStroke[] myKeyStrokes;
  private final Predicate<KeyEvent> myRunnable;

  private Character myMnemonic = null;
  private Supplier<Boolean> myEnabledSupplier = null;
  private Integer myMnemonicKey = null;
  private boolean mySeparatorBefore = false;
  private boolean myHidden = false;

  public TerminalAction(String name, KeyStroke[] keyStrokes, Predicate<KeyEvent> runnable) {
    myName = name;
    myKeyStrokes = keyStrokes;
    myRunnable = runnable;
  }

  public boolean matches(KeyEvent e) {
    for (KeyStroke ks : myKeyStrokes) {
      if (ks.equals(KeyStroke.getKeyStrokeForEvent(e))) {
        return true;
      }
    }
    return false;
  }

  public boolean perform(KeyEvent e) {
    if (myEnabledSupplier != null && !myEnabledSupplier.get()) {
      return false;
    }
    return myRunnable.test(e);
  }

  public static boolean processEvent(TerminalActionProvider actionProvider, final KeyEvent e) {
    for (TerminalAction a : actionProvider.getActions()) {
      if (a.matches(e)) {
        return a.perform(e);
      }
    }

    if (actionProvider.getNextProvider() != null) {
      return processEvent(actionProvider.getNextProvider(), e);
    }

    return false;
  }

  public static boolean addToMenu(JPopupMenu menu, TerminalActionProvider actionProvider) {
    boolean added = false;
    if (actionProvider.getNextProvider() != null) {
      added = addToMenu(menu, actionProvider.getNextProvider());
    }
    boolean addSeparator = added;
    for (final TerminalAction a : actionProvider.getActions()) {
      if (a.isHidden()) {
        continue;
      }
      if (!addSeparator) {
        addSeparator = a.isSeparated();
      }
      if (addSeparator) {
        menu.addSeparator();
        addSeparator = false;
      }

      menu.add(a.toMenuItem());

      added = true;
    }

    return added;
  }

  public int getKeyCode() {
    for (KeyStroke ks : myKeyStrokes) {
      return ks.getKeyCode();
    }
    return 0;
  }

  public int getModifiers() {
    for (KeyStroke ks : myKeyStrokes) {
      return ks.getModifiers();
    }
    return 0;
  }

  public String getName() {
    return myName;
  }
  
  public TerminalAction withMnemonic(Character ch) {
    myMnemonic = ch;
    return this;
  }

  public TerminalAction withMnemonicKey(Integer key) {
    myMnemonicKey = key;
    return this;
  }

  public boolean isEnabled() {
    if (myEnabledSupplier != null) {
      return myEnabledSupplier.get();
    }
    return true;
  }
  
  public TerminalAction withEnabledSupplier(Supplier<Boolean> enabledSupplier) {
    myEnabledSupplier = enabledSupplier;
    return this;
  }

  public TerminalAction separatorBefore(boolean enabled) {
    mySeparatorBefore = enabled;
    return this;
  }
  
  public JMenuItem toMenuItem() {
    JMenuItem menuItem = new JMenuItem(myName);

    if (myMnemonic != null) {
      menuItem.setMnemonic(myMnemonic);
    }
    if (myMnemonicKey != null) {
      menuItem.setMnemonic(myMnemonicKey);
    }

    if (myKeyStrokes.length > 0) {
      menuItem.setAccelerator(myKeyStrokes[0]);
    }

    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        myRunnable.test(null);
      }
    });
    menuItem.setEnabled(isEnabled());
    
    return menuItem;
  }

  public boolean isSeparated() {
    return mySeparatorBefore;
  }

  public boolean isHidden() {
    return myHidden;
  }

  public TerminalAction withHidden(boolean hidden) {
    myHidden = hidden;
    return this;
  }
}
