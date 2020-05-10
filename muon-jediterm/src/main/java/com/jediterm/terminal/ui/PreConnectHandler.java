package com.jediterm.terminal.ui;

import com.jediterm.terminal.Questioner;
import com.jediterm.terminal.Terminal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PreConnectHandler implements Questioner, KeyListener {
  private Object mySync = new Object();
  private Terminal myTerminal;
  private StringBuffer myAnswer;
  private boolean myVisible;

  public PreConnectHandler(Terminal terminal) {
    this.myTerminal = terminal;
    this.myVisible = true;
  }

  // These methods will suspend the current thread and wait for 
  // the event handling thread to provide the answer.
  public String questionHidden(String question) {
    myVisible = false;
    String answer = questionVisible(question, null);
    myVisible = true;
    return answer;
  }

  public String questionVisible(String question, String defValue) {
    synchronized (mySync) {
      myTerminal.writeUnwrappedString(question);
      myAnswer = new StringBuffer();
      if (defValue != null) {
        myAnswer.append(defValue);
        myTerminal.writeUnwrappedString(defValue);
      }
      try {
        mySync.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      String answerStr = myAnswer.toString();
      myAnswer = null;
      return answerStr;
    }
  }

  public void showMessage(String message) {
    myTerminal.writeUnwrappedString(message);
    myTerminal.nextLine();
  }

  public void keyPressed(KeyEvent e) {
    if (myAnswer == null) return;
    synchronized (mySync) {
      boolean release = false;

      switch (e.getKeyCode()) {
        case KeyEvent.VK_BACK_SPACE:
          if (myAnswer.length() > 0) {
            myTerminal.backspace();
            myTerminal.eraseInLine(0);
            myAnswer.deleteCharAt(myAnswer.length() - 1);
          }
          break;
        case KeyEvent.VK_ENTER:
          myTerminal.nextLine();
          release = true;
          break;
      }

      if (release) mySync.notifyAll();
    }

  }

  public void keyReleased(KeyEvent e) {

  }

  public void keyTyped(KeyEvent e) {
    if (myAnswer == null) return;
    char c = e.getKeyChar();
    if (Character.getType(c) != Character.CONTROL) {
      if (myVisible) myTerminal.writeCharacters(Character.toString(c));
      myAnswer.append(c);
    }
  }


}
