package snowflake.components.diskusage;

import snowflake.utils.PathUtils;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DuOutputParser {
    private static final Pattern duPattern = Pattern.compile("([df])\\s([\\d]+)\\s+(.+)");
    private DiskUsageEntry root = new DiskUsageEntry("", "", 0, 0, true);

    public final DiskUsageEntry parseList(List<String> lines, int prefixLen) {
        for (String line : lines) {
            Matcher matcher = duPattern.matcher(line);
            if (matcher.find()) {
                try {
                    String type = matcher.group(1);
                    long size = Long.parseLong(matcher.group(2)) * 512;
                    String path = matcher.group(3).substring(prefixLen);
                    addEntry(type, size, path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return root;
    }

    private void addEntry(String type, long size, String path) {
        String arr[] = path.split("\\/");
        DiskUsageEntry node = root;
        for (int i = 1; i < arr.length - 1; i++) {
            String s = arr[i];
            boolean found = false;
            for (int j = 0; j < node.getChildren().size(); j++) {
                DiskUsageEntry entry = node.getChildren().get(j);
                if (entry.getName().equals(s)) {
                    node = entry;
                    found = true;
                    break;
                }
            }
            if (!found) {
                DiskUsageEntry entry = new DiskUsageEntry(s, node.getPath() + "/" + s, -1, 0, true);
                node.getChildren().add(entry);
                node = entry;
            }
        }
        String name = arr[arr.length - 1];
        DiskUsageEntry entry = null;
        for (DiskUsageEntry ent : node.getChildren()) {
            if (ent.getName().equals(name)) {
                entry = ent;
                break;
            }
        }
        if (entry == null) {
            entry = new DiskUsageEntry(arr[arr.length - 1], node.getPath() + "/" + name,
                    size, 0, type.equals("d"));
            node.getChildren().add(entry);
        } else {
            entry.setSize(size);
        }
    }

//    public static final long parse(List<String> inputList, List<DiskUsageEntry> outputList) {
//        long total = -1;
//        ListIterator<String> reverseIterator = inputList.listIterator(inputList.size());
//        boolean first = true;
//        while (reverseIterator.hasPrevious()) {
//            String item = reverseIterator.previous();
//            // System.out.println("Parsing item: " + item);
//            Matcher matcher = duPattern.matcher(item);
//            if (matcher.find()) {
//                if (first) {
//                    total = Long.parseLong(matcher.group(1)) * 512;
//                    first = false;
//                }
//
//                long size = Long.parseLong(matcher.group(1)) * 512;
//                String path = matcher.group(2);
//                String name = PathUtils.getFileName(path);
//                double usage = ((double) size * 100) / total;
//                DiskUsageEntry ent = new DiskUsageEntry(name, path, size, usage < 0 ? 0 : usage);
//                outputList.add(ent);
//            }
//        }
//        return total;
//    }
}
