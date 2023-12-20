package muon.config;

import java.io.File;

public class AppConfig {
    public static final String CONFIG_DIR = System.getProperty("user.home") + File.separatorChar + "muon-ssh";
    public static final String SESSION_DB_FILE = "session-store.db";

}
