package snowflake.components.files.editor;

import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.*;
import snowflake.App;
import snowflake.common.FileInfo;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
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
    private GoToDialog goToDialog;
    private boolean wrapText = false;
    private TextEditor editor;

    public EditorTab(FileInfo info, String text, String localFile, TextEditor editor) {
        super(new BorderLayout());
        this.info = info;
        this.localFile = localFile;
        this.textArea = new RSyntaxTextArea();
        this.editor = editor;
        this.textArea.setText(text);
        setBorder(new LineBorder(new Color(240, 240, 240), 1));
        if (text.length() > 0) {
            this.textArea.setCaretPosition(0);
        }
        this.goToDialog = new GoToDialog((JFrame) SwingUtilities.windowForComponent(this));
        this.sp = new RTextScrollPane(textArea);
        this.sp.setBorder(null);
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
                setHasChanges(true);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("document change event");
                setHasChanges(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("document change event");
                setHasChanges(true);
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
        editor.hasUnsavedChanges(value);
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
        setHasChanges(false);
        if (content.length() > 0) {
            this.textArea.setCaretPosition(0);
        }
    }

    public JComboBox<String> getCmbSyntax() {
        return cmbSyntax;
    }

    public void cutText() {
        textArea.cut();
    }

    public void copyText() {
        textArea.copy();
    }

    public void pasteText() {
        textArea.paste();
    }

    public void gotoLine() {
        goToDialog.setMaxLineNumberAllowed(textArea.getLineCount());
        goToDialog.setVisible(true);
        int line = goToDialog.getLineNumber();
        if (line > 0) {
            try {
                textArea.setCaretPosition(
                        textArea.getLineStartOffset(line - 1));
            } catch (BadLocationException ble) { // Never happens
                UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                ble.printStackTrace();
            }
        }
    }

    public boolean getWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean value) {
        this.textArea.setLineWrap(value);
        this.textArea.setWrapStyleWord(value);
        this.wrapText = true;
    }

}
