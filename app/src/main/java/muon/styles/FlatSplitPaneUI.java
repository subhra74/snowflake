package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FlatSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new FlatSplitPaneUI();
    }

//    @Override
//    public void finishedPaintingChildren(JSplitPane sp, Graphics g) {
//        super.finishedPaintingChildren(sp, g);
//        System.out.println("finishedPaintingChildren");
//    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        var divider = super.createDefaultDivider();
        divider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
//                var comp=(JLabel)splitPane.getClientProperty("floating.divider");
//                if(comp!=null){
//                    comp.setVisible(true);
//                }

//                splitPane.setBackground(AppTheme.INSTANCE.getSelectionColor());
//                splitPane.setDividerSize(10);
//                splitPane.setDividerLocation(splitPane.getDividerLocation() - 5);
            }

            @Override
            public void mouseExited(MouseEvent e) {
//                splitPane.setBackground(AppTheme.INSTANCE.getSplitPaneBackground());
//                splitPane.setDividerSize(1);
//                splitPane.setDividerLocation(splitPane.getDividerLocation() + 5);
            }
        });
        return divider;
    }
}
