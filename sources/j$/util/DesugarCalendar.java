package j$.util;

import j$.time.Instant;
import java.util.Calendar;

public class DesugarCalendar {
    public static final int LONG_FORMAT = 2;
    public static final int LONG_STANDALONE = 32770;
    public static final int NARROW_FORMAT = 4;
    public static final int NARROW_STANDALONE = 32772;
    public static final int SHORT_FORMAT = 1;
    public static final int SHORT_STANDALONE = 32769;
    static final int STANDALONE_MASK = 32768;

    private DesugarCalendar() {
    }

    public static Instant toInstant(Calendar instance) {
        return Instant.ofEpochMilli(instance.getTimeInMillis());
    }
}
