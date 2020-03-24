/**
 * 
 */
package snowflake.common.ssh;

import java.util.concurrent.atomic.AtomicBoolean;

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

	@Override
	public char[] reqPassword(Resource<?> resource) {
		JPasswordField txtPass = new JPasswordField();

		int ret = JOptionPane.showOptionDialog(null,
				new Object[] { resource.toString(), txtPass }, "Passphrase",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
		if (ret == JOptionPane.OK_OPTION) {
			return txtPass.getPassword();
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
