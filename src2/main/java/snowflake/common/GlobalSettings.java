package snowflake.common;

public class GlobalSettings {
    private boolean showMessage;
    private String openMode; //DEFAULT_APP, INTERNAL_EDITOR, EXTERNAL_EDITOR
    private String externalEditor;

    public String getExternalEditor() {
        return externalEditor;
    }

    public void setExternalEditor(String externalEditor) {
        this.externalEditor = externalEditor;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }


}
