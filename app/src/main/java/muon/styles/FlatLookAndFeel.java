package muon.styles;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class FlatLookAndFeel extends MetalLookAndFeel {

    @Override
    public void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        table.putDefaults(new Object[]{
                "ButtonUI", FlatButtonUI.class.getName(),
                "TextFieldUI", FlatTextFieldUI.class.getName(),
                "PasswordFieldUI", FlatPasswordFieldUI.class.getName(),
                "TreeUI", FlatTreeUI.class.getName(),
                "SplitPaneUI", FlatSplitPaneUI.class.getName(),
                "PanelUI", FlatPanelUI.class.getName(),
                "ComboBoxUI", FlatComboBoxUI.class.getName(),
                "LabelUI", FlatLabelUI.class.getName(),
                "CheckBoxUI", FlatCheckBoxUI.class.getName(),
                "ProgressBarUI", FlatProgressBarUI.class.getName(),
                "ScrollBarUI", FlatScrollBarUI.class.getName()
        });
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);
        //table.putDefaults(AppTheme.INSTANCE.getDefaultStyles());
    }

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
