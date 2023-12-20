package muon.widgets;

import muon.constants.Orientation;
import muon.screens.sessiontabs.filebrowser.InputBlockerWidget;
import muon.styles.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplitPanel extends JPanel {
    private int diffX;
    private int diffY;
    private final JSplitPane splitPane;
    private final JLayeredPane layeredPane;
    private Orientation orientation;
    private JLabel dividerGrip;
    private InputBlockerWidget inputBlockerWidget;
    private AtomicBoolean initialResize = new AtomicBoolean(false);

    public SplitPanel(Orientation orientation) {
        super(new BorderLayout());
        this.orientation = orientation;
        splitPane = new JSplitPane(orientation == Orientation.Horizontal ?
                JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(1);
        splitPane.setOneTouchExpandable(false);
        splitPane.setBackground(AppTheme.INSTANCE.getSplitPaneBackground());
        splitPane.setResizeWeight(0.5);

        dividerGrip = new JLabel();
        dividerGrip.setCursor(new Cursor(
                orientation == Orientation.Horizontal ?
                        Cursor.W_RESIZE_CURSOR | Cursor.E_RESIZE_CURSOR :
                        Cursor.N_RESIZE_CURSOR | Cursor.S_RESIZE_CURSOR));

        dividerGrip.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (orientation == Orientation.Horizontal) {
                    var x = e.getX() - diffX;
                    dividerGrip.setLocation(dividerGrip.getX() + x, 0);
                    splitPane.setDividerLocation(dividerGrip.getX());
                } else {
                    var y = e.getY() - diffY;
                    dividerGrip.setLocation(0, dividerGrip.getY() + y);
                    splitPane.setDividerLocation(dividerGrip.getY());
                }
            }
        });
        dividerGrip.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (orientation == Orientation.Horizontal) {
                    diffX = e.getX();
                } else {
                    diffY = e.getY();
                }
            }
        });

        inputBlockerWidget = new InputBlockerWidget();
        inputBlockerWidget.setVisible(false);

        splitPane.putClientProperty("floating.divider", dividerGrip);

        layeredPane = new JLayeredPane();
        layeredPane.add(splitPane, Integer.valueOf(1));
        layeredPane.add(dividerGrip, Integer.valueOf(2));
        layeredPane.add(inputBlockerWidget, Integer.valueOf(3));
        add(layeredPane);

        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!initialResize.get()) {
                    initialResize.set(true);
                    var dividerLocation = splitPane.getDividerLocation() >= 0 ? splitPane.getDividerLocation() :
                            orientation == Orientation.Horizontal ? getWidth() / 2 : getHeight() / 2;
                    splitPane.setDividerLocation(dividerLocation);
                }
                splitPane.setBounds(0, 0, getWidth(), getHeight());
                inputBlockerWidget.setBounds(0, 0, getWidth(), getHeight());
                updateGripLocation();
                revalidate();
                repaint();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                splitPane.setBounds(0, 0, getWidth(), getHeight());
                inputBlockerWidget.setBounds(0, 0, getWidth(), getHeight());
                updateGripLocation();
                revalidate();
                repaint();
            }
        });
        //updateGripLocation();
    }

    private void updateGripLocation() {
        System.out.println("splitPane.getDividerLocation() " + splitPane.getDividerLocation());
        if (orientation == Orientation.Horizontal) {
            dividerGrip.setBounds(splitPane.getDividerLocation(), 0, 10, getHeight());
        } else {
            dividerGrip.setBounds(0, splitPane.getDividerLocation(), getWidth(), 10);
        }
    }

    public void setLeftComponent(Component c) {
        splitPane.setLeftComponent(c);
    }

    public void setRightComponent(Component c) {
        splitPane.setRightComponent(c);
    }

    public void setTopComponent(Component c) {
        splitPane.setTopComponent(c);
    }

    public void setBottomComponent(Component c) {
        splitPane.setBottomComponent(c);
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
        updateGripLocation();
        revalidate();
        repaint();
    }

    public void disableUi() {
        inputBlockerWidget.setVisible(true);
    }

    public void enableUi() {
        inputBlockerWidget.setVisible(false);
    }
}


