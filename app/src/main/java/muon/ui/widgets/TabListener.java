package muon.ui.widgets;

public interface TabListener {
    void selectionChanged(TabEvent e);

    boolean tabClosing(TabEvent e);

    void tabClosed(TabEvent e);
}
