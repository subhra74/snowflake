package muon.util;

import java.nio.file.Paths;
import java.util.Objects;

public class PathUtils {
    public static String getParentDir(String path) {
        var parent = Paths.get(path).getParent();
        if (Objects.nonNull(parent)) {
            return parent.toAbsolutePath().toString();
        }
        return null;
    }

    public static String getFileName(String path) {
        var name = Paths.get(path).getFileName();
        if (Objects.nonNull(name)) {
            return name.toString();
        }
        return null;
    }
}
