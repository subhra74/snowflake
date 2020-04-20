/**
 * 
 */
package muon.app.ssh;

import java.io.BufferedOutputStream;
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
	public SSHRemoteFileOutputStream(RemoteFile remoteFile, int remoteMaxPacketSize) {
		this.remoteFile = remoteFile;
		this.remoteFileOutputStream = new BufferedOutputStream(this.remoteFile.new RemoteFileOutputStream(0, 0),
				remoteMaxPacketSize - this.remoteFile.getOutgoingPacketOverhead());
	}

	private RemoteFile remoteFile;
	private OutputStream remoteFileOutputStream;

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

}
