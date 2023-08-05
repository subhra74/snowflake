package muon.ui.widgets;

import java.awt.event.ActionEvent;

public class TabEvent extends ActionEvent {
    private int selectedIndex;

    public TabEvent(Object source, int index) {
        super(source, index, "tab_event");
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
