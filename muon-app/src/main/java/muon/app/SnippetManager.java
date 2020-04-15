/**
 * 
 */
package muon.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muon.app.ui.components.session.terminal.snippets.SnippetItem;

/**
 * @author subhro
 *
 */
public class SnippetManager {
	private List<SnippetItem> snippetItems = new ArrayList<>();

	public synchronized void loadSnippets() {
		File file = new File(App.CONFIG_DIR, App.SNIPPETS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		if (file.exists()) {
			try {
				snippetItems = objectMapper.readValue(file,
						new TypeReference<List<SnippetItem>>() {
						});
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		snippetItems = new ArrayList<>();
	}

	public synchronized void saveSnippets() {
		File file = new File(App.CONFIG_DIR, App.SNIPPETS_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, snippetItems);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized List<SnippetItem> getSnippetItems() {
		loadSnippets();
		return snippetItems;
	}

}
