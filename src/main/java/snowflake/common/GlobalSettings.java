package snowflake.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GlobalSettings {
    private boolean showMessage;
    private String openMode; //DEFAULT_APP, INTERNAL_EDITOR, EXTERNAL_EDITOR
    private String externalEditor;
}
