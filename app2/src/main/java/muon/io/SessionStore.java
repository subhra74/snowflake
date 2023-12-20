package muon.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import muon.config.AppConfig;
import muon.model.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class SessionStore {

    public synchronized static SavedSessionTree load() {
        File file = Paths.get(AppConfig.CONFIG_DIR, AppConfig.SESSION_DB_FILE).toFile();
        return load(file);
    }

    public synchronized static SavedSessionTree load(File file) {
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SavedSessionTree savedSessionTree = objectMapper.readValue(file, new TypeReference<SavedSessionTree>() {
                });
                return savedSessionTree;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SessionFolder rootFolder = new SessionFolder();
        rootFolder.setName("Hosts");
        SavedSessionTree tree = new SavedSessionTree();
        tree.setFolder(rootFolder);
        return tree;
    }

    public synchronized static void save(SessionFolder folder, String lastSelectionPath) {
        File file = Paths.get(AppConfig.CONFIG_DIR, AppConfig.SESSION_DB_FILE).toFile();
        save(folder, lastSelectionPath, file);
    }

    public synchronized static void save(SessionFolder folder, String lastSelectionPath, File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SavedSessionTree tree = new SavedSessionTree();
            tree.setFolder(folder);
            tree.setLastSelection(lastSelectionPath);
            objectMapper.writeValue(file, tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void updateFavourites(String id, List<String> localFolders, List<String> remoteFolders) {
        SavedSessionTree tree = load();
        SessionFolder folder = tree.getFolder();

        updateFavourites(folder, id, localFolders, remoteFolders);
        save(folder, tree.getLastSelection());
    }

    private static boolean updateFavourites(SessionFolder folder, String id, List<String> localFolders,
                                            List<String> remoteFolders) {
        for (SessionInfo info : folder.getItems()) {
            if (info.getId().equals(id)) {
                if (remoteFolders != null) {
                    System.out.println("Remote folders saving: " + remoteFolders);
                    info.setFavouriteRemoteFolders(remoteFolders);
                }
                if (localFolders != null) {
                    System.out.println("Local folders saving: " + localFolders);
                    info.setFavouriteLocalFolders(localFolders);
                }
                return true;
            }
        }
        for (SessionFolder childFolder : folder.getFolders()) {
            if (updateFavourites(childFolder, id, localFolders, remoteFolders)) {
                return true;
            }
        }
        return false;
    }
}
