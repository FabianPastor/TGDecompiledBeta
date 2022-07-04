package j$.util;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.ZonedDateTime;
import j$.time.temporal.ChronoField;
import java.util.Date;
import java.util.GregorianCalendar;

public class DesugarGregorianCalendar {
    private DesugarGregorianCalendar() {
    }

    public static ZonedDateTime toZonedDateTime(GregorianCalendar instance) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(instance.getTimeInMillis()), DesugarTimeZone.toZoneId(instance.getTimeZone()));
    }

    public static GregorianCalendar from(ZonedDateTime zdt) {
        GregorianCalendar cal = new GregorianCalendar(DesugarTimeZone.getTimeZone(zdt.getZone()));
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        cal.setFirstDayOfWeek(2);
        cal.setMinimalDaysInFirstWeek(4);
        try {
            cal.setTimeInMillis(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(zdt.toEpochSecond(), 1000), (long) zdt.get(ChronoField.MILLI_OF_SECOND)));
            return cal;
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
