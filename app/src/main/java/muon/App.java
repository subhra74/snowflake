package muon;

import muon.ui.styles.FlatLookAndFeel;
import muon.ui.widgets.HomePanel;
import muon.ui.widgets.TabItem;
import muon.ui.widgets.TabbedPanel;
import muon.util.FontIcon;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * Hello world!
 */
public class App {

    static EmptyBorder border = new MatteBorder(0, 0, 0, 0, Color.BLACK);
    static EmptyBorder border1 = new MatteBorder(0, 10, 0, 0, new Color(24, 24, 24));
    static JPanel panelRight;

    public static void main(String[] args) throws InterruptedException, InvocationTargetException, UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
//        System.setProperty("awt.useSystemAAFontSettings", "lcd");
//        System.setProperty("swing.aatext", "true");
//        System.setProperty("sun.java2d.uiScale.enabled", "false");
//        System.setProperty("sun.java2d.uiScale", "2.0");
        System.setProperty("sun.java2d.metal", "false");
        System.setProperty("apple.awt.application.appearance", "system");
        UIManager.setLookAndFeel(new FlatLookAndFeel());
        //JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame f = new JFrame();
        //f.setUndecorated(true);
        f.setSize(800, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setBackground(Color.BLACK);
        f.setBackground(Color.BLACK);
        f.add(new HomePanel());
        //var toolbar = createToolbar();
        //f.add(toolbar, BorderLayout.NORTH);
        //f.add(createMainPanel());
        SwingUtilities.invokeAndWait(() -> f.setVisible(true));
    }

    private static JPanel createFileBrowser(EmptyBorder border, boolean right) {
        var c1 = new Color(31, 31, 31);
        var c2 = new Color(24, 24, 24);
        var panel = new JPanel(new BorderLayout());
        if (right) {
            panelRight = panel;
        }
        panel.setBorder(border);
        var toolbar = Box.createHorizontalBox();
        toolbar.setOpaque(true);
        toolbar.setBackground(c1);
        toolbar.add(createFontLabel("\uEA5C"));
        toolbar.add(createFontLabel("\uEA68"));
        toolbar.add(createFontLabel("\uEE1D"));
        var addressBar = new JPanel(new BorderLayout());
        addressBar.setBackground(c1);

        var addressIcon = createFontLabel("\uF395");
        addressIcon.setBorder(new EmptyBorder(5, 5, 3, 10));

        addressBar.add(addressIcon, BorderLayout.WEST);
        var txtAddress = new JTextField();
        txtAddress.setBackground(c1);
        txtAddress.setForeground(new Color(100, 100, 100));
        txtAddress.setBorder(new EmptyBorder(0, 0, 0, 0));
        txtAddress.setText("/usr/home/user/documents");
        addressBar.add(txtAddress);
        addressBar.add(createFontLabel("\uea6c"), BorderLayout.EAST);
        toolbar.add(addressBar);
        toolbar.add(createFontLabel("\uEF77"));
        toolbar.setBorder(new EmptyBorder(2, 5, 0, 0));

        panel.setBackground(c1);
        String data[][] = {{"Documents", "20 M", "25/02/2002"},
                {"Video", "100 G", "04/12/2012"},
                {"Downloads", "32 K", "15/02/2002"}};
        String column[] = {"Name", "Size", "Date"};
        JTable jt = new JTable(data, column);
        jt.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (e.getX() < 20) {
                    panelRight.setBorder(border1);
                    System.out.println("Border set");
                } else {
                    panelRight.setBorder(border);
                    System.out.println("Border unset");
                }
            }
        });
        jt.setShowGrid(false);
        jt.setBackground(c2);
        jt.setForeground(Color.GRAY);
        jt.setFillsViewportHeight(true);
        var header = jt.getTableHeader();
        header.setBackground(Color.black);
        header.setForeground(Color.yellow);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
        header.setDefaultRenderer((a, b, c, d, e, f) -> {
            var label = new JLabel(b.toString());
            label.setBorder(
                    new CompoundBorder(
                            new MatteBorder(1, f == 0 ? 0 : 1, 1, 0, new Color(15, 15, 15)),
                            new EmptyBorder(5, 10, 5, 10)
                    ));
            label.setOpaque(true);
            label.setBackground(c1);
            label.setForeground(new Color(80, 80, 80));
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            return label;
        });
        jt.setRowHeight(30);

        jt.setDefaultRenderer(Object.class, (a, b, c, d, e, f) -> {
            var p = new JPanel(new BorderLayout());
            p.setBackground(c2);
            if (f == 0) {
                var folderIconLbl = new JLabel();
                folderIconLbl.setVerticalAlignment(JLabel.CENTER);
                folderIconLbl.setVerticalTextPosition(JLabel.CENTER);
                folderIconLbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                folderIconLbl.setForeground(new Color(0, 120, 212));
                folderIconLbl.setFont(IconFont.getSharedInstance().getIconFont(24.0f));
                folderIconLbl.setText("\uED61");
                p.add(folderIconLbl, BorderLayout.WEST);
            }
            var label = new JLabel(b.toString());
            label.setBorder(
                    new CompoundBorder(
                            new MatteBorder(0, 0, 0, 0, c1),
                            new EmptyBorder(5, 10, 5, 10)));
            label.setForeground(Color.GRAY);
            label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            p.add(label);
            return p;
        });
        JScrollPane sp = new JScrollPane(jt);
        sp.setBackground(c2);
        sp.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
        sp.setBorder(new EmptyBorder(0, 0, 0, 0));
        panel.add(sp);

        var tab = Box.createHorizontalBox();
        tab.setOpaque(true);
        //tab.setBackground(new Color(68, 69, 73));
        tab.setBackground(c2);
        tab.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 0, new Color(0, 120, 212)),
                new EmptyBorder(5, 0, 5, 0)
        ));
        tab.add(Box.createRigidArea(new Dimension(10, 5)));
