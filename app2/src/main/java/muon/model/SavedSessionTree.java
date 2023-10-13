package muon.model;

import muon.model.SessionFolder;

public class SavedSessionTree {
    private SessionFolder folder;
    private String lastSelection;

    public synchronized SessionFolder getFolder() {
        return folder;
    }

    public synchronized void setFolder(SessionFolder folder) {
        this.folder = folder;
    }

    public synchronized String getLastSelection() {
        return lastSelection;
    }

    public synchronized void setLastSelection(String lastSelection) {
        this.lastSelection = lastSelection;
    }
}
