package muon.styles;

import muon.dto.session.SessionInfo;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class FlatTreeRenderer extends JLabel implements TreeCellRenderer {
    private Icon fileIcon, folderIcon;

    public FlatTreeRenderer() {
        setOpaque(true);
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setIconTextGap(10);
        fileIcon = AppUtils.createSVGIcon("file-2-fill.svg", 20, AppTheme.INSTANCE.getListIconColor());
        folderIcon = AppUtils.createSVGIcon("folder-fill.svg", 20, AppTheme.INSTANCE.getListIconColor());
        setIcon(fileIcon);
        setText("Sample text");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        setText(value.toString());
        setIcon(((DefaultMutableTreeNode) value).getUserObject() instanceof SessionInfo ? fileIcon : folderIcon);
        setBackground(selected ? UIManager.getColor("Tree.selectionBackground") : tree.getBackground());
        setForeground(selected ? UIManager.getColor("Tree.selectionForeground") : tree.getForeground());
        return this;
    }
}
