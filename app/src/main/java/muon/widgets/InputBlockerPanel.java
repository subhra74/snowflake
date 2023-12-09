package muon.widgets;

import muon.dto.session.SessionInfo;
import muon.service.InputBlocker;
import muon.styles.AppTheme;
import muon.util.IconCode;
import muon.util.IconFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InputBlockerPanel extends JPanel implements InputBlocker {
    private JTextArea txtBanner;
    private JPanel connectionProgressPanel;
    private CardLayout cardLayout;
    private Lock lock;
    private Condition signal;
    private InteractivePromptPanel interactivePromptPanel;
    private AtomicBoolean firstAttempt = new AtomicBoolean(true);

    public InputBlockerPanel(ActionListener retryCallback) {
        super(new BorderLayout());
        lock = new ReentrantLock();
        signal = lock.newCondition();

        createConnectionProgressPanel(retryCallback);

        addMouseListener(new MouseAdapter() {
        });
        addMouseMotionListener(new MouseAdapter() {
        });
        addKeyListener(new KeyAdapter() {
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });
        setFocusTraversalKeysEnabled(false);
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void showConnectionInProgress() {
        SwingUtilities.invokeLater(() -> {
            setOpaque(true);
            connectionProgressPanel.setVisible(true);
            cardLayout.show(connectionProgressPanel, "ProgressPanel");
        });
    }

    @Override
    public void showRetryOption() {
        SwingUtilities.invokeLater(() -> {
            setOpaque(true);
            connectionProgressPanel.setVisible(true);
            cardLayout.show(connectionProgressPanel, "RetryPanel");
        });
    }

    public void blockInput() {
        this.connectionProgressPanel.setVisible(false);
        this.setVisible(true);
    }

    public void unblockInput() {
        this.connectionProgressPanel.setVisible(false);
        this.setVisible(false);
        this.setOpaque(false);
    }

    @Override
    public String[] getUserInput(String text1, String text2, String[] prompt, boolean[] echo) {
        SwingUtilities.invokeLater(() -> {
            interactivePromptPanel.showPrompt(text2 + "@" + text1, prompt, echo);
            cardLayout.show(connectionProgressPanel, "PasswordPanel");
            revalidate();
            repaint();
        });
        await();
        var inputs = interactivePromptPanel.getInputs();
        return inputs.toArray(new String[inputs.size()]);
    }

    @Override
    public String getPassword(String host, String user) {
        return getUserInput(host, user, new String[]{"Password"}, new boolean[]{true})[0];
    }

    @Override
    public void showBanner(String message) {
        SwingUtilities.invokeLater(() -> {
            txtBanner.setText(message);
            cardLayout.show(connectionProgressPanel, "BannerPanel");
            txtBanner.setCaretPosition(0);
        });
        await();
    }

    @Override
    public void showError() {
        SwingUtilities.invokeLater(() -> {
            connectionProgressPanel.setVisible(true);
            cardLayout.show(connectionProgressPanel, "ErrorPanel");
        });
    }

    private void createConnectionProgressPanel(ActionListener retryCallback) {
        cardLayout = new CardLayout();
        interactivePromptPanel = createPasswordPanel();
        connectionProgressPanel = new JPanel(cardLayout);
        connectionProgressPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        connectionProgressPanel.add("ProgressPanel", createProgressPanel());
        connectionProgressPanel.add("BannerPanel", createBannerPanel());
        connectionProgressPanel.add("RetryPanel", createRetryPanel(retryCallback));
        connectionProgressPanel.add("PasswordPanel", interactivePromptPanel);
        connectionProgressPanel.add("ErrorPanel", createErrorPanel());

        //var gc = new GridBagConstraints();
        //add(connectionProgressPanel, gc);
        add(connectionProgressPanel);
    }

    private Container createProgressPanel() {
        var label = new JLabel("Please wait");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        var prg = new JProgressBar();
        prg.setAlignmentX(Component.CENTER_ALIGNMENT);
        prg.setPreferredSize(new Dimension(200, 5));
        prg.setMaximumSize(new Dimension(200, 5));
        prg.setIndeterminate(true);

        var vbox = Box.createVerticalBox();
        vbox.add(Box.createVerticalGlue());
        vbox.add(label);
        vbox.add(prg);
        vbox.add(Box.createVerticalGlue());

        return vbox;
    }

    private JPanel createBannerPanel() {
        txtBanner = new JTextArea();
        txtBanner.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtBanner.setEditable(false);
        var jsp = new JScrollPane(txtBanner);
        jsp.setBorder(new MatteBorder(0, 0, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()));
        jsp.setBackground(getBackground());
        //jsp.setBorder(new LineBorder(AppTheme.INSTANCE.getButtonBorderColor(), 0));

        var button = new JButton("Continue");
        button.addActionListener(e -> {
            cardLayout.show(connectionProgressPanel, "ProgressPanel");
            signal();
        });

        var bottom = Box.createHorizontalBox();
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottom.add(Box.createHorizontalGlue());
        bottom.add(button);

        var panel = new JPanel(new BorderLayout());
        panel.add(jsp);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private Container createRetryPanel(ActionListener retryCallback) {
        var label = new JLabel();
        label.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        label.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        var lblError = new JLabel("Unable to connect");
        lblError.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        var button = new JButton("Try again");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(retryCallback);

        var vbox = Box.createVerticalBox();
        vbox.add(Box.createVerticalGlue());
        vbox.add(label);
        vbox.add(lblError);
        vbox.add(button);
        vbox.add(Box.createVerticalGlue());

        return vbox;
    }

    private Container createErrorPanel() {
        var label = new JLabel();
        label.setFont(IconFont.getSharedInstance().getIconFont(48.0f));
        label.setText(IconCode.RI_ACCOUNT_ALERT_FILL.getValue());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        var lblError = new JLabel("Operation failed");
        lblError.setBorder(new EmptyBorder(10, 10, 10, 10));
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        var button = new JButton("OK");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            this.unblockInput();
        });

        var vbox = Box.createVerticalBox();
        vbox.add(Box.createVerticalGlue());
        vbox.add(label);
        vbox.add(lblError);
        vbox.add(button);
        vbox.add(Box.createVerticalGlue());

        return vbox;
    }

    private InteractivePromptPanel createPasswordPanel() {
        return new InteractivePromptPanel(e -> {
            cardLayout.show(connectionProgressPanel, "ProgressPanel");
            signal();
        });
    }

    private void signal() {
        try {
            System.out.println("Acquire lock before signal: " + Thread.currentThread());
            lock.lock();
            System.out.println("Acquired lock, signalling: " + Thread.currentThread());
            signal.signalAll(); //unblock invoking thread
        } finally {
            lock.unlock();
        }
    }

    private void await() {
        try {
            lock.lock();
            System.out.println("Waiting for signal: " + Thread.currentThread());
            signal.await(); //block current thread until signalled
            System.out.println("Singal received: " + Thread.currentThread());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        signal();
    }
}
