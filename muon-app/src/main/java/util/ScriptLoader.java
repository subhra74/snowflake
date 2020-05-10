package util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScriptLoader {
    public static synchronized String loadShellScript(String path) {
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(
                    ScriptLoader.class.getResourceAsStream(path)))) {
                while (true) {
                    String s = r.readLine();
                    if (s == null) {
                        break;
                    }
                    sb.append(s + "\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
