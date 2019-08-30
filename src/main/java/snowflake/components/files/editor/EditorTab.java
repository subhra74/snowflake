package snowflake.components.files.editor;

import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.*;
import snowflake.App;
import snowflake.common.FileInfo;
import snowflake.common.FileSystem;
import snowflake.components.newsession.SessionInfo;
import snowflake.utils.PathUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

public class EditorTab extends JPanel implements SearchListener {
    private FileInfo info;
    private RSyntaxTextArea textArea;
    private RTextScrollPane sp;
    private JComboBox<String> cmbSyntax;
    private String localFile;
    private boolean hasChanges;
    private JPanel replaceToolBar;
    private boolean replaceToolBarVisible = false;

    public EditorTab(FileInfo info, String text, String localFile) {
        super(new BorderLayout());
        this.info = info;
        this.localFile = localFile;
        this.textArea = new RSyntaxTextArea();
        this.textArea.setText(text);
        if (text.length() > 0) {
            this.textArea.setCaretPosition(0);
        }
        this.sp = new RTextScrollPane(textArea);
        Gutter gutter = this.sp.getGutter();
        gutter.setBorder(new Gutter.GutterBorder(0, 0, 0, 0));
        add(sp);

        replaceToolBar = new JPanel(new BorderLayout());
        replaceToolBar.add(new ReplaceToolBar(this));

        JButton closeToolbar = new JButton();
        closeToolbar.setFont(App.getFontAwesomeFont());
        closeToolbar.setText("\uf00d");
        closeToolbar.addActionListener(e -> {
            this.remove(replaceToolBar);
            this.revalidate();
            this.repaint();
            replaceToolBarVisible = false;
            SearchContext ctx = new SearchContext();
            ctx.setMarkAll(false);
            SearchEngine.markAll(textArea, ctx);
        });

        JPanel xp = new JPanel();
        xp.add(closeToolbar);

        replaceToolBar.add(xp, BorderLayout.EAST);

        hasChanges = false;

        TokenMakerFactory factory = TokenMakerFactory.getDefaultInstance();
        Set<String> styles = factory.keySet();
        String stylesArr[] = new String[styles.size()];
        stylesArr = styles.toArray(stylesArr);
        cmbSyntax = new JComboBox<>(stylesArr);
        cmbSyntax.addItemListener(e -> {
            textArea.setSyntaxEditingStyle(e.getItem() + "");
        });

        this.textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("document change event");
                hasChanges = true;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("document change event");
                hasChanges = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("document change event");
                hasChanges = true;
            }
        });
    }

    public void saveContentsToLocal() throws Exception {
        //String path = PathUtils.combine(PathUtils.getParent(this.localFile), prefix + PathUtils.getFileName(this.localFile), File.separator);
        Files.write(Paths.get(this.localFile), textArea.getText().getBytes("utf-8"));
    }

    public void openFile() {

    }

    public FileInfo getInfo() {
        return info;
    }

    public String getLocalFile() {
        return localFile;
    }


    public boolean hasUnsavedChanges() {
        return hasChanges;
    }

    public void setHasChanges(boolean value) {
        this.hasChanges = value;
    }

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result = null;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                // result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null,
                        result.getCount() + " occurrences replaced.");
                break;
        }
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();
    }

    public void openFindReplace() {
        if (!replaceToolBarVisible) {
            this.add(replaceToolBar, BorderLayout.SOUTH);
            replaceToolBarVisible = true;
            this.revalidate();
            this.repaint();
        }
    }

    public void setText(String content) {
        this.textArea.setText(content);
        this.hasChanges = false;
        if (content.length() > 0) {
            this.textArea.setCaretPosition(0);
        }
    }

    public JComboBox<String> getCmbSyntax() {
        return cmbSyntax;
    }
}
