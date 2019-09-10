package snowflake.components.files.logviewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogSearch {
    public static List<Integer> search(List<LineEntry> lines, String localFile,
                                       AtomicBoolean stopFlag,
                                       String searchText, boolean regex,
                                       boolean caseSensitive, boolean fullWord) {
        List<Integer> lineIndexes = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(localFile, "r")) {
            Pattern pattern = null;
            if (regex) {
                if (!caseSensitive) {
                    pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
                } else {
                    pattern = Pattern.compile(searchText);
                }
            } else {
                if (caseSensitive) {
                    pattern = Pattern.compile((fullWord ? "\\b" : "") +
                            Pattern.quote(searchText) + (fullWord ? "\\b" : ""));
                } else {
                    pattern = Pattern.compile((fullWord ? "\\b" : "") +
                                    Pattern.quote(searchText) + (fullWord ? "\\b" : ""),
                            Pattern.CASE_INSENSITIVE);
                }
            }

            int c = 0;
            for (LineEntry ent : lines) {
                if (stopFlag.get()) break;
                byte b[] = new byte[ent.length];
                raf.seek(ent.offset);
                raf.read(b);
                String str = new String(b, "utf-8");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    lineIndexes.add(c);
                }
                c++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineIndexes;
    }
}
