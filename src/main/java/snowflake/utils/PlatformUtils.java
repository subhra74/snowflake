package snowflake.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlatformUtils {
    public static void openWithSystemDefaultApp(String path) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
