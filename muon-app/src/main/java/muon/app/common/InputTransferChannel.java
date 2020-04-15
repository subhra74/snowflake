package muon.app.common;

import java.io.Closeable;
import java.io.InputStream;

public interface InputTransferChannel extends AutoCloseable {
	InputStream getInputStream(String path) throws Exception;

	InputStream getInputStream(String path, long offset) throws Exception;

	String getSeparator();

	long getSize(String path) throws Exception;

	void close();
}
