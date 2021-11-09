package util;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import static muonssh.app.App.bundle;

public class Constants {
    public static final String BASE_URL="https://github.com/devlinx9";
    public static final String HELP_URL="https://github.com/subhra74/snowflake/wiki"; //TODO change wiki pages
    public static final String UPDATE_URL="https://devlinx9.github.io/muon-ssh";
    public static final String API_UPDATE_URL="https://api.github.com/repos/devlinx9/muon-ssh/releases/latest";
    public static final String REPOSITORY_URL=BASE_URL + "/muon-ssh";
    public static final String APPLICATION_VERSION ="2.0.0";
    public static final String APPLICATION_NAME="Muon SSH";

    public enum ConflictAction {


        OVERWRITE(0,bundle.getString("overwrite")), AUTORENAME(1,bundle.getString("autorename")), SKIP(2,bundle.getString("skip")), PROMPT(3, bundle.getString("prompt")), CANCEL(4, bundle.getString("cancel"));
        private int key;
        private String value;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        ConflictAction(int key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static void update(){
            OVERWRITE.setValue(bundle.getString("overwrite"));
            AUTORENAME.setValue(bundle.getString("autorename"));
            SKIP.setValue(bundle.getString("skip"));
            PROMPT.setValue(bundle.getString("prompt"));
            CANCEL.setValue(bundle.getString("cancel"));
        }
    }

    public enum TransferMode {

        @JsonEnumDefaultValue NORMAL(0, bundle.getString("transfer_normally")), BACKGROUND(1, bundle.getString("transfer_background"));//, NORMAL ;

        private int key;
        private String value;

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        TransferMode(int key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static void update(){
            NORMAL.setValue(bundle.getString("transfer_normally"));
            BACKGROUND.setValue(bundle.getString("transfer_background"));
        }
    }

}
