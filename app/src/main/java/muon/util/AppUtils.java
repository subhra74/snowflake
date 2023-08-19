package muon.util;

import javax.swing.*;
import java.awt.*;

public class AppUtils {
    public static Dimension calculateDefaultWindowSize() {
        Insets inset = Toolkit.getDefaultToolkit().getScreenInsets(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getDefaultScreenDevice().getDefaultConfiguration());

        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();

        int screenWidth = screenD.width - inset.left - inset.right;
        int screenHeight = screenD.height - inset.top - inset.bottom;

        int width = (screenWidth * 80) / 100;
        int height = (screenHeight * 80) / 100;

        width = Math.min(width, 1024);
        height = Math.min(height, 700);

        return new Dimension(width, height);
    }

    public static void makeEqualSize(JComponent... components) {
        var width = 0;
        var height = 0;
        for (var component :
                components) {
            width = Math.max(component.getPreferredSize().width, width);
            height = Math.max(component.getPreferredSize().height, height);
        }

        var buttonSize = new Dimension(width, height);

        for (var component :
                components) {
            component.setPreferredSize(buttonSize);
            component.setMaximumSize(buttonSize);
        }
    }
}
