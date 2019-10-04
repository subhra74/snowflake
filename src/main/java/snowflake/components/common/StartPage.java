package snowflake.components.common;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class StartPage extends JPanel {
    private JLabel lblTitle;
    private JLabel lblDescription;
    private JButton btnStart;

    public StartPage(String title, String description, String startText, Consumer<?> callback) {
        super(new GridLayout(2, 1));

        lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(80, 80, 80));
        lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 32));
        lblTitle.setAlignmentX(Box.CENTER_ALIGNMENT);

        lblDescription = new JLabel(description);
        lblDescription.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        lblDescription.setForeground(new Color(40, 40, 40));
        lblDescription.setForeground(new Color(120, 120, 120));
        lblDescription.setAlignmentX(Box.CENTER_ALIGNMENT);

        btnStart = new JButton(startText);
        btnStart.setForeground(new Color(80, 80, 80));
        btnStart.setMargin(new Insets(8, 20, 8, 20));
        btnStart.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        btnStart.setAlignmentX(Box.CENTER_ALIGNMENT);
        btnStart.addActionListener(e -> {
            callback.accept(null);
        });

        Box topBox = Box.createVerticalBox();
        topBox.add(Box.createVerticalGlue());
        topBox.add(lblTitle);
        topBox.add(lblDescription);
        topBox.add(Box.createVerticalStrut(20));
        topBox.add(btnStart);
        topBox.add(Box.createVerticalGlue());

        add(topBox);

        JLabel bottomLabel = new JLabel();
        bottomLabel.setOpaque(true);
        bottomLabel.setBackground(new Color(240, 240, 240));
        add(bottomLabel);
    }
}
