package muon.screens.appwin;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import muon.AppContext;
import muon.exceptions.AuthenticationException;
import muon.screens.appwin.tabs.filebrowser.DualPaneFileBrowser;
import muon.screens.appwin.tabs.terminal.TabbedTerminal;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;
import muon.widgets.TabbedPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainContainer extends JPanel implements AppWin {
    //private JPanel contentPanel;
    private DualPaneFileBrowser dualPaneFileBrowser;
    private TabbedTerminal tabbedTerminal;
    private AtomicBoolean init = new AtomicBoolean(false);
    private BottomTabItem tabs[];
    private JPasswordField txtPassword;
    private JButton btnPassword;

    private ImageIcon createIcon(String name, int size) {
        FlatSVGIcon.ColorFilter filter = new FlatSVGIcon.ColorFilter();
        filter.add(Color.BLACK, Color.GRAY);
        FlatSVGIcon icon = null;
        try {
            icon = new FlatSVGIcon(getClass().getResourceAsStream("/icons/" + name));
            icon.setColorFilter(filter);
            return icon.derive(size, size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MainContainer() {
        super(new CardLayout());
        setBorder(new MatteBorder(1, 0, 0, 0, UIManager.getColor("TableHeader.bottomSeparatorColor")));
        var tabs = new JTabbedPane();
        tabs.setTabPlacement(JTabbedPane.BOTTOM);

        dualPaneFileBrowser = new DualPaneFileBrowser(this);
        tabbedTerminal = new TabbedTerminal();

        tabs.addTab("File Browser", createIcon("folder-fill.svg", 16), dualPaneFileBrowser);
        tabs.addTab("Remote Terminal", createIcon("terminal-box-fill.svg", 16), tabbedTerminal);
        tabs.addTab("Port Forwarding", createIcon("swap-box-fill.svg", 16), new JPanel());
        tabs.addTab("Key Manager", createIcon("key-fill.svg", 16), new JPanel());

//        contentPanel = new JPanel(new CardLayout());
//        contentPanel.add(dualPaneFileBrowser, "FILES");
//        contentPanel.add(tabbedTerminal, "TERM");
//
//        var panel = new JPanel(new BorderLayout());
//        panel.add(contentPanel);
//        panel.add(createBottomTabs(), BorderLayout.SOUTH);

        this.add(createProgressPanel(), "PROGRESS");
        this.add(createPasswordPanel(), "PASSWORD");
        this.add(tabs, "CONTENT");

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!init.get()) {
                    init.set(true);
                    loadInitialSessionContext();
                }
            }

//            @Override
//            public void componentShown(ComponentEvent e) {
//                contentPanel.setBounds(0, 0, getWidth(), getHeight());
//                inputBlockerPanel.setBounds(0, 0, getWidth(), getHeight());
//                revalidate();
//                repaint();
//            }
        });
    }

    private void initContent() {
        ((CardLayout) this.getLayout()).show(this, "CONTENT");
//        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "FILES");
        revalidate();
        repaint();
        dualPaneFileBrowser.init();
    }

    private void loadInitialSessionContext() {
        AppUtils.runAsync(() -> {
            try {
                AppContext.loadSessionTree();
                SwingUtilities.invokeLater(this::initContent);
            } catch (AuthenticationException ex) {
                SwingUtilities.invokeLater(() -> {
                    ((CardLayout) this.getLayout()).show(this, "PASSWORD");
                });
            }
        });
    }

