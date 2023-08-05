package muon.app.ui.components.session;

import java.util.*;

public class SessionFolder extends NamedItem{
    private List<SessionFolder> folders = new ArrayList<>();
    private List<SessionInfo> items = new ArrayList<>();

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the folders
     */
    public List<SessionFolder> getFolders() {
        return folders;
    }

    /**
     * @param folders the folders to set
     */
    public void setFolders(List<SessionFolder> folders) {
        this.folders = folders;
    }

    /**
     * @return the items
     */
    public List<SessionInfo> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<SessionInfo> items) {
        this.items = items;
    }
}

