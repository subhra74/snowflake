package muon.screens.sessionmgr;

import muon.model.SessionInfo;
import muon.widgets.CustomDialog;
import muon.util.AppUtils;

import javax.swing.*;
import java.awt.*;

public class SessionManager {
    public static SessionInfo showDialog(Window window) {
        var mgr = new SessionManagerPanel();
        var dlg = AppUtils.isWindows() ? new CustomDialog(window, "Session Manager")
                : new JDialog(window, "Session Manager");
        dlg.setModal(true);
        dlg.setSize(800, 600);
        dlg.setLocationRelativeTo(window);
        dlg.add(mgr);
        dlg.setVisible(true);
        return mgr.getSelectedSession();
    }
}
