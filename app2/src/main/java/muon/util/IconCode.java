package muon.util;

public enum IconCode {
    RI_ADD_LINE("\uea13"),
    RI_SUBTRACT_LINE("\uF1AF"),
    RI_MENU_LINE("\uef3e"),
    RI_INSTANCE_LINE("\uf383"),
    RI_ARROW_LEFT_LINE("\uea60"),
    RI_ARROW_RIGHT_LINE("\uea6c"),
    RI_ARROW_UP_LINE("\uea76"),
    RI_LOOP_RIGHT_LINE("\uf33f"),
    RI_MORE_2_LINE("\uef77"),
    RI_CLOSE_LINE("\ueb99"),
    RI_COMPUTER_FILL("\uEBC9"),
    RI_ARROW_LEFT_RIGHT_LINE("\uEA62"),
    RI_CLOUD_FILL("\uEB9C"),
    RI_DATABASE_2_FILL("\uEC15"),
    RI_TOOLS_FILL("\uF21A"),
    RI_BOX_3_FILL("\uF2F4"),
    RI_FOLDER_FORBID_FILL("\uED62"),
    RI_SAFE_2_FILL("\uF0A8"),
    RI_FOLDER_FILL("\uED61"),
    RI_FOLDER_OPEN_FILL("\uED6F"),
    RI_ARROW_RIGHT_S_LINE("\uEA6E"),
    RI_ARROW_DOWN_S_LINE("\uEA4E"),
    RI_TOGGLE_LINE("\uF219"),
    RI_TOGGLE_FILL("\uF218"),
    RI_FOLDER_2_LINE("\uED52"),
    RI_FOLDER_LINE("\uED6A"),
    RI_CHECKBOX_BLANK_LINE("\uEB7F"),
    RI_ARROW_LEFT_CIRCLE_LINE("\uEA5C"),
    RI_ARROW_RIGHT_CIRCLE_LINE("\uEA68"),
    RI_ARROW_UP_CIRCLE_LINE("\uEA72"),
    RI_HOME_4_LINE("\uEE1D"),
    RI_HARD_DRIVE_3_LINE("\uF395"),
    RI_FILE_FILL("\uECE0"),
    RI_SEARCH_LINE("\uF0D1"),
    RI_USER_LINE("\uF264"),
    RI_ACCOUNT_CIRCLE_LINE("\uEA09"),
    RI_ACCOUNT_CIRCLE_FILL("\uEA08"),
    RI_ACCOUNT_ALERT_FILL("\uEA20"),
    RI_SERVER_FILL("\uF0DF");

    public String getValue() {
        return value;
    }

    private IconCode(String value) {
        this.value = value;
    }

    private String value;
}
