package snowflake.components.files.editor;

import snowflake.App;

import javax.swing.*;
import java.awt.*;

public class TabHeader extends JPanel {
    private JLabel lblTitle, btnClose;

    public TabHeader(String title) {
        super(new BorderLayout(10,10));
        setOpaque(false);
        lblTitle = new JLabel(title);
        add(lblTitle);
        btnClose = new JLabel();
        btnClose.setFont(App.getFontAwesomeFont());
        btnClose.setText("\uf00d");
        add(btnClose, BorderLayout.EAST);
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public JLabel getBtnClose() {
        return btnClose;
    }

    public void setTitle(String text) {
        lblTitle.setText(text);
    }
}
