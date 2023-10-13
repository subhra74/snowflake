package muon.styles;

import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AppTheme {
    public static final AppTheme INSTANCE = new AppTheme();
    public static Insets BUTTON_MARGIN = new Insets(150, 15, 5, 15);

    public Color getBackground() {
        return new Color(40, 42, 45);
    }

    public Color getButtonBackground() {
        return new Color(40, 42, 45);
    }

    public Color getButtonRollOverBackground() {
        return new Color(45, 48, 52);
    }

    public Color getButtonPressedBackground() {
        return new Color(27, 27, 27);
    }

    public Color getDarkControlBackground() {
        return new Color(35, 37, 39);
    }

    public Color getForeground() {
        return new Color(180, 180, 180);
    }

    public Color getDisabledForeground() {
        return new Color(100, 100, 100);
    }

    public Color getTitleForeground() {
        return new Color(180, 180, 180);
    }

    public Color getDarkForeground() {
        return new Color(140, 140, 140);
    }

    public Color getHomePanelButtonColor() {
        return new Color(31, 31, 31);
    }

    public Color getButtonBorderColor() {
        return new Color(60, 60, 60);
    }

    public Color getScrollThumbColor() {
        return new Color(75, 75, 75);
    }

    public Color getScrollThumbRollOverColor() {
        return new Color(90, 90, 90);
    }

    public Color getTextFieldBorderColor() {
        return new Color(64, 64, 64);
    }

    public Color getSelectionColor() {
        return new Color(52, 117, 233);
    }

    public Color getSelectionForeground() {
        return Color.WHITE;
    }

    public int getButtonBorderArc() {
        return 7;
    }

    public Color getSplitPaneBackground() {
        return getButtonBorderColor();
    }

    public Color getListSelectionColor() {
        return new Color(46, 67, 110);
    }

    public Color getListIconColor() {
        return new Color(0, 120, 212);
    }

    public Object[] getDefaultStyles() {
        Font widgetFont = new Font(Font.DIALOG, Font.PLAIN, 12);
        return new Object[]{
                "Button.font", widgetFont,
                "Button.background", getButtonBackground(),
                "Button.disabledForeground", getDisabledForeground(),
                "Button.foreground", getForeground(),
                "Button.light", getButtonRollOverBackground(),
                "Button.highlight", getButtonPressedBackground(),
                "Button.border", new EmptyBorder(new Insets(5, 15, 5, 15)),

                "CheckBox.font", widgetFont,
                "CheckBox.background", getBackground(),
                "CheckBox.foreground", getForeground(),
                "CheckBox.border", new EmptyBorder(new Insets(0, 0, 0, 0)),

                "ProgressBar.foreground", getSelectionColor(),
                "ProgressBar.selectionBackground", getButtonPressedBackground(),
                "ProgressBar.border", null,

                "Label.font", widgetFont,
                "Label.background", getBackground(),
                "Label.foreground", getForeground(),
                "Label.disabledForeground", getForeground(),
                "Label.disabledShadow", getForeground(),
                "Label.border", null,

                "TextField.background", getBackground(),
                "TextField.border", new EmptyBorder(5, 5, 5, 5),
                "TextField.foreground", getForeground(),
                "TextField.selectionBackground", getSelectionColor(),
                "TextField.selectionForeground", getSelectionForeground(),
                "TextField.caretForeground", getForeground(),

                "PasswordField.background", getBackground(),
                "PasswordField.border", new EmptyBorder(5, 5, 5, 5),
                "PasswordField.foreground", getForeground(),
                "PasswordField.selectionBackground", getSelectionColor(),
                "PasswordField.selectionForeground", getSelectionForeground(),
                "PasswordField.caretForeground", getForeground(),

                "TextArea.background", getBackground(),
                "TextArea.border", new EmptyBorder(5, 5, 5, 5),
                "TextArea.foreground", getForeground(),
                "TextArea.selectionBackground", getSelectionColor(),
                "TextArea.selectionForeground", getSelectionForeground(),
                "TextArea.caretForeground", getForeground(),

                "Tree.background", getBackground(),
                "Tree.foreground", getForeground(),
//                "Tree.openIcon", new FontIcon(FontIconCodes.RI_FOLDER_OPEN_FILL,
//                24, 24, 16.0f, getForeground()),
//                "Tree.closedIcon", new FontIcon(FontIconCodes.RI_FOLDER_FILL,
//                24, 24, 16.0f, getForeground()),
//                "Tree.leafIcon", new FontIcon(FontIconCodes.RI_CLOSE_LINE,
//                24, 24, 16.0f, getForeground()),
                "Tree.selectionForeground", getSelectionForeground(),
                "Tree.textForeground", getForeground(),
                "Tree.selectionBackground", getListSelectionColor(),
                "Tree.textBackground", getBackground(),
                "Tree.rendererFillBackground", true,
                //"Tree.rendererMargins", new Insets(25,25,5,5),
                "Tree.selectionBorderColor", getListSelectionColor(),

                "SplitPane.background", getButtonBorderColor(),
                "SplitPane.border", new EmptyBorder(0, 0, 0, 0),
                "SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0),

                "ComboBox.background", getBackground(),
                "ComboBox.foreground", getForeground(),
                "ComboBox.border", new EmptyBorder(0, 0, 0, 0),
                "ComboBox.padding", new Insets(5, 5, 5, 5),
                "ComboBox.squareButton", Boolean.FALSE,
                "ComboBox.Border", new EmptyBorder(3, 3, 3, 3),

                "Panel.background", getBackground(),

                "ScrollBar.width", 12,
//                "ScrollBar.maximumThumbSize", new Dimension(5, 5),
                "ScrollBar.minimumThumbSize", new Dimension(12, 12),
        };
    }
}
