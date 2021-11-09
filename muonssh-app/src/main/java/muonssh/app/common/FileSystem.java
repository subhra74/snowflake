package muonssh.app.common;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface FileSystem extends AutoCloseable {

	FileInfo getInfo(String path) throws Exception;

	List<FileInfo> list(String path) throws Exception;

	String getHome() throws Exception;

	boolean isLocal();

//	public InputStream getInputStream(String file, long offset)
//			throws FileNotFoundException, Exception;
//
//	public OutputStream getOutputStream(String file)
//			throws FileNotFoundException, Exception;

	void rename(String oldName, String newName)
			throws Exception;

	void delete(FileInfo f) throws Exception;

	void deleteFile(String f) throws Exception;

	void mkdir(String path) throws Exception;

	void close() throws Exception;

	boolean isConnected();

	void chmod(int perm, String path) throws Exception;

	boolean mkdirs(String absPath) throws Exception;

	long getAllFiles(String dir, String baseDir,
                     Map<String, String> fileMap, Map<String, String> folderMap)
			throws Exception;

	String getProtocol();

	void createFile(String path) throws Exception;

	String[] getRoots() throws Exception;

	void createLink(String src, String dst, boolean hardLink)
			throws Exception;

	String getName();

	InputTransferChannel inputTransferChannel() throws Exception;

	OutputTransferChannel outputTransferChannel() throws Exception;

	String getSeparator();
}
