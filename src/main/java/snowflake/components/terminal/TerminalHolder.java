package snowflake.components.terminal;

import snowflake.App;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.terminal.snippets.SnippetPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TerminalHolder extends JPanel implements AutoCloseable {
    private SessionInfo info;
    private DefaultComboBoxModel<TerminalComponent> terminals;
    private JComboBox<TerminalComponent> cmbTerminals;
    private JButton btnStartTerm, btnStopTerm, btnSnippets;
    private CardLayout card;
    private JPanel content;
    private ExecutorService threadPool = Executors.newFixedThreadPool(1);
    private JPopupMenu snippetPopupMenu;
    private SnippetPanel snippetPanel;

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

        Dimension dimension = new Dimension(200, this.cmbTerminals.getPreferredSize().height);
        this.cmbTerminals.setPreferredSize(dimension);
        this.cmbTerminals.setMaximumSize(dimension);
        this.cmbTerminals.setMinimumSize(dimension);


        this.btnStopTerm = new JButton();
        this.btnStopTerm.setText("Close");
//        this.btnStopTerm.setFont(App.getFontAwesomeFont());
//        this.btnStopTerm.setText("\uf0c8");
        this.btnStopTerm.setMargin(new Insets(3, 3, 3, 3));
        this.btnStopTerm.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        this.btnStopTerm.addActionListener(e -> {
            removeTerminal();
        });

        this.btnSnippets = new JButton();
        this.btnSnippets.setText("Snippets");
//        this.btnStopTerm.setFont(App.getFontAwesomeFont());
//        this.btnStopTerm.setText("\uf0c8");
        this.btnSnippets.setMargin(new Insets(3, 3, 3, 3));
        this.btnSnippets.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        this.btnSnippets.addActionListener(e -> {
            showSnippets();
        });

        this.btnStartTerm = new JButton();
        this.btnStartTerm.setText("New terminal");
//        this.btnStartTerm.setFont(App.getFontAwesomeFont());
//        this.btnStartTerm.setText("\uf0fe");
        this.btnStartTerm.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        this.btnStartTerm.setMargin(new Insets(3, 3, 3, 3));
        this.btnStartTerm.addActionListener(e -> {
            createNewTerminal();
        });

        Box b1 = Box.createHorizontalBox();
//        b1.setOpaque(true);
//        b1.setBackground(new Color(250, 250, 250));
        //b1.setBorder(new EmptyBorder(1, 2, 4, 2));
        b1.add(Box.createRigidArea(new Dimension(10, 10)));
//        JLabel lbl=new JLabel();
//        lbl.setFont(App.getFontAwesomeFont());
//        lbl.setText("\uf120");
//        b1.add(lbl);
        JLabel lblTerminal = new JLabel("Terminal");
        lblTerminal.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        b1.add(lblTerminal);
        b1.add(Box.createHorizontalGlue());
        b1.add(cmbTerminals);
        b1.add(btnStartTerm);
        b1.add(btnStopTerm);
        b1.add(btnSnippets);
        this.add(b1, BorderLayout.NORTH);
        this.add(content);

        snippetPanel = new SnippetPanel(e -> {
            int index = cmbTerminals.getSelectedIndex();
            if (index == -1) return;
            TerminalComponent tc = terminals.getElementAt(index);
            tc.sendCommand(e + "\n");
        }, e -> {
            this.snippetPopupMenu.setVisible(false);
        });

        snippetPopupMenu = new JPopupMenu();
        snippetPopupMenu.add(snippetPanel);

        createNewTerminal();
    }

    private void showSnippets() {
        this.snippetPanel.loadSnippets();
        this.snippetPopupMenu.pack();
        this.snippetPopupMenu.setInvoker(this.btnSnippets);
        this.snippetPopupMenu.show(this.btnSnippets,
                this.btnSnippets.getWidth() - this.snippetPopupMenu.getPreferredSize().width,
                this.btnSnippets.getHeight());
    }

    public void createNewTerminal(String command) {
        int count = terminals.getSize();
        TerminalComponent tc = new TerminalComponent(info, c + "", command);
        c++;
        content.add(tc, tc.hashCode() + "");
        terminals.addElement(tc);
        cmbTerminals.setSelectedIndex(count);
    }

    public void createNewTerminal() {
        int count = terminals.getSize();
        TerminalComponent tc = new TerminalComponent(info, c + "", null);
        c++;
        content.add(tc, tc.hashCode() + "");
        terminals.addElement(tc);
        cmbTerminals.setSelectedIndex(count);
    }

    public void removeTerminal() {
        if (App.getGlobalSettings().isConfirmBeforeTerminalClosing() &&
                JOptionPane.showConfirmDialog(null,
                        "Are you sure about closing this terminal?") != JOptionPane.YES_OPTION) {
            return;
        }
        int index = cmbTerminals.getSelectedIndex();
        if (index == -1) return;
        TerminalComponent tc = terminals.getElementAt(index);
        terminals.removeElement(tc);
        content.remove(tc);
        c--;
        threadPool.submit(() -> tc.close());
        revalidate();
        repaint();
    }

    public void close() {
        for (int i = 0; i < terminals.getSize(); i++) {
            TerminalComponent tc = terminals.getElementAt(i);
            terminals.removeElement(tc);
            threadPool.submit(() -> tc.close());
        }
        revalidate();
        repaint();
    }
}
