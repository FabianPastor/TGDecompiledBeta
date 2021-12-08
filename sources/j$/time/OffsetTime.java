package j$.time;

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
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

public final class OffsetTime implements Temporal, TemporalAdjuster, Comparable<OffsetTime>, Serializable {
    public static final OffsetTime MAX = LocalTime.MAX.atOffset(ZoneOffset.MIN);
    public static final OffsetTime MIN = LocalTime.MIN.atOffset(ZoneOffset.MAX);
    private static final long serialVersionUID = 7264499704384272492L;
    private final ZoneOffset offset;
    private final LocalTime time;

    public static OffsetTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static OffsetTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static OffsetTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    public static OffsetTime of(LocalTime time2, ZoneOffset offset2) {
        return new OffsetTime(time2, offset2);
    }

    public static OffsetTime of(int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset2) {
        return new OffsetTime(LocalTime.of(hour, minute, second, nanoOfSecond), offset2);
    }

    public static OffsetTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        ZoneOffset offset2 = zone.getRules().getOffset(instant);
        return new OffsetTime(LocalTime.ofNanoOfDay((((long) ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(instant.getEpochSecond() + ((long) offset2.getTotalSeconds()), 86400))) * NUM) + ((long) instant.getNano())), offset2);
    }

    public static OffsetTime from(TemporalAccessor temporal) {
        if (temporal instanceof OffsetTime) {
            return (OffsetTime) temporal;
        }
        try {
            return new OffsetTime(LocalTime.from(temporal), ZoneOffset.from(temporal));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain OffsetTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static OffsetTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_OFFSET_TIME);
    }

    public static OffsetTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (OffsetTime) formatter.parse(text, OffsetTime$$ExternalSyntheticLambda0.INSTANCE);
    }

    private OffsetTime(LocalTime time2, ZoneOffset offset2) {
        this.time = (LocalTime) Objects.requireNonNull(time2, "time");
        this.offset = (ZoneOffset) Objects.requireNonNull(offset2, "offset");
    }

    private OffsetTime with(LocalTime time2, ZoneOffset offset2) {
        if (this.time != time2 || !this.offset.equals(offset2)) {
            return new OffsetTime(time2, offset2);
        }
        return this;
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field.isTimeBased() || field == ChronoField.OFFSET_SECONDS) {
                return true;
            }
            return false;
        } else if (field == null || !field.isSupportedBy(this)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSupported(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return unit.isTimeBased();
        }
        return unit != null && unit.isSupportedBy(this);
    }

    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.rangeRefinedBy(this);
        }
        if (field == ChronoField.OFFSET_SECONDS) {
            return field.range();
        }
        return this.time.range(field);
    }

    public int get(TemporalField field) {
        return TemporalAccessor.CC.$default$get(this, field);
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        if (field == ChronoField.OFFSET_SECONDS) {
            return (long) this.offset.getTotalSeconds();
        }
        return this.time.getLong(field);
    }

    public ZoneOffset getOffset() {
        return this.offset;
    }

    public OffsetTime withOffsetSameLocal(ZoneOffset offset2) {
        return (offset2 == null || !offset2.equals(this.offset)) ? new OffsetTime(this.time, offset2) : this;
    }

    public OffsetTime withOffsetSameInstant(ZoneOffset offset2) {
        if (offset2.equals(this.offset)) {
            return this;
        }
        return new OffsetTime(this.time.plusSeconds((long) (offset2.getTotalSeconds() - this.offset.getTotalSeconds())), offset2);
    }

    public LocalTime toLocalTime() {
        return this.time;
    }

    public int getHour() {
        return this.time.getHour();
    }

    public int getMinute() {
        return this.time.getMinute();
    }

    public int getSecond() {
        return this.time.getSecond();
    }

    public int getNano() {
        return this.time.getNano();
    }

    public OffsetTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalTime) {
            return with((LocalTime) adjuster, this.offset);
        }
        if (adjuster instanceof ZoneOffset) {
            return with(this.time, (ZoneOffset) adjuster);
        }
        if (adjuster instanceof OffsetTime) {
            return (OffsetTime) adjuster;
        }
        return (OffsetTime) adjuster.adjustInto(this);
    }

    public OffsetTime with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (OffsetTime) field.adjustInto(this, newValue);
        }
        if (field == ChronoField.OFFSET_SECONDS) {
            return with(this.time, ZoneOffset.ofTotalSeconds(((ChronoField) field).checkValidIntValue(newValue)));
        }
        return with(this.time.with(field, newValue), this.offset);
    }

    public OffsetTime withHour(int hour) {
        return with(this.time.withHour(hour), this.offset);
    }

    public OffsetTime withMinute(int minute) {
        return with(this.time.withMinute(minute), this.offset);
    }

    public OffsetTime withSecond(int second) {
        return with(this.time.withSecond(second), this.offset);
    }

    public OffsetTime withNano(int nanoOfSecond) {
        return with(this.time.withNano(nanoOfSecond), this.offset);
    }

    public OffsetTime truncatedTo(TemporalUnit unit) {
        return with(this.time.truncatedTo(unit), this.offset);
    }

    public OffsetTime plus(TemporalAmount amountToAdd) {
        return (OffsetTime) amountToAdd.addTo(this);
    }

    public OffsetTime plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(this.time.plus(amountToAdd, unit), this.offset);
        }
        return (OffsetTime) unit.addTo(this, amountToAdd);
    }

    public OffsetTime plusHours(long hours) {
        return with(this.time.plusHours(hours), this.offset);
    }

    public OffsetTime plusMinutes(long minutes) {
        return with(this.time.plusMinutes(minutes), this.offset);
    }

    public OffsetTime plusSeconds(long seconds) {
        return with(this.time.plusSeconds(seconds), this.offset);
    }

    public OffsetTime plusNanos(long nanos) {
        return with(this.time.plusNanos(nanos), this.offset);
    }

    public OffsetTime minus(TemporalAmount amountToSubtract) {
        return (OffsetTime) amountToSubtract.subtractFrom(this);
    }

    public OffsetTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public OffsetTime minusHours(long hours) {
        return with(this.time.minusHours(hours), this.offset);
    }

    public OffsetTime minusMinutes(long minutes) {
        return with(this.time.minusMinutes(minutes), this.offset);
    }

    public OffsetTime minusSeconds(long seconds) {
        return with(this.time.minusSeconds(seconds), this.offset);
    }

    public OffsetTime minusNanos(long nanos) {
        return with(this.time.minusNanos(nanos), this.offset);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.zone()) {
            return this.offset;
        }
        boolean z = true;
        boolean z2 = temporalQuery == TemporalQueries.zoneId();
        if (temporalQuery != TemporalQueries.chronology()) {
            z = false;
        }
        if ((z2 || z) || temporalQuery == TemporalQueries.localDate()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.localTime()) {
            return this.time;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.NANOS;
        }
        return temporalQuery.queryFrom(this);
    }

    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.NANO_OF_DAY, this.time.toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, (long) this.offset.getTotalSeconds());
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        OffsetTime end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        long nanosUntil = end.toEpochNano() - toEpochNano();
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return nanosUntil;
            case 2:
                return nanosUntil / 1000;
            case 3:
                return nanosUntil / 1000000;
            case 4:
                return nanosUntil / NUM;
            case 5:
                return nanosUntil / 60000000000L;
            case 6:
                return nanosUntil / 3600000000000L;
            case 7:
                return nanosUntil / 43200000000000L;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    /* renamed from: j$.time.OffsetTime$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.NANOS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MICROS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HALF_DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public OffsetDateTime atDate(LocalDate date) {
        return OffsetDateTime.of(date, this.time, this.offset);
    }

    private long toEpochNano() {
        return this.time.toNanoOfDay() - (((long) this.offset.getTotalSeconds()) * NUM);
    }

    public int compareTo(OffsetTime other) {
        if (this.offset.equals(other.offset)) {
            return this.time.compareTo(other.time);
        }
        int compare = (toEpochNano() > other.toEpochNano() ? 1 : (toEpochNano() == other.toEpochNano() ? 0 : -1));
        if (compare == 0) {
            return this.time.compareTo(other.time);
        }
        return compare;
    }

    public boolean isAfter(OffsetTime other) {
        return toEpochNano() > other.toEpochNano();
    }

    public boolean isBefore(OffsetTime other) {
        return toEpochNano() < other.toEpochNano();
    }

    public boolean isEqual(OffsetTime other) {
        return toEpochNano() == other.toEpochNano();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OffsetTime)) {
            return false;
        }
        OffsetTime other = (OffsetTime) obj;
        if (!this.time.equals(other.time) || !this.offset.equals(other.offset)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.time.hashCode() ^ this.offset.hashCode();
    }

    public String toString() {
        return this.time.toString() + this.offset.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 9, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(ObjectOutput out) {
        this.time.writeExternal(out);
        this.offset.writeExternal(out);
    }

    static OffsetTime readExternal(ObjectInput in) {
        return of(LocalTime.readExternal(in), ZoneOffset.readExternal(in));
    }
}
