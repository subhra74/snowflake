package muon.screens.appwin.tabs.terminal;

import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.SettingsProvider;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class CustomTerminal extends JediTermWidget {
    private boolean started = false;

    public CustomTerminal() {
        this(new CustomizedSettingsProvider());
    }

    public CustomTerminal(SettingsProvider settingsProvider) {
        super(settingsProvider);
        setFont(settingsProvider.getTerminalFont());
        getTerminal().setAutoNewLine(false);
        getTerminalPanel().setFont(settingsProvider.getTerminalFont());
        getTerminalPanel().setFocusable(true);
        setFocusable(true);

        addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }

            @Override
            public void ancestorAdded(AncestorEvent event) {
                getTerminalPanel().requestFocusInWindow();
            }
        });
    }

    @Override
    protected JScrollBar createScrollBar() {
        return new JScrollBar();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public void start() {
        started = true;
        super.start();
    }
}