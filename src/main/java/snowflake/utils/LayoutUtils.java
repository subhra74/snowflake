package snowflake.utils;

import javax.swing.*;
import java.awt.*;

public final class LayoutUtils {
    public static final void makeSameSize(JComponent... components) {
        int maxWidth = 0, maxHeight = 0;
        for (JComponent component : components) {
            Dimension dimension = component.getPreferredSize();
            maxWidth = Math.max(maxWidth, dimension.width);
            maxHeight = Math.max(maxHeight, dimension.height);
        }
        Dimension maxDimension = new Dimension(maxWidth, maxHeight);
        for (JComponent component : components) {
            component.setMaximumSize(maxDimension);
            component.setPreferredSize(maxDimension);
            component.setMinimumSize(maxDimension);
        }
    }
}
