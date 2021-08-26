package j$.util;

import java.util.TimeZone;

public class DesugarTimeZone {
    public static TimeZone getTimeZone(String str) {
        return TimeZone.getTimeZone(str);
    }
}
