package muon.app.ui.components.session;

public class HopEntry {
	public HopEntry(String id, String host, int port, String user, String password, String keypath) {
		super();
		this.id = id;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.keypath = keypath;
	}

	public HopEntry() {
		// TODO Auto-generated constructor stub
	}

	private String id, host, user, password, keypath;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKeypath() {
		return keypath;
	}

	public void setKeypath(String keypath) {
		this.keypath = keypath;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return host != null ? (user != null ? user + "@" + host : host) : "";
	}
}
