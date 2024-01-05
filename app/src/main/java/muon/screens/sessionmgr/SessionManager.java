package muon.screens.sessionmgr;

import muon.AppContext;
import muon.dto.session.SavedSessionTree;
import muon.dto.session.SessionInfo;
import muon.widgets.CustomDialog;
import muon.util.AppUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SessionManager {
    public static SessionInfo showDialog(Window window) {
        var mgr = new SessionManagerPanel();
        var dlg = new JDialog(window, "Session Manager");
        dlg.setModal(true);
        dlg.setSize(800, 600);
        dlg.setLocationRelativeTo(window);
        dlg.add(mgr);
        dlg.setVisible(true);
        mgr.saveSessionUpdates();
        return mgr.getSelectedSession();
    }
}
