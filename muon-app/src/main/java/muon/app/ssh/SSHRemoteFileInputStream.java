/**
 * 
 */
package muon.app.ssh;

import java.io.IOException;
import java.io.InputStream;

import net.schmizz.sshj.sftp.RemoteFile;

/**
 * @author subhro
 *
 */
public class SSHRemoteFileInputStream extends InputStream {

	private RemoteFile remoteFile;
	private InputStream in;
	private int bufferCapacity;

	/**
	 * @param remoteFile
	 */
	public SSHRemoteFileInputStream(RemoteFile remoteFile, int localMaxPacketSize) {
		this.remoteFile = remoteFile;
		this.bufferCapacity = localMaxPacketSize;
		this.in = this.remoteFile.new ReadAheadRemoteFileInputStream(16);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return this.in.read(b, off, len);
	}

	@Override
	public int read() throws IOException {
		return this.in.read();
	}

	@Override
	public void close() throws IOException {
		try {
			this.remoteFile.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			this.in.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

}
