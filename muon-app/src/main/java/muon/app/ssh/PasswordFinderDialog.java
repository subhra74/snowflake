/**
 * 
 */
package muon.app.ssh;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.Resource;

/**
 * @author subhro
 *
 */
public class PasswordFinderDialog implements PasswordFinder {

	private boolean retry = true;
	private CachedCredentialProvider cachedCredentialProvider;
	private AtomicBoolean firstAttempt = new AtomicBoolean(true);

	public PasswordFinderDialog(CachedCredentialProvider cachedCredentialProvider) {
		this.cachedCredentialProvider = cachedCredentialProvider;
	}

	@Override
	public char[] reqPassword(Resource<?> resource) {
		// if pass phrase was already cached
		if (firstAttempt.get() && this.cachedCredentialProvider.getCachedPassPhrase() != null) {
			firstAttempt.set(false);
			return this.cachedCredentialProvider.getCachedPassPhrase();
		}
		firstAttempt.set(false);
		JPasswordField txtPass = new JPasswordField();
		JCheckBox chkUseCache = new JCheckBox("Remember for this session");

		int ret = JOptionPane.showOptionDialog(null, new Object[] { resource.toString(), txtPass, chkUseCache },
				"Passphrase", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (ret == JOptionPane.OK_OPTION) {
			char[] passPhrase = txtPass.getPassword();
			if (chkUseCache.isSelected()) {
				this.cachedCredentialProvider.setCachedPassPhrase(passPhrase);
			}
			return passPhrase;
		}
		retry = false;
		return null;
//		if (password != null && firstAttempt.get()) {
//			return password.toCharArray();
//		}
//		txtPass.setText("");
//		
//if(JOptionPane.showOptionDialog(null, , resource, JOptionPane., messageType, icon, options, initialValue))
//		return null;
	}

	@Override
	public boolean shouldRetry(Resource<?> resource) {
		// TODO Auto-generated method stub
		return retry;
	}

}
