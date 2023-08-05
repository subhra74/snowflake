package muon.ui.styles;

import java.awt.*;

public class AppTheme {
    public static final AppTheme INSTANCE=new AppTheme();

    public Color getBackground(){
        return new Color(31, 31, 31);
    }

    public Color getTabSelectionColor(){
        return new Color(52, 117, 233);
    }
}
