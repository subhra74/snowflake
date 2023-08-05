package muon.ui.widgets;

import muon.util.FontIcon;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class TabItem extends JComponent {
    private JLabel tabIconLabel;
    private JLabel tabTitle;
    private JLabel tabCloseButton;
    private boolean isSelected;
    private boolean isStretchable, isCloseButtonHidden;
    private Color selectionColor, backgroundColor, iconColor, closeButtonColor, selectionBackground, titleColor, selectedTitleColor;
    private Border selectedBorder, unselectedBorder;
    private FontIcon titleIcon, closeIcon;
    private ActionListener tabClicked, closeClicked;

    public TabItem(FontIcon titleIcon, FontIcon closeIcon,
                   Color selectionColor, boolean isSelected,
                   Color backgroundColor, boolean hideCloseButton,
                   Color iconColor, Color closeButtonColor,
                   Color selectionBackground, Color titleColor,
                   Color selectedTitleColor, boolean isStretchable,
                   ActionListener tabClicked, ActionListener closeClicked) {
        super();

        this.titleIcon = titleIcon;
        this.closeIcon = closeIcon;
        this.selectionColor = selectionColor;
        this.isSelected = isSelected;
        this.isStretchable = isStretchable;
        this.iconColor = iconColor;
        this.closeButtonColor = closeButtonColor;
        this.selectionBackground = selectionBackground;
        this.backgroundColor = backgroundColor;
        this.isCloseButtonHidden = hideCloseButton;
        this.titleColor = titleColor;
        this.selectedTitleColor = selectedTitleColor;
        this.selectedBorder = createBorder(selectionColor);
        this.unselectedBorder = createBorder(backgroundColor);
        if (Objects.nonNull(titleIcon)) {
            this.tabIconLabel = new JLabel();
            this.tabIconLabel.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        }
        this.tabTitle = new JLabel();
        this.tabTitle.setMinimumSize(new Dimension(0, 0));
        this.tabTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        this.tabTitle.setBorder(new EmptyBorder(0, 10, 2, 10));
        if (Objects.nonNull(closeIcon)) {
            this.tabCloseButton = new JLabel();
            this.tabCloseButton.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
            this.tabCloseButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (closeClicked != null) {
                        closeClicked.actionPerformed(new ActionEvent(TabItem.this,
                                TabItem.this.hashCode(),
                                "tab_close"));
                    }
                }
            });
        }

        setOpaque(true);
        if (isStretchable) {
            setLayout(new BorderLayout(10, 10));
            add(tabIconLabel, BorderLayout.WEST);
            add(tabTitle);
            add(tabCloseButton, BorderLayout.EAST);
        } else {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            add(tabIconLabel);
            add(tabTitle);
            add(tabCloseButton);
        }
        setSelected(isSelected);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tabClicked != null) {
                    tabClicked.actionPerformed(new ActionEvent(TabItem.this,
                            TabItem.this.hashCode(),
                            "tab_click"));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isCloseButtonHidden) {
                    tabCloseButton.setForeground(closeButtonColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isCloseButtonHidden) {
                    tabCloseButton.setForeground(getBackground());
                }
            }
        });
    }

    public void setTabTitle(String title) {
        tabTitle.setText(title);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateStyles();
    }

    public boolean isSelected() {
        return isSelected;
    }

    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void updateStyles() {
        if (isSelected) {
            setBackground(selectionBackground);
            tabTitle.setForeground(selectedTitleColor);
            setBorder(selectedBorder);
        } else {
            setBackground(backgroundColor);
            tabTitle.setForeground(titleColor);
            setBorder(unselectedBorder);
        }
        if (tabIconLabel != null) {
            tabIconLabel.setForeground(iconColor);
            tabIconLabel.setText(titleIcon.getValue());
        }
        if (tabCloseButton != null) {
            tabCloseButton.setForeground(isCloseButtonHidden ? getBackground() : closeButtonColor);
            tabCloseButton.setText(closeIcon.getValue());
        }
        revalidate();
        repaint();
    }

    private Border createBorder(Color color) {
        return new CompoundBorder(
                new MatteBorder(1, 0, 0, 0,
                        color),
                new EmptyBorder(5, 10, 5, 10)
        );
    }

}
