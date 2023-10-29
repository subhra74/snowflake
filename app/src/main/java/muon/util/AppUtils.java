package muon.util;

import muon.styles.AppTheme;
import muon.widgets.FlatButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppUtils {
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

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

    public static boolean isWindows() {
        return "windows".equalsIgnoreCase(System.getProperty("os.name"));
    }

    public static JButton createIconButton(IconCode iconCode) {
        return createIconButton(iconCode, 18.0f);
    }

    public static JButton createIconButton(IconCode iconCode, float iconSize) {
        var button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(AppTheme.INSTANCE.getForeground());
        button.setBorder(new EmptyBorder(1, 1, 1, 1));
        button.setFont(IconFont.getSharedInstance().getIconFont(iconSize));
        button.setText(iconCode.getValue());
        return button;
    }

    public static FlatButton createAddTabButton() {
        return new FlatButton(IconCode.RI_ADD_LINE);
    }

    public static FlatButton createViewButton() {
        return new FlatButton(IconCode.RI_LAYOUT_GRID_LINE);
    }

    public static FlatButton createSnippetButton() {
        return new FlatButton(IconCode.RI_CODE_BOX_LINE);
    }

    public static FlatButton createMoreButton() {
        return new FlatButton(IconCode.RI_MORE_2_LINE);
    }

    public static void runAsync(Runnable r) {
        cachedThreadPool.submit(r);
    }

    public static String formatSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static final String formatDate(LocalDateTime dateTime) {
        //return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    public static boolean isPopupTrigger(MouseEvent e) {
        if (e.isPopupTrigger()) {
            return true;
        }
        if (isMacOSX()) {
            return (e.getButton() == 1 && e.isControlDown());
        }
        return false;
    }

    public static boolean isMacOSX() {
        return Optional.ofNullable(System.getProperty("os.name")).orElse("Linux").toLowerCase().contains("mac");
    }

    public static LocalDateTime getModificationTime(BasicFileAttributes attrs) {
        return LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(),
                ZoneId.systemDefault());
    }
}
