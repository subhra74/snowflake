/**
 * 
 */
package muon.app.ui.components.session.files.remote2remote;

/**
 * @author subhro
 *
 */
public class RemoteServerEntry {
	public RemoteServerEntry(String host, int port, String user, String path) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.path = path;
	}

	public RemoteServerEntry() {
		// TODO Auto-generated constructor stub
	}

	private String id, host, user, path;
	private int port;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
