package muon.exceptions;

import java.io.IOException;

public class FSException extends IOException {
    public FSException(String message) {
        super(message);
    }

    public FSException(String message, Throwable cause) {
        super(message, cause);
    }
}
