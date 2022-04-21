package j$.time;

import j$.time.format.DateTimeParseException;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Duration implements TemporalAmount, Comparable<Duration>, Serializable {
    private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(NUM);
    private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);
    public static final Duration ZERO = new Duration(0, 0);
    private static final long serialVersionUID = 3078945930695997490L;
    private final int nanos;
    private final long seconds;

    public static Duration ofDays(long days) {
        return create(Duration$$ExternalSyntheticBackport1.m(days, 86400), 0);
    }

    public static Duration ofHours(long hours) {
        return create(Duration$$ExternalSyntheticBackport1.m(hours, 3600), 0);
    }

    public static Duration ofMinutes(long minutes) {
        return create(Duration$$ExternalSyntheticBackport1.m(minutes, 60), 0);
    }

    public static Duration ofSeconds(long seconds2) {
        return create(seconds2, 0);
    }

    public static Duration ofSeconds(long seconds2, long nanoAdjustment) {
        return create(Clock$OffsetClock$$ExternalSyntheticBackport0.m(seconds2, Duration$$ExternalSyntheticBackport0.m(nanoAdjustment, NUM)), (int) Clock$TickClock$$ExternalSyntheticBackport0.m(nanoAdjustment, NUM));
    }

    public static Duration ofMillis(long millis) {
        long secs = millis / 1000;
        int mos = (int) (millis % 1000);
        if (mos < 0) {
            mos += 1000;
            secs--;
        }
        return create(secs, 1000000 * mos);
    }

    public static Duration ofNanos(long nanos2) {
        long secs = nanos2 / NUM;
        int nos = (int) (nanos2 % NUM);
        if (nos < 0) {
            nos = (int) (((long) nos) + NUM);
            secs--;
        }
        return create(secs, nos);
    }

    public static Duration of(long amount, TemporalUnit unit) {
        return ZERO.plus(amount, unit);
    }

    public static Duration from(TemporalAmount amount) {
        Objects.requireNonNull(amount, "amount");
        Duration duration = ZERO;
        for (TemporalUnit unit : amount.getUnits()) {
            duration = duration.plus(amount.get(unit), unit);
        }
        return duration;
    }

    public static Duration parse(CharSequence text) {
        CharSequence charSequence = text;
        Objects.requireNonNull(charSequence, "text");
        Matcher matcher = PATTERN.matcher(charSequence);
        if (matcher.matches() && !"T".equals(matcher.group(3))) {
            int i = 1;
            boolean negate = "-".equals(matcher.group(1));
            String dayMatch = matcher.group(2);
            String hourMatch = matcher.group(4);
            String minuteMatch = matcher.group(5);
            String secondMatch = matcher.group(6);
            String fractionMatch = matcher.group(7);
            if (!(dayMatch == null && hourMatch == null && minuteMatch == null && secondMatch == null)) {
                long daysAsSecs = parseNumber(charSequence, dayMatch, 86400, "days");
                long hoursAsSecs = parseNumber(charSequence, hourMatch, 3600, "hours");
                long minsAsSecs = parseNumber(charSequence, minuteMatch, 60, "minutes");
                long seconds2 = parseNumber(charSequence, secondMatch, 1, "seconds");
                if (seconds2 < 0) {
                    i = -1;
                }
                String str = fractionMatch;
                String str2 = minuteMatch;
                String str3 = secondMatch;
                String str4 = hourMatch;
                try {
                    return create(negate, daysAsSecs, hoursAsSecs, minsAsSecs, seconds2, parseFraction(charSequence, fractionMatch, i));
                } catch (ArithmeticException e) {
                    throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: overflow", charSequence, 0).initCause(e));
                }
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Duration", charSequence, 0);
    }

    private static long parseNumber(CharSequence text, String parsed, int multiplier, String errorText) {
        if (parsed == null) {
            return 0;
        }
        if (parsed.charAt(0) == '+') {
            parsed = parsed.substring(1);
        }
        try {
            return Duration$$ExternalSyntheticBackport1.m(Long.parseLong(parsed), (long) multiplier);
        } catch (ArithmeticException | NumberFormatException ex) {
            throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex));
        }
    }

    private static int parseFraction(CharSequence text, String parsed, int negate) {
        if (parsed == null || parsed.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt((parsed + "NUM").substring(0, 9)) * negate;
        } catch (ArithmeticException | NumberFormatException ex) {
            throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: fraction", text, 0).initCause(ex));
        }
    }

    private static Duration create(boolean negate, long daysAsSecs, long hoursAsSecs, long minsAsSecs, long secs, int nanos2) {
        long seconds2 = Clock$OffsetClock$$ExternalSyntheticBackport0.m(daysAsSecs, Clock$OffsetClock$$ExternalSyntheticBackport0.m(hoursAsSecs, Clock$OffsetClock$$ExternalSyntheticBackport0.m(minsAsSecs, secs)));
        if (negate) {
            return ofSeconds(seconds2, (long) nanos2).negated();
        }
        return ofSeconds(seconds2, (long) nanos2);
    }

    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        long nanos2;
        try {
            return ofNanos(startInclusive.until(endExclusive, ChronoUnit.NANOS));
        } catch (DateTimeException | ArithmeticException e) {
            long secs = startInclusive.until(endExclusive, ChronoUnit.SECONDS);
            try {
                nanos2 = endExclusive.getLong(ChronoField.NANO_OF_SECOND) - startInclusive.getLong(ChronoField.NANO_OF_SECOND);
                if (secs <= 0 || nanos2 >= 0) {
                    if (secs < 0 && nanos2 > 0) {
                        secs--;
                    }
                    return ofSeconds(secs, nanos2);
                }
                secs++;
                return ofSeconds(secs, nanos2);
            } catch (DateTimeException e2) {
                nanos2 = 0;
            }
        }
    }

    private static Duration create(long seconds2, int nanoAdjustment) {
        if ((((long) nanoAdjustment) | seconds2) == 0) {
            return ZERO;
        }
        return new Duration(seconds2, nanoAdjustment);
    }

    private Duration(long seconds2, int nanos2) {
        this.seconds = seconds2;
        this.nanos = nanos2;
    }

    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.SECONDS) {
            return this.seconds;
        }
        if (unit == ChronoUnit.NANOS) {
            return (long) this.nanos;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    public List<TemporalUnit> getUnits() {
        return DurationUnits.UNITS;
    }

    private static class DurationUnits {
        static final List<TemporalUnit> UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[]{ChronoUnit.SECONDS, ChronoUnit.NANOS}));

        private DurationUnits() {
        }
    }

    public boolean isZero() {
        return (this.seconds | ((long) this.nanos)) == 0;
    }

    public boolean isNegative() {
        return this.seconds < 0;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public int getNano() {
        return this.nanos;
    }

    public Duration withSeconds(long seconds2) {
        return create(seconds2, this.nanos);
    }

    public Duration withNanos(int nanoOfSecond) {
        ChronoField.NANO_OF_SECOND.checkValidIntValue((long) nanoOfSecond);
        return create(this.seconds, nanoOfSecond);
    }

    public Duration plus(Duration duration) {
        return plus(duration.getSeconds(), (long) duration.getNano());
    }

    public Duration plus(long amountToAdd, TemporalUnit unit) {
        Objects.requireNonNull(unit, "unit");
        if (unit == ChronoUnit.DAYS) {
            return plus(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 86400), 0);
        }
        if (unit.isDurationEstimated()) {
            throw new UnsupportedTemporalTypeException("Unit must not have an estimated duration");
        } else if (amountToAdd == 0) {
            return this;
        } else {
            if (unit instanceof ChronoUnit) {
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                    case 1:
                        return plusNanos(amountToAdd);
                    case 2:
                        return plusSeconds((amountToAdd / NUM) * 1000).plusNanos((amountToAdd % NUM) * 1000);
                    case 3:
                        return plusMillis(amountToAdd);
                    case 4:
                        return plusSeconds(amountToAdd);
                    default:
                        return plusSeconds(Duration$$ExternalSyntheticBackport1.m(unit.getDuration().seconds, amountToAdd));
                }
            } else {
                Duration duration = unit.getDuration().multipliedBy(amountToAdd);
                return plusSeconds(duration.getSeconds()).plusNanos((long) duration.getNano());
            }
        }
    }

    /* renamed from: j$.time.Duration$1  reason: invalid class name */
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
        }
    }

    public Duration plusDays(long daysToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(daysToAdd, 86400), 0);
    }

    public Duration plusHours(long hoursToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(hoursToAdd, 3600), 0);
    }

    public Duration plusMinutes(long minutesToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(minutesToAdd, 60), 0);
    }

    public Duration plusSeconds(long secondsToAdd) {
        return plus(secondsToAdd, 0);
    }

    public Duration plusMillis(long millisToAdd) {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    public Duration plusNanos(long nanosToAdd) {
        return plus(0, nanosToAdd);
    }

    private Duration plus(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        return ofSeconds(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.seconds, secondsToAdd), nanosToAdd / NUM), ((long) this.nanos) + (nanosToAdd % NUM));
    }

    public Duration minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNano();
        if (secsToSubtract == Long.MIN_VALUE) {
            return plus(Long.MAX_VALUE, (long) (-nanosToSubtract)).plus(1, 0);
        }
        return plus(-secsToSubtract, (long) (-nanosToSubtract));
    }

    public Duration minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public Duration minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract);
    }

    public Duration minusHours(long hoursToSubtract) {
        return hoursToSubtract == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-hoursToSubtract);
    }

    public Duration minusMinutes(long minutesToSubtract) {
        return minutesToSubtract == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-minutesToSubtract);
    }

    public Duration minusSeconds(long secondsToSubtract) {
        return secondsToSubtract == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-secondsToSubtract);
    }

    public Duration minusMillis(long millisToSubtract) {
        return millisToSubtract == Long.MIN_VALUE ? plusMillis(Long.MAX_VALUE).plusMillis(1) : plusMillis(-millisToSubtract);
    }

    public Duration minusNanos(long nanosToSubtract) {
        return nanosToSubtract == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-nanosToSubtract);
    }

    public Duration multipliedBy(long multiplicand) {
        if (multiplicand == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        return create(toSeconds().multiply(BigDecimal.valueOf(multiplicand)));
    }

    public Duration dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        } else if (divisor == 1) {
            return this;
        } else {
            return create(toSeconds().divide(BigDecimal.valueOf(divisor), RoundingMode.DOWN));
        }
    }

    private BigDecimal toSeconds() {
        return BigDecimal.valueOf(this.seconds).add(BigDecimal.valueOf((long) this.nanos, 9));
    }

    private static Duration create(BigDecimal seconds2) {
        BigInteger nanos2 = seconds2.movePointRight(9).toBigIntegerExact();
        BigInteger[] divRem = nanos2.divideAndRemainder(BI_NANOS_PER_SECOND);
        if (divRem[0].bitLength() <= 63) {
            return ofSeconds(divRem[0].longValue(), (long) divRem[1].intValue());
        }
        throw new ArithmeticException("Exceeds capacity of Duration: " + nanos2);
    }

    public Duration negated() {
        return multipliedBy(-1);
    }

    public Duration abs() {
        return isNegative() ? negated() : this;
    }

    public Temporal addTo(Temporal temporal) {
        long j = this.seconds;
        if (j != 0) {
            temporal = temporal.plus(j, ChronoUnit.SECONDS);
        }
        int i = this.nanos;
        if (i != 0) {
            return temporal.plus((long) i, ChronoUnit.NANOS);
        }
        return temporal;
    }

    public Temporal subtractFrom(Temporal temporal) {
        long j = this.seconds;
        if (j != 0) {
            temporal = temporal.minus(j, ChronoUnit.SECONDS);
        }
        int i = this.nanos;
        if (i != 0) {
            return temporal.minus((long) i, ChronoUnit.NANOS);
        }
        return temporal;
    }

    public long toDays() {
        return this.seconds / 86400;
    }

    public long toHours() {
        return this.seconds / 3600;
    }

    public long toMinutes() {
        return this.seconds / 60;
    }

    public long toMillis() {
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(this.seconds, 1000), (long) (this.nanos / 1000000));
    }

    public long toNanos() {
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(this.seconds, NUM), (long) this.nanos);
    }

    public int compareTo(Duration otherDuration) {
        int cmp = (this.seconds > otherDuration.seconds ? 1 : (this.seconds == otherDuration.seconds ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.nanos - otherDuration.nanos;
    }

    public boolean equals(Object otherDuration) {
        if (this == otherDuration) {
            return true;
        }
        if (!(otherDuration instanceof Duration)) {
            return false;
        }
        Duration other = (Duration) otherDuration;
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
        if (this == ZERO) {
            return "PT0S";
        }
        long j = this.seconds;
        long hours = j / 3600;
        int minutes = (int) ((j % 3600) / 60);
        int secs = (int) (j % 60);
        StringBuilder buf = new StringBuilder(24);
        buf.append("PT");
        if (hours != 0) {
            buf.append(hours);
            buf.append('H');
        }
        if (minutes != 0) {
            buf.append(minutes);
            buf.append('M');
        }
        if (secs == 0 && this.nanos == 0 && buf.length() > 2) {
            return buf.toString();
        }
        if (secs >= 0 || this.nanos <= 0) {
            buf.append(secs);
        } else if (secs == -1) {
            buf.append("-0");
        } else {
            buf.append(secs + 1);
        }
        if (this.nanos > 0) {
            int pos = buf.length();
            if (secs < 0) {
                buf.append(NUM - ((long) this.nanos));
            } else {
                buf.append(((long) this.nanos) + NUM);
            }
            while (buf.charAt(buf.length() - 1) == '0') {
                buf.setLength(buf.length() - 1);
            }
            buf.setCharAt(pos, '.');
        }
        buf.append('S');
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 1, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeLong(this.seconds);
        out.writeInt(this.nanos);
    }

    static Duration readExternal(DataInput in) {
        return ofSeconds(in.readLong(), (long) in.readInt());
    }
}
