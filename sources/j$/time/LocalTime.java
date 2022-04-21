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

public final class LocalTime implements Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable {
    private static final LocalTime[] HOURS = new LocalTime[24];
    static final int HOURS_PER_DAY = 24;
    public static final LocalTime MAX = new LocalTime(23, 59, 59, NUM);
    static final long MICROS_PER_DAY = 86400000000L;
    public static final LocalTime MIDNIGHT;
    static final long MILLIS_PER_DAY = 86400000;
    public static final LocalTime MIN;
    static final int MINUTES_PER_DAY = 1440;
    static final int MINUTES_PER_HOUR = 60;
    static final long NANOS_PER_DAY = 86400000000000L;
    static final long NANOS_PER_HOUR = 3600000000000L;
    static final long NANOS_PER_MINUTE = 60000000000L;
    static final long NANOS_PER_SECOND = NUM;
    public static final LocalTime NOON;
    static final int SECONDS_PER_DAY = 86400;
    static final int SECONDS_PER_HOUR = 3600;
    static final int SECONDS_PER_MINUTE = 60;
    private static final long serialVersionUID = 6414437269572265201L;
    private final byte hour;
    private final byte minute;
    private final int nano;
    private final byte second;

    static {
        int i = 0;
        while (true) {
            LocalTime[] localTimeArr = HOURS;
            if (i < localTimeArr.length) {
                localTimeArr[i] = new LocalTime(i, 0, 0, 0);
                i++;
            } else {
                MIDNIGHT = localTimeArr[0];
                NOON = localTimeArr[12];
                MIN = localTimeArr[0];
                return;
            }
        }
    }

