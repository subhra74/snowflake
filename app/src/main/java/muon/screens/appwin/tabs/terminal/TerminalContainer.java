package muon.screens.appwin.tabs.terminal;

import muon.dto.session.SessionInfo;
import muon.styles.AppTheme;
import muon.util.AppUtils;
import muon.util.IconCode;
import muon.util.IconFont;
import muon.widgets.InputBlockerPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Objects;

public class TerminalContainer extends JPanel {
    private SessionInfo sessionInfo;
    private InputBlockerPanel inputBlockerPanel;
    private SshTtyConnector ttyConnector;
    private CustomTerminal customTerminal;
    private JPanel contentPanel;
    private Component notificationComp;

    public TerminalContainer(SessionInfo sessionInfo) {
        super(new CardLayout());
        this.sessionInfo = sessionInfo;
        this.inputBlockerPanel = new InputBlockerPanel(e -> {
            beginSession();
        });

        ttyConnector = new SshTtyConnector(null, this.sessionInfo, this.inputBlockerPanel);
        customTerminal = new CustomTerminal();
        customTerminal.addListener(e -> {
            System.out.println("allSessionsClosed");
            SwingUtilities.invokeLater(() -> {
                notificationComp.setVisible(true);
                revalidate();
                repaint();
            });
        });

        notificationComp = createErrorNotificationPanel();
        notificationComp.setVisible(false);
        contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.add(notificationComp, BorderLayout.NORTH);
        contentPanel.add(customTerminal);
        contentPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        this.add(inputBlockerPanel, "INPUT_BLOCKER");
        this.add(contentPanel, "TERMINAL");
    }

    private Component createErrorNotificationPanel() {
        var box = Box.createHorizontalBox();
        box.setOpaque(true);
        var lbl1 = new JLabel("Session inactive");
        lbl1.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        var lbl12 = new JLabel();
        lbl12.setFont(IconFont.getSharedInstance().getIconFont(24.0f));
        lbl12.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        lbl12.setForeground(new Color(150, 40, 40));
        var btn = new JButton("Reconnect");
        box.add(lbl12);
        box.add(Box.createRigidArea(new Dimension(10, 10)));
        box.add(lbl1);
        box.add(Box.createHorizontalGlue());
        box.add(btn);
        box.setBorder(
                new CompoundBorder(
                        new MatteBorder(0, 0, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()),
                        new EmptyBorder(5, 10, 10, 10)));
        btn.addActionListener(e -> {
            notificationComp.setVisible(false);
            revalidate();
            repaint();
            beginSession();
        });
        return box;
    }

    public void beginSession() {
        ((CardLayout) this.getLayout()).show(this, "INPUT_BLOCKER");
        AppUtils.runAsync(() -> {
            try {
                ttyConnector.start();
                SwingUtilities.invokeAndWait(() -> {
                    ((CardLayout) this.getLayout()).show(this, "TERMINAL");
                });
                customTerminal.setTtyConnector(ttyConnector);
                customTerminal.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                inputBlockerPanel.showRetryOption();
            }
        });
    }

    public void dispose() {
        if (Objects.nonNull(customTerminal)) {
            customTerminal.close();
        }
    }
}
