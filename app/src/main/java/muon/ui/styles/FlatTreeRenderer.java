package muon.ui.styles;

import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class FlatTreeRenderer implements TreeCellRenderer {
    private JPanel panel;
    private JLabel expandArrow, collapsedArrow, nodeIcon, leafIcon, text;
    private Component blank;

    public FlatTreeRenderer() {
        panel = new JPanel(null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        expandArrow = createLabel(IconCode.RI_ARROW_DOWN_S_LINE);
        collapsedArrow = createLabel(IconCode.RI_ARROW_RIGHT_S_LINE);
        nodeIcon = createLabel(IconCode.RI_FOLDER_LINE);
        leafIcon = createLabel(IconCode.RI_INSTANCE_LINE);

        expandArrow.setBorder(new EmptyBorder(0,5,0,0));
        collapsedArrow.setBorder(new EmptyBorder(0,5,0,0));

        int mw = 0, mh = 0;
        for (var label :
                new JLabel[]{expandArrow, collapsedArrow, nodeIcon, leafIcon}) {
            var dim = label.getPreferredSize();
            if (dim.width > mw) {
                mw = dim.width;
            }
            if (dim.height > mh) {
                mh = dim.height;
            }
        }

        var dim = new Dimension(mw, mh);
        for (var label :
                new JLabel[]{expandArrow, collapsedArrow, nodeIcon, leafIcon}) {
            label.setPreferredSize(dim);
            label.setMinimumSize(dim);
            label.setMaximumSize(dim);
        }

        blank = Box.createRigidArea(dim);
        text = new JLabel();

        panel.add(blank);
        panel.add(expandArrow);
        panel.add(collapsedArrow);
        panel.add(Box.createRigidArea(new Dimension(5,5)));
        panel.add(nodeIcon);
        panel.add(leafIcon);
        panel.add(text);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

//        var dim1=panel.getPreferredSize();
//        dim1.width=Short.MAX_VALUE;
//        panel.setPreferredSize(dim1);
    }

    public int getPreferredHeight() {
        return panel.getPreferredSize().height;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        text.setText(value.toString());
        if (leaf) {
            expandArrow.setVisible(false);
            collapsedArrow.setVisible(false);
            nodeIcon.setVisible(false);
            leafIcon.setVisible(true);
            blank.setVisible(true);
        } else {
            expandArrow.setVisible(expanded);
            collapsedArrow.setVisible(!expanded);
            nodeIcon.setVisible(true);
            leafIcon.setVisible(false);
            blank.setVisible(false);
        }
        expandArrow.setForeground(selected ? AppTheme.INSTANCE.getSelectionForeground() : AppTheme.INSTANCE.getForeground());
        collapsedArrow.setForeground(selected ? AppTheme.INSTANCE.getSelectionForeground() : AppTheme.INSTANCE.getForeground());
        nodeIcon.setForeground(selected ? AppTheme.INSTANCE.getSelectionForeground() : AppTheme.INSTANCE.getForeground());
        leafIcon.setForeground(selected ? AppTheme.INSTANCE.getSelectionForeground() : AppTheme.INSTANCE.getForeground());
        text.setForeground(selected ? AppTheme.INSTANCE.getSelectionForeground() : AppTheme.INSTANCE.getForeground());
        panel.setBackground(selected ? AppTheme.INSTANCE.getListSelectionColor() : tree.getBackground());
        return panel;
    }

    private JLabel createLabel(IconCode code) {
        var lbl = new JLabel();
        lbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        lbl.setText(code.getValue());
        return lbl;
    }
}
