package muon.app.common;

import java.io.Closeable;
import java.io.OutputStream;

public interface OutputTransferChannel extends AutoCloseable {
    OutputStream getOutputStream(String path) throws Exception;
    String getSeparator();
    void close();
}
