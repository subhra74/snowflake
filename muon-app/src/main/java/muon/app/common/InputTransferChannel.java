package muon.app.common;

import java.io.InputStream;

public interface InputTransferChannel {
	InputStream getInputStream(String path) throws Exception;

	String getSeparator();

	long getSize(String path) throws Exception;
}
