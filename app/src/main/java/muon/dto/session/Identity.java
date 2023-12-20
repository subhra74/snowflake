package muon.dto.session;

public class Identity {
    private String user, password, key;
    private int mode;

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getKey() {
        return key;
    }

    public int getMode() {
        return mode;
    }
}
