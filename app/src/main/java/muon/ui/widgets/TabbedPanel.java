package muon.ui.widgets;

import muon.util.IconCode;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TabbedPanel extends JPanel {
    private boolean isStretchable, isCloseButtonHidden;
    private Color selectionColor, backgroundColor, iconColor,
            closeButtonColor, selectionBackground, titleColor,
            selectedTitleColor;
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
                       Color titleColor,
                       Color selectedTitleColor,
                       IconCode closeIcon,
                       Color tabBorderColor) {
        super(new BorderLayout(), true);
        this.isStretchable = isStretchable;
        this.isCloseButtonHidden = isCloseButtonHidden;
        this.selectionColor = selectionColor;
        this.backgroundColor = backgroundColor;
        this.iconColor = iconColor;
        this.closeButtonColor = closeButtonColor;
        this.selectionBackground = selectionBackground;
        this.titleColor = titleColor;
        this.selectedTitleColor = selectedTitleColor;
        this.closeIcon = closeIcon;

        this.tabListeners = Collections.synchronizedList(new ArrayList<>());

        this.tabHolder = Box.createHorizontalBox();
        if (Objects.nonNull(tabBorderColor)) {
            this.tabHolder.setBorder(new MatteBorder(1, 0, 1, 0, tabBorderColor));
        }

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(true);
        cardPanel.setBackground(this.backgroundColor);

        JPanel tabTop = new JPanel(new BorderLayout());
        tabTop.setOpaque(true);
        tabTop.setBackground(this.backgroundColor);
        tabTop.add(tabHolder);

        add(tabTop, BorderLayout.NORTH);
        add(cardPanel);
    }


    public void addTab(String tabTitle, IconCode tabIcon, Component body) {
        var tab = new TabItem(tabIcon, this.closeIcon,
                selectionColor, true,
                backgroundColor, isCloseButtonHidden,
                iconColor, closeButtonColor,
                selectionBackground, titleColor,
                selectedTitleColor, isStretchable, e -> {
            int index = getTabIndex((TabItem) e.getSource());
            setSelectedIndex(index);
            notifyTabSelection(index);
        }, e -> {
            int index = getTabIndex((TabItem) e.getSource());
            closeTab(index);
        });
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
                tabListener.selectionChanged(new TabEvent(this, index));
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

    private boolean notifyTabClosing(int index) {
        synchronized (tabListeners) {
            for (var tabListener :
                    tabListeners) {
                if (!tabListener.tabClosing(new TabEvent(this, index))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void notifyTabClosed(int index) {
        synchronized (tabListeners) {
            for (var tabListener :
                    tabListeners) {
                if (!tabListener.tabClosing(new TabEvent(this, index))) {
                    return;
                }
            }
        }
    }

    public void closeTab(int index) {
        if (tabHolder.getComponentCount() == 0) {
            return;
        }
        if (notifyTabClosing(index)) {
            var tab = (TabItem) tabHolder.getComponent(index);
            tabHolder.remove(tab);
            cardPanel.remove((Component) tab.getClientProperty("tab.content"));
            notifyTabClosed(index);
            if (index == tabHolder.getComponentCount()) {
                index--;
            }
            setSelectedIndex(index);
            revalidate();
            repaint();
        }
    }

}
