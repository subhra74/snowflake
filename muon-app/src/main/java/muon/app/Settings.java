package muon.app;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import muon.app.ui.components.session.files.transfer.FileTransfer.ConflictAction;
import muon.app.ui.components.session.files.transfer.FileTransfer.TransferMode;
import muon.app.ui.components.settings.DarkTerminalTheme;
import muon.app.ui.components.settings.EditorEntry;
import util.CollectionHelper;

public class Settings {
	private boolean usingMasterPassword = false;
	private TransferMode fileTransferMode = TransferMode.Prompt;
	private ConflictAction conflictAction = ConflictAction.AutoRename;
	private boolean confirmBeforeDelete = true;
	private boolean confirmBeforeMoveOrCopy = false;
	private boolean showHiddenFilesByDefault = false;
	private boolean promptForSudo = true;
	private boolean directoryCache = true;
	private boolean showPathBar = true;
	private boolean useDarkThemeForTerminal = false;
	private boolean showMessagePrompt = false;
	private boolean useGlobalDarkTheme = true;

	private int logViewerFont = 14;
	private boolean logViewerUseWordWrap = true;
	private int logViewerLinesPerPage = 50;

	private int sysloadRefreshInterval = 3;

	private boolean puttyLikeCopyPaste = false;
	private String terminalType = "xterm-256color";
	private boolean confirmBeforeTerminalClosing = true;
	private int termWidth = 80;
	private int termHeight = 24;
	private boolean terminalBell = false;
	private String terminalFontName = "NotoMono-Regular";
	private int terminalFontSize = 14;
	private String terminalTheme = "Dark";
	private String terminalPalette = "xterm";
	private int[] palleteColors = { 0x000000, 0xcd0000, 0x00cd00, 0xcdcd00, 0x1e90ff, 0xcd00cd, 0x00cdcd, 0xe5e5e5,
			0x4c4c4c, 0xff0000, 0x00ff00, 0xffff00, 0x4682b4, 0xff00ff, 0x00ffff, 0xffffff };
	private int backgroundTransferQueueSize = 2;
	private int defaultColorFg = DarkTerminalTheme.DEF_FG, defaultColorBg = DarkTerminalTheme.DEF_BG,
			defaultSelectionFg = DarkTerminalTheme.SEL_FG, defaultSelectionBg = DarkTerminalTheme.SEL_BG,
			defaultFoundFg = DarkTerminalTheme.FIND_FG, defaultFoundBg = DarkTerminalTheme.FIND_BG,
			defaultHrefFg = DarkTerminalTheme.HREF_FG, defaultHrefBg = DarkTerminalTheme.HREF_BG;

	public static final String COPY_KEY = "Copy", PASTE_KEY = "Paste", CLEAR_BUFFER = "Clear buffer", FIND_KEY = "Find";

	private Map<String, Integer> keyCodeMap = new CollectionHelper.OrderedDict<String, Integer>()
			.putItem(COPY_KEY, KeyEvent.VK_C).putItem(PASTE_KEY, KeyEvent.VK_V)
			.putItem(CLEAR_BUFFER, App.IS_MAC ? KeyEvent.VK_K : KeyEvent.VK_L).putItem(FIND_KEY, KeyEvent.VK_F);

	private Map<String, Integer> keyModifierMap = new CollectionHelper.Dict<String, Integer>()
			.putItem(COPY_KEY,
					App.IS_MAC ? KeyEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)
			.putItem(PASTE_KEY,
					App.IS_MAC ? KeyEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)
			.putItem(CLEAR_BUFFER, App.IS_MAC ? KeyEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK)
			.putItem(FIND_KEY, App.IS_MAC ? KeyEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK);

	private boolean dualPaneMode = true;
	private boolean listViewEnabled = false;

	private int defaultOpenAction = 0
	// 0 Open with default application
	// 1 Open with default editor
	// 2 Open with internal editor
			, numberOfSimultaneousConnection = 3;

	private double uiScaling = 1.0;
	private boolean manualScaling = false;

	private List<EditorEntry> editors = new ArrayList<>();

	private String defaultPanel = "FILES";

	public boolean isConfirmBeforeDelete() {
		return confirmBeforeDelete;
	}

