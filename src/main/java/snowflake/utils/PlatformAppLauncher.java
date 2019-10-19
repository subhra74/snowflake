package snowflake.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PlatformAppLauncher {
    public static void open(File f) throws FileNotFoundException {
        if (!f.exists()) {
            throw new FileNotFoundException();
        }
        try {
            ProcessBuilder builder = new ProcessBuilder();
            ArrayList<String> lst = new ArrayList<String>();
            lst.add("rundll32");
            lst.add("url.dll,FileProtocolHandler");
            lst.add(f.getAbsolutePath());
            builder.command(lst);
            builder.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean shellLaunch(String file) {
        System.out.println("Launching file: " + file);
        if (File.separator.equals("\\")) {
            try {
                open(new File(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(file));
                    return true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Desktop not supported");
            }
        }

        return false;
    }

    public static boolean shellEdit(String file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().edit(new File(file));
                return true;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }
}
