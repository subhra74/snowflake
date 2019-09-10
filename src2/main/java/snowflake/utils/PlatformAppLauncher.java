package snowflake.utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlatformAppLauncher {
    public static boolean shellLaunch(String file) {
        System.out.println("Launching file: " + file);
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(file));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Desktop not supported");
        }
        return false;
    }

    public static boolean shellEdit(String file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File(file));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
