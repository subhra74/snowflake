package muon.ui.styles;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AppTheme {
    public static final AppTheme INSTANCE = new AppTheme();
    public static Insets BUTTON_MARGIN = new Insets(150, 15, 5, 15);

    public Color getBackground() {
        return new Color(31, 31, 31);
    }

    public Color getButtonBackground() {
        return new Color(35, 35, 35);
    }

    public Color getButtonRollOverBackground() {
        return new Color(40, 40, 40);
    }

    public Color getButtonPressedBackground() {
        return new Color(27, 27, 27);
    }

    public Color getDarkControlBackground() {
        return new Color(24, 24, 24);
    }

    public Color getForeground() {
        return new Color(120, 120, 120);
    }

    public Color getLightForeground() {
        return new Color(100, 100, 100);
    }

    public Color getHomePanelButtonColor() {
        return new Color(31, 31, 31);
    }

    public Color getButtonBorderColor() {
        return new Color(54, 54, 54);
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
        return Color.BLACK;
    }

    public Color getListSelectionColor(){
        return new Color(46,67,110);
    }

    public Object[] getDefaultStyles() {
        Font widgetFont = new Font(Font.DIALOG, Font.PLAIN, 12);
        return new Object[]{
                "Button.font", widgetFont,
                "Button.background", getButtonBackground(),
                "Button.foreground", getForeground(),
                "Button.light", getButtonRollOverBackground(),
                "Button.highlight", getButtonPressedBackground(),
                "Button.border", new EmptyBorder(new Insets(5, 15, 5, 15)),

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
                "PasswordField.border", new LineBorder(Color.RED, 5),
                "PasswordField.foreground", getForeground(),
                "PasswordField.selectionBackground", getSelectionColor(),
                "PasswordField.selectionForeground", getSelectionForeground(),
                "PasswordField.caretForeground", getForeground(),

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

                "SplitPane.background", Color.BLACK,
                "SplitPane.border", new EmptyBorder(0, 0, 0, 0),
                "SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0),

                "ComboBox.background", getBackground(),
                "ComboBox.foreground", getForeground(),
                "ComboBox.border", new EmptyBorder(0, 0, 0, 0),
                "ComboBox.padding", new Insets(5, 5, 5, 5),
                "ComboBox.squareButton", Boolean.FALSE,
                "ComboBox.Border", new EmptyBorder(3,3,3,3),

                "Panel.background", getBackground(),
        };
    }
}