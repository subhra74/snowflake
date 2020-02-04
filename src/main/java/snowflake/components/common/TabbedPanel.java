package snowflake.components.common;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabbedPanel extends JPanel {
    private Color unselectedBg = Color.WHITE, selectedBg = new Color(240, 240, 240);
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel tabHolder;
    private Border selectedTabBorder = new CompoundBorder(new MatteBorder(0, 0, 0, 0, new Color(240, 240, 240)),
            new MatteBorder(3, 0, 0, 0, new Color(33, 136, 255)));
    private Border unselectedTabBorder = new CompoundBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)),
            new MatteBorder(3, 0, 0, 0, Color.WHITE));

    public TabbedPanel() {
        super(new BorderLayout());
        setOpaque(true);
        tabHolder = new JPanel(new GridLayout(1, 0, 1, 1));
        tabHolder.setBackground(new Color(240, 240, 240));
        tabHolder.setOpaque(true);
        //tabHolder.setBorder(new MatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        add(tabHolder, BorderLayout.NORTH);
        add(cardPanel);
    }

    public void addTab(String title, Component body) {
        int index = tabHolder.getComponentCount();
        cardPanel.add(body, body.hashCode() + "");
        JPanel tabTitlePanel = new JPanel(new BorderLayout());
        tabTitlePanel.setBackground(Color.WHITE);
        tabTitlePanel.setOpaque(true);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(3, 0, 5, 0));
        tabTitlePanel.add(titleLabel);
        tabTitlePanel.setName(body.hashCode() + "");
        tabHolder.add(tabTitlePanel);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < tabHolder.getComponentCount(); i++) {
                    JComponent c = (JComponent) tabHolder.getComponent(i);
                    if (c == tabTitlePanel) {
                        setSelectedIndex(i);
                        break;
                    }
                }
            }
        };

        tabTitlePanel.addMouseListener(mouseAdapter);
        titleLabel.addMouseListener(mouseAdapter);

        setSelectedIndex(index);
    }

    public int getSelectedIndex() {
        for (int i = 0; i < tabHolder.getComponentCount(); i++) {
            JComponent c = (JComponent) tabHolder.getComponent(i);
            if (c.getClientProperty("Tab.selected") == Boolean.TRUE) {
                return i;
            }
        }
        return -1;
    }

    public void setSelectedIndex(int n) {
        JComponent c = (JComponent) tabHolder.getComponent(n);
        String id = c.getName();
        cardLayout.show(cardPanel, id);
        for (int i = 0; i < tabHolder.getComponentCount(); i++) {
            JComponent cc = (JComponent) tabHolder.getComponent(i);
            cc.putClientProperty("Tab.selected", null);
            unselectTabTitle(cc);
        }
        JComponent cc = (JComponent) tabHolder.getComponent(n);
        cc.putClientProperty("Tab.selected", Boolean.TRUE);
        selectTabTitle(cc);
    }

    private void selectTabTitle(JComponent c) {
        c.setBorder(selectedTabBorder);
        c.setBackground(selectedBg);
        c.revalidate();
        c.repaint();
    }

    private void unselectTabTitle(JComponent c) {
        c.setBorder(unselectedTabBorder);
        c.setBackground(unselectedBg);
        c.revalidate();
        c.repaint();
    }
}
