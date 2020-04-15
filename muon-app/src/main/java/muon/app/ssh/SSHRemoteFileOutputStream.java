/**
 * 
 */
package muon.app.ssh;

import java.io.IOException;
import java.io.OutputStream;

import net.schmizz.sshj.sftp.RemoteFile;

/**
 * @author subhro
 *
 */
public class SSHRemoteFileOutputStream extends OutputStream {

	/**
	 * @param remoteFile
	 */
	public SSHRemoteFileOutputStream(RemoteFile remoteFile) {
		this.remoteFile = remoteFile;
	}

	private RemoteFile remoteFile;
	private long offset;

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		remoteFile.write(this.offset, b, off, len);
		this.offset += len;
	}

	@Override
	public void write(int b) throws IOException {
		byte[] buf = new byte[1];
		this.remoteFile.write(this.offset, buf, 0, 1);
		this.offset += 1;
	}

	@Override
	public void close() throws IOException {
		this.remoteFile.close();
	}

}