	public void setConfirmBeforeDelete(boolean confirmBeforeDelete) {
		this.confirmBeforeDelete = confirmBeforeDelete;
	}

	public boolean isConfirmBeforeMoveOrCopy() {
		return confirmBeforeMoveOrCopy;
	}

	public void setConfirmBeforeMoveOrCopy(boolean confirmBeforeMoveOrCopy) {
		this.confirmBeforeMoveOrCopy = confirmBeforeMoveOrCopy;
	}

	public boolean isShowHiddenFilesByDefault() {
		return showHiddenFilesByDefault;
	}

	public void setShowHiddenFilesByDefault(boolean showHiddenFilesByDefault) {
		this.showHiddenFilesByDefault = showHiddenFilesByDefault;
	}

	public boolean isPromptForSudo() {
		return promptForSudo;
	}

	public void setPromptForSudo(boolean promptForSudo) {
		this.promptForSudo = promptForSudo;
	}

	public boolean isDirectoryCache() {
		return directoryCache;
	}

	public void setDirectoryCache(boolean directoryCache) {
		this.directoryCache = directoryCache;
	}

	public boolean isShowPathBar() {
		return showPathBar;
	}

	public void setShowPathBar(boolean showPathBar) {
		this.showPathBar = showPathBar;
	}

	public boolean isConfirmBeforeTerminalClosing() {
		return confirmBeforeTerminalClosing;
	}

	public void setConfirmBeforeTerminalClosing(boolean confirmBeforeTerminalClosing) {
		this.confirmBeforeTerminalClosing = confirmBeforeTerminalClosing;
	}

	public boolean isUseDarkThemeForTerminal() {
		return useDarkThemeForTerminal;
	}

	public void setUseDarkThemeForTerminal(boolean useDarkThemeForTerminal) {
		this.useDarkThemeForTerminal = useDarkThemeForTerminal;
	}

	public boolean isShowMessagePrompt() {
		return showMessagePrompt;
	}

	public void setShowMessagePrompt(boolean showMessagePrompt) {
		this.showMessagePrompt = showMessagePrompt;
	}

	public int getDefaultOpenAction() {
		return defaultOpenAction;
	}

	public void setDefaultOpenAction(int defaultOpenAction) {
		this.defaultOpenAction = defaultOpenAction;
	}

	public int getNumberOfSimultaneousConnection() {
		return numberOfSimultaneousConnection;
	}

