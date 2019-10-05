package snowflake.components.files.browser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SessionDropDownRenderer extends JPanel implements ListCellRenderer<Object> {
    private JLabel lblHost, lblDirectory;
    private Color hostColor = new Color(60, 60, 60), pathColor = new Color(120, 120, 120);

    public SessionDropDownRenderer() {
        super(new BorderLayout(5, 5));
        lblHost = new JLabel();
        lblHost.setForeground(hostColor);
        lblDirectory = new JLabel();
        lblDirectory.setForeground(pathColor);
        add(lblHost, BorderLayout.WEST);
        add(lblDirectory, BorderLayout.CENTER);
        setBorder(new EmptyBorder(3, 5, 3, 5));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setBackground(isSelected || cellHasFocus ? UIManager.getColor("nimbusSelectionBackground") : Color.WHITE);
        lblHost.setForeground(isSelected || cellHasFocus ? Color.WHITE : hostColor);
        lblDirectory.setForeground(isSelected || cellHasFocus ? Color.WHITE : pathColor);
        if (value instanceof String) {
            lblHost.setText(value.toString());
            lblDirectory.setText("");
        } else if (value instanceof AbstractFileBrowserView) {
            AbstractFileBrowserView abstractFileBrowserView = (AbstractFileBrowserView) value;
            lblHost.setText(abstractFileBrowserView.getHostText());
            lblDirectory.setText(abstractFileBrowserView.getPathText());
        }
        return this;
    }
}
