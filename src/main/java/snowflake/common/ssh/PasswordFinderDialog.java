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
	private String password;
	private AtomicBoolean firstAttempt = new AtomicBoolean(true);
	private JPasswordField txtPass = new JPasswordField(30);

	@Override
	public char[] reqPassword(Resource<?> resource) {
		return "Starscream@64".toCharArray();
//		if (password != null && firstAttempt.get()) {
//			return password.toCharArray();
//		}
//		txtPass.setText("");
//		
//		if(JOptionPane.showOptionDialog(null, new Object[] {resource.toString(),txtPass}, resource, JOptionPane., messageType, icon, options, initialValue))
//		return null;
	}

	@Override
	public boolean shouldRetry(Resource<?> resource) {
		// TODO Auto-generated method stub
		return false;
	}

}
