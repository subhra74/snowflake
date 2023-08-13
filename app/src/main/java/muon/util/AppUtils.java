package muon.util;

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

        width = Math.min(width, 900);
        height = Math.min(height, 768);

        return new Dimension(width, height);
    }
}
