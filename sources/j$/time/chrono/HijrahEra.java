package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.chrono.Era;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalField;
import j$.time.temporal.ValueRange;

public enum HijrahEra implements Era {
    AH;

    public static HijrahEra of(int hijrahEra) {
        if (hijrahEra == 1) {
            return AH;
        }
        throw new DateTimeException("Invalid era: " + hijrahEra);
    }

    public int getValue() {
        return 1;
    }

    public ValueRange range(TemporalField field) {
        if (field == ChronoField.ERA) {
            return ValueRange.of(1, 1);
        }
        return Era.CC.$default$range(this, field);
    }
}
