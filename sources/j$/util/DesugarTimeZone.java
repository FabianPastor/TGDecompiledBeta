package j$.util;

import j$.time.ZoneId;
import java.util.TimeZone;

public class DesugarTimeZone {
    private DesugarTimeZone() {
    }

    public static TimeZone getTimeZone(String ID) {
        return TimeZone.getTimeZone(ID);
    }

    public static TimeZone getTimeZone(ZoneId zoneId) {
        String tzid = zoneId.getId();
        char c = tzid.charAt(0);
        if (c == '+' || c == '-') {
            tzid = "GMT" + tzid;
        } else if (c == 'Z' && tzid.length() == 1) {
            tzid = "UTC";
        }
        return TimeZone.getTimeZone(tzid);
    }

    public static ZoneId toZoneId(TimeZone zone) {
        return ZoneId.of(zone.getID(), ZoneId.SHORT_IDS);
    }
}
