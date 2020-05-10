/**
 * 
 */
package util;

import java.awt.Component;
import java.awt.Dimension;

/**
 * @author subhro
 *
 */
public final class LayoutUtilities {
	public static void equalizeSize(Component... components) {
		int maxWidth = 0, maxHeight = 0;
		for (Component item : components) {
			Dimension dim = item.getPreferredSize();
			if (maxWidth <= dim.width) {
				maxWidth = dim.width;
			}
			if (maxHeight <= dim.height) {
				maxHeight = dim.height;
			}
		}

		Dimension dimMax = new Dimension(maxWidth, maxHeight);
		for (Component item : components) {
			item.setPreferredSize(dimMax);
			item.setMinimumSize(dimMax);
			item.setMaximumSize(dimMax);
		}
	}
}
