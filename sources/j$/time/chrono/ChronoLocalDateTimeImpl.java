package j$.time.chrono;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoLocalDateTime;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

final class ChronoLocalDateTimeImpl<D extends ChronoLocalDate> implements ChronoLocalDateTime<D>, Temporal, TemporalAdjuster, Serializable {
    static final int HOURS_PER_DAY = 24;
    static final long MICROS_PER_DAY = 86400000000L;
    static final long MILLIS_PER_DAY = 86400000;
    static final int MINUTES_PER_DAY = 1440;
    static final int MINUTES_PER_HOUR = 60;
    static final long NANOS_PER_DAY = 86400000000000L;
    static final long NANOS_PER_HOUR = 3600000000000L;
    static final long NANOS_PER_MINUTE = 60000000000L;
    static final long NANOS_PER_SECOND = NUM;
    static final int SECONDS_PER_DAY = 86400;
    static final int SECONDS_PER_HOUR = 3600;
    static final int SECONDS_PER_MINUTE = 60;
    private static final long serialVersionUID = 4556003607393004514L;
    private final transient D date;
    private final transient LocalTime time;

    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        return ChronoLocalDateTime.CC.$default$adjustInto(this, temporal);
    }

    public /* synthetic */ int compareTo(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$compareTo((ChronoLocalDateTime) this, chronoLocalDateTime);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return compareTo((ChronoLocalDateTime) obj);
    }

    public /* synthetic */ String format(DateTimeFormatter dateTimeFormatter) {
        return ChronoLocalDateTime.CC.$default$format(this, dateTimeFormatter);
    }

    public /* synthetic */ Chronology getChronology() {
        return ChronoLocalDateTime.CC.$default$getChronology(this);
    }

    public /* synthetic */ boolean isAfter(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isAfter(this, chronoLocalDateTime);
    }

    public /* synthetic */ boolean isBefore(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isBefore(this, chronoLocalDateTime);
    }

    public /* synthetic */ boolean isEqual(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isEqual(this, chronoLocalDateTime);
    }

    public /* synthetic */ boolean isSupported(TemporalUnit temporalUnit) {
        return ChronoLocalDateTime.CC.$default$isSupported(this, temporalUnit);
    }

    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return ChronoLocalDateTime.CC.$default$query(this, temporalQuery);
    }

    public /* synthetic */ long toEpochSecond(ZoneOffset zoneOffset) {
        return ChronoLocalDateTime.CC.$default$toEpochSecond(this, zoneOffset);
    }

    public /* synthetic */ Instant toInstant(ZoneOffset zoneOffset) {
        return ChronoLocalDateTime.CC.$default$toInstant(this, zoneOffset);
    }

    static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> of(R date2, LocalTime time2) {
        return new ChronoLocalDateTimeImpl<>(date2, time2);
    }

    static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> ensureValid(Chronology chrono, Temporal temporal) {
        java.time.chrono.ChronoLocalDateTimeImpl<R> other = (ChronoLocalDateTimeImpl) temporal;
        if (chrono.equals(other.getChronology())) {
            return other;
        }
        throw new ClassCastException("Chronology mismatch, required: " + chrono.getId() + ", actual: " + other.getChronology().getId());
    }

    private ChronoLocalDateTimeImpl(D date2, LocalTime time2) {
        Objects.requireNonNull(date2, "date");
        Objects.requireNonNull(time2, "time");
        this.date = date2;
        this.time = time2;
    }

    private ChronoLocalDateTimeImpl<D> with(Temporal newDate, LocalTime newTime) {
        D d = this.date;
        if (d == newDate && this.time == newTime) {
            return this;
        }
        return new ChronoLocalDateTimeImpl<>(ChronoLocalDateImpl.ensureValid(d.getChronology(), newDate), newTime);
    }

    public D toLocalDate() {
        return this.date;
    }

    public LocalTime toLocalTime() {
        return this.time;
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (f.isDateBased() || f.isTimeBased()) {
                return true;
            }
            return false;
        } else if (field == null || !field.isSupportedBy(this)) {
            return false;
        } else {
            return true;
        }
    }

    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            return ((ChronoField) field).isTimeBased() ? this.time.range(field) : this.date.range(field);
        }
        return field.rangeRefinedBy(this);
    }

    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            return ((ChronoField) field).isTimeBased() ? this.time.get(field) : this.date.get(field);
        }
        return range(field).checkValidIntValue(getLong(field), field);
    }

    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            return ((ChronoField) field).isTimeBased() ? this.time.getLong(field) : this.date.getLong(field);
        }
        return field.getFrom(this);
    }

    public ChronoLocalDateTimeImpl<D> with(TemporalAdjuster adjuster) {
        if (adjuster instanceof ChronoLocalDate) {
            return with((Temporal) (ChronoLocalDate) adjuster, this.time);
        }
        if (adjuster instanceof LocalTime) {
            return with((Temporal) this.date, (LocalTime) adjuster);
        }
        if (adjuster instanceof ChronoLocalDateTimeImpl) {
            return ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl) adjuster);
        }
        return ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl) adjuster.adjustInto(this));
    }

    public ChronoLocalDateTimeImpl<D> with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return ensureValid(this.date.getChronology(), field.adjustInto(this, newValue));
        }
        if (((ChronoField) field).isTimeBased()) {
            return with((Temporal) this.date, this.time.with(field, newValue));
        }
        return with((Temporal) this.date.with(field, newValue), this.time);
    }

    public ChronoLocalDateTimeImpl<D> plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return ensureValid(this.date.getChronology(), unit.addTo(this, amountToAdd));
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusNanos(amountToAdd);
            case 2:
                return plusDays(amountToAdd / 86400000000L).plusNanos((amountToAdd % 86400000000L) * 1000);
            case 3:
                return plusDays(amountToAdd / 86400000).plusNanos((amountToAdd % 86400000) * 1000000);
            case 4:
                return plusSeconds(amountToAdd);
            case 5:
                return plusMinutes(amountToAdd);
            case 6:
                return plusHours(amountToAdd);
            case 7:
                return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);
            default:
                return with((Temporal) this.date.plus(amountToAdd, unit), this.time);
        }
    }

    /* renamed from: j$.time.chrono.ChronoLocalDateTimeImpl$1  reason: invalid class name */
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

    private ChronoLocalDateTimeImpl<D> plusDays(long days) {
        return with((Temporal) this.date.plus(days, ChronoUnit.DAYS), this.time);
    }

    private ChronoLocalDateTimeImpl<D> plusHours(long hours) {
        return plusWithOverflow(this.date, hours, 0, 0, 0);
    }

    private ChronoLocalDateTimeImpl<D> plusMinutes(long minutes) {
        return plusWithOverflow(this.date, 0, minutes, 0, 0);
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDateTimeImpl<D> plusSeconds(long seconds) {
        return plusWithOverflow(this.date, 0, 0, seconds, 0);
    }

    private ChronoLocalDateTimeImpl<D> plusNanos(long nanos) {
        return plusWithOverflow(this.date, 0, 0, 0, nanos);
    }

    private ChronoLocalDateTimeImpl<D> plusWithOverflow(D newDate, long hours, long minutes, long seconds, long nanos) {
        D d = newDate;
        if ((hours | minutes | seconds | nanos) == 0) {
            return with((Temporal) d, this.time);
        }
        long curNoD = this.time.toNanoOfDay();
        long totNanos = (nanos % 86400000000000L) + ((seconds % 86400) * NUM) + ((minutes % 1440) * 60000000000L) + ((hours % 24) * 3600000000000L) + curNoD;
        long totDays = (nanos / 86400000000000L) + (seconds / 86400) + (minutes / 1440) + (hours / 24) + Duration$$ExternalSyntheticBackport0.m(totNanos, 86400000000000L);
        long newNoD = Clock$TickClock$$ExternalSyntheticBackport0.m(totNanos, 86400000000000L);
        return with((Temporal) d.plus(totDays, ChronoUnit.DAYS), newNoD == curNoD ? this.time : LocalTime.ofNanoOfDay(newNoD));
    }

    public ChronoZonedDateTime<D> atZone(ZoneId zone) {
        return ChronoZonedDateTimeImpl.ofBest(this, zone, (ZoneOffset) null);
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        Objects.requireNonNull(endExclusive, "endExclusive");
        ChronoLocalDateTime localDateTime = getChronology().localDateTime(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            Objects.requireNonNull(unit, "unit");
            return unit.between(this, localDateTime);
        } else if (unit.isTimeBased()) {
            long amount = localDateTime.getLong(ChronoField.EPOCH_DAY) - this.date.getLong(ChronoField.EPOCH_DAY);
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000000000L);
                    break;
                case 2:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000000L);
                    break;
                case 3:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000);
                    break;
                case 4:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400);
                    break;
                case 5:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 1440);
                    break;
                case 6:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 24);
                    break;
                case 7:
                    amount = Duration$$ExternalSyntheticBackport1.m(amount, 2);
                    break;
            }
            return Clock$OffsetClock$$ExternalSyntheticBackport0.m(amount, this.time.until(localDateTime.toLocalTime(), unit));
        } else {
            ChronoLocalDate endDate = localDateTime.toLocalDate();
            if (localDateTime.toLocalTime().isBefore(this.time)) {
                endDate = endDate.minus(1, ChronoUnit.DAYS);
            }
            return this.date.until(endDate, unit);
        }
    }

    private Object writeReplace() {
        return new Ser((byte) 2, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(ObjectOutput out) {
        out.writeObject(this.date);
        out.writeObject(this.time);
    }

    static ChronoLocalDateTime<?> readExternal(ObjectInput in) {
        return ((ChronoLocalDate) in.readObject()).atTime((LocalTime) in.readObject());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChronoLocalDateTime) || compareTo((ChronoLocalDateTime) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return toLocalDate().hashCode() ^ toLocalTime().hashCode();
    }

    public String toString() {
        return toLocalDate().toString() + 'T' + toLocalTime().toString();
    }
}
