package muonssh.app.ui.components.session.importer;

import muonssh.app.ui.components.session.SessionInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SSHConfigImporter {


    static String HOST_TEXT = "Host";
    static String IP_TEXT = "HostName";
    static String PORT_TEXT = "Port";
    static String IDENTITY_FILE_TEXT = "IdentityFile";
    static String USER_TEXT = "User";

    public static List<SessionInfo> getSessionFromFile(File file) throws FileNotFoundException {
        List<SessionInfo> sessionInfoList = new ArrayList<>();
        Scanner myReader = new Scanner(file);
        String linea = myReader.hasNextLine() ? myReader.nextLine() : null;
        SessionInfo info = new SessionInfo();
        if (linea.contains(HOST_TEXT)) {
            info.setName(sanitizeString(linea, HOST_TEXT));
        }
        while (myReader.hasNextLine()) {
            linea = myReader.nextLine();
            if (linea.contains(IP_TEXT)) {
                info.setHost(sanitizeString(linea, IP_TEXT));
            } else if (linea.contains(USER_TEXT)) {
                info.setUser(sanitizeString(linea, USER_TEXT));
            } else if (linea.contains(PORT_TEXT)) {
                info.setPort(Integer.parseInt(sanitizeString(linea, PORT_TEXT)));
            } else if (linea.contains(IDENTITY_FILE_TEXT)) {
                info.setPrivateKeyFile(sanitizeString(linea, IDENTITY_FILE_TEXT));
            } else if (linea.contains(HOST_TEXT)) {
                if (info.getName()!= null){
                    sessionInfoList.add(info);
                }
                info = new SessionInfo();
                info.setName(sanitizeString(linea, HOST_TEXT));
            }
        }
        if (info.getName()!= null){
            sessionInfoList.add(info);
        }

        return sessionInfoList;
    }

    public static String sanitizeString(String line, String key) {
        return line.trim().replace(key, "").replaceAll("\"", "").replaceAll("\t", "").trim();
    }

}