//        var iconLbl = new JLabel();
//        iconLbl.setForeground(new Color(52, 117, 233));
//        iconLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
//        iconLbl.setText("\uF2F5");
//        tab.add(iconLbl);
        var lblTitle = new JLabel("Documents");
        lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblTitle.setForeground(new Color(80, 80, 80));
        lblTitle.setBorder(new EmptyBorder(0, 10, 2, 10));
        tab.add(lblTitle);
        tab.add(Box.createHorizontalGlue());
//        var closeLbl = new JLabel();
//        closeLbl.setForeground(new Color(100, 100, 100));
//        closeLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
//        closeLbl.setText("\uEB96");
//        tab.add(closeLbl);
        tab.add(Box.createRigidArea(new Dimension(5, 5)));
        var mainTabHolder = Box.createHorizontalBox();
//        mainTabHolder.setBorder(new MatteBorder(1, 0, 0, 0,
//                new Color(0,0,0)
//        ));
        mainTabHolder.setOpaque(true);
        //mainTabHolder.setBorder(new MatteBorder(1,0,0,0,new Color(15,15,15)));
        mainTabHolder.setBackground(c2);
        mainTabHolder.add(tab);

        var addTabLbl = new JLabel();
        addTabLbl.setOpaque(true);
        addTabLbl.setBackground(c2);
        addTabLbl.setForeground(new Color(100, 100, 100));
        addTabLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        addTabLbl.setText("\uEA13");
        addTabLbl.setBorder(new EmptyBorder(0, 5, 0, 5));
        mainTabHolder.add(addTabLbl);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(mainTabHolder, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createBottomPanel() {
        var c1 = new Color(24, 24, 24);
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(c1);

        var tab = Box.createHorizontalBox();
        tab.setOpaque(true);
        //tab.setBackground(new Color(68, 69, 73));
        tab.setBackground(c1);
        tab.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 0, new Color(0, 120, 212)),
                new EmptyBorder(5, 0, 5, 0)
        ));
        tab.add(Box.createRigidArea(new Dimension(10, 5)));
        var iconLbl = new JLabel();
        iconLbl.setForeground(new Color(0, 120, 212));
        iconLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        iconLbl.setText("\uF2F5");
        tab.add(iconLbl);
        var lblTitle = new JLabel("user@server");
        lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblTitle.setForeground(new Color(180, 180, 180));
        lblTitle.setBorder(new EmptyBorder(0, 10, 2, 10));
        tab.add(lblTitle);
