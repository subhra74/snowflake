package muon.exceptions;

public class FSConnectException extends FSException {
    public FSConnectException(String message) {
        super(message);
    }

    public FSConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
