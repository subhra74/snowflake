package muon.ui.widgets;

import muon.ui.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;

public class CustomFramePanel extends JPanel {
    private JPanel contentPanel;

    private JLabel lblRightGrip, lblLeftGrip, lblTopGrip, lblBottomGrip;

    private JButton btnMin, btnMax;

    private final Window frame;

    public CustomFramePanel(Window frame) {
        this.frame = frame;
        initUI();
    }

    public Component addContent(Component c) {
        contentPanel.removeAll();
        contentPanel.validate();
        var comp = contentPanel.add(c);
        contentPanel.validate();
        return comp;
    }

    private JLabel createGripLabel() {
        var label = new JLabel();
        int gripSize = 2;
        label.setMinimumSize(new Dimension(gripSize, gripSize));
        label.setPreferredSize(new Dimension(gripSize, gripSize));
        return label;
    }

    private void createTopGrip() {
        lblTopGrip = createGripLabel();
        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 4;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(lblTopGrip, gc);
    }

    private void createLeftGrip() {
        lblLeftGrip = createGripLabel();
        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridwidth = 1;
        gc.gridheight = 3;
        add(lblLeftGrip, gc);
    }

    private void createRightGrip() {
        lblRightGrip = createGripLabel();
        var gc = new GridBagConstraints();
        gc.gridx = 3;
        gc.gridy = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.VERTICAL;
        gc.gridwidth = 1;
        gc.gridheight = 3;
        add(lblRightGrip, gc);
    }

    private void createBottomGrip() {
        lblBottomGrip = createGripLabel();
        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 4;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(lblBottomGrip, gc);
    }

    protected JLabel createFrameIcon() {
        var lblLogoIcon = new JLabel();
        lblLogoIcon.setBorder(new EmptyBorder(0, 3, 2, 3));
        lblLogoIcon.setForeground(Color.WHITE);
        lblLogoIcon.setBackground(AppTheme.INSTANCE.getSelectionColor());
        lblLogoIcon.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
        lblLogoIcon.setText("m");
        lblLogoIcon.setOpaque(true);
        return lblLogoIcon;
    }

    private void createTitlePanel() {
        var title = "";
        if (frame instanceof JFrame) {
            title = ((JFrame) frame).getTitle();
        } else if (frame instanceof JDialog) {
            title = ((JDialog) frame).getTitle();
        }
        var lblTitle = new JLabel(title);
        lblTitle.setForeground(AppTheme.INSTANCE.getTitleForeground());

        var titleBox = Box.createHorizontalBox();
        titleBox.add(Box.createRigidArea(new Dimension(10, 10)));
        titleBox.add(createFrameIcon());
        titleBox.add(Box.createRigidArea(new Dimension(10, 10)));
        titleBox.add(lblTitle);

        var gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;

        JPanel titlePanel = new FrameDragPanel(new BorderLayout(), frame);
        titlePanel.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
        titlePanel.add(titleBox);
        add(titlePanel, gc);
    }

    private void createControlBox() {
        btnMin = createWindowButton(IconCode.RI_SUBTRACT_LINE, 18.0f);
        btnMax = createWindowButton(IconCode.RI_CHECKBOX_BLANK_LINE, 14.0f);
        var btnClose = createWindowButton(IconCode.RI_CLOSE_LINE, 18.0f);

        if (frame instanceof JFrame jFrame) {
            btnMin.addActionListener(e -> jFrame.setExtendedState(jFrame.getExtendedState() | JFrame.ICONIFIED));
            btnMax.addActionListener(e -> jFrame.setExtendedState(jFrame.getExtendedState() ^ JFrame.MAXIMIZED_BOTH));
        }
        btnClose.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        var buttons = new JButton[]{
                btnMin, btnMax, btnClose
        };

        AppUtils.makeEqualSize(buttons);

        var box = Box.createHorizontalBox();
        box.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
        box.setOpaque(true);
        box.add(buttons[0]);
        box.add(buttons[1]);
        box.add(buttons[2]);

        var gc = new GridBagConstraints();
        gc.gridx = 2;
        gc.gridy = 1;
        gc.gridwidth = 1;
        add(box, gc);
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());