//        var closeLbl = new JLabel();
//        closeLbl.setForeground(new Color(100, 100, 100));
//        closeLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
//        closeLbl.setText("\uEB99");
//        tab.add(closeLbl);
        tab.add(Box.createRigidArea(new Dimension(10, 5)));
        var mainTabHolder = Box.createHorizontalBox();
        mainTabHolder.setBorder(new MatteBorder(0, 0, 0, 0,
                new Color(0, 0, 0)
        ));
        mainTabHolder.add(tab);
        panel.add(mainTabHolder, BorderLayout.SOUTH);

        var bottomCenter = new JPanel(new BorderLayout());
        bottomCenter.setBackground(c1);
        panel.add(bottomCenter);
        //panel.setBorder(new EmptyBorder(5, 7, 5, 7));


        var topTab = Box.createHorizontalBox();
        topTab.setOpaque(true);
        //tab.setBackground(new Color(68, 69, 73));
        topTab.setBackground(c1);
        topTab.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 0, 0, new Color(52, 117, 233)),
                new EmptyBorder(10, 0, 10, 0)
        ));
        topTab.add(Box.createRigidArea(new Dimension(10, 5)));
//        var iconLbl = new JLabel();
//        iconLbl.setForeground(new Color(52, 117, 233));
//        iconLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
//        iconLbl.setText("\uF2F5");
//        tab.add(iconLbl);
        var lblTopTitle = new JLabel("Terminal 1");
        lblTopTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        lblTopTitle.setForeground(new Color(180, 180, 180));
        lblTopTitle.setBorder(new EmptyBorder(0, 10, 2, 10));
        topTab.add(lblTopTitle);
        topTab.add(Box.createHorizontalGlue());
//        var closeLbl = new JLabel();
//        closeLbl.setForeground(new Color(100, 100, 100));
//        closeLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
//        closeLbl.setText("\uEB96");
//        tab.add(closeLbl);
        topTab.add(Box.createRigidArea(new Dimension(5, 5)));
        var mainTopTabHolder = Box.createHorizontalBox();
