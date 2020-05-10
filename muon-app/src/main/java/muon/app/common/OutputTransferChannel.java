package muon.app.common;

import java.io.OutputStream;

public interface OutputTransferChannel {
	OutputStream getOutputStream(String path) throws Exception;

	String getSeparator();
}
