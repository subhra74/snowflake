package snowflake.components.terminal.snippets;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SnippetListRenderer extends JPanel implements ListCellRenderer<SnippetItem> {
    private JLabel lblName;
    private JLabel lblCommand;

    public SnippetListRenderer() {
        super(new BorderLayout(5, 5));
        setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0,
                new Color(230, 230, 230)),
                new EmptyBorder(5, 10, 5, 10)));
        lblName = new JLabel();
        lblName.setFont(lblName.getFont().deriveFont(Font.PLAIN, 14.0f));
        lblCommand = new JLabel();
        lblCommand.setForeground(Color.DARK_GRAY);
        add(lblName);
        add(lblCommand, BorderLayout.SOUTH);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends SnippetItem> list,
                                                  SnippetItem value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        setBackground(isSelected ? new Color(3, 155, 229) : Color.WHITE);
        lblName.setForeground(isSelected ? Color.WHITE : Color.BLACK);
        lblCommand.setForeground(isSelected ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        lblName.setText(value.getName());
        lblCommand.setText(value.getCommand());
        return this;
    }
}