	public void setNumberOfSimultaneousConnection(int numberOfSimultaneousConnection) {
		this.numberOfSimultaneousConnection = numberOfSimultaneousConnection;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public boolean isPuttyLikeCopyPaste() {
		return puttyLikeCopyPaste;
	}

	public void setPuttyLikeCopyPaste(boolean puttyLikeCopyPaste) {
		this.puttyLikeCopyPaste = puttyLikeCopyPaste;
	}

	public void setDefaultPanel(String defaultPanel) {
		this.defaultPanel = defaultPanel;
	}

	public String getDefaultPanel() {
		return defaultPanel;
	}

	/**
	 * @return the useGlobalDarkTheme
	 */
	public boolean isUseGlobalDarkTheme() {
		return useGlobalDarkTheme;
	}

	/**
	 * @param useGlobalDarkTheme the useGlobalDarkTheme to set
	 */
	public void setUseGlobalDarkTheme(boolean useGlobalDarkTheme) {
		this.useGlobalDarkTheme = useGlobalDarkTheme;
	}

	/**
	 * @return the termWidth
	 */
	public int getTermWidth() {
		return termWidth;
	}

	/**
	 * @param termWidth the termWidth to set
	 */
	public void setTermWidth(int termWidth) {
		this.termWidth = termWidth;
	}

	/**
	 * @return the termHeight
	 */
	public int getTermHeight() {
		return termHeight;
	}

	/**
	 * @param termHeight the termHeight to set
	 */
	public void setTermHeight(int termHeight) {
		this.termHeight = termHeight;
	}

	/**
	 * @return the terminalBell
	 */
	public boolean isTerminalBell() {
		return terminalBell;
	}

	/**
	 * @param terminalBell the terminalBell to set
	 */
	public void setTerminalBell(boolean terminalBell) {
		this.terminalBell = terminalBell;
	}

	/**
	 * @return the terminalFontName
	 */
	public String getTerminalFontName() {
		return terminalFontName;
	}

	/**
	 * @param terminalFontName the terminalFontName to set
	 */
	public void setTerminalFontName(String terminalFontName) {
		this.terminalFontName = terminalFontName;
	}

	/**
	 * @return the terminalFontSize
	 */
	public int getTerminalFontSize() {
		return terminalFontSize;
	}

	/**
	 * @param terminalFontSize the terminalFontSize to set
	 */
	public void setTerminalFontSize(int terminalFontSize) {
		this.terminalFontSize = terminalFontSize;
	}

	/**
	 * @return the defaultColorFg
	 */
	public int getDefaultColorFg() {
		return defaultColorFg;
	}

	/**
	 * @param defaultColorFg the defaultColorFg to set
	 */
	public void setDefaultColorFg(int defaultColorFg) {
		this.defaultColorFg = defaultColorFg;
	}

	/**
	 * @return the defaultColorBg
	 */
	public int getDefaultColorBg() {
		return defaultColorBg;
	}

	/**
	 * @param defaultColorBg the defaultColorBg to set
	 */
	public void setDefaultColorBg(int defaultColorBg) {
		this.defaultColorBg = defaultColorBg;
	}

	/**
	 * @return the defaultSelectionFg
	 */
	public int getDefaultSelectionFg() {
		return defaultSelectionFg;
	}

	/**
	 * @param defaultSelectionFg the defaultSelectionFg to set
	 */
	public void setDefaultSelectionFg(int defaultSelectionFg) {
		this.defaultSelectionFg = defaultSelectionFg;
	}

	/**
	 * @return the defaultSelectionBg
	 */
	public int getDefaultSelectionBg() {
		return defaultSelectionBg;
	}

	/**
	 * @param defaultSelectionBg the defaultSelectionBg to set
	 */
	public void setDefaultSelectionBg(int defaultSelectionBg) {
		this.defaultSelectionBg = defaultSelectionBg;
	}

	/**
	 * @return the defaultFoundFg
	 */
	public int getDefaultFoundFg() {
		return defaultFoundFg;
	}

	/**
	 * @param defaultFoundFg the defaultFoundFg to set
	 */
	public void setDefaultFoundFg(int defaultFoundFg) {
		this.defaultFoundFg = defaultFoundFg;
	}

	/**
	 * @return the defaultFoundBg
	 */
	public int getDefaultFoundBg() {
		return defaultFoundBg;
	}

	/**
	 * @param defaultFoundBg the defaultFoundBg to set
	 */
	public void setDefaultFoundBg(int defaultFoundBg) {
		this.defaultFoundBg = defaultFoundBg;
	}

	/**
	 * @return the defaultHrefFg
	 */
	public int getDefaultHrefFg() {
		return defaultHrefFg;
	}

	/**
	 * @param defaultHrefFg the defaultHrefFg to set
	 */
	public void setDefaultHrefFg(int defaultHrefFg) {
		this.defaultHrefFg = defaultHrefFg;
	}

	/**
	 * @return the defaultHrefBg
	 */
	public int getDefaultHrefBg() {
		return defaultHrefBg;
	}

	/**
	 * @param defaultHrefBg the defaultHrefBg to set
	 */
	public void setDefaultHrefBg(int defaultHrefBg) {
		this.defaultHrefBg = defaultHrefBg;
	}

	/**
	 * @return the terminalTheme
	 */
	public String getTerminalTheme() {
		return terminalTheme;
	}

	/**
	 * @param terminalTheme the terminalTheme to set
	 */
	public void setTerminalTheme(String terminalTheme) {
		this.terminalTheme = terminalTheme;
	}

	/**
	 * @return the terminalPalette
	 */
	public String getTerminalPalette() {
		return terminalPalette;
	}

	/**
	 * @param terminalPalette the terminalPalette to set
	 */
	public void setTerminalPalette(String terminalPalette) {
		this.terminalPalette = terminalPalette;
	}

	/**
	 * @return the palleteColors
	 */
	public int[] getPalleteColors() {
		return palleteColors;
	}

	/**
	 * @param palleteColors the palleteColors to set
	 */
	public void setPalleteColors(int[] palleteColors) {
		this.palleteColors = palleteColors;
	}

	/**
	 * @return the keyCodeMap
	 */
	public Map<String, Integer> getKeyCodeMap() {
		return keyCodeMap;
	}

	/**
	 * @param keyCodeMap the keyCodeMap to set
	 */
	public void setKeyCodeMap(Map<String, Integer> keyCodeMap) {
		this.keyCodeMap = keyCodeMap;
	}

	/**
	 * @return the keyModifierMap
	 */
	public Map<String, Integer> getKeyModifierMap() {
		return keyModifierMap;
	}

	/**
	 * @param keyModifierMap the keyModifierMap to set
	 */
	public void setKeyModifierMap(Map<String, Integer> keyModifierMap) {
		this.keyModifierMap = keyModifierMap;
	}

	/**
	 * @return the logViewerFont
	 */
	public int getLogViewerFont() {
		return logViewerFont;
	}

	/**
	 * @param logViewerFont the logViewerFont to set
	 */
	public void setLogViewerFont(int logViewerFont) {
		this.logViewerFont = logViewerFont;
	}

	/**
	 * @return the logViewerUseWordWrap
	 */
	public boolean isLogViewerUseWordWrap() {
		return logViewerUseWordWrap;
	}

	/**
	 * @param logViewerUseWordWrap the logViewerUseWordWrap to set
	 */
	public void setLogViewerUseWordWrap(boolean logViewerUseWordWrap) {
		this.logViewerUseWordWrap = logViewerUseWordWrap;
	}

	/**
	 * @return the logViewerLinesPerPage
	 */
	public int getLogViewerLinesPerPage() {
		return logViewerLinesPerPage;
	}

	/**
	 * @param logViewerLinesPerPage the logViewerLinesPerPage to set
	 */
	public void setLogViewerLinesPerPage(int logViewerLinesPerPage) {
		this.logViewerLinesPerPage = logViewerLinesPerPage;
	}

	/**
	 * @return the sysloadRefreshInterval
	 */
	public int getSysloadRefreshInterval() {
		return sysloadRefreshInterval;
	}

	/**
	 * @param sysloadRefreshInterval the sysloadRefreshInterval to set
	 */
	public void setSysloadRefreshInterval(int sysloadRefreshInterval) {
		this.sysloadRefreshInterval = sysloadRefreshInterval;
	}

	/**
	 * @return Number of transfer allowed in background per session
	 */
	public int getBackgroundTransferQueueSize() {
		return backgroundTransferQueueSize;
	}

	/**
	 * @param backgroundTransferQueueSize Number of transfer allowed in background
	 *                                    per session
	 */
	public void setBackgroundTransferQueueSize(int backgroundTransferQueueSize) {
		this.backgroundTransferQueueSize = backgroundTransferQueueSize;
	}

	public TransferMode getFileTransferMode() {
		return fileTransferMode;
	}

	public void setFileTransferMode(TransferMode fileTransferMode) {
		this.fileTransferMode = fileTransferMode;
	}

	public ConflictAction getConflictAction() {
		return conflictAction;
	}

	public void setConflictAction(ConflictAction conflictAction) {
		this.conflictAction = conflictAction;
	}

	public List<EditorEntry> getEditors() {
		return editors;
	}

	public void setEditors(List<EditorEntry> editors) {
		this.editors = editors;
	}

	public double getUiScaling() {
		return uiScaling;
	}

	public void setUiScaling(double uiScaling) {
		this.uiScaling = uiScaling;
	}

	public boolean isManualScaling() {
		return manualScaling;
	}

	public void setManualScaling(boolean manualScaling) {
		this.manualScaling = manualScaling;
	}

	public boolean isUsingMasterPassword() {
		return usingMasterPassword;
	}

	public void setUsingMasterPassword(boolean usingMasterPassword) {
		this.usingMasterPassword = usingMasterPassword;
	}

	public boolean isDualPaneMode() {
		return dualPaneMode;
	}

	public void setDualPaneMode(boolean dualPaneMode) {
		this.dualPaneMode = dualPaneMode;
	}

	public boolean isListViewEnabled() {
		return listViewEnabled;
	}

	public void setListViewEnabled(boolean listViewEnabled) {
		this.listViewEnabled = listViewEnabled;
	}

}
