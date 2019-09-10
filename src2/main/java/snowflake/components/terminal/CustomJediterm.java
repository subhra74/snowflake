package snowflake.components.terminal;

import java.awt.*;

import javax.swing.JScrollBar;

import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.SettingsProvider;

public class CustomJediterm extends JediTermWidget {
    private boolean started = false;

    public CustomJediterm(SettingsProvider settingsProvider) {
        super(settingsProvider);
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
    public Dimension getPreferredSize() {
        // TODO Auto-generated method stub
        return super.getPreferredSize();
    }

    @Override
    public void start() {
        started = true;
        super.start();
    }


}
