package snowflake.common.ssh;

import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.util.List;

public class SshModalUserInteraction extends AbstractUserInteraction {
    public SshModalUserInteraction(SessionInfo info) {
        super(info);
    }

    protected boolean showModal(List<JComponent> components, boolean yesNo) {
        return JOptionPane.showOptionDialog(null, components.toArray(), "Action required",
                yesNo ? JOptionPane.OK_CANCEL_OPTION : JOptionPane.OK_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION;
    }
}
