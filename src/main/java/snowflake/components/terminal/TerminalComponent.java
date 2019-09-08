package snowflake.components.terminal;

import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import snowflake.common.ssh.SshUserInteraction;
import snowflake.components.main.ConnectedResource;
import snowflake.components.newsession.SessionInfo;
import snowflake.components.terminal.ssh.DisposableTtyConnector;
import snowflake.components.terminal.ssh.SshTtyConnector;

import javax.swing.*;
import java.awt.*;

public class TerminalComponent extends JPanel implements ConnectedResource {
    private JRootPane rootPane;
    private JPanel contentPane;
    private JediTermWidget term;
    private DisposableTtyConnector tty;
    private String name;

    public TerminalComponent(SessionInfo info, String name) {
        setLayout(new BorderLayout());
        this.name = name;
        contentPane = new JPanel(new BorderLayout());
        rootPane = new JRootPane();
        rootPane.setContentPane(contentPane);
        add(rootPane);

        tty = new SshTtyConnector(new SshUserInteraction(info, rootPane));
        term = new CustomJediterm(new DefaultSettingsProvider());
        term.setTtyConnector(tty);
        term.start();
        contentPane.add(term);
    }

    @Override
    public String toString() {
        return "Terminal " + this.name;
    }

    @Override
    public boolean isInitiated() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return tty.isConnected();
    }

    @Override
    public void close() {
        tty.close();
    }
}
