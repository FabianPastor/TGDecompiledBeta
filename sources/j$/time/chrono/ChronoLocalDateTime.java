package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.util.Objects;
import java.util.Comparator;

public interface ChronoLocalDateTime<D extends ChronoLocalDate> extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDateTime<?>> {
    Temporal adjustInto(Temporal temporal);

    ChronoZonedDateTime<D> atZone(ZoneId zoneId);

    int compareTo(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    Chronology getChronology();

    int hashCode();

    boolean isAfter(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean isBefore(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean isEqual(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean isSupported(TemporalField temporalField);

    boolean isSupported(TemporalUnit temporalUnit);

    ChronoLocalDateTime<D> minus(long j, TemporalUnit temporalUnit);

    ChronoLocalDateTime<D> minus(TemporalAmount temporalAmount);

    ChronoLocalDateTime<D> plus(long j, TemporalUnit temporalUnit);

    ChronoLocalDateTime<D> plus(TemporalAmount temporalAmount);

    <R> R query(TemporalQuery<R> temporalQuery);

    long toEpochSecond(ZoneOffset zoneOffset);

    Instant toInstant(ZoneOffset zoneOffset);

    D toLocalDate();

    LocalTime toLocalTime();

    String toString();

    ChronoLocalDateTime<D> with(TemporalAdjuster temporalAdjuster);

    ChronoLocalDateTime<D> with(TemporalField temporalField, long j);

    /* renamed from: j$.time.chrono.ChronoLocalDateTime$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoLocalDateTime<?>> timeLineOrder() {
            return AbstractChronology.DATE_TIME_ORDER;
        }

        public static ChronoLocalDateTime<?> from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoLocalDateTime) {
                return (ChronoLocalDateTime) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono != null) {
                return chrono.localDateTime(temporal);
            }
            throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + temporal.getClass());
        }

        public static Chronology $default$getChronology(ChronoLocalDateTime _this) {
            return _this.toLocalDate().getChronology();
        }

        public static boolean $default$isSupported(ChronoLocalDateTime _this, TemporalUnit unit) {
            if (unit instanceof ChronoUnit) {
                if (unit != ChronoUnit.FOREVER) {
                    return true;
                }
                return false;
            } else if (unit == null || !unit.isSupportedBy(_this)) {
                return false;
            } else {
                return true;
            }
        }

        public static ChronoLocalDateTime $default$with(ChronoLocalDateTime _this, TemporalAdjuster adjuster) {
            return ChronoLocalDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$with(_this, adjuster));
        }

        public static ChronoLocalDateTime $default$plus(ChronoLocalDateTime _this, TemporalAmount amount) {
            return ChronoLocalDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$plus(_this, amount));
        }

        public static ChronoLocalDateTime $default$minus(ChronoLocalDateTime _this, TemporalAmount amount) {
            return ChronoLocalDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amount));
        }

        public static ChronoLocalDateTime $default$minus(ChronoLocalDateTime _this, long amountToSubtract, TemporalUnit unit) {
            return ChronoLocalDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amountToSubtract, unit));
        }

        public static <R> Object $default$query(ChronoLocalDateTime _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset()) {
                return null;
            }
            if (temporalQuery == TemporalQueries.localTime()) {
                return _this.toLocalTime();
            }
            if (temporalQuery == TemporalQueries.chronology()) {
                return _this.getChronology();
            }
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.NANOS;
            }
            return temporalQuery.queryFrom(_this);
        }

        public static Temporal $default$adjustInto(ChronoLocalDateTime _this, Temporal temporal) {
            return temporal.with(ChronoField.EPOCH_DAY, _this.toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, _this.toLocalTime().toNanoOfDay());
        }

        public static String $default$format(ChronoLocalDateTime _this, DateTimeFormatter formatter) {
            Objects.requireNonNull(formatter, "formatter");
            return formatter.format(_this);
        }

        public static Instant $default$toInstant(ChronoLocalDateTime _this, ZoneOffset offset) {
            return Instant.ofEpochSecond(_this.toEpochSecond(offset), (long) _this.toLocalTime().getNano());
        }

        public static long $default$toEpochSecond(ChronoLocalDateTime _this, ZoneOffset offset) {
            Objects.requireNonNull(offset, "offset");
            return ((86400 * _this.toLocalDate().toEpochDay()) + ((long) _this.toLocalTime().toSecondOfDay())) - ((long) offset.getTotalSeconds());
        }

        public static int $default$compareTo(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            int cmp = _this.toLocalDate().compareTo(chronoLocalDateTime.toLocalDate());
            if (cmp != 0) {
                return cmp;
            }
            int cmp2 = _this.toLocalTime().compareTo(chronoLocalDateTime.toLocalTime());
            if (cmp2 == 0) {
                return _this.getChronology().compareTo(chronoLocalDateTime.getChronology());
            }
            return cmp2;
        }

        public static boolean $default$isAfter(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            long thisEpDay = _this.toLocalDate().toEpochDay();
            long otherEpDay = chronoLocalDateTime.toLocalDate().toEpochDay();
            return thisEpDay > otherEpDay || (thisEpDay == otherEpDay && _this.toLocalTime().toNanoOfDay() > chronoLocalDateTime.toLocalTime().toNanoOfDay());
        }

        public static boolean $default$isBefore(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            long thisEpDay = _this.toLocalDate().toEpochDay();
            long otherEpDay = chronoLocalDateTime.toLocalDate().toEpochDay();
            return thisEpDay < otherEpDay || (thisEpDay == otherEpDay && _this.toLocalTime().toNanoOfDay() < chronoLocalDateTime.toLocalTime().toNanoOfDay());
        }

        public static boolean $default$isEqual(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            return _this.toLocalTime().toNanoOfDay() == chronoLocalDateTime.toLocalTime().toNanoOfDay() && _this.toLocalDate().toEpochDay() == chronoLocalDateTime.toLocalDate().toEpochDay();
        }
    }
}
