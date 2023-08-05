package muon.ui.styles;

import javax.swing.plaf.basic.BasicLookAndFeel;

public class FlatLookAndFeel extends BasicLookAndFeel {
    @Override
    public String getName() {
        return "FlatLookAndFeel";
    }

    @Override
    public String getID() {
        return "FlatLookAndFeel";
    }

    @Override
    public String getDescription() {
        return "FlatLookAndFeel";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
}
