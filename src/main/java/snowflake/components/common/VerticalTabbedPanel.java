package snowflake.components.common;

import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VerticalTabbedPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Box tabHolder;
    private Color selectionColor = new Color(33, 136, 255);

    public VerticalTabbedPanel() {
        super(new BorderLayout());
        setBackground(Color.WHITE);
        setOpaque(true);
        tabHolder = Box.createVerticalBox();
        tabHolder.setOpaque(false);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        add(tabHolder, BorderLayout.WEST);
        add(cardPanel);
    }

    public void addTab(String title, Component body) {
        int index = tabHolder.getComponentCount();
        cardPanel.add(body, body.hashCode() + "");
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(new EmptyBorder(10,10,10,10));
        titleLabel.setFont(App.getFontAwesomeFont().deriveFont(Font.PLAIN,20.0f));
        titleLabel.setBackground(selectionColor);
        titleLabel.setName(body.hashCode() + "");
        titleLabel.setAlignmentX(Box.LEFT_ALIGNMENT);
        tabHolder.add(titleLabel);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < tabHolder.getComponentCount(); i++) {
                    JComponent c = (JComponent) tabHolder.getComponent(i);
                    if (c == titleLabel) {
                        setSelectedIndex(i);
                        break;
                    }
                }
            }
        };

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
        c.setOpaque(true);
        tabHolder.revalidate();
        tabHolder.repaint();
    }

    private void unselectTabTitle(JComponent c) {
        c.setOpaque(false);
        tabHolder.revalidate();
        tabHolder.repaint();
    }

}
