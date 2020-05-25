package muon.app.ui.components.session;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.App;

public final class BookmarkManager {
	public static final synchronized Map<String, List<String>> getAll() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		File bookmarkFile = new File(App.CONFIG_DIR, App.BOOKMARKS_FILE);
		if (bookmarkFile.exists()) {
			try {
				Map<String, List<String>> bookmarkMap = objectMapper.readValue(bookmarkFile,
						new TypeReference<Map<String, List<String>>>() {
						});
				return Collections.synchronizedMap(bookmarkMap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Collections.synchronizedMap(new HashMap<>());
	}

	public static final synchronized void save(Map<String, List<String>> bookmarks) {
		ObjectMapper objectMapper = new ObjectMapper();
		File bookmarkFile = new File(App.CONFIG_DIR, App.BOOKMARKS_FILE);
		try {
			objectMapper.writeValue(bookmarkFile, bookmarks);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final synchronized void addEntry(String id, String path) {
		if (id == null) {
			id = "";
		}
		Map<String, List<String>> bookmarkMap = BookmarkManager.getAll();
		List<String> bookmarks = bookmarkMap.get(id);
		if (bookmarks == null) {
			bookmarks = new ArrayList<String>();
		}
		bookmarks.add(path);
		bookmarkMap.put(id, bookmarks);
		save(bookmarkMap);
	}

	public static final synchronized void addEntry(String id, List<String> path) {
		if (id == null) {
			id = "";
		}
		Map<String, List<String>> bookmarkMap = BookmarkManager.getAll();
		List<String> bookmarks = bookmarkMap.get(id);
		if (bookmarks == null) {
			bookmarks = new ArrayList<String>();
		}
		bookmarks.addAll(path);
		bookmarkMap.put(id, bookmarks);
		save(bookmarkMap);
	}

	public static final synchronized List<String> getBookmarks(String id) {
		if (id == null) {
			id = "";
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		File bookmarkFile = new File(App.CONFIG_DIR, App.BOOKMARKS_FILE);
		if (bookmarkFile.exists()) {
			try {
				Map<String, List<String>> bookmarkMap = objectMapper.readValue(bookmarkFile,
						new TypeReference<Map<String, List<String>>>() {
						});
				List<String> bookmarks = bookmarkMap.get(id);
				if (bookmarks != null) {
					return new ArrayList<String>(bookmarks);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<String>();
	}
}
