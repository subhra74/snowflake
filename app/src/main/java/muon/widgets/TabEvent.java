package muon.widgets;

import java.awt.*;
import java.awt.event.ActionEvent;

public class TabEvent extends ActionEvent {
    private int selectedIndex;
    private Component tabContent;

    public TabEvent(Object source, int index, Component tabContent) {
        super(source, index, "tab_event");
        this.selectedIndex = index;
        this.tabContent = tabContent;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Component getTabContent() {
        return tabContent;
    }
}
