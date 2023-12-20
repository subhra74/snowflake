package muon.screens.dialogs;

import muon.styles.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class BannerDialog extends JDialog {
    private JTextArea txtBanner;

    public BannerDialog(Window window) {
        super(window);
        setModal(true);
        getContentPane().add(createBannerPanel());
        setTitle("Message");
        setSize(640, 480);
    }

    public void showBanner(String text) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                txtBanner.setText(text);
                setLocationRelativeTo(this.getOwner());
                setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> this.setVisible(false));
            throw new RuntimeException(e);
        }
    }

    private JPanel createBannerPanel() {
        txtBanner = new JTextArea();
        txtBanner.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtBanner.setEditable(false);
        var jsp = new JScrollPane(txtBanner);
        jsp.setBorder(new MatteBorder(0, 0, 1, 0, AppTheme.INSTANCE.getButtonBorderColor()));
        jsp.setBackground(getBackground());

        var button = new JButton("OK");
        button.addActionListener(e -> {
            setVisible(false);
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
}
