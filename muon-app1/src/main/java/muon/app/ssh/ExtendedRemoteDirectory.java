/**
 * 
 */
package muon.app.ssh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.PacketType;
import net.schmizz.sshj.sftp.PathComponents;
import net.schmizz.sshj.sftp.RemoteDirectory;
import net.schmizz.sshj.sftp.RemoteResourceFilter;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.Response;
import net.schmizz.sshj.sftp.SFTPEngine;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.sftp.Response.StatusCode;

/**
 * @author subhro
 *
 */
public class ExtendedRemoteDirectory extends RemoteDirectory {

	/**
	 * @param requester
	 * @param path
	 * @param handle
	 */
	public ExtendedRemoteDirectory(SFTPEngine requester, String path,
			byte[] handle) {
		super(requester, path, handle);
	}

	public List<RemoteResourceInfoWrapper> scanExtended(
			RemoteResourceFilter filter) throws IOException {
		List<RemoteResourceInfoWrapper> rri = new ArrayList<>();
		// TODO: Remove GOTO!
		loop: for (;;) {
			final Response res = requester
					.request(newRequest(PacketType.READDIR))
					.retrieve(requester.getTimeoutMs(), TimeUnit.MILLISECONDS);
			switch (res.getType()) {

			case NAME:
				final int count = res.readUInt32AsInt();
				for (int i = 0; i < count; i++) {
					final String name = res.readString(
							requester.getSubsystem().getRemoteCharset());
					final String longName = res.readString();

					final FileAttributes attrs = res.readFileAttributes();
					final PathComponents comps = requester.getPathHelper()
							.getComponents(path, name);
					final RemoteResourceInfo inf = new RemoteResourceInfo(comps,
							attrs);
					final RemoteResourceInfoWrapper wri = new RemoteResourceInfoWrapper(
							inf, longName);
					if (!(".".equals(name) || "..".equals(name))
							&& (filter == null || filter.accept(inf))) {
						rri.add(wri);
					}
				}
				break;

			case STATUS:
				res.ensureStatusIs(StatusCode.EOF);
				break loop;

			default:
				throw new SFTPException("Unexpected packet: " + res.getType());
			}
		}
		return rri;
	}

}
