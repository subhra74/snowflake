package util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final LocalDateTime toDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli),
                ZoneId.systemDefault());
    }

    public static final String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
    }

    public static final long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
