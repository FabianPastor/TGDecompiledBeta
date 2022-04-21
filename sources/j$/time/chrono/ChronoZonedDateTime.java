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
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.util.Comparator;

public interface ChronoZonedDateTime<D extends ChronoLocalDate> extends Temporal, Comparable<ChronoZonedDateTime<?>> {
    int compareTo(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    int get(TemporalField temporalField);

    Chronology getChronology();

    long getLong(TemporalField temporalField);

    ZoneOffset getOffset();

    ZoneId getZone();

    int hashCode();

    boolean isAfter(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean isBefore(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean isEqual(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean isSupported(TemporalField temporalField);

    boolean isSupported(TemporalUnit temporalUnit);

    ChronoZonedDateTime<D> minus(long j, TemporalUnit temporalUnit);

    ChronoZonedDateTime<D> minus(TemporalAmount temporalAmount);

    ChronoZonedDateTime<D> plus(long j, TemporalUnit temporalUnit);

    ChronoZonedDateTime<D> plus(TemporalAmount temporalAmount);

    <R> R query(TemporalQuery<R> temporalQuery);

    ValueRange range(TemporalField temporalField);

    long toEpochSecond();

    Instant toInstant();

    D toLocalDate();

    ChronoLocalDateTime<D> toLocalDateTime();

    LocalTime toLocalTime();

    String toString();

    ChronoZonedDateTime<D> with(TemporalAdjuster temporalAdjuster);

    ChronoZonedDateTime<D> with(TemporalField temporalField, long j);

    ChronoZonedDateTime<D> withEarlierOffsetAtOverlap();

    ChronoZonedDateTime<D> withLaterOffsetAtOverlap();

    ChronoZonedDateTime<D> withZoneSameInstant(ZoneId zoneId);

    ChronoZonedDateTime<D> withZoneSameLocal(ZoneId zoneId);

    /* renamed from: j$.time.chrono.ChronoZonedDateTime$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoZonedDateTime<?>> timeLineOrder() {
            return AbstractChronology.INSTANT_ORDER;
        }

        public static ChronoZonedDateTime<?> from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoZonedDateTime) {
                return (ChronoZonedDateTime) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono != null) {
                return chrono.zonedDateTime(temporal);
            }
            throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + temporal.getClass());
        }

        public static ValueRange $default$range(ChronoZonedDateTime _this, TemporalField field) {
            if (!(field instanceof ChronoField)) {
                return field.rangeRefinedBy(_this);
            }
            if (field == ChronoField.INSTANT_SECONDS || field == ChronoField.OFFSET_SECONDS) {
                return field.range();
            }
            return _this.toLocalDateTime().range(field);
        }

        public static int $default$get(ChronoZonedDateTime _this, TemporalField field) {
            if (!(field instanceof ChronoField)) {
                return TemporalAccessor.CC.$default$get(_this, field);
            }
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                case 1:
                    throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
                case 2:
                    return _this.getOffset().getTotalSeconds();
                default:
                    return _this.toLocalDateTime().get(field);
            }
        }

        public static long $default$getLong(ChronoZonedDateTime _this, TemporalField field) {
            if (!(field instanceof ChronoField)) {
                return field.getFrom(_this);
            }
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                case 1:
                    return _this.toEpochSecond();
                case 2:
                    return (long) _this.getOffset().getTotalSeconds();
                default:
                    return _this.toLocalDateTime().getLong(field);
            }
        }

        public static ChronoLocalDate $default$toLocalDate(ChronoZonedDateTime _this) {
            return _this.toLocalDateTime().toLocalDate();
        }

        public static LocalTime $default$toLocalTime(ChronoZonedDateTime _this) {
            return _this.toLocalDateTime().toLocalTime();
        }

        public static Chronology $default$getChronology(ChronoZonedDateTime _this) {
            return _this.toLocalDate().getChronology();
        }

        public static boolean $default$isSupported(ChronoZonedDateTime _this, TemporalUnit unit) {
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

        public static ChronoZonedDateTime $default$with(ChronoZonedDateTime _this, TemporalAdjuster adjuster) {
            return ChronoZonedDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$with(_this, adjuster));
        }

        public static ChronoZonedDateTime $default$plus(ChronoZonedDateTime _this, TemporalAmount amount) {
            return ChronoZonedDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$plus(_this, amount));
        }

        public static ChronoZonedDateTime $default$minus(ChronoZonedDateTime _this, TemporalAmount amount) {
            return ChronoZonedDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amount));
        }

        public static ChronoZonedDateTime $default$minus(ChronoZonedDateTime _this, long amountToSubtract, TemporalUnit unit) {
            return ChronoZonedDateTimeImpl.ensureValid(_this.getChronology(), Temporal.CC.$default$minus(_this, amountToSubtract, unit));
        }

        public static <R> Object $default$query(ChronoZonedDateTime _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.zoneId()) {
                return _this.getZone();
            }
            if (temporalQuery == TemporalQueries.offset()) {
                return _this.getOffset();
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

        public static String $default$format(ChronoZonedDateTime _this, DateTimeFormatter formatter) {
            Objects.requireNonNull(formatter, "formatter");
            return formatter.format(_this);
        }

        public static Instant $default$toInstant(ChronoZonedDateTime _this) {
            return Instant.ofEpochSecond(_this.toEpochSecond(), (long) _this.toLocalTime().getNano());
        }

        public static long $default$toEpochSecond(ChronoZonedDateTime _this) {
            return ((86400 * _this.toLocalDate().toEpochDay()) + ((long) _this.toLocalTime().toSecondOfDay())) - ((long) _this.getOffset().getTotalSeconds());
        }

        public static int $default$compareTo(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            int cmp = (_this.toEpochSecond() > chronoZonedDateTime.toEpochSecond() ? 1 : (_this.toEpochSecond() == chronoZonedDateTime.toEpochSecond() ? 0 : -1));
            if (cmp != 0) {
                return cmp;
            }
            int cmp2 = _this.toLocalTime().getNano() - chronoZonedDateTime.toLocalTime().getNano();
            if (cmp2 != 0) {
                return cmp2;
            }
            int cmp3 = _this.toLocalDateTime().compareTo(chronoZonedDateTime.toLocalDateTime());
            if (cmp3 != 0) {
                return cmp3;
            }
            int cmp4 = _this.getZone().getId().compareTo(chronoZonedDateTime.getZone().getId());
            if (cmp4 == 0) {
                return _this.getChronology().compareTo(chronoZonedDateTime.getChronology());
            }
            return cmp4;
        }

        public static boolean $default$isBefore(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            long thisEpochSec = _this.toEpochSecond();
            long otherEpochSec = chronoZonedDateTime.toEpochSecond();
            return thisEpochSec < otherEpochSec || (thisEpochSec == otherEpochSec && _this.toLocalTime().getNano() < chronoZonedDateTime.toLocalTime().getNano());
        }

        public static boolean $default$isAfter(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            long thisEpochSec = _this.toEpochSecond();
            long otherEpochSec = chronoZonedDateTime.toEpochSecond();
            return thisEpochSec > otherEpochSec || (thisEpochSec == otherEpochSec && _this.toLocalTime().getNano() > chronoZonedDateTime.toLocalTime().getNano());
        }

        public static boolean $default$isEqual(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            return _this.toEpochSecond() == chronoZonedDateTime.toEpochSecond() && _this.toLocalTime().getNano() == chronoZonedDateTime.toLocalTime().getNano();
        }
    }

    /* renamed from: j$.time.chrono.ChronoZonedDateTime$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.INSTANT_SECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.OFFSET_SECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
