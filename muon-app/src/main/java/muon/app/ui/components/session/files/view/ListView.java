package muon.app.ui.components.session.files.view;

import javax.swing.*;

import muon.app.common.FileInfo;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListView extends JList<FileInfo> {

	public ListView(DefaultListModel<FileInfo> model) {
		super(model);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (locationToIndex(e.getPoint()) == -1 && !e.isShiftDown()
						&& !isMenuShortcutKeyDown(e)) {
					clearSelection();
				}
			}

			private boolean isMenuShortcutKeyDown(InputEvent event) {
				return (event.getModifiersEx() & Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMaskEx()) != 0;
			}
		});
	}

	@Override
	public final int locationToIndex(Point location) {
		int index = super.locationToIndex(location);
		if (index != -1 && !getCellBounds(index, index).contains(location)) {
			return -1;
		} else {
			return index;
		}
	}
}