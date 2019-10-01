package snowflake.common;

public class Settings {
    private boolean confirmBeforeDelete = true, confirmBeforeMoveOrCopy = true,
            showHiddenFilesByDefault = false, promptForSudo = true, directoryCache = true,
            showPathBar = true, confirmBeforeTerminalClosing = true, useDarkThemeForTerminal = false;
    private int defaultOpenAction = 0
            //  0 Open with default application
            //  1 Open with default editor
            //  2 Open with internal editor
            , numberOfSimultaneousConnection = 3;


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
}
