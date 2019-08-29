package snowflake.utils;

import java.awt.*;

public class DpiHelper {
    private static float dpiScale;
    private static Font smallFont, normatFont, largeFont;

    public static final int toPixel(int value) {
        if (dpiScale == 0.0f) {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            dpiScale = dpi / 96.0f;
        }
        return (int) (value * dpiScale);
    }
}
