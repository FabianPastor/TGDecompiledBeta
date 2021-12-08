package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.LocalTime;
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
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.util.Objects;
import java.util.Comparator;

public interface ChronoLocalDate extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDate> {
    Temporal adjustInto(Temporal temporal);

    ChronoLocalDateTime<?> atTime(LocalTime localTime);

    int compareTo(ChronoLocalDate chronoLocalDate);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    Chronology getChronology();

    Era getEra();

    int hashCode();

    boolean isAfter(ChronoLocalDate chronoLocalDate);

    boolean isBefore(ChronoLocalDate chronoLocalDate);

    boolean isEqual(ChronoLocalDate chronoLocalDate);

    boolean isLeapYear();

    boolean isSupported(TemporalField temporalField);

    boolean isSupported(TemporalUnit temporalUnit);

    int lengthOfMonth();

    int lengthOfYear();

    ChronoLocalDate minus(long j, TemporalUnit temporalUnit);

    ChronoLocalDate minus(TemporalAmount temporalAmount);

    ChronoLocalDate plus(long j, TemporalUnit temporalUnit);

    ChronoLocalDate plus(TemporalAmount temporalAmount);

    <R> R query(TemporalQuery<R> temporalQuery);

    long toEpochDay();

    String toString();

    long until(Temporal temporal, TemporalUnit temporalUnit);

    ChronoPeriod until(ChronoLocalDate chronoLocalDate);

    ChronoLocalDate with(TemporalAdjuster temporalAdjuster);

    ChronoLocalDate with(TemporalField temporalField, long j);

    /* renamed from: j$.time.chrono.ChronoLocalDate$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoLocalDate> timeLineOrder() {
            return AbstractChronology.DATE_ORDER;
        }

        public static ChronoLocalDate from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoLocalDate) {
                return (ChronoLocalDate) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono != null) {
                return chrono.date(temporal);
            }
            throw new DateTimeException("Unable to obtain ChronoLocalDate from TemporalAccessor: " + temporal.getClass());
        }

        public static Era $default$getEra(ChronoLocalDate _this) {
            return _this.getChronology().eraOf(_this.get(ChronoField.ERA));
        }

        public static boolean $default$isLeapYear(ChronoLocalDate _this) {
            return _this.getChronology().isLeapYear(_this.getLong(ChronoField.YEAR));
        }

        public static int $default$lengthOfYear(ChronoLocalDate _this) {
            return _this.isLeapYear() ? 366 : 365;
        }

        public static boolean $default$isSupported(ChronoLocalDate _this, TemporalField field) {
            if (field instanceof ChronoField) {
                return field.isDateBased();
            }
            return field != null && field.isSupportedBy(_this);
        }

        public static boolean $default$isSupported(ChronoLocalDate _this, TemporalUnit unit) {
            if (unit instanceof ChronoUnit) {
                return unit.isDateBased();
            }
            return unit != null && unit.isSupportedBy(_this);
        }

        public static ChronoLocalDate $default$with(ChronoLocalDate _this, TemporalAdjuster adjuster) {
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$with(_this, adjuster));
        }

        public static ChronoLocalDate $default$with(ChronoLocalDate _this, TemporalField field, long newValue) {
            if (!(field instanceof ChronoField)) {
                return ChronoLocalDateImpl.ensureValid(_this.getChronology(), field.adjustInto(_this, newValue));
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }

        public static ChronoLocalDate $default$plus(ChronoLocalDate _this, TemporalAmount amount) {
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$plus(_this, amount));
        }

        public static ChronoLocalDate $default$plus(ChronoLocalDate _this, long amountToAdd, TemporalUnit unit) {
            if (!(unit instanceof ChronoUnit)) {
                return ChronoLocalDateImpl.ensureValid(_this.getChronology(), unit.addTo(_this, amountToAdd));
            }
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }

        public static ChronoLocalDate $default$minus(ChronoLocalDate _this, TemporalAmount amount) {
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amount));
        }

        public static ChronoLocalDate $default$minus(ChronoLocalDate _this, long amountToSubtract, TemporalUnit unit) {
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amountToSubtract, unit));
        }

        public static <R> Object $default$query(ChronoLocalDate _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.localTime()) {
                return null;
            }
            if (temporalQuery == TemporalQueries.chronology()) {
                return _this.getChronology();
            }
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.DAYS;
            }
            return temporalQuery.queryFrom(_this);
        }

        public static Temporal $default$adjustInto(ChronoLocalDate _this, Temporal temporal) {
            return temporal.with(ChronoField.EPOCH_DAY, _this.toEpochDay());
        }

        public static String $default$format(ChronoLocalDate _this, DateTimeFormatter formatter) {
            Objects.requireNonNull(formatter, "formatter");
            return formatter.format(_this);
        }

        public static ChronoLocalDateTime $default$atTime(ChronoLocalDate _this, LocalTime localTime) {
            return ChronoLocalDateTimeImpl.of(_this, localTime);
        }

        public static long $default$toEpochDay(ChronoLocalDate _this) {
            return _this.getLong(ChronoField.EPOCH_DAY);
        }

        public static int $default$compareTo(ChronoLocalDate _this, ChronoLocalDate other) {
            int cmp = (_this.toEpochDay() > other.toEpochDay() ? 1 : (_this.toEpochDay() == other.toEpochDay() ? 0 : -1));
            if (cmp == 0) {
                return _this.getChronology().compareTo(other.getChronology());
            }
            return cmp;
        }

        public static boolean $default$isAfter(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() > other.toEpochDay();
        }

        public static boolean $default$isBefore(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() < other.toEpochDay();
        }

        public static boolean $default$isEqual(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() == other.toEpochDay();
        }
    }
}
