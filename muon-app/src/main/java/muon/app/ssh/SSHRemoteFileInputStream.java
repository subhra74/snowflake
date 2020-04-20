/**
 * 
 */
package muon.app.ssh;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.RemoteFile.ReadAheadRemoteFileInputStream;

/**
 * @author subhro
 *
 */
public class SSHRemoteFileInputStream extends InputStream {

	private RemoteFile remoteFile;
	private InputStream in;

	/**
	 * @param remoteFile
	 */
	public SSHRemoteFileInputStream(RemoteFile remoteFile, int localMaxPacketSize) {
		this.remoteFile = remoteFile;
		this.in = new BufferedInputStream(this.remoteFile.new ReadAheadRemoteFileInputStream(0), localMaxPacketSize);
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

}
