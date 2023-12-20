package muon.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.util.function.Consumer;

public class DocumentChangeAdapter implements DocumentListener {
    private JTextComponent textComponent;
    private Consumer<String>callback;

    public DocumentChangeAdapter(JTextComponent textComponent, Consumer<String> callback) {
        this.textComponent = textComponent;
        this.callback = callback;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        callback.accept(textComponent.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        callback.accept(textComponent.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        callback.accept(textComponent.getText());
    }
}
