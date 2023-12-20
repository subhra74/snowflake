package muon.constants;

public enum AuthMode {
    Password(0), Key(1), Identity(2);

    private final int mode;

    AuthMode(int mode) {
        this.mode = mode;
    }

    public int intValue() {
        return mode;
    }
}
