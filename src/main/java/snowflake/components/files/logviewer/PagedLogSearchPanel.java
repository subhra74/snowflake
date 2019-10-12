package snowflake.components.files.logviewer;

import com.google.common.primitives.Longs;
import snowflake.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.RandomAccessFile;

public class PagedLogSearchPanel extends JPanel {
    private JTextField txtSearch;
    //private JCheckBox chkRegEx, chkMatchCase, chkFullWord;
    private JLabel lblResults;
    private SearchListener searchListener;
    private int index;
    private RandomAccessFile raf;
    private long resultCount = 0;

    public PagedLogSearchPanel(SearchListener searchListener) {
        super(new BorderLayout());
        this.searchListener = searchListener;
        txtSearch = new JTextField(20);

        JButton btnSearch = new JButton();
        btnSearch.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnSearch.setFont(App.getFontAwesomeFont());
        btnSearch.setText("\uf002");
        btnSearch.addActionListener(e -> {
            this.index = 0;
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception ex) {
                }
                raf = null;
            }
            searchListener.search(txtSearch.getText());
        });

        JButton btnNext = new JButton();
        btnNext.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnNext.setFont(App.getFontAwesomeFont());
        btnNext.setText("\uf107");
        btnNext.addActionListener(e -> {
            if (raf == null || this.resultCount < 1) return;
            if (index < this.resultCount - 1) {
                index++;
                System.out.println("Index: " + index);
                long lineNo = getLineNumber();
                System.out.println("Line number: " + lineNo);
                if (lineNo != -1) {
                    searchListener.select(lineNo);
                    this.lblResults.setText((index + 1) + "/" + this.resultCount);
                }
            }
        });

        JButton btnPrev = new JButton();
        btnPrev.putClientProperty("Nimbus.Overrides", App.toolBarButtonSkin);
        btnPrev.setFont(App.getFontAwesomeFont());
        btnPrev.setText("\uf106");
        btnPrev.addActionListener(e -> {
            if (raf == null || this.resultCount < 1) return;
            if (index > 0) {
                index--;
                System.out.println("Index: " + index);
                long lineNo = getLineNumber();
                System.out.println("Line number: " + lineNo);
                if (lineNo != -1) {
                    searchListener.select(lineNo);
                    this.lblResults.setText((index + 1) + "/" + this.resultCount);
                }
            }
        });

        lblResults = new JLabel();

        Box b1 = Box.createHorizontalBox();
        b1.add(new JLabel("Search"));
        b1.add(Box.createHorizontalStrut(10));
        b1.add(txtSearch);
        b1.add(btnSearch);
        b1.add(btnPrev);
        b1.add(btnNext);
        b1.add(Box.createHorizontalStrut(5));
        b1.add(lblResults);

        setBorder(new EmptyBorder(3, 5, 5, 5));

        add(b1);
    }

    public void setResults(RandomAccessFile raf, long len) {
        if (len / 8 < 1) {
            return;
        }
        this.raf = raf;
        this.index = 0;
        this.resultCount = len / 8;
        System.out.println("Total items found: " + this.resultCount);
        this.lblResults.setText(resultCount > 0 ? 1 + "/" + resultCount : "0/0");
        if (resultCount > 0) {
            this.index = 0;
            searchListener.select(getLineNumber());
        }
    }

    private long getLineNumber() {
        try {
            long offset = index * 8;
            raf.seek(offset);
            byte[] b = new byte[8];
            if (raf.read(b) != 8) {
                return -1;
            }
            return Longs.fromByteArray(b) - 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void close() {
        if (raf != null) {
            try {
                raf.close();
            } catch (Exception ex) {
            }
            raf = null;
        }
    }
}

