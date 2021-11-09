package muonssh.app.ssh;

public interface CachedCredentialProvider {
	char[] getCachedPassword();

	void cachePassword(char[] password);

	char[] getCachedPassPhrase();

	void setCachedPassPhrase(char[] cachedPassPhrase);

	String getCachedUser();

	void setCachedUser(String user);
}
