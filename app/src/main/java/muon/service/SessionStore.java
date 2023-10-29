package muon.service;

import muon.config.AppConfig;
import muon.dto.session.SavedSessionTree;
import muon.dto.session.SessionFolder;
import muon.dto.session.SessionInfo;
import muon.exceptions.AuthenticationException;
import muon.util.AESUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

public class SessionStore {

    public synchronized static SavedSessionTree load(String password) throws AuthenticationException {
        File file = Paths.get(AppConfig.CONFIG_DIR, AppConfig.SESSION_DB_FILE).toFile();
        return load(file, password);
    }

    public synchronized static SavedSessionTree load(File file, String password) throws AuthenticationException {
        if (file.exists()) {
            try {
                return AESUtils.decrypt(file, password);
            } catch (Exception e) {
                if (e instanceof AuthenticationException) {
                    throw (AuthenticationException) e;
                }
                e.printStackTrace();
            }
        }
        var firstSelectionId = UUID.randomUUID().toString();
        SessionFolder rootFolder = new SessionFolder();
        rootFolder.setName("Hosts");
        rootFolder.setId(UUID.randomUUID().toString());

        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setName("New host");
        sessionInfo.setId(firstSelectionId);
        rootFolder.setItems(List.of(sessionInfo));

        SavedSessionTree tree = new SavedSessionTree();
        tree.setFolder(rootFolder);
        tree.setLastSelectionId(firstSelectionId);
        return tree;
    }

    public synchronized static void save(SavedSessionTree tree, String password) throws Exception {
        File file = Paths.get(AppConfig.CONFIG_DIR, AppConfig.SESSION_DB_FILE).toFile();
        save(tree, file, password);
    }

    public synchronized static void save(SavedSessionTree tree, File file, String password) throws Exception {
        AESUtils.encrypt(file, password, tree);
    }

//    public synchronized static void updateFavourites(String id, List<String> localFolders, List<String> remoteFolders) {
//        SavedSessionTree tree = load();
//        SessionFolder folder = tree.getFolder();
//
//        updateFavourites(folder, id, localFolders, remoteFolders);
//        save(folder, tree.getLastSelection());
//    }
//
//    private static boolean updateFavourites(SessionFolder folder, String id, List<String> localFolders,
//                                            List<String> remoteFolders) {
//        for (SessionInfo info : folder.getItems()) {
//            if (info.getId().equals(id)) {
//                if (remoteFolders != null) {
//                    System.out.println("Remote folders saving: " + remoteFolders);
//                    info.setFavouriteRemoteFolders(remoteFolders);
//                }
//                if (localFolders != null) {
//                    System.out.println("Local folders saving: " + localFolders);
//                    info.setFavouriteLocalFolders(localFolders);
//                }
//                return true;
//            }
//        }
//        for (SessionFolder childFolder : folder.getFolders()) {
//            if (updateFavourites(childFolder, id, localFolders, remoteFolders)) {
//                return true;
//            }
//        }
//        return false;
//    }
}
