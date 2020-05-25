package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtils {
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (si ? "" : "i");
        return String.format("%.1f %s", bytes / Math.pow(unit, exp), pre);
    }

    public static final String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
    }
}
