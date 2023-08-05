package muon.app.ui.components.session.terminal.snippets;

import java.util.UUID;

/**
 * @author subhro
 */
public class SnippetItem {

	private String name, command, id;

	/**
	 *
	 */
	public SnippetItem() {
		// TODO Auto-generated constructor stub
	}

	public SnippetItem(String name, String command) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
