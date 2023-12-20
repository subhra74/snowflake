package muon.widgets;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;

public class SwitchButton extends JLabel {
    private IconCode onIcon, offIcon;

    public SwitchButton() {
        this(IconCode.RI_TOGGLE_FILL, IconCode.RI_TOGGLE_LINE, 32);
    }

    public SwitchButton(IconCode onIcon, IconCode offIcon, int size) {
        setFont(IconFont.getSharedInstance().getIconFont(size));
        this.onIcon = onIcon;
        this.offIcon = offIcon;
        setText(onIcon.getValue());
    }
}
