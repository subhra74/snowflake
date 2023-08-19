//package muon.ui.widgets;
//
//import muon.ui.styles.AppTheme;
//import muon.util.AppUtils;
//import muon.util.IconCode;
//import muon.util.IconFont;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.event.*;
//
//public class CustomFrame extends JFrame {
//
//    JPanel contentPanel;
//
//    JLabel lblRightGrip, lblLeftGrip, lblTopGrip, lblBottomGrip;
//
//    JButton btnMin, btnMax;
//
//
//    public CustomFrame() {
//        initUI(this);
//    }
//
//    private JLabel createGripLabel() {
//        var label = new JLabel();
//        int gripSize = 2;
//        label.setMinimumSize(new Dimension(gripSize, gripSize));
//        label.setPreferredSize(new Dimension(gripSize, gripSize));
//        return label;
//    }
//
//    private void createTopGrip() {
//        var lblTopGrip = createGripLabel();
//        var gc = new GridBagConstraints();
//        gc.gridx = 0;
//        gc.gridy = 0;
//        gc.gridwidth = 4;
//        gc.weightx = 1;
//        gc.fill = GridBagConstraints.HORIZONTAL;
//        container.add(parts.lblTopGrip, gc);
//    }
//
//    private void createLeftGrip(FrameComponents parts,Container container) {
//        parts.lblLeftGrip = createGripLabel();
//        var gc = new GridBagConstraints();
//        gc.gridx = 0;
//        gc.gridy = 2;
//        gc.weighty = 1;
//        gc.fill = GridBagConstraints.VERTICAL;
//        gc.gridwidth = 1;
//        gc.gridheight = 3;
//        container.add(parts.lblLeftGrip, gc);
//    }
//
//    private void createRightGrip(FrameComponents parts,Container container) {
//        parts.lblRightGrip = createGripLabel();
//        var gc = new GridBagConstraints();
//        gc.gridx = 3;
//        gc.gridy = 2;
//        gc.weighty = 1;
//        gc.fill = GridBagConstraints.VERTICAL;
//        gc.gridwidth = 1;
//        gc.gridheight = 3;
//        container.add(parts.lblRightGrip, gc);
//    }
//
//    private void createBottomGrip(FrameComponents parts,Container container) {
//        parts.lblBottomGrip = createGripLabel();
//        var gc = new GridBagConstraints();
//        gc.gridx = 0;
//        gc.gridy = 4;
//        gc.gridwidth = 4;
//        gc.weightx = 1;
//        gc.fill = GridBagConstraints.HORIZONTAL;
//        container.add(parts.lblBottomGrip, gc);
//    }
//
//    protected JLabel createFrameIcon() {
//        var lblLogoIcon = new JLabel();
//        lblLogoIcon.setBorder(new EmptyBorder(0, 3, 2, 3));
//        lblLogoIcon.setForeground(Color.WHITE);
//        lblLogoIcon.setBackground(AppTheme.INSTANCE.getSelectionColor());
//        lblLogoIcon.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
//        lblLogoIcon.setText("m");
//        lblLogoIcon.setOpaque(true);
//        return lblLogoIcon;
//    }
//
//    private void createTitlePanel(String title, Container container) {
//        var lblTitle = new JLabel(title);
//        lblTitle.setForeground(AppTheme.INSTANCE.getTitleForeground());
//
//        var titleBox = Box.createHorizontalBox();
//        titleBox.add(Box.createRigidArea(new Dimension(10, 10)));
//        titleBox.add(createFrameIcon());
//        titleBox.add(Box.createRigidArea(new Dimension(10, 10)));
//        titleBox.add(lblTitle);
//
//        var gc = new GridBagConstraints();
//        gc.gridx = 0;
//        gc.gridy = 1;
//        gc.gridwidth = 2;
//        gc.weightx = 1;
//        gc.fill = GridBagConstraints.BOTH;
//
//        JPanel titlePanel = new FrameDragPanel(new BorderLayout(), this);
//        titlePanel.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//        titlePanel.add(titleBox);
//        container.add(titlePanel, gc);
//    }
//
//    private void createControlBox(FrameComponents parts, Container contentPane, Frame window) {
//        parts.btnMin = createWindowButton(IconCode.RI_SUBTRACT_LINE, 18.0f);
//        parts.btnMax = createWindowButton(IconCode.RI_CHECKBOX_BLANK_LINE, 14.0f);
//        var btnClose = createWindowButton(IconCode.RI_CLOSE_LINE, 18.0f);
//
//        parts.btnMin.addActionListener(e -> window.setExtendedState(window.getExtendedState() | JFrame.ICONIFIED));
//        parts.btnMax.addActionListener(e -> window.setExtendedState(window.getExtendedState() ^ JFrame.MAXIMIZED_BOTH));
//        btnClose.addActionListener(e -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
//
//        var buttons = new JButton[]{
//                parts.btnMin, parts.btnMax, btnClose
//        };
//
//        AppUtils.makeEqualSize(buttons);
//
//        var box = Box.createHorizontalBox();
//        box.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//        box.setOpaque(true);
//        box.add(buttons[0]);
//        box.add(buttons[1]);
//        box.add(buttons[2]);
//
//        var gc = new GridBagConstraints();
//        gc.gridx = 2;
//        gc.gridy = 1;
//        gc.gridwidth = 1;
//        contentPane.add(box, gc);
//    }
//
//    private void createContentPanel(FrameComponents parts, Container container) {
//        parts.contentPanel = new JPanel(new BorderLayout());
//
//        var gc = new GridBagConstraints();
//        gc.gridx = 1;
//        gc.gridy = 2;
//        gc.gridwidth = 2;
//        gc.weightx = 1;
//        gc.weighty = 1;
//        gc.fill = GridBagConstraints.BOTH;
//        container.add(parts.contentPanel, gc);
//    }
//
//    private void createResizeGrip(FrameComponents parts) {
//        GripMouseAdapter gma = new GripMouseAdapter();
//        if (isResizable()) {
//            parts.lblTopGrip.addMouseListener(gma);
//            parts.lblTopGrip.addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseDragged(MouseEvent me) {
//                    int y = me.getYOnScreen();
//                    int diff = CustomFrame.this.getLocationOnScreen().y - y;
//                    CustomFrame.this.setLocation(CustomFrame.this.getLocation().x, me.getLocationOnScreen().y);
//                    CustomFrame.this.setSize(CustomFrame.this.getWidth(), CustomFrame.this.getHeight() + diff);
//                }
//            });
//
//            parts.lblRightGrip.addMouseListener(gma);
//            parts.lblRightGrip.addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseDragged(MouseEvent me) {
//                    int x = me.getXOnScreen();
//                    int diff = x - CustomFrame.this.getLocationOnScreen().x;
//                    CustomFrame.this.setSize(diff, CustomFrame.this.getHeight());
//                }
//            });
//
//            parts.lblLeftGrip.addMouseListener(gma);
//            parts.lblLeftGrip.addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseDragged(MouseEvent me) {
//                    int x = me.getXOnScreen();
//                    int diff = CustomFrame.this.getLocationOnScreen().x - x;
//                    CustomFrame.this.setLocation(me.getLocationOnScreen().x, CustomFrame.this.getLocation().y);
//                    CustomFrame.this.setSize(diff + CustomFrame.this.getWidth(), CustomFrame.this.getHeight());
//                }
//            });
//
//            parts.lblBottomGrip.addMouseListener(gma);
//            parts.lblBottomGrip.addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseDragged(MouseEvent me) {
//                    int y = me.getYOnScreen();
//                    int diff = y - CustomFrame.this.getLocationOnScreen().y;
//                    CustomFrame.this.setSize(CustomFrame.this.getWidth(), diff);
//                }
//            });
//        }
//
//    }
//
//    private void initUI() {
//        this.setUndecorated(true);
//        this.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
//        this.setLayout(new GridBagLayout());
//        this.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//
//        createTopGrip(frame);
//        createTitlePanel(frame);
//        createControlBox(frame);
//        createLeftGrip(frame);
//        createContentPanel();
//        createRightGrip();
//        createBottomGrip();
//        createResizeGrip();
//
//        createCursors();
//    }
//
//    public boolean isMaximizable() {
//        return btnMax.isVisible();
//    }
//
//    public void setMaximizable(boolean value) {
//        btnMax.setVisible(value);
//    }
//
//    public boolean isMinimizable() {
//        return btnMin.isVisible();
//    }
//
//    public void setMinimizable(boolean value) {
//        btnMin.setVisible(value);
//    }
//
//    @Override
//    public Component add(Component c) {
//        System.out.println("Adding new components");
//        contentPanel.removeAll();
//        contentPanel.validate();
//        var comp = contentPanel.add(c);
//        contentPanel.validate();
//        return comp;
//    }
//
//    class GripMouseAdapter extends MouseAdapter {
//        @Override
//        public void mouseEntered(MouseEvent me) {
//            if (me.getSource() == lblBottomGrip) {
//                lblBottomGrip.setCursor(curSResize);
//            } else if (me.getSource() == lblRightGrip) {
//                lblRightGrip.setCursor(curEResize);
//            } else if (me.getSource() == lblLeftGrip) {
//                lblLeftGrip.setCursor(curWResize);
//            } else if (me.getSource() == lblTopGrip) {
//                lblTopGrip.setCursor(curNResize);
//            }
//        }
//
//        @Override
//        public void mouseExited(MouseEvent me) {
//            ((JLabel) me.getSource()).setCursor(curDefault);
//        }
//
//    }
//
//    Cursor curDefault, curNResize, curEResize, curWResize, curSResize, curSEResize, curSWResize;
//
//    private void createCursors() {
//        curDefault = new Cursor(Cursor.DEFAULT_CURSOR);
//        curNResize = new Cursor(Cursor.N_RESIZE_CURSOR);
//        curWResize = new Cursor(Cursor.W_RESIZE_CURSOR);
//        curEResize = new Cursor(Cursor.E_RESIZE_CURSOR);
//        curSResize = new Cursor(Cursor.S_RESIZE_CURSOR);
//    }
//
//    private JButton createWindowButton(IconCode code, float size) {
//        var button = new JButton();
//        button.setBorderPainted(false);
//        button.setContentAreaFilled(false);
//        button.putClientProperty("button.arc", 0);
//        button.setFont(IconFont.getSharedInstance().getIconFont(size));
//        button.setText(code.getValue());
//        return button;
//    }
//}
