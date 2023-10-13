package muon.widgets;

import java.awt.event.ActionEvent;

public class TabSelectionEvent extends ActionEvent {
    private int selectedIndex;
    public TabSelectionEvent(Object source, int index) {
        super(source, index, "tab_selection");
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
