package muonssh.app.ui.components.session;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;

import muonssh.app.App;
import muonssh.app.PasswordStore;

import javax.swing.tree.*;
import java.util.*;
import java.io.*;
import java.nio.file.Paths;

public class SessionStore {

	public synchronized static SavedSessionTree load() {
		File file = Paths.get(App.CONFIG_DIR, App.SESSION_DB_FILE).toFile();
		return load(file);
	}

	public synchronized static SavedSessionTree load(File file) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			SavedSessionTree savedSessionTree = objectMapper.readValue(file, new TypeReference<SavedSessionTree>() {
			});
			try {
				System.out.println("Loading passwords...");
				PasswordStore.getSharedInstance().populatePassword(savedSessionTree);
				System.out.println("Loading passwords... done");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return savedSessionTree;
		} catch (IOException e) {
			e.printStackTrace();
			SessionFolder rootFolder = new SessionFolder();
			rootFolder.setName("My sites");
			SavedSessionTree tree = new SavedSessionTree();
			tree.setFolder(rootFolder);
			return tree;
		}
	}

	public synchronized static void save(SessionFolder folder, String lastSelectionPath) {
		File file = Paths.get(App.CONFIG_DIR, App.SESSION_DB_FILE).toFile();
		save(folder, lastSelectionPath, file);
	}

	public synchronized static void save(SessionFolder folder, String lastSelectionPath, File file) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			SavedSessionTree tree = new SavedSessionTree();
			tree.setFolder(folder);
			tree.setLastSelection(lastSelectionPath);
			objectMapper.writeValue(file, tree);
			try {
				PasswordStore.getSharedInstance().savePasswords(tree);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized SessionFolder convertModelFromTree(DefaultMutableTreeNode node) {
		SessionFolder folder = new SessionFolder();
		folder.setName(node.getUserObject() + "");
		folder.setId(((NamedItem)node.getUserObject()).getId());
		Enumeration<TreeNode> childrens = node.children();
		while (childrens.hasMoreElements()) {
			DefaultMutableTreeNode c = (DefaultMutableTreeNode) childrens.nextElement();
			if (c.getUserObject() instanceof SessionInfo) {
				folder.getItems().add((SessionInfo) c.getUserObject());
			} else {
				folder.getFolders().add(convertModelFromTree(c));
			}
		}
		return folder;
	}

	public synchronized static DefaultMutableTreeNode getNode(SessionFolder folder) {
		NamedItem item = new NamedItem();
		item.setName(folder.getName());
		item.setId(folder.getId());
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
		for (SessionInfo info : folder.getItems()) {
			DefaultMutableTreeNode c = new DefaultMutableTreeNode(info);
			c.setAllowsChildren(false);
			node.add(c);
		}

		for (SessionFolder folderItem : folder.getFolders()) {
			node.add(getNode(folderItem));
		}
		return node;
	}

//	public synchronized static void store(SessionFolder folder) {
//		File file = new File(App.CONFIG_DIR, App.SESSION_DB_FILE);
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			objectMapper.writeValue(file, folder);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public synchronized static void updateFavourites(String id, List<String> localFolders, List<String> remoteFolders) {
		SavedSessionTree tree = load();
		SessionFolder folder = tree.getFolder();

		updateFavourites(folder, id, localFolders, remoteFolders);
		save(folder, tree.getLastSelection());
	}

	private static boolean updateFavourites(SessionFolder folder, String id, List<String> localFolders,
			List<String> remoteFolders) {
		for (SessionInfo info : folder.getItems()) {
			if (info.id.equals(id)) {
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
