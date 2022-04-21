package j$.time.temporal;

import j$.time.DateTimeException;

public class UnsupportedTemporalTypeException extends DateTimeException {
    private static final long serialVersionUID = -6158898438688206006L;

    public UnsupportedTemporalTypeException(String message) {
        super(message);
    }

    public UnsupportedTemporalTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
