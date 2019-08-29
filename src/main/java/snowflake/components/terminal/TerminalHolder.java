package snowflake.components.terminal;

import snowflake.App;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TerminalHolder extends JPanel {
    private SessionInfo info;
    private DefaultComboBoxModel<TerminalComponent> terminals;
    private JComboBox<TerminalComponent> cmbTerminals;
    private JButton btnStartTerm,btnStopTerm;
    private CardLayout card;
    private JPanel content;

    private int c = 1;

    public TerminalHolder(SessionInfo info) {
        super(new BorderLayout());
        card = new CardLayout();
        content = new JPanel(card);
        this.info = info;
        this.terminals = new DefaultComboBoxModel<>();
        this.cmbTerminals = new JComboBox<>(terminals);
        this.cmbTerminals.addItemListener(e -> {
            int index = cmbTerminals.getSelectedIndex();
            if (index != -1) {
                TerminalComponent tc = terminals.getElementAt(index);
                card.show(content, tc.hashCode() + "");
            }
        });

        this.btnStopTerm = new JButton();
        this.btnStopTerm.setFont(App.getFontAwesomeFont());
        this.btnStopTerm.setText("\uf0c8");
        this.btnStopTerm.setMargin(new Insets(3,3,3,3));

        this.btnStartTerm = new JButton();
        this.btnStartTerm.setFont(App.getFontAwesomeFont());
        this.btnStartTerm.setText("\uf0fe");
        this.btnStartTerm.setMargin(new Insets(3,3,3,3));
        this.btnStartTerm.addActionListener(e -> {
            createNewTerminal();
        });

        JToolBar b1=new JToolBar();
        b1.setBorder(new EmptyBorder(1,2,4,2));
        b1.setFloatable(false);
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
//        JLabel lbl=new JLabel();
//        lbl.setFont(App.getFontAwesomeFont());
//        lbl.setText("\uf120");
//        b1.add(lbl);
        JLabel lblTerminal=new JLabel("Terminal");
        lblTerminal.setFont(new Font(Font.DIALOG,Font.PLAIN,14));
        b1.add(lblTerminal);
        b1.add(Box.createHorizontalGlue());
        b1.add(cmbTerminals);
        b1.add(btnStartTerm);
        b1.add(btnStopTerm);
        this.add(b1, BorderLayout.NORTH);
        this.add(content);

        createNewTerminal();
    }

    public void createNewTerminal() {
        TerminalComponent tc = new TerminalComponent(info, c + "");
        c++;
        content.add(tc, tc.hashCode()+"");
        terminals.addElement(tc);
    }

    public void removeTerminal() {

    }
}
