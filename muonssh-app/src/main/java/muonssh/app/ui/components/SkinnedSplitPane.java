/**
 * 
 */
package muonssh.app.ui.components;

import javax.swing.JSplitPane;

import muonssh.app.App;

/**
 * @author subhro
 *
 */
public class SkinnedSplitPane extends JSplitPane {
	/**
	 * 
	 */
	public SkinnedSplitPane() {
		applySkin();
	}

	public SkinnedSplitPane(int orientation) {
		super(orientation);
		applySkin();
	}

	public void applySkin() {
		this.putClientProperty("Nimbus.Overrides", App.SKIN.getSplitPaneSkin());
	}

}
