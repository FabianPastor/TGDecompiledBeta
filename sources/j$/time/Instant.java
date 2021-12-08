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
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class Instant implements Temporal, TemporalAdjuster, Comparable<Instant>, Serializable {
    public static final Instant EPOCH = new Instant(0, 0);
    public static final Instant MAX = ofEpochSecond(31556889864403199L, NUM);
    private static final long MAX_SECOND = 31556889864403199L;
    public static final Instant MIN = ofEpochSecond(-31557014167219200L, 0);
    private static final long MIN_SECOND = -31557014167219200L;
    private static final long serialVersionUID = -665713676816604388L;
    private final int nanos;
    private final long seconds;

    public static Instant now() {
        return Clock.systemUTC().instant();
    }

    public static Instant now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return clock.instant();
    }

    public static Instant ofEpochSecond(long epochSecond) {
        return create(epochSecond, 0);
    }

    public static Instant ofEpochSecond(long epochSecond, long nanoAdjustment) {
        return create(Clock$OffsetClock$$ExternalSyntheticBackport0.m(epochSecond, Duration$$ExternalSyntheticBackport0.m(nanoAdjustment, NUM)), (int) Clock$TickClock$$ExternalSyntheticBackport0.m(nanoAdjustment, NUM));
    }

    public static Instant ofEpochMilli(long epochMilli) {
        return create(Duration$$ExternalSyntheticBackport0.m(epochMilli, 1000), 1000000 * ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(epochMilli, 1000)));
    }

    public static Instant from(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            return ofEpochSecond(temporal.getLong(ChronoField.INSTANT_SECONDS), (long) temporal.get(ChronoField.NANO_OF_SECOND));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Instant from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static Instant parse(CharSequence text) {
        return (Instant) DateTimeFormatter.ISO_INSTANT.parse(text, Instant$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static Instant create(long seconds2, int nanoOfSecond) {
        if ((((long) nanoOfSecond) | seconds2) == 0) {
            return EPOCH;
        }
        if (seconds2 >= -31557014167219200L && seconds2 <= 31556889864403199L) {
            return new Instant(seconds2, nanoOfSecond);
        }
        throw new DateTimeException("Instant exceeds minimum or maximum instant");
    }

    private Instant(long epochSecond, int nanos2) {
        this.seconds = epochSecond;
        this.nanos = nanos2;
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.INSTANT_SECONDS || field == ChronoField.NANO_OF_SECOND || field == ChronoField.MICRO_OF_SECOND || field == ChronoField.MILLI_OF_SECOND) {
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
            if (unit.isTimeBased() || unit == ChronoUnit.DAYS) {
                return true;
            }
            return false;
        } else if (unit == null || !unit.isSupportedBy(this)) {
            return false;
        } else {
            return true;
        }
    }

    public ValueRange range(TemporalField field) {
        return TemporalAccessor.CC.$default$range(this, field);
    }

    public int get(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return range(field).checkValidIntValue(field.getFrom(this), field);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return this.nanos;
            case 2:
                return this.nanos / 1000;
            case 3:
                return this.nanos / 1000000;
            case 4:
                ChronoField.INSTANT_SECONDS.checkValidIntValue(this.seconds);
                break;
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return (long) this.nanos;
            case 2:
                return (long) (this.nanos / 1000);
            case 3:
                return (long) (this.nanos / 1000000);
            case 4:
                return this.seconds;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public long getEpochSecond() {
        return this.seconds;
    }

    public int getNano() {
        return this.nanos;
    }

    public Instant with(TemporalAdjuster adjuster) {
        return (Instant) adjuster.adjustInto(this);
    }

    public Instant with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (Instant) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        f.checkValidValue(newValue);
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return newValue != ((long) this.nanos) ? create(this.seconds, (int) newValue) : this;
            case 2:
                int nval = ((int) newValue) * 1000;
                return nval != this.nanos ? create(this.seconds, nval) : this;
            case 3:
                int nval2 = ((int) newValue) * 1000000;
                return nval2 != this.nanos ? create(this.seconds, nval2) : this;
            case 4:
                return newValue != this.seconds ? create(newValue, this.nanos) : this;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public Instant truncatedTo(TemporalUnit unit) {
        if (unit == ChronoUnit.NANOS) {
            return this;
        }
        Duration unitDur = unit.getDuration();
        if (unitDur.getSeconds() <= 86400) {
            long dur = unitDur.toNanos();
            if (86400000000000L % dur == 0) {
                long nod = ((this.seconds % 86400) * NUM) + ((long) this.nanos);
                return plusNanos(((nod / dur) * dur) - nod);
            }
            throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
        }
        throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
    }

    public Instant plus(TemporalAmount amountToAdd) {
        return (Instant) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.Instant$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
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
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DAYS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.NANO_OF_SECOND.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_SECOND.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_SECOND.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.INSTANT_SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public Instant plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (Instant) unit.addTo(this, amountToAdd);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusNanos(amountToAdd);
            case 2:
                return plus(amountToAdd / 1000000, (amountToAdd % 1000000) * 1000);
            case 3:
                return plusMillis(amountToAdd);
            case 4:
                return plusSeconds(amountToAdd);
            case 5:
                return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 60));
            case 6:
                return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 3600));
            case 7:
                return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 43200));
            case 8:
                return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 86400));
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public Instant plusSeconds(long secondsToAdd) {
        return plus(secondsToAdd, 0);
    }

    public Instant plusMillis(long millisToAdd) {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    public Instant plusNanos(long nanosToAdd) {
        return plus(0, nanosToAdd);
    }

    private Instant plus(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        return ofEpochSecond(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.seconds, secondsToAdd), nanosToAdd / NUM), ((long) this.nanos) + (nanosToAdd % NUM));
    }

    public Instant minus(TemporalAmount amountToSubtract) {
        return (Instant) amountToSubtract.subtractFrom(this);
    }

    public Instant minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public Instant minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == Long.MIN_VALUE) {
            return plusSeconds(Long.MAX_VALUE).plusSeconds(1);
        }
        return plusSeconds(-secondsToSubtract);
    }

    public Instant minusMillis(long millisToSubtract) {
        if (millisToSubtract == Long.MIN_VALUE) {
            return plusMillis(Long.MAX_VALUE).plusMillis(1);
        }
        return plusMillis(-millisToSubtract);
    }

    public Instant minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == Long.MIN_VALUE) {
            return plusNanos(Long.MAX_VALUE).plusNanos(1);
        }
        return plusNanos(-nanosToSubtract);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.NANOS;
        }
        if (temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.localDate() || temporalQuery == TemporalQueries.localTime()) {
            return null;
        }
        return temporalQuery.queryFrom(this);
    }

    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.INSTANT_SECONDS, this.seconds).with(ChronoField.NANO_OF_SECOND, (long) this.nanos);
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        Instant end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return nanosUntil(end);
            case 2:
                return nanosUntil(end) / 1000;
            case 3:
                return Instant$$ExternalSyntheticBackport0.m(end.toEpochMilli(), toEpochMilli());
            case 4:
                return secondsUntil(end);
            case 5:
                return secondsUntil(end) / 60;
            case 6:
                return secondsUntil(end) / 3600;
            case 7:
                return secondsUntil(end) / 43200;
            case 8:
                return secondsUntil(end) / 86400;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    private long nanosUntil(Instant end) {
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(Instant$$ExternalSyntheticBackport0.m(end.seconds, this.seconds), NUM), (long) (end.nanos - this.nanos));
    }

    private long secondsUntil(Instant end) {
        long secsDiff = Instant$$ExternalSyntheticBackport0.m(end.seconds, this.seconds);
        long nanosDiff = (long) (end.nanos - this.nanos);
        if (secsDiff > 0 && nanosDiff < 0) {
            return secsDiff - 1;
        }
        if (secsDiff >= 0 || nanosDiff <= 0) {
            return secsDiff;
        }
        return secsDiff + 1;
    }

    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.ofInstant(this, offset);
    }

    public ZonedDateTime atZone(ZoneId zone) {
        return ZonedDateTime.ofInstant(this, zone);
    }

    public long toEpochMilli() {
        long millis = this.seconds;
        if (millis >= 0 || this.nanos <= 0) {
            return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(millis, 1000), (long) (this.nanos / 1000000));
        }
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(millis + 1, 1000), (long) ((this.nanos / 1000000) - 1000));
    }

    public int compareTo(Instant otherInstant) {
        int cmp = (this.seconds > otherInstant.seconds ? 1 : (this.seconds == otherInstant.seconds ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.nanos - otherInstant.nanos;
    }

    public boolean isAfter(Instant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    public boolean isBefore(Instant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (!(otherInstant instanceof Instant)) {
            return false;
        }
        Instant other = (Instant) otherInstant;
        if (this.seconds == other.seconds && this.nanos == other.nanos) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.seconds;
        return ((int) (j ^ (j >>> 32))) + (this.nanos * 51);
    }

    public String toString() {
        return DateTimeFormatter.ISO_INSTANT.format(this);
    }

    private Object writeReplace() {
        return new Ser((byte) 2, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeLong(this.seconds);
        out.writeInt(this.nanos);
    }

    static Instant readExternal(DataInput in) {
        return ofEpochSecond(in.readLong(), (long) in.readInt());
    }
}
