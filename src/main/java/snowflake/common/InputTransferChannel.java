package snowflake.common;

import java.io.Closeable;
import java.io.InputStream;

public interface InputTransferChannel extends Closeable {
    InputStream getInputStream(String path) throws Exception;
    String getSeparator();
    long getSize(String path) throws Exception;
    void close();
}
