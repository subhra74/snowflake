package snowflake.components.files.logviewer;

import snowflake.App;

import javax.swing.*;
import java.util.*;
import java.awt.BorderLayout;

public class LogSearchPanel extends JPanel {
    private JTextField txtSearch;
    private JCheckBox chkRegEx, chkMatchCase, chkFullWord;
    private JLabel lblResults;
    private SearchListener searchListener;
    private List<Integer> lines;
    private int index;

    public LogSearchPanel(SearchListener searchListener) {
        super(new BorderLayout());
        this.searchListener = searchListener;
        txtSearch = new JTextField(20);

        JButton btnSearch = new JButton();
        btnSearch.setFont(App.getFontAwesomeFont());
        btnSearch.setText("\uf002");
        btnSearch.addActionListener(e -> {
            this.index = 0;
            this.lines = new ArrayList<>();
            searchListener.search(txtSearch.getText(), chkRegEx.isSelected(), chkMatchCase.isSelected(), chkFullWord.isSelected());
        });

        JButton btnNext = new JButton();
        btnNext.setFont(App.getFontAwesomeFont());
        btnNext.setText("\uf107");
        btnNext.addActionListener(e -> {
            if (lines.size() < 1) return;
            if (index < lines.size() - 1) {
                index++;
            }
            searchListener.select(lines.get(index));
            this.lblResults.setText((index + 1) + "/" + lines.size());
        });

        JButton btnPrev = new JButton();
        btnPrev.setFont(App.getFontAwesomeFont());
        btnPrev.setText("\uf106");
        btnPrev.addActionListener(e -> {
            if (lines.size() < 1) return;
            if (index > 0) {
                index--;
            }
            searchListener.select(lines.get(index));
            this.lblResults.setText((index + 1) + "/" + lines.size());
        });

        chkFullWord = new JCheckBox("Match full word");
        chkMatchCase = new JCheckBox("Match case");
        chkRegEx = new JCheckBox("Regular expression");
        chkRegEx.addActionListener(e -> {
            if (chkRegEx.isSelected()) {
                chkFullWord.setSelected(false);
                chkFullWord.setEnabled(false);
            } else {
                chkFullWord.setEnabled(true);
            }
        });

        lblResults = new JLabel();

        Box b1 = Box.createHorizontalBox();
        b1.add(new JLabel("Search"));
        b1.add(txtSearch);
        b1.add(btnSearch);
        b1.add(btnPrev);
        b1.add(btnNext);
        b1.add(chkRegEx);
        b1.add(chkMatchCase);
        b1.add(chkFullWord);
        b1.add(lblResults);

        add(b1);
    }

    public void setResults(List<Integer> lines) {
        this.lines = lines;
        this.index = 0;
        this.lblResults.setText(lines.size() > 0 ? 1 + "/" + lines.size() : "0/0");
        if (lines.size() > 0) {
            searchListener.select(0);
        }
    }
}
