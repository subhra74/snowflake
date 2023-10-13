package muon.widgets;

import muon.util.AppUtils;
import muon.util.IconCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AddTabButton extends JPanel {
    private JButton button;

    public AddTabButton() {
        super(new BorderLayout());
        setOpaque(false);
        var layout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(layout);
        button = AppUtils.createIconButton(IconCode.RI_ADD_LINE);
        button.setBorder(new EmptyBorder(1, 3, 1, 3));
        add(Box.createRigidArea(new Dimension(0, 24)));
        setBorder(new EmptyBorder(2, 2, 2, 4));
        add(button);
    }

    public void addActionListener(ActionListener actionListener) {
        button.addActionListener(actionListener);
    }
}
