package muon;

import muon.dto.session.SavedSessionTree;
import muon.exceptions.AuthenticationException;
import muon.service.SessionStore;

public class AppContext {
    public static SavedSessionTree sessionTree;
    public static String password = "";

    public static void loadSessionTree() throws AuthenticationException {
        sessionTree = SessionStore.load(password);
    }

    public static void loadSessionTree(String password) throws AuthenticationException {
        sessionTree = SessionStore.load(password);
    }

    public static void saveSession() throws Exception {
        SessionStore.save(sessionTree, password);
    }

    public static SavedSessionTree getSessionTree() {
        return sessionTree;
    }

    public static void setSessionTree(SavedSessionTree sessionTree) {
        AppContext.sessionTree = sessionTree;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AppContext.password = password;
    }
}
