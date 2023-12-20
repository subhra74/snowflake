package muon.styles;

import javax.swing.border.Border;
import java.awt.*;

public class RounderBorder implements Border {
    private Insets insets;
    private int arc;
    private int thickness;
    private Color color;

    public RounderBorder(Insets insets) {
        this.insets = insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
