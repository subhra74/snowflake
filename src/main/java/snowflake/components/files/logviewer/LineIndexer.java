package snowflake.components.files.logviewer;

import org.apache.commons.io.input.CountingInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LineIndexer {
    static class IndexLines {
        public int maxLen = 0;
        public List<LineEntry> lines = new ArrayList<>();
    }

    public static IndexLines indexLines(String file, long startOffset, AtomicBoolean stopFlag) {
        IndexLines lines = new IndexLines();
        try (FileInputStream fin = new FileInputStream(file)) {
            try (CountingInputStream cin = new CountingInputStream(
                    new BufferedInputStream(fin))) {
                cin.skip(startOffset);
                System.out.println("Reading file");
                long offset = startOffset;
                while (!stopFlag.get()) {
                    int ch = cin.read();
                    if (ch == '\n' || ch == -1) {
                        LineEntry ent = new LineEntry();
                        ent.offset = offset;
                        offset = cin.getByteCount();
                        ent.length = (int) (offset - ent.offset);
                        if (ent.length > lines.maxLen) {
                            lines.maxLen = ent.length;
                        }
                        lines.lines.add(ent);
                    }
                    if (ch == -1) {
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
