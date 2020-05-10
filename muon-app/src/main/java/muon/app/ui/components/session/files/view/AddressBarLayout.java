package muon.app.ui.components.session.files.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddressBarLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public Dimension preferredLayoutSize(Container c) {
        int w = 0;
        int h = 0;
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component comp = c.getComponent(i);
            Dimension pref = comp.getPreferredSize();
            w += pref.width;
            h = Math.max(h, pref.height);
        }
        Insets border = c.getInsets();
        return new Dimension(w + border.left + border.right,
                h + border.top + border.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(0, 0);
    }

    private int getTotalLength(Container c) {
        int total = 0;
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component comp = c.getComponent(i);
            Dimension pref = comp.getPreferredSize();
            total += pref.getWidth();
        }
        return total;
    }

    @Override
    public void layoutContainer(Container c) {
        Insets border = c.getInsets();

        int w = c.getWidth() - border.left - border.right;
        int h = c.getHeight() - border.top - border.bottom;
        int availableWidth = w;
        int count = 0;

        List<Component> componentList = new ArrayList<>();

        for (int i = c.getComponentCount() - 1; i >= 0; i--) {
            Component comp = c.getComponent(i);
            comp.setLocation(-100, -100);
        }

        for (int i = c.getComponentCount() - 1; i >= 0; i--) {
            Component comp = c.getComponent(i);
            int prefWidth = comp.getPreferredSize().width;
            if (prefWidth < availableWidth) {
                componentList.add(comp);
                availableWidth -= prefWidth;
            } else {
                break;
            }
        }

        Collections.reverse(componentList);

        int x = 0;

        for (int i = 0; i < componentList.size(); i++) {
            Component component = componentList.get(i);
            int prefWidth = component.getPreferredSize().width;
            component.setBounds(x, 0, prefWidth, h);
            x += prefWidth;
        }
    }
}
