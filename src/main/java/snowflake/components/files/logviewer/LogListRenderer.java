package snowflake.components.files.logviewer;

import javax.swing.*;
import java.awt.*;

public class LogListRenderer extends JLabel implements ListCellRenderer<String> {
    public LogListRenderer() {
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        setText("The quick brown fox jumped over the lazy dog");
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value);
        setBackground(isSelected ? UIManager.getColor("nimbusSelectionBackground") : Color.WHITE);
        setForeground(isSelected ? Color.WHITE : Color.BLACK);
        return this;
    }

    public void setFontSize(int fontSize) {
        setFont(getFont().deriveFont((float) fontSize));
    }
}