//    private Container createBottomTabs() {
//        String[] str = new String[]{"FILES", "TERM"};
//        ActionListener tabSelection = e -> {
//            for (var i = 0; i < tabs.length; i++) {
//                tabs[i].setSelected(e.getSource() == tabs[i]);
//                if (e.getSource() == tabs[i] && i < str.length) {
//                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, str[i]);
//                }
//            }
//        };
//
//        var box = Box.createHorizontalBox();
//        box.setOpaque(true);
//        box.setBackground(AppTheme.INSTANCE.getDarkControlBackground());
//
//        var btnFiles = new BottomTabItem(IconCode.RI_FOLDER_FILL, "File Browser", tabSelection);
//        var btnTerminal = new BottomTabItem(IconCode.RI_TERMINAL_BOX_FILL, "Remote Terminal", tabSelection);
//        var btnPortFwd = new BottomTabItem(IconCode.RI_SWAP_BOX_FILL, "Port Forwarding", tabSelection);
//        var btnKeyMgr = new BottomTabItem(IconCode.RI_KEY_FILL, "Key Manager", tabSelection);
//
//        tabs = new BottomTabItem[]{btnFiles, btnTerminal, btnPortFwd, btnKeyMgr};
//
//        btnFiles.setBackground(AppTheme.INSTANCE.getBackground());
//        //AppUtils.makeEqualSize(btnFiles, btnTerminal, btnPortFwd, btnKeyMgr);
//
//        box.add(btnFiles);
//        box.add(btnTerminal);
//        box.add(btnPortFwd);
//        box.add(btnKeyMgr);
//        box.add(Box.createHorizontalGlue());
//
//        box.setBorder(new MatteBorder(1, 0, 0, 0, AppTheme.INSTANCE.getButtonBorderColor()));
//        return box;
//    }

    private JPanel createPasswordPanel() {
        var lbl = new JLabel("Unlock with master password");

        txtPassword = new JPasswordField(10);
        txtPassword.setEchoChar('*');

        btnPassword = new JButton("Unlock");
        btnPassword.addActionListener(e -> {
            ((CardLayout) this.getLayout()).show(this, "PROGRESS");
            AppUtils.runAsync(() -> {
                try {
                    AppContext.loadSessionTree(new String(txtPassword.getPassword()));
                    SwingUtilities.invokeLater(this::initContent);
                } catch (AuthenticationException ex) {
                    SwingUtilities.invokeLater(() -> {
                        ((CardLayout) this.getLayout()).show(this, "PASSWORD");
                    });
                }
            });
        });

        var hbox2 = Box.createHorizontalBox();
        hbox2.add(lbl);
        hbox2.add(Box.createRigidArea(new Dimension(40, 10)));

        var hbox1 = Box.createHorizontalBox();
        hbox1.add(Box.createHorizontalGlue());
        hbox1.add(btnPassword);

        var box = Box.createVerticalBox();
        box.add(hbox2);
        box.add(Box.createRigidArea(new Dimension(5, 5)));
        box.add(txtPassword);
        box.add(Box.createRigidArea(new Dimension(5, 5)));
        box.add(hbox1);

        var panel = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        panel.add(box, gc);

        return panel;
    }

    private JPanel createProgressPanel() {
        var panel = new JPanel(new GridBagLayout());
        var gc = new GridBagConstraints();
        var prg = new JProgressBar();
        prg.setPreferredSize(new Dimension(200, 5));
        prg.setIndeterminate(true);
        panel.add(prg, gc);
        return panel;
    }

    class BottomTabItem extends JPanel {
        private Border b1 = new EmptyBorder(5, 15, 5, 15);
        private Border b2 = new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppTheme.INSTANCE.getSelectionColor()),
                new EmptyBorder(4, 15, 5, 15));

        public BottomTabItem(IconCode iconCode, String text, ActionListener a) {
            var layout = new BoxLayout(this, BoxLayout.X_AXIS);
            setLayout(layout);
            var lbl1 = new JLabel(text);
            var iconLbl = new JLabel();
            iconLbl.setHorizontalAlignment(JLabel.CENTER);
            iconLbl.setForeground(AppTheme.INSTANCE.getDarkForeground());
            iconLbl.setFont(IconFont.getSharedInstance().getIconFont(18));
            iconLbl.setText(iconCode.getValue());

            this.add(iconLbl);
            this.add(Box.createRigidArea(new Dimension(10, 0)));
            this.add(lbl1);

            this.setBorder(b1);
            this.setBackground(AppTheme.INSTANCE.getDarkControlBackground());

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    a.actionPerformed(
                            new ActionEvent(BottomTabItem.this, 0, "BottomTabItem.Selection"));
                }
            });
        }

        public void setSelected(boolean selected) {
            setBorder(selected ? b2 : b1);
            setBackground(selected ?
                    AppTheme.INSTANCE.getBackground() :
                    AppTheme.INSTANCE.getDarkControlBackground());
            revalidate();
            repaint();
        }
    }
}
