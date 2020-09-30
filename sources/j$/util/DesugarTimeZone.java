package j$.util;

import java.util.TimeZone;

public class DesugarTimeZone {
    private DesugarTimeZone() {
    }

    public static TimeZone getTimeZone(String ID) {
        return TimeZone.getTimeZone(ID);
    }
}
