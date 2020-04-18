package muon.app.ui.components.settings;

public enum SettingsPageName {
	General("General", 0), Terminal("Terminal", 1), Editor("Editor", 2), Misc("Misc", 3);

	public final String name;
	public final int index;

	private SettingsPageName(String name, int index) {
		this.name = name;
		this.index = index;
	}
}
