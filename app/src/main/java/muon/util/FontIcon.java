package muon.util;

public enum FontIcon {
    RI_ADD_LINE("\uea13"),
    RI_MENU_LINE("\uef3e"),
    RI_INSTANCE_LINE("\uf383"),
    RI_ARROW_LEFT_LINE("\uea60"),
    RI_ARROW_RIGHT_LINE("\uea6c"),
    RI_ARROW_UP_LINE("\uea76"),
    RI_LOOP_RIGHT_LINE("\uf33f"),
    RI_MORE_2_LINE("\uef77"),
    RI_CLOSE_LINE("\ueb99");

    public String getValue() {
        return value;
    }

    private FontIcon(String value) {
        this.value = value;
    }

    private String value;
}
