package com.jediterm.terminal.ui;

//import com.google.common.base.Supplier;
import com.jediterm.terminal.util.Util;
////import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author traff
 */
public class UIUtil {
  public static final String OS_NAME = System.getProperty("os.name");
  public static final String OS_VERSION = System.getProperty("os.version").toLowerCase();

  protected static final String _OS_NAME = OS_NAME.toLowerCase();
  public static final boolean isWindows = _OS_NAME.startsWith("windows");
  public static final boolean isOS2 = _OS_NAME.startsWith("os/2") || _OS_NAME.startsWith("os2");
  public static final boolean isMac = _OS_NAME.startsWith("mac");
  public static final boolean isLinux = _OS_NAME.startsWith("linux");
  public static final boolean isUnix = !isWindows && !isOS2;
  private static final boolean IS_ORACLE_JVM = isOracleJvm();

  public static final String JAVA_RUNTIME_VERSION = System.getProperty("java.runtime.version");

  public static boolean isRetina() {
    if (isJavaVersionAtLeast("1.7.0_40") && IS_ORACLE_JVM) {
      GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
      final GraphicsDevice device = env.getDefaultScreenDevice();

      try {
        Field field = device.getClass().getDeclaredField("scale");

        if (field != null) {
          field.setAccessible(true);
          Object scale = field.get(device);

          if (scale instanceof Integer && ((Integer)scale).intValue() == 2) {
            return true;
          }
        }
      }
      catch (Exception ignore) {
      }
    }

    final Float scaleFactor = (Float)Toolkit.getDefaultToolkit().getDesktopProperty("apple.awt.contentScaleFactor");

    if (scaleFactor != null && scaleFactor.intValue() == 2) {
      return true;
    }
    return false;
  }

  private static boolean isOracleJvm() {
    final String vendor = getJavaVmVendor();
    return vendor != null && Util.containsIgnoreCase(vendor, "Oracle");
  }

  public static String getJavaVmVendor() {
    return System.getProperty("java.vm.vendor");
  }

  public static boolean isJavaVersionAtLeast(String v) {
    return Util.compareVersionNumbers(JAVA_RUNTIME_VERSION, v) >= 0;
  }

  public static void applyRenderingHints(final Graphics g) {
    Graphics2D g2d = (Graphics2D)g;
    Toolkit tk = Toolkit.getDefaultToolkit();
    //noinspection HardCodedStringLiteral
    Map map = (Map)tk.getDesktopProperty("awt.font.desktophints");
    if (map != null) {
      g2d.addRenderingHints(map);
    }
  }
}
