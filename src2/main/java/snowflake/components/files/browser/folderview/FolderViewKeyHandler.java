package snowflake.components.files.browser.folderview;

import snowflake.common.FileInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FolderViewKeyHandler extends KeyAdapter {

    private JTable table;
    private FolderViewTableModel model;

    private String prefix = "";
    private String typedString = "";
    private long lastTime = 0L;

    private long timeFactor = 1000L;

    public FolderViewKeyHandler(JTable table, FolderViewTableModel model) {
        super();
        this.table = table;
        this.model = model;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Table key press");
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            return;
        }
        if (isNavigationKey(e)) {
            prefix = "";
            typedString = "";
            lastTime = 0L;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

        if (table.getRowCount() == 0 || e.isAltDown()
                || isMenuShortcutKeyDown(e) || isNavigationKey(e)) {
            // Nothing to select
            return;
        }
        boolean startingFromSelection = true;

        char c = e.getKeyChar();

        long time = e.getWhen();
        int startIndex = adjustIndex(
                table.getSelectedRows().length > 0 ? table.getSelectedRows()[0]
                        : -1,
                table);
        if (time - lastTime < timeFactor) {
            typedString += c;
            if ((prefix.length() == 1) && (c == prefix.charAt(0))) {
                // Subsequent same key presses move the keyboard focus to the
                // next
                // object that starts with the same letter.
                startIndex++;
            } else {
                prefix = typedString;
            }
        } else {
            startIndex++;
            typedString = "" + c;
            prefix = typedString;
        }
        lastTime = time;

        if (startIndex < 0 || startIndex >= table.getRowCount()) {
            startingFromSelection = false;
            startIndex = 0;
        }
        int index = getNextMatch(prefix, startIndex);
        if (index >= 0) {
            System.out.println("Selecting column: " + index);
            table.setRowSelectionInterval(index, index);
            table.scrollRectToVisible(
                    new Rectangle(table.getCellRect(index, 0, true)));
        } else if (startingFromSelection) { // wrap
            index = getNextMatch(prefix, 0);
            if (index >= 0) {
                table.setRowSelectionInterval(index, index);
                table.scrollRectToVisible(
                        new Rectangle(table.getCellRect(index, 0, true)));
            }
        }
    }

    private boolean isNavigationKey(KeyEvent event) {
        InputMap inputMap = table
                .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke key = KeyStroke.getKeyStrokeForEvent(event);
        if (inputMap != null && inputMap.get(key) != null) {
            return true;
        }
        return false;
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public FolderViewTableModel getModel() {
        return model;
    }

    public void setModel(FolderViewTableModel model) {
        this.model = model;
    }

    static boolean isMenuShortcutKeyDown(InputEvent event) {
        return (event.getModifiersEx()
                & Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()) != 0;
    }

    private static int adjustIndex(int index, JTable list) {
        return index < list.getRowCount() ? index : -1;
    }

    private int getNextMatch(String prefix, int startIndex) {
        for (int i = startIndex; i < table.getRowCount(); i++) {
            int index = table.convertRowIndexToModel(i);
            FileInfo info = model.getItemAt(index);
            if (info.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }
}
