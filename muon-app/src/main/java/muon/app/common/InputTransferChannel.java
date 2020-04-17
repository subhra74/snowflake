package muon.app.common;

import java.io.InputStream;

public interface InputTransferChannel {
	InputStream getInputStream(String path) throws Exception;

	InputStream getInputStream(String path, long offset) throws Exception;

	String getSeparator();

	long getSize(String path) throws Exception;
}
