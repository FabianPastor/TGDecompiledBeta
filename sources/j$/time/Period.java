package j$.time;

import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoPeriod;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeParseException;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Period implements ChronoPeriod, Serializable {
    private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
    private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(new TemporalUnit[]{ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS}));
    public static final Period ZERO = new Period(0, 0, 0);
    private static final long serialVersionUID = -3587258372562876L;
    private final int days;
    private final int months;
    private final int years;

    public static Period ofYears(int years2) {
        return create(years2, 0, 0);
    }

    public static Period ofMonths(int months2) {
        return create(0, months2, 0);
    }

    public static Period ofWeeks(int weeks) {
        return create(0, 0, Period$$ExternalSyntheticBackport2.m(weeks, 7));
    }

    public static Period ofDays(int days2) {
        return create(0, 0, days2);
    }

    public static Period of(int years2, int months2, int days2) {
        return create(years2, months2, days2);
    }

    public static Period from(TemporalAmount amount) {
        if (amount instanceof Period) {
            return (Period) amount;
        }
        if (!(amount instanceof ChronoPeriod) || IsoChronology.INSTANCE.equals(((ChronoPeriod) amount).getChronology())) {
            Objects.requireNonNull(amount, "amount");
            int years2 = 0;
            int months2 = 0;
            int days2 = 0;
            for (TemporalUnit unit : amount.getUnits()) {
                long unitAmount = amount.get(unit);
                if (unit == ChronoUnit.YEARS) {
                    years2 = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
                } else if (unit == ChronoUnit.MONTHS) {
                    months2 = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
                } else if (unit == ChronoUnit.DAYS) {
                    days2 = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
                } else {
                    throw new DateTimeException("Unit must be Years, Months or Days, but was " + unit);
                }
            }
            return create(years2, months2, days2);
        }
        throw new DateTimeException("Period requires ISO chronology: " + amount);
    }

    public static Period parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = 1;
            if ("-".equals(matcher.group(1))) {
                negate = -1;
            }
            String yearMatch = matcher.group(2);
            String monthMatch = matcher.group(3);
            String weekMatch = matcher.group(4);
            String dayMatch = matcher.group(5);
            if (!(yearMatch == null && monthMatch == null && dayMatch == null && weekMatch == null)) {
                try {
                    return create(parseNumber(text, yearMatch, negate), parseNumber(text, monthMatch, negate), Period$$ExternalSyntheticBackport0.m(parseNumber(text, dayMatch, negate), Period$$ExternalSyntheticBackport2.m(parseNumber(text, weekMatch, negate), 7)));
                } catch (NumberFormatException ex) {
                    throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0, ex);
                }
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0);
    }

    private static int parseNumber(CharSequence text, String str, int negate) {
        if (str == null) {
            return 0;
        }
        if (str.charAt(0) == '+') {
            str = str.substring(1);
        }
        try {
            return Period$$ExternalSyntheticBackport2.m(Integer.parseInt(str), negate);
        } catch (ArithmeticException ex) {
            throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0, ex);
        }
    }

    public static Period between(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return startDateInclusive.until((ChronoLocalDate) endDateExclusive);
    }

    private static Period create(int years2, int months2, int days2) {
        if ((years2 | months2 | days2) == 0) {
            return ZERO;
        }
        return new Period(years2, months2, days2);
    }

    private Period(int years2, int months2, int days2) {
        this.years = years2;
        this.months = months2;
        this.days = days2;
    }

    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.YEARS) {
            return (long) getYears();
        }
        if (unit == ChronoUnit.MONTHS) {
            return (long) getMonths();
        }
        if (unit == ChronoUnit.DAYS) {
            return (long) getDays();
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    public IsoChronology getChronology() {
        return IsoChronology.INSTANCE;
    }

    public boolean isZero() {
        return this == ZERO;
    }

    public boolean isNegative() {
        return this.years < 0 || this.months < 0 || this.days < 0;
    }

    public int getYears() {
        return this.years;
    }

    public int getMonths() {
        return this.months;
    }

    public int getDays() {
        return this.days;
    }

    public Period withYears(int years2) {
        if (years2 == this.years) {
            return this;
        }
        return create(years2, this.months, this.days);
    }

    public Period withMonths(int months2) {
        if (months2 == this.months) {
            return this;
        }
        return create(this.years, months2, this.days);
    }

    public Period withDays(int days2) {
        if (days2 == this.days) {
            return this;
        }
        return create(this.years, this.months, days2);
    }

    public Period plus(TemporalAmount amountToAdd) {
        Period isoAmount = from(amountToAdd);
        return create(Period$$ExternalSyntheticBackport0.m(this.years, isoAmount.years), Period$$ExternalSyntheticBackport0.m(this.months, isoAmount.months), Period$$ExternalSyntheticBackport0.m(this.days, isoAmount.days));
    }

    public Period plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return create(LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m((long) this.years, yearsToAdd)), this.months, this.days);
    }

    public Period plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        return create(this.years, LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m((long) this.months, monthsToAdd)), this.days);
    }

    public Period plusDays(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        return create(this.years, this.months, LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m((long) this.days, daysToAdd)));
    }

    public Period minus(TemporalAmount amountToSubtract) {
        Period isoAmount = from(amountToSubtract);
        return create(Period$$ExternalSyntheticBackport1.m(this.years, isoAmount.years), Period$$ExternalSyntheticBackport1.m(this.months, isoAmount.months), Period$$ExternalSyntheticBackport1.m(this.days, isoAmount.days));
    }

    public Period minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract);
    }

    public Period minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract);
    }

    public Period minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract);
    }

    public Period multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return create(Period$$ExternalSyntheticBackport2.m(this.years, scalar), Period$$ExternalSyntheticBackport2.m(this.months, scalar), Period$$ExternalSyntheticBackport2.m(this.days, scalar));
    }

    public Period negated() {
        return multipliedBy(-1);
    }

    public Period normalized() {
        long totalMonths = toTotalMonths();
        long splitYears = totalMonths / 12;
        int splitMonths = (int) (totalMonths % 12);
        if (splitYears == ((long) this.years) && splitMonths == this.months) {
            return this;
        }
        return create(LocalDate$$ExternalSyntheticBackport0.m(splitYears), splitMonths, this.days);
    }

    public long toTotalMonths() {
        return (((long) this.years) * 12) + ((long) this.months);
    }

    public Temporal addTo(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.plus((long) i, ChronoUnit.YEARS);
            }
        } else {
            long totalMonths = toTotalMonths();
            if (totalMonths != 0) {
                temporal = temporal.plus(totalMonths, ChronoUnit.MONTHS);
            }
        }
        int i2 = this.days;
        if (i2 != 0) {
            return temporal.plus((long) i2, ChronoUnit.DAYS);
        }
        return temporal;
    }

    public Temporal subtractFrom(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.minus((long) i, ChronoUnit.YEARS);
            }
        } else {
            long totalMonths = toTotalMonths();
            if (totalMonths != 0) {
                temporal = temporal.minus(totalMonths, ChronoUnit.MONTHS);
            }
        }
        int i2 = this.days;
        if (i2 != 0) {
            return temporal.minus((long) i2, ChronoUnit.DAYS);
        }
        return temporal;
    }

    private void validateChrono(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        Chronology temporalChrono = (Chronology) temporal.query(TemporalQueries.chronology());
        if (temporalChrono != null && !IsoChronology.INSTANCE.equals(temporalChrono)) {
            throw new DateTimeException("Chronology mismatch, expected: ISO, actual: " + temporalChrono.getId());
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Period)) {
            return false;
        }
        Period other = (Period) obj;
        if (this.years == other.years && this.months == other.months && this.days == other.days) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16);
    }

    public String toString() {
        if (this == ZERO) {
            return "P0D";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('P');
        int i = this.years;
        if (i != 0) {
            buf.append(i);
            buf.append('Y');
        }
        int i2 = this.months;
        if (i2 != 0) {
            buf.append(i2);
            buf.append('M');
        }
        int i3 = this.days;
        if (i3 != 0) {
            buf.append(i3);
            buf.append('D');
        }
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 14, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(this.years);
        out.writeInt(this.months);
        out.writeInt(this.days);
    }

    static Period readExternal(DataInput in) {
        return of(in.readInt(), in.readInt(), in.readInt());
    }
}