        var gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 2;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(contentPanel, gc);
    }

    private void createResizeGrip() {
        GripMouseAdapter gma = new GripMouseAdapter();

        lblTopGrip.addMouseListener(gma);
        lblTopGrip.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                int y = me.getYOnScreen();
                int diff = frame.getLocationOnScreen().y - y;
                frame.setLocation(frame.getLocation().x, me.getLocationOnScreen().y);
                frame.setSize(frame.getWidth(), frame.getHeight() + diff);
            }
        });

        lblRightGrip.addMouseListener(gma);
        lblRightGrip.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                int x = me.getXOnScreen();
                int diff = x - getLocationOnScreen().x;
                frame.setSize(diff, frame.getHeight());
            }
        });

        lblLeftGrip.addMouseListener(gma);
        lblLeftGrip.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                int x = me.getXOnScreen();
                int diff = frame.getLocationOnScreen().x - x;
                frame.setLocation(me.getLocationOnScreen().x, frame.getLocation().y);
                frame.setSize(diff + frame.getWidth(), frame.getHeight());
            }
        });

        lblBottomGrip.addMouseListener(gma);
        lblBottomGrip.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                int y = me.getYOnScreen();
                int diff = y - frame.getLocationOnScreen().y;
                frame.setSize(frame.getWidth(), diff);
            }
        });


    }

    private void initUI() {
        if (frame instanceof JDialog dialog) {
            dialog.setUndecorated(true);
            dialog.getContentPane().setBackground(AppTheme.INSTANCE.getBackground());
        } else if (frame instanceof JFrame jFrame) {
            jFrame.setUndecorated(true);
            jFrame.getContentPane().setBackground(AppTheme.INSTANCE.getBackground());
            jFrame.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        }
        frame.setBackground(AppTheme.INSTANCE.getBackground());
        this.setLayout(new GridBagLayout());
        this.setBackground(AppTheme.INSTANCE.getDarkControlBackground());

        createTopGrip();
        createTitlePanel();
        createControlBox();
        createLeftGrip();
        createContentPanel();
        createRightGrip();
        createBottomGrip();
        createResizeGrip();

        createCursors();
    }

    public boolean isMaximizable() {
        return btnMax.isVisible();
    }

    public void setMaximizable(boolean value) {
        btnMax.setVisible(value);
    }

    public boolean isMinimizable() {
        return btnMin.isVisible();
    }

    public void setMinimizable(boolean value) {
        btnMin.setVisible(value);
    }

    class GripMouseAdapter extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent me) {
            if (me.getSource() == lblBottomGrip) {
                lblBottomGrip.setCursor(curSResize);
            } else if (me.getSource() == lblRightGrip) {
                lblRightGrip.setCursor(curEResize);
            } else if (me.getSource() == lblLeftGrip) {
                lblLeftGrip.setCursor(curWResize);
            } else if (me.getSource() == lblTopGrip) {
                lblTopGrip.setCursor(curNResize);
            }
        }

        @Override
        public void mouseExited(MouseEvent me) {
            ((JLabel) me.getSource()).setCursor(curDefault);
        }

    }

    Cursor curDefault, curNResize, curEResize, curWResize, curSResize, curSEResize, curSWResize;

    private void createCursors() {
        curDefault = new Cursor(Cursor.DEFAULT_CURSOR);
        curNResize = new Cursor(Cursor.N_RESIZE_CURSOR);
        curWResize = new Cursor(Cursor.W_RESIZE_CURSOR);
        curEResize = new Cursor(Cursor.E_RESIZE_CURSOR);
        curSResize = new Cursor(Cursor.S_RESIZE_CURSOR);
    }

    private JButton createWindowButton(IconCode code, float size) {
        var button = new JButton();
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.putClientProperty("button.arc", 0);
        button.setFont(IconFont.getSharedInstance().getIconFont(size));
        button.setText(code.getValue());
        return button;
    }
}
