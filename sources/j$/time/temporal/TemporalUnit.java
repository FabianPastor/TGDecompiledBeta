package j$.time.temporal;

import j$.time.Duration;
import j$.time.LocalTime;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoLocalDateTime;
import j$.time.chrono.ChronoZonedDateTime;

public interface TemporalUnit {
    <R extends Temporal> R addTo(R r, long j);

    long between(Temporal temporal, Temporal temporal2);

    Duration getDuration();

    boolean isDateBased();

    boolean isDurationEstimated();

    boolean isSupportedBy(Temporal temporal);

    boolean isTimeBased();

    String toString();

    /* renamed from: j$.time.temporal.TemporalUnit$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static boolean $default$isSupportedBy(TemporalUnit _this, Temporal temporal) {
            if (temporal instanceof LocalTime) {
                return _this.isTimeBased();
            }
            if (temporal instanceof ChronoLocalDate) {
                return _this.isDateBased();
            }
            if ((temporal instanceof ChronoLocalDateTime) || (temporal instanceof ChronoZonedDateTime)) {
                return true;
            }
            try {
                temporal.plus(1, _this);
                return true;
            } catch (UnsupportedTemporalTypeException e) {
                return false;
            } catch (RuntimeException e2) {
                try {
                    temporal.plus(-1, _this);
                    return true;
                } catch (RuntimeException e3) {
                    return false;
                }
            }
        }
    }
}
