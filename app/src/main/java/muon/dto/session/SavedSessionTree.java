package muon.dto.session;

import java.io.Serializable;

public class SavedSessionTree implements Serializable {
    private SessionFolder folder;
    private String lastSelectionId;

    public synchronized SessionFolder getFolder() {
        return folder;
    }

    public synchronized void setFolder(SessionFolder folder) {
        this.folder = folder;
    }

    public synchronized String getLastSelectionId() {
        return lastSelectionId;
    }

    public synchronized void setLastSelectionId(String lastSelectionId) {
        this.lastSelectionId = lastSelectionId;
    }
}
