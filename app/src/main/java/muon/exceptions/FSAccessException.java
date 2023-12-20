package muon.exceptions;

public class FSAccessException extends FSException {
    public FSAccessException(String message) {
        super(message);
    }

    public FSAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
