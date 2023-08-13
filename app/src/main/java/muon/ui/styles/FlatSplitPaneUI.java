package muon.ui.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FlatSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new FlatSplitPaneUI();
    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        var divider = super.createDefaultDivider();
        divider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                splitPane.setBackground(AppTheme.INSTANCE.getSelectionColor());
                splitPane.setDividerSize(5);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                splitPane.setBackground(AppTheme.INSTANCE.getSplitPaneBackground());
                splitPane.setDividerSize(1);
            }
        });
        return divider;
    }
}
