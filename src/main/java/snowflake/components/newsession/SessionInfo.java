package snowflake.components.newsession;

import java.io.Serializable;
import java.util.*;

public class SessionInfo extends NamedItem implements Serializable {
	private String host, user, password, localFolder, remoteFolder;
	private int port = 22;
	private List<String> favouriteRemoteFolders = new ArrayList<>();
	private List<String> favouriteLocalFolders = new ArrayList<>();
	private String privateKeyFile;
	private int proxyPort = 8080;
	private String proxyHost, proxyUser, proxyPassword;
	private int proxyType = 0;

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the localFolder
	 */
	public String getLocalFolder() {
		return localFolder;
	}

	/**
	 * @param localFolder the localFolder to set
	 */
	public void setLocalFolder(String localFolder) {
		this.localFolder = localFolder;
	}

	/**
	 * @return the remoteFolder
	 */
	public String getRemoteFolder() {
		return remoteFolder;
	}

	/**
	 * @param remoteFolder the remoteFolder to set
	 */
	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the favouriteFolders
	 */
	public List<String> getFavouriteRemoteFolders() {
		return favouriteRemoteFolders;
	}

	/**
	 * @param favouriteFolders the favouriteFolders to set
	 */
	public void setFavouriteRemoteFolders(List<String> favouriteFolders) {
		this.favouriteRemoteFolders = favouriteFolders;
	}

	/**
	 * @return the privateKeyFile
	 */
	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	/**
	 * @param privateKeyFile the privateKeyFile to set
	 */
	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public SessionInfo copy() {
		SessionInfo info = new SessionInfo();
		info.setId(UUID.randomUUID().toString());
		info.setHost(this.host);
		info.setPort(this.port);
		info.getFavouriteRemoteFolders().addAll(favouriteRemoteFolders);
		info.getFavouriteLocalFolders().addAll(favouriteLocalFolders);
		info.setLocalFolder(this.localFolder);
		info.setRemoteFolder(this.remoteFolder);
		info.setPassword(this.password);
		info.setPrivateKeyFile(privateKeyFile);
		info.setUser(user);
		info.setName(name);
		return info;
	}

	/**
	 * @return the favouriteLocalFolders
	 */
	public List<String> getFavouriteLocalFolders() {
		return favouriteLocalFolders;
	}

	/**
	 * @param favouriteLocalFolders the favouriteLocalFolders to set
	 */
	public void setFavouriteLocalFolders(List<String> favouriteLocalFolders) {
		this.favouriteLocalFolders = favouriteLocalFolders;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyHost the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return the proxyUser
	 */
	public String getProxyUser() {
		return proxyUser;
	}

	/**
	 * @param proxyUser the proxyUser to set
	 */
	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	/**
	 * @return the proxyPassword
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * @param proxyPassword the proxyPassword to set
	 */
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	/**
	 * @return the proxyType
	 */
	public int getProxyType() {
		return proxyType;
	}

	/**
	 * @param proxyType the proxyType to set
	 */
	public void setProxyType(int proxyType) {
		this.proxyType = proxyType;
	}
}
