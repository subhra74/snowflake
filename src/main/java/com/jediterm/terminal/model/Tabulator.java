package com.jediterm.terminal.model;


/**
 * Provides a tabulator that keeps track of the tab stops of a terminal.
 */
public interface Tabulator
{
  /**
   * Clears the tab stop at the given position.
   * 
   * @param position
   *          the column position used to determine the next tab stop, > 0.
   */
  void clearTabStop(int position);

  /**
   * Clears all tab stops.
   */
  void clearAllTabStops();

  /**
   * Returns the width of the tab stop that is at or after the given position.
   * 
   * @param position
   *          the column position used to determine the next tab stop, >= 0.
   * @return the next tab stop width, >= 0.
   */
  int getNextTabWidth(int position);

  /**
   * Returns the width of the tab stop that is before the given position.
   * 
   * @param position
   *          the column position used to determine the previous tab stop, >= 0.
   * @return the previous tab stop width, >= 0.
   */
  int getPreviousTabWidth(int position);

  /**
   * Returns the next tab stop that is at or after the given position.
   * 
   * @param position
   *          the column position used to determine the next tab stop, >= 0.
   * @return the next tab stop, >= 0.
   */
  int nextTab(int position);

  /**
   * Returns the previous tab stop that is before the given position.
   * 
   * @param position
   *          the column position used to determine the previous tab stop, >= 0.
   * @return the previous tab stop, >= 0.
   */
  int previousTab(int position);

  /**
   * Sets the tab stop to the given position.
   * 
   * @param position
   *          the position of the (new) tab stop, > 0.
   */
  void setTabStop(int position);

  void resize(int width);
}
