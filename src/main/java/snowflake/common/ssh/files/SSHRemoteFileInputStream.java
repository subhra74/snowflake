/**
 * 
 */
package snowflake.common.ssh.files;

import java.io.IOException;
import java.io.InputStream;

import net.schmizz.sshj.sftp.RemoteFile;

/**
 * @author subhro
 *
 */
public class SSHRemoteFileInputStream extends InputStream {

	private RemoteFile remoteFile;
	private long offset;

	/**
	 * @param remoteFile
	 */
	public SSHRemoteFileInputStream(RemoteFile remoteFile) {
		this.remoteFile = remoteFile;
		this.offset = 0L;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int x = this.remoteFile.read(this.offset, b, off, len);
		if (x != -1) {
			this.offset += x;
		}
		return x;
	}

	@Override
	public int read() throws IOException {
		byte b[] = new byte[1];
		int x = this.read(b, 0, 1);
		if (x == -1) {
			return x;
		}
		return b[0] & 0xff;
	}

	@Override
	public void close() throws IOException {
		this.remoteFile.close();
	}

	@Override
	public long skip(long n) throws IOException {
		this.offset = n;
		return this.offset;
	}

}
