package muon.util;

import java.util.Objects;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return Objects.isNull(str) || str.isEmpty();
    }

    public static boolean equalsIgnoreCase(String s1, String s2) {
        if (Objects.isNull(s1) || Objects.isNull(s2)) {
            return false;
        }
        return s1.equalsIgnoreCase(s2);
    }
}
