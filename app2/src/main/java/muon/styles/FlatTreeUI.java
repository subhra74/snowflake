package muon.styles;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.*;

public class FlatTreeUI extends BasicTreeUI {
    public static ComponentUI createUI(JComponent x) {
        return new FlatTreeUI();
    }

    private JTree tree;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        if (c instanceof JTree) {
            this.tree = (JTree) c;
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        super.paint(g2, c);
    }

    @Override
    protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
        g.setColor(tree.isRowSelected(row) ? AppTheme.INSTANCE.getListSelectionColor() : tree.getBackground());
        g.fillRect(0, bounds.y, clipBounds.width , bounds.height);
        bounds.x = bounds.x;
        bounds.width = bounds.width;
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }

    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        //super.paintHorizontalLine(g, c, y, left, right);
    }

    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        //super.paintVerticalLine(g, c, x, top, bottom);
    }

//    @Override
//    public Icon getExpandedIcon() {
//        return new FontIcon(FontIconCodes.RI_ARROW_DOWN_S_LINE,
//                24, 24, 18.0f, AppTheme.INSTANCE.getForeground());
//    }
//
//    @Override
//    public Icon getCollapsedIcon() {
//        return new FontIcon(FontIconCodes.RI_ARROW_RIGHT_S_LINE,
//            24, 24, 18.0f, AppTheme.INSTANCE.getForeground());
//    }

    //    @Override
//    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
//        //super.paintVerticalPartOfLeg(g, clipBounds, insets, path);
//    }
//
//    @Override
//    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {
//        //super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
//    }
}
