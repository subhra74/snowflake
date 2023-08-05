/**
 * 
 */
package muon.app.ssh;

import net.schmizz.sshj.sftp.RemoteResourceInfo;

/**
 * @author subhro
 *
 */
public class RemoteResourceInfoWrapper {
	/**
	 * @param info
	 * @param longPath
	 */
	public RemoteResourceInfoWrapper(RemoteResourceInfo info, String longPath) {
		super();
		this.info = info;
		this.longPath = longPath;
	}

	private RemoteResourceInfo info;
	private String longPath;

	/**
	 * @return the info
	 */
	public RemoteResourceInfo getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(RemoteResourceInfo info) {
		this.info = info;
	}

	/**
	 * @return the longPath
	 */
	public String getLongPath() {
		return longPath;
	}

	/**
	 * @param longPath the longPath to set
	 */
	public void setLongPath(String longPath) {
		this.longPath = longPath;
	}
}