//        mainTabHolder.setBorder(new MatteBorder(1, 0, 0, 0,
//                new Color(0,0,0)
//        ));
        mainTopTabHolder.setOpaque(true);
        mainTopTabHolder.setBackground(c1);
        mainTopTabHolder.add(topTab);

        var addTopTabLbl = new JLabel();
        addTopTabLbl.setOpaque(true);
        addTopTabLbl.setBackground(c1);
        addTopTabLbl.setForeground(new Color(100, 100, 100));
        addTopTabLbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        addTopTabLbl.setText("\uEA13");
        addTopTabLbl.setBorder(new EmptyBorder(0, 5, 0, 5));
        mainTopTabHolder.add(addTopTabLbl);
        panel.add(mainTopTabHolder, BorderLayout.NORTH);
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(15, 15, 15)));

        return panel;
    }

    private static JPanel createContentPanel(){
        var mainTop = new JPanel(new GridLayout(1, 2));
        mainTop.add(createFileBrowser(new MatteBorder(0, 0, 0, 1, new Color(15, 15, 15)), false));
        mainTop.add(createFileBrowser(border, true));

        var mainBottom = createBottomPanel();

        var mainCenter = new JPanel(new GridLayout(2, 1));
        mainCenter.add(mainTop);
        mainCenter.add(mainBottom);
        return mainCenter;
    }

    private static JPanel createMainPanel() {
        var tabbedPanel = new TabbedPanel(
                false,
                false,
                new Color(52, 117, 233),
                new Color(24, 24, 24),
                new Color(52, 117, 233),
                new Color(100, 100, 100),
                new Color(31, 31, 31),
                new Color(130, 130, 130),
                new Color(180, 180, 180),
                FontIcon.RI_INSTANCE_LINE,
                FontIcon.RI_CLOSE_LINE,
                Color.BLACK
        );
        tabbedPanel.addTab("tab1", FontIcon.RI_INSTANCE_LINE, createContentPanel());
        tabbedPanel.addTab("tab2", FontIcon.RI_INSTANCE_LINE, createContentPanel());
//
//
//
//        var mainTabHolder = new Box(BoxLayout.X_AXIS);
//        mainTabHolder.setBackground(new Color(24, 24, 24));
//        mainTabHolder.setBorder(new MatteBorder(1, 0, 1, 0,
//                Color.BLACK)
//        );
//
//        for (var i = 0; i < 5; i++) {
//            var tab = new TabItem(FontIcon.RI_INSTANCE_LINE, FontIcon.RI_CLOSE_LINE,
//                    new Color(52, 117, 233), i == 0,
//                    new Color(24, 24, 24), false,
//                    new Color(52, 117, 233), new Color(100, 100, 100),
//                    new Color(31, 31, 31), new Color(180, 180, 180),
//                    new Color(180, 180, 180), false, e -> {
//            }, e -> {
//            });
//            tab.setTabTitle("user@server");
////            var tab = new TabItem(true, true,new Color(180, 180, 180));
////            tab.setBackground(new Color(31, 31, 31));
////            tab.setTabSelectionColor();
////            tab.setTabIcon(FontIcon.RI_INSTANCE_LINE);
////            tab.setTabIconColor(new Color(52, 117, 233));
////            tab.setTabTitle("user@server");
////            tab.setTabTitleForeground(new Color(180, 180, 180));
////            tab.setTabCloseButtonColor(new Color(100, 100, 100));
////            tab.setTabCloseButtonIcon();
////            tab.setOpaque(true);
//
//            mainTabHolder.add(tab);
//        }
//
//        var mainTop = new JPanel(new GridLayout(1, 2));
//        mainTop.add(createFileBrowser(new MatteBorder(0, 0, 0, 1, new Color(15, 15, 15)), false));
//        mainTop.add(createFileBrowser(border, true));
//
//        var mainBottom = createBottomPanel();
//
//        var mainCenter = new JPanel(new GridLayout(2, 1));
//        mainCenter.add(mainTop);
//        mainCenter.add(mainBottom);
//
//
//        tabbedPanel.addTab("user@server",FontIcon.RI_INSTANCE_LINE,mainCenter);
//
//        var mainTab = new JPanel(new BorderLayout());
//        mainTab.add(mainTabHolder, BorderLayout.NORTH);
//        mainTab.add(mainCenter);
//
//        mainTop.setBackground(new Color(31, 31, 31));
//        mainTab.setBackground(new Color(24, 24, 24));
//        //mainTab.setBackground(new Color(31,30,36));
        return tabbedPanel;
    }

    private static Box createToolbar() {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createRigidArea(new Dimension(10, 30)));
        box.add(createFontLabel("\uEA0E"));
        box.add(createFontLabel("\uEB08"));
        box.add(createFontLabel("\uEE4C"));
        box.add(createFontLabel("\uEB9C"));
        box.add(createFontLabel("\uEA85"));
        box.add(createFontLabel("\uEFF9"));
        box.add(createFontLabel("\uF1F5"));
        box.add(createFontLabel("\uEB08"));
        box.add(createFontLabel("\uEE4C"));
        box.add(createFontLabel("\uEB9C"));
        box.add(createFontLabel("\uEA85"));
        box.add(createFontLabel("\uEFF9"));
        box.add(createFontLabel("\uF1F5"));
        box.add(createFontLabel("\uEB08"));
        box.add(createFontLabel("\uEE4C"));
        box.add(createFontLabel("\uEB9C"));
        box.add(createFontLabel("\uEA85"));
        box.add(createFontLabel("\uEFF9"));
        box.add(createFontLabel("\uF1F5"));
        return box;
    }

    private static JLabel createFontLabel(String hex) {
        var lbl = new JLabel();
        lbl.setForeground(new Color(80, 80, 80));
        lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
        lbl.setFont(IconFont.getSharedInstance().getIconFont(18.0f));
        lbl.setText(hex);
        return lbl;
    }

    static class MyTableCellRenderer extends JLabel implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int rowIndex, int vColIndex) {
            setText(value.toString());
            setToolTipText((String) value);
            return this;
        }
    }
}
