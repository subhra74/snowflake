package muon.screens.appwin;

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
import java.util.concurrent.atomic.AtomicBoolean;

public class MainContainer extends JPanel implements AppWin {
    private JPanel contentPanel;
    private DualPaneFileBrowser dualPaneFileBrowser;
    private TabbedTerminal tabbedTerminal;
    private AtomicBoolean init = new AtomicBoolean(false);
    private BottomTabItem tabs[];

    public MainContainer() {
        super(new BorderLayout());

        dualPaneFileBrowser = new DualPaneFileBrowser(this);
        tabbedTerminal = new TabbedTerminal();

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(dualPaneFileBrowser, "FILES");
        contentPanel.add(tabbedTerminal, "TERM");

        add(contentPanel);
        add(createBottomTabs(), BorderLayout.SOUTH);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!init.get()) {
                    init.set(true);
                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, "FILES");
                    revalidate();
                    repaint();
                    dualPaneFileBrowser.init();
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

    private Container createBottomTabs() {
        String[] str = new String[]{"FILES", "TERM"};
        ActionListener tabSelection = e -> {
            for (var i = 0; i < tabs.length; i++) {
                tabs[i].setSelected(e.getSource() == tabs[i]);
                if (e.getSource() == tabs[i] && i < str.length) {
                    ((CardLayout) contentPanel.getLayout()).show(contentPanel, str[i]);
                }
            }
        };

        var box = Box.createHorizontalBox();
        box.setOpaque(true);
        box.setBackground(AppTheme.INSTANCE.getDarkControlBackground());

        var btnFiles = new BottomTabItem(IconCode.RI_FOLDER_FILL, "File Browser", tabSelection);
        var btnTerminal = new BottomTabItem(IconCode.RI_TERMINAL_BOX_FILL, "Remote Terminal", tabSelection);
        var btnPortFwd = new BottomTabItem(IconCode.RI_SWAP_BOX_FILL, "Port Forwarding", tabSelection);
        var btnKeyMgr = new BottomTabItem(IconCode.RI_KEY_FILL, "Key Manager", tabSelection);

        tabs = new BottomTabItem[]{btnFiles, btnTerminal, btnPortFwd, btnKeyMgr};

        btnFiles.setBackground(AppTheme.INSTANCE.getBackground());
        AppUtils.makeEqualSize(btnFiles, btnTerminal, btnPortFwd, btnKeyMgr);

        box.add(btnFiles);
        box.add(btnTerminal);
        box.add(btnPortFwd);
        box.add(btnKeyMgr);
        box.add(Box.createHorizontalGlue());

        box.setBorder(new MatteBorder(1, 0, 0, 0, AppTheme.INSTANCE.getButtonBorderColor()));
        return box;
    }

    class BottomTabItem extends JPanel {
        private Border b1 = new EmptyBorder(5, 15, 5, 15);
        private Border b2 = new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, AppTheme.INSTANCE.getSelectionColor()),
                new EmptyBorder(4, 15, 5, 15));

        public BottomTabItem(IconCode iconCode, String text, ActionListener a) {
            super(new BorderLayout(10, 10));
            var lbl1 = new JLabel(text);
            var iconLbl = new JLabel();
            iconLbl.setHorizontalAlignment(JLabel.CENTER);
            iconLbl.setForeground(AppTheme.INSTANCE.getDarkForeground());
            iconLbl.setFont(IconFont.getSharedInstance().getIconFont(18));
            iconLbl.setText(iconCode.getValue());

            this.add(iconLbl, BorderLayout.WEST);
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
            setBackground(selected ? AppTheme.INSTANCE.getBackground() : AppTheme.INSTANCE.getDarkControlBackground());
            revalidate();
            repaint();
        }
    }
}
