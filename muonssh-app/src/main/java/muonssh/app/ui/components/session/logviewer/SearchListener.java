/**
 * 
 */
package muonssh.app.ui.components.session.logviewer;

/**
 * @author subhro
 *
 */
public interface SearchListener {
	void search(String text);

	void select(long index);
}