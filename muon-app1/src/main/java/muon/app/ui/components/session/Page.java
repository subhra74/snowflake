package muon.app.ui.components.session;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * 
 * Each component panel behaves like a page, which is implemented as CardLayout
 * Whenever use selects the page or the page is selected programmatically,
 * onLoad will be called to inform the page component to perform necessary
 * initialization or rendering
 * 
 * @author subhro
 *
 */
public abstract class Page extends JPanel {

	/**
	 * Create a page with border layout and double buffering
	 */
	public Page() {
		super(new BorderLayout(), true);
	}

	/**
	 * Create a page with provided layout and double buffering
	 */
	public Page(LayoutManager layout) {
		super(layout, true);
	}

	/**
	 * This method is called when the page is selected in session content.
	 * Initialization or rendering should not be implemented in component
	 * constructor, it should be implemented in this method, also care should be
	 * taken to perform once time activities, as this will be called each time
	 * the page is selected
	 */
	public abstract void onLoad();

	/**
	 * Returns the Font awesome icon to be used in toolbar
	 * 
	 * @return font awesome icon string in UTF-8
	 */
	public abstract String getIcon();

	/**
	 * Returns the text to be used in toolbar
	 * 
	 * @return text string in UTF-8
	 */
	public abstract String getText();
}
