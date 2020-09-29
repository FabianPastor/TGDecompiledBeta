package j$.time.format;

import j$.time.f;

public class DateTimeParseException extends f {
    public DateTimeParseException(String message, CharSequence parsedData, int errorIndex) {
        super(message);
        parsedData.toString();
    }

    public DateTimeParseException(String message, CharSequence parsedData, int errorIndex, Throwable cause) {
        super(message, cause);
        parsedData.toString();
    }
}
