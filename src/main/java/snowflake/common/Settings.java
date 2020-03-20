package snowflake.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Settings {
	private boolean confirmBeforeDelete = true;
	private boolean confirmBeforeMoveOrCopy = true;
	private boolean showHiddenFilesByDefault = false;
	private boolean promptForSudo = true;
	private boolean directoryCache = true;
	private boolean showPathBar = true;
	private boolean confirmBeforeTerminalClosing = true;
	private boolean useDarkThemeForTerminal = false;
	private boolean showMessagePrompt = false;
	private boolean puttyLikeCopyPaste = false;
	private int defaultOpenAction = 0;
	// 0 Open with default application
	// 1 Open with default editor
	// 2 Open with internal editor
	private int numberOfSimultaneousConnection = 3;
	private String terminalType = "xterm-256color";
	private String defaultPanel = SnowFlakePanel.FILES.getName();
}
