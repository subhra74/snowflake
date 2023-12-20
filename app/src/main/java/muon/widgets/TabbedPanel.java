package muon.widgets;

import muon.util.IconCode;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TabbedPanel extends JPanel {
    private boolean isStretchable, isCloseButtonHidden, centerAlign;
    private Color selectionColor, backgroundColor, iconColor,
            closeButtonColor, selectionBackground, titleColor,
            selectedTitleColor, inactiveColor;
    private IconCode closeIcon;
    private Box tabHolder;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private int selectedIndex;
    private final List<TabListener> tabListeners;

    public TabbedPanel(boolean isStretchable,
                       boolean isCloseButtonHidden,
                       Color selectionColor,
                       Color backgroundColor,
                       Color iconColor,
                       Color closeButtonColor,
                       Color selectionBackground,
                       Color inactiveBackground,
                       Color titleColor,
                       Color selectedTitleColor,
                       IconCode closeIcon,
                       Color tabBorderColor,
                       Component rightComponent,
                       boolean showTabsAtBottom,
                       boolean showBorder,
                       boolean isCentered) {
        super(new BorderLayout(), true);
        this.isStretchable = isStretchable;
        this.isCloseButtonHidden = isCloseButtonHidden;
        this.selectionColor = selectionColor;
        this.inactiveColor = inactiveBackground;
        this.backgroundColor = backgroundColor;
        this.iconColor = iconColor;
        this.closeButtonColor = closeButtonColor;
        this.selectionBackground = selectionBackground;
        this.titleColor = titleColor;
        this.selectedTitleColor = selectedTitleColor;
        this.closeIcon = closeIcon;
        this.centerAlign = isCentered;

        this.tabListeners = Collections.synchronizedList(new ArrayList<>());

        this.tabHolder = Box.createHorizontalBox();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(true);
        cardPanel.setBackground(this.backgroundColor);

        JPanel tabTop = new JPanel(new BorderLayout());
        if (showBorder) {
            tabTop.setBorder(new MatteBorder(1, 0, 1, 0, tabBorderColor));
        }
        tabTop.setOpaque(true);
        tabTop.setBackground(this.backgroundColor);
        tabTop.add(tabHolder);
        if (Objects.nonNull(rightComponent)) {
            tabTop.add(rightComponent, BorderLayout.EAST);
        }

        add(tabTop, showTabsAtBottom ? BorderLayout.SOUTH : BorderLayout.NORTH);
        add(cardPanel);
    }

    public void addTab(String tabTitle, IconCode tabIcon, Component body) {
        var tab = new TabItem(tabIcon, this.closeIcon,
                selectionColor, true,
                inactiveColor, isCloseButtonHidden,
                iconColor, closeButtonColor,
                selectionBackground, titleColor,
                selectedTitleColor, isStretchable, e -> {
            int index = getTabIndex((TabItem) e.getSource());
            setSelectedIndex(index);
            notifyTabSelection(index);
        }, e -> {
            int index = getTabIndex((TabItem) e.getSource());
            closeTab(index);
        }, centerAlign);
        tab.setTabTitle(tabTitle);
        tab.putClientProperty("tab.content", body);
        tabHolder.add(tab);
        cardPanel.add(body, tab.hashCode() + "");

        int index = getTabIndex(tab);
        setSelectedIndex(index);
        notifyTabSelection(index);
    }

    private void notifyTabSelection(int index) {
        synchronized (tabListeners) {
            for (var tabListener :
                    tabListeners) {
                var tab = (TabItem) tabHolder.getComponent(index);
                var content = (Component) tab.getClientProperty("tab.content");
                tabListener.selectionChanged(new TabEvent(this, index, content));
            }
        }
    }

    public void setSelectedIndex(int index) {
        if (index < 0) {
            return;
        }
        this.selectedIndex = index;
        for (int i = 0; i < tabHolder.getComponentCount(); i++) {
            var t = (TabItem) tabHolder.getComponent(i);
            if (i == index) {
                t.setSelected(true);
                cardLayout.show(cardPanel, t.hashCode() + "");
            } else {
                t.setSelected(false);
            }
        }
    }

    private int getTabIndex(TabItem item) {
        for (int i = 0; i < tabHolder.getComponentCount(); i++) {
            var t = (TabItem) tabHolder.getComponent(i);
            if (item == t) {
                return i;
            }
        }
        return -1;
    }

    public TabItem getTabForComponent(Component component) {
        for (int i = 0; i < tabHolder.getComponentCount(); i++) {
            var t = (TabItem) tabHolder.getComponent(i);
            if (component == t.getClientProperty("tab.content")) {
                return t;
            }
        }
        return null;
    }

    private boolean notifyTabClosing(int index, Component content) {
        synchronized (tabListeners) {
            for (var tabListener :
                    tabListeners) {
                if (!tabListener.tabClosing(new TabEvent(this, index, content))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void notifyTabClosed(int index, Component content) {
        synchronized (tabListeners) {
            for (var tabListener :
                    tabListeners) {
                tabListener.tabClosed(new TabEvent(this, index, content));
            }
        }
    }

    public void closeTab(int index) {
        if (tabHolder.getComponentCount() == 0) {
            return;
        }
        var tab = (TabItem) tabHolder.getComponent(index);
        var content = (Component) tab.getClientProperty("tab.content");
        if (notifyTabClosing(index, content)) {
            tabHolder.remove(tab);
            cardPanel.remove(content);
            notifyTabClosed(index, content);
            if (index == tabHolder.getComponentCount()) {
                index--;
            }
            setSelectedIndex(index);
            revalidate();
            repaint();
        }
    }

    public int getTabCount() {
        return tabHolder.getComponentCount();
    }

    public void addTabListener(TabListener tabListener) {
        this.tabListeners.add(tabListener);
    }
}
