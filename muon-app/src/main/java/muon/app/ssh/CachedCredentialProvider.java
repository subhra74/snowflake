package muon.app.ssh;

public interface CachedCredentialProvider {
	public char[] getCachedPassword();

	public void cachePassword(char[] password);

	public char[] getCachedPassPhrase();

	public void setCachedPassPhrase(char[] cachedPassPhrase);

	public String getCachedUser();

	public void setCachedUser(String user);
}
