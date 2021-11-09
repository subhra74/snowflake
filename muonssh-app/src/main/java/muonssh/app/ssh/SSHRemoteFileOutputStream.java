/**
 * 
 */
package muonssh.app.ssh;

import java.io.IOException;
import java.io.OutputStream;

import net.schmizz.sshj.sftp.RemoteFile;

/**
 * @author subhro
 *
 */
public class SSHRemoteFileOutputStream extends OutputStream {
	private int bufferCapacity;

	/**
	 * @param remoteFile
	 */
	public SSHRemoteFileOutputStream(RemoteFile remoteFile, int remoteMaxPacketSize) {
		this.remoteFile = remoteFile;
		this.bufferCapacity = remoteMaxPacketSize - this.remoteFile.getOutgoingPacketOverhead();
		this.remoteFileOutputStream = this.remoteFile.new RemoteFileOutputStream(0, 16);
	}

	private final RemoteFile remoteFile;
	private final OutputStream remoteFileOutputStream;

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.remoteFileOutputStream.write(b, off, len);
	}

	@Override
	public void write(int b) throws IOException {
		this.remoteFileOutputStream.write(b);
	}

	@Override
	public void close() throws IOException {
		System.out.println(this.getClass().getName() + " closing");
		try {
			this.remoteFile.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		try {
			this.remoteFileOutputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void flush() throws IOException {
		System.out.println(this.getClass().getName() + " flushing");
		this.remoteFileOutputStream.flush();
	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}

}
