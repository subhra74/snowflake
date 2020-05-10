package util;

public class PathUtils {
    public static String combineUnix(String path1, String path2) {
        return combine(path1, path2, "/");
    }

    public static String combineWin(String path1, String path2) {
        return combine(path1, path2, "\\");
    }

    public static String combine(String path1, String path2, String separator) {
        if (path2.startsWith(separator)) {
            path2 = path2.substring(1);
        }
        if (!path1.endsWith(separator)) {
            return path1 + separator + path2;
        } else {
            return path1 + path2;
        }
    }

    public static String getFileName(String file) {
        if (file.endsWith("/") || file.endsWith("\\")) {
            file = file.substring(0, file.length() - 1);
        }
        int index1 = file.lastIndexOf('/');
        int index2 = file.lastIndexOf('\\');
        int index = index1 > index2 ? index1 : index2;
        if (index >= 0) {
            return file.substring(index + 1);
        }
        return file;
    }

    public static String getParent(String file) {
        if (file.endsWith("/") || file.endsWith("\\")) {
            file = file.substring(0, file.length() - 1);
        }
        if (file.length() == 0) {
            return null;
        }
        int index1 = file.lastIndexOf('/');
        int index2 = file.lastIndexOf('\\');
        int index = index1 > index2 ? index1 : index2;
        if (index >= 0) {
            return file.substring(0, index + 1);
        }
        return file;
    }

    public static boolean isSamePath(String path1, String path2) {
        if (path1 == null && path2 == null) {
            return true;
        }
        if (path1 == null) {
            return false;
        }
        return (path1.equals(path2) || (path1 + "/").equals(path2) || (path1 + "\\").equals(path2));
    }
}
