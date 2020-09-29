package j$.util;

import j$.time.o;
import java.util.TimeZone;

public class DesugarTimeZone {
    private DesugarTimeZone() {
    }

    public static TimeZone getTimeZone(String ID) {
        return TimeZone.getTimeZone(ID);
    }

    public static o a(TimeZone zone) {
        return o.M(zone.getID(), o.a);
    }
}