    public static LocalTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static LocalTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static LocalTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        return ofNanoOfDay((((long) ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(now.getEpochSecond() + ((long) clock.getZone().getRules().getOffset(now).getTotalSeconds()), 86400))) * NUM) + ((long) now.getNano()));
    }

    public static LocalTime of(int hour2, int minute2) {
        ChronoField.HOUR_OF_DAY.checkValidValue((long) hour2);
        if (minute2 == 0) {
            return HOURS[hour2];
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue((long) minute2);
        return new LocalTime(hour2, minute2, 0, 0);
    }

    public static LocalTime of(int hour2, int minute2, int second2) {
        ChronoField.HOUR_OF_DAY.checkValidValue((long) hour2);
        if ((minute2 | second2) == 0) {
            return HOURS[hour2];
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue((long) minute2);
        ChronoField.SECOND_OF_MINUTE.checkValidValue((long) second2);
        return new LocalTime(hour2, minute2, second2, 0);
    }

    public static LocalTime of(int hour2, int minute2, int second2, int nanoOfSecond) {
        ChronoField.HOUR_OF_DAY.checkValidValue((long) hour2);
        ChronoField.MINUTE_OF_HOUR.checkValidValue((long) minute2);
        ChronoField.SECOND_OF_MINUTE.checkValidValue((long) second2);
        ChronoField.NANO_OF_SECOND.checkValidValue((long) nanoOfSecond);
        return create(hour2, minute2, second2, nanoOfSecond);
    }

    public static LocalTime ofSecondOfDay(long secondOfDay) {
        ChronoField.SECOND_OF_DAY.checkValidValue(secondOfDay);
        int hours = (int) (secondOfDay / 3600);
        long secondOfDay2 = secondOfDay - ((long) (hours * 3600));
        int minutes = (int) (secondOfDay2 / 60);
        return create(hours, minutes, (int) (secondOfDay2 - ((long) (minutes * 60))), 0);
    }

    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        ChronoField.NANO_OF_DAY.checkValidValue(nanoOfDay);
        int hours = (int) (nanoOfDay / 3600000000000L);
        long nanoOfDay2 = nanoOfDay - (((long) hours) * 3600000000000L);
        int minutes = (int) (nanoOfDay2 / 60000000000L);
        long nanoOfDay3 = nanoOfDay2 - (((long) minutes) * 60000000000L);
        int seconds = (int) (nanoOfDay3 / NUM);
        return create(hours, minutes, seconds, (int) (nanoOfDay3 - (((long) seconds) * NUM)));
    }

    public static LocalTime from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        LocalTime time = (LocalTime) temporal.query(TemporalQueries.localTime());
        if (time != null) {
            return time;
        }
        throw new DateTimeException("Unable to obtain LocalTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    public static LocalTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static LocalTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (LocalTime) formatter.parse(text, LocalTime$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static LocalTime create(int hour2, int minute2, int second2, int nanoOfSecond) {
        if ((minute2 | second2 | nanoOfSecond) == 0) {
            return HOURS[hour2];
        }
        return new LocalTime(hour2, minute2, second2, nanoOfSecond);
    }

    private LocalTime(int hour2, int minute2, int second2, int nanoOfSecond) {
        this.hour = (byte) hour2;
        this.minute = (byte) minute2;
        this.second = (byte) second2;
        this.nano = nanoOfSecond;
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field.isTimeBased();
        }
        return field != null && field.isSupportedBy(this);
    }

    public boolean isSupported(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return unit.isTimeBased();
        }
        return unit != null && unit.isSupportedBy(this);
    }

    public ValueRange range(TemporalField field) {
        return TemporalAccessor.CC.$default$range(this, field);
    }

    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            return get0(field);
        }
        return TemporalAccessor.CC.$default$get(this, field);
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        if (field == ChronoField.NANO_OF_DAY) {
            return toNanoOfDay();
        }
        if (field == ChronoField.MICRO_OF_DAY) {
            return toNanoOfDay() / 1000;
        }
        return (long) get0(field);
    }

    private int get0(TemporalField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return this.nano;
            case 2:
                throw new UnsupportedTemporalTypeException("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
            case 3:
                return this.nano / 1000;
            case 4:
                throw new UnsupportedTemporalTypeException("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
            case 5:
                return this.nano / 1000000;
            case 6:
                return (int) (toNanoOfDay() / 1000000);
            case 7:
                return this.second;
            case 8:
                return toSecondOfDay();
            case 9:
                return this.minute;
            case 10:
                return (this.hour * 60) + this.minute;
            case 11:
                return this.hour % 12;
            case 12:
                int ham = this.hour % 12;
                if (ham % 12 == 0) {
                    return 12;
                }
                return ham;
            case 13:
                return this.hour;
            case 14:
                byte b = this.hour;
                if (b == 0) {
                    return 24;
                }
                return b;
            case 15:
                return this.hour / 12;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getSecond() {
        return this.second;
    }

    public int getNano() {
        return this.nano;
    }

    public LocalTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalTime) {
            return (LocalTime) adjuster;
        }
        return (LocalTime) adjuster.adjustInto(this);
    }

    public LocalTime with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (LocalTime) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        f.checkValidValue(newValue);
        long j = 0;
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return withNano((int) newValue);
            case 2:
                return ofNanoOfDay(newValue);
            case 3:
                return withNano(((int) newValue) * 1000);
            case 4:
                return ofNanoOfDay(1000 * newValue);
            case 5:
                return withNano(((int) newValue) * 1000000);
            case 6:
                return ofNanoOfDay(1000000 * newValue);
            case 7:
                return withSecond((int) newValue);
            case 8:
                return plusSeconds(newValue - ((long) toSecondOfDay()));
            case 9:
                return withMinute((int) newValue);
            case 10:
                return plusMinutes(newValue - ((long) ((this.hour * 60) + this.minute)));
            case 11:
                return plusHours(newValue - ((long) (this.hour % 12)));
            case 12:
                if (newValue != 12) {
                    j = newValue;
                }
                return plusHours(j - ((long) (this.hour % 12)));
            case 13:
                return withHour((int) newValue);
            case 14:
                if (newValue != 24) {
                    j = newValue;
                }
                return withHour((int) j);
            case 15:
                return plusHours((newValue - ((long) (this.hour / 12))) * 12);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public LocalTime withHour(int hour2) {
        if (this.hour == hour2) {
            return this;
        }
        ChronoField.HOUR_OF_DAY.checkValidValue((long) hour2);
        return create(hour2, this.minute, this.second, this.nano);
    }

    public LocalTime withMinute(int minute2) {
        if (this.minute == minute2) {
            return this;
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue((long) minute2);
        return create(this.hour, minute2, this.second, this.nano);
    }

    public LocalTime withSecond(int second2) {
        if (this.second == second2) {
            return this;
        }
        ChronoField.SECOND_OF_MINUTE.checkValidValue((long) second2);
        return create(this.hour, this.minute, second2, this.nano);
    }

    public LocalTime withNano(int nanoOfSecond) {
        if (this.nano == nanoOfSecond) {
            return this;
        }
        ChronoField.NANO_OF_SECOND.checkValidValue((long) nanoOfSecond);
        return create(this.hour, this.minute, this.second, nanoOfSecond);
    }

    public LocalTime truncatedTo(TemporalUnit unit) {
        if (unit == ChronoUnit.NANOS) {
            return this;
        }
        Duration unitDur = unit.getDuration();
        if (unitDur.getSeconds() <= 86400) {
            long dur = unitDur.toNanos();
            if (86400000000000L % dur == 0) {
                return ofNanoOfDay((toNanoOfDay() / dur) * dur);
            }
            throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
        }
        throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
    }

    public LocalTime plus(TemporalAmount amountToAdd) {
        return (LocalTime) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.LocalTime$1  reason: invalid class name */
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
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.NANO_OF_SECOND.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.NANO_OF_DAY.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_SECOND.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_DAY.ordinal()] = 4;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_SECOND.ordinal()] = 5;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_DAY.ordinal()] = 6;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.SECOND_OF_MINUTE.ordinal()] = 7;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.SECOND_OF_DAY.ordinal()] = 8;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MINUTE_OF_HOUR.ordinal()] = 9;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MINUTE_OF_DAY.ordinal()] = 10;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.HOUR_OF_AMPM.ordinal()] = 11;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.CLOCK_HOUR_OF_AMPM.ordinal()] = 12;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.HOUR_OF_DAY.ordinal()] = 13;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.CLOCK_HOUR_OF_DAY.ordinal()] = 14;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.AMPM_OF_DAY.ordinal()] = 15;
            } catch (NoSuchFieldError e22) {
            }
        }
    }

    public LocalTime plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (LocalTime) unit.addTo(this, amountToAdd);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusNanos(amountToAdd);
            case 2:
                return plusNanos((amountToAdd % 86400000000L) * 1000);
            case 3:
                return plusNanos((amountToAdd % 86400000) * 1000000);
            case 4:
                return plusSeconds(amountToAdd);
            case 5:
                return plusMinutes(amountToAdd);
            case 6:
                return plusHours(amountToAdd);
            case 7:
                return plusHours((amountToAdd % 2) * 12);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public LocalTime plusHours(long hoursToAdd) {
        if (hoursToAdd == 0) {
            return this;
        }
        return create(((((int) (hoursToAdd % 24)) + this.hour) + 24) % 24, this.minute, this.second, this.nano);
    }

    public LocalTime plusMinutes(long minutesToAdd) {
        if (minutesToAdd == 0) {
            return this;
        }
        int mofd = (this.hour * 60) + this.minute;
        int newMofd = ((((int) (minutesToAdd % 1440)) + mofd) + 1440) % 1440;
        if (mofd == newMofd) {
            return this;
        }
        return create(newMofd / 60, newMofd % 60, this.second, this.nano);
    }

    public LocalTime plusSeconds(long secondstoAdd) {
        if (secondstoAdd == 0) {
            return this;
        }
        int sofd = (this.hour * 3600) + (this.minute * 60) + this.second;
        int newSofd = ((((int) (secondstoAdd % 86400)) + sofd) + 86400) % 86400;
        if (sofd == newSofd) {
            return this;
        }
        return create(newSofd / 3600, (newSofd / 60) % 60, newSofd % 60, this.nano);
    }

    public LocalTime plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = (((nanosToAdd % 86400000000000L) + nofd) + 86400000000000L) % 86400000000000L;
        if (nofd == newNofd) {
            return this;
        }
        return create((int) (newNofd / 3600000000000L), (int) ((newNofd / 60000000000L) % 60), (int) ((newNofd / NUM) % 60), (int) (newNofd % NUM));
    }

    public LocalTime minus(TemporalAmount amountToSubtract) {
        return (LocalTime) amountToSubtract.subtractFrom(this);
    }

    public LocalTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public LocalTime minusHours(long hoursToSubtract) {
        return plusHours(-(hoursToSubtract % 24));
    }

    public LocalTime minusMinutes(long minutesToSubtract) {
        return plusMinutes(-(minutesToSubtract % 1440));
    }

    public LocalTime minusSeconds(long secondsToSubtract) {
        return plusSeconds(-(secondsToSubtract % 86400));
    }

    public LocalTime minusNanos(long nanosToSubtract) {
        return plusNanos(-(nanosToSubtract % 86400000000000L));
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.localTime()) {
            return this;
        }
        if (temporalQuery == TemporalQueries.localDate()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.NANOS;
        }
        return temporalQuery.queryFrom(this);
    }

    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.NANO_OF_DAY, toNanoOfDay());
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        LocalTime end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        long nanosUntil = end.toNanoOfDay() - toNanoOfDay();
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

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public LocalDateTime atDate(LocalDate date) {
        return LocalDateTime.of(date, this);
    }

    public OffsetTime atOffset(ZoneOffset offset) {
        return OffsetTime.of(this, offset);
    }

    public int toSecondOfDay() {
        return (this.hour * 3600) + (this.minute * 60) + this.second;
    }

    public long toNanoOfDay() {
        return (((long) this.hour) * 3600000000000L) + (((long) this.minute) * 60000000000L) + (((long) this.second) * NUM) + ((long) this.nano);
    }

    public int compareTo(LocalTime other) {
        int cmp = LocalTime$$ExternalSyntheticBackport0.m(this.hour, other.hour);
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = LocalTime$$ExternalSyntheticBackport0.m(this.minute, other.minute);
        if (cmp2 != 0) {
            return cmp2;
        }
        int cmp3 = LocalTime$$ExternalSyntheticBackport0.m(this.second, other.second);
        if (cmp3 == 0) {
            return LocalTime$$ExternalSyntheticBackport0.m(this.nano, other.nano);
        }
        return cmp3;
    }

    public boolean isAfter(LocalTime other) {
        return compareTo(other) > 0;
    }

    public boolean isBefore(LocalTime other) {
        return compareTo(other) < 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalTime)) {
            return false;
        }
        LocalTime other = (LocalTime) obj;
        if (this.hour == other.hour && this.minute == other.minute && this.second == other.second && this.nano == other.nano) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long nod = toNanoOfDay();
        return (int) ((nod >>> 32) ^ nod);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        int hourValue = this.hour;
        int minuteValue = this.minute;
        int secondValue = this.second;
        int nanoValue = this.nano;
        buf.append(hourValue < 10 ? "0" : "");
        buf.append(hourValue);
        String str = ":0";
        buf.append(minuteValue < 10 ? str : ":");
        buf.append(minuteValue);
        if (secondValue > 0 || nanoValue > 0) {
            if (secondValue >= 10) {
                str = ":";
            }
            buf.append(str);
            buf.append(secondValue);
            if (nanoValue > 0) {
                buf.append('.');
                if (nanoValue % 1000000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000000) + 1000).substring(1));
                } else if (nanoValue % 1000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000) + 1000000).substring(1));
                } else {
                    buf.append(Integer.toString(NUM + nanoValue).substring(1));
                }
            }
        }
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 4, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        if (this.nano != 0) {
            out.writeByte(this.hour);
            out.writeByte(this.minute);
            out.writeByte(this.second);
            out.writeInt(this.nano);
        } else if (this.second != 0) {
            out.writeByte(this.hour);
            out.writeByte(this.minute);
            out.writeByte(this.second ^ -1);
        } else if (this.minute == 0) {
            out.writeByte(this.hour ^ -1);
        } else {
            out.writeByte(this.hour);
            out.writeByte(this.minute ^ -1);
        }
    }

    static LocalTime readExternal(DataInput in) {
        int hour2 = in.readByte();
        int minute2 = 0;
        int second2 = 0;
        int nano2 = 0;
        if (hour2 < 0) {
            hour2 ^= -1;
        } else {
            minute2 = in.readByte();
            if (minute2 < 0) {
                minute2 ^= -1;
            } else {
                second2 = in.readByte();
                if (second2 < 0) {
                    second2 ^= -1;
                } else {
                    nano2 = in.readInt();
                }
            }
        }
        return of(hour2, minute2, second2, nano2);
    }
}
