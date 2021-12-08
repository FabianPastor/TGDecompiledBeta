package j$.time;

import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeFormatter;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.SignStyle;
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

public final class YearMonth implements Temporal, TemporalAdjuster, Comparable<YearMonth>, Serializable {
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).toFormatter();
    private static final long serialVersionUID = 4183400860270640070L;
    private final int month;
    private final int year;

    public static YearMonth now() {
        return now(Clock.systemDefaultZone());
    }

    public static YearMonth now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static YearMonth now(Clock clock) {
        LocalDate now = LocalDate.now(clock);
        return of(now.getYear(), now.getMonth());
    }

    public static YearMonth of(int year2, Month month2) {
        Objects.requireNonNull(month2, "month");
        return of(year2, month2.getValue());
    }

    public static YearMonth of(int year2, int month2) {
        ChronoField.YEAR.checkValidValue((long) year2);
        ChronoField.MONTH_OF_YEAR.checkValidValue((long) month2);
        return new YearMonth(year2, month2);
    }

    public static YearMonth from(TemporalAccessor temporal) {
        if (temporal instanceof YearMonth) {
            return (YearMonth) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (!IsoChronology.INSTANCE.equals(Chronology.CC.from(temporal))) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(ChronoField.YEAR), temporal.get(ChronoField.MONTH_OF_YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain YearMonth from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static YearMonth parse(CharSequence text) {
        return parse(text, PARSER);
    }

    public static YearMonth parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (YearMonth) formatter.parse(text, YearMonth$$ExternalSyntheticLambda0.INSTANCE);
    }

    private YearMonth(int year2, int month2) {
        this.year = year2;
        this.month = month2;
    }

    private YearMonth with(int newYear, int newMonth) {
        if (this.year == newYear && this.month == newMonth) {
            return this;
        }
        return new YearMonth(newYear, newMonth);
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.YEAR || field == ChronoField.MONTH_OF_YEAR || field == ChronoField.PROLEPTIC_MONTH || field == ChronoField.YEAR_OF_ERA || field == ChronoField.ERA) {
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
            if (unit == ChronoUnit.MONTHS || unit == ChronoUnit.YEARS || unit == ChronoUnit.DECADES || unit == ChronoUnit.CENTURIES || unit == ChronoUnit.MILLENNIA || unit == ChronoUnit.ERAS) {
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
        if (field != ChronoField.YEAR_OF_ERA) {
            return TemporalAccessor.CC.$default$range(this, field);
        }
        return ValueRange.of(1, getYear() <= 0 ? NUM : NUM);
    }

    public int get(TemporalField field) {
        return range(field).checkValidIntValue(getLong(field), field);
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        int i = 1;
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return (long) this.month;
            case 2:
                return getProlepticMonth();
            case 3:
                int i2 = this.year;
                if (i2 < 1) {
                    i2 = 1 - i2;
                }
                return (long) i2;
            case 4:
                return (long) this.year;
            case 5:
                if (this.year < 1) {
                    i = 0;
                }
                return (long) i;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    private long getProlepticMonth() {
        return ((((long) this.year) * 12) + ((long) this.month)) - 1;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonthValue() {
        return this.month;
    }

    public Month getMonth() {
        return Month.of(this.month);
    }

    public boolean isLeapYear() {
        return IsoChronology.INSTANCE.isLeapYear((long) this.year);
    }

    public boolean isValidDay(int dayOfMonth) {
        return dayOfMonth >= 1 && dayOfMonth <= lengthOfMonth();
    }

    public int lengthOfMonth() {
        return getMonth().length(isLeapYear());
    }

    public int lengthOfYear() {
        return isLeapYear() ? 366 : 365;
    }

    public YearMonth with(TemporalAdjuster adjuster) {
        return (YearMonth) adjuster.adjustInto(this);
    }

    public YearMonth with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (YearMonth) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        f.checkValidValue(newValue);
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return withMonth((int) newValue);
            case 2:
                return plusMonths(newValue - getProlepticMonth());
            case 3:
                return withYear((int) (this.year < 1 ? 1 - newValue : newValue));
            case 4:
                return withYear((int) newValue);
            case 5:
                return getLong(ChronoField.ERA) == newValue ? this : withYear(1 - this.year);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public YearMonth withYear(int year2) {
        ChronoField.YEAR.checkValidValue((long) year2);
        return with(year2, this.month);
    }

    public YearMonth withMonth(int month2) {
        ChronoField.MONTH_OF_YEAR.checkValidValue((long) month2);
        return with(this.year, month2);
    }

    public YearMonth plus(TemporalAmount amountToAdd) {
        return (YearMonth) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.YearMonth$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.MONTHS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.YEARS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DECADES.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.CENTURIES.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLENNIA.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.ERAS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.MONTH_OF_YEAR.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.PROLEPTIC_MONTH.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 5;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public YearMonth plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (YearMonth) unit.addTo(this, amountToAdd);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusMonths(amountToAdd);
            case 2:
                return plusYears(amountToAdd);
            case 3:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 10));
            case 4:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 100));
            case 5:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 1000));
            case 6:
                return with((TemporalField) ChronoField.ERA, Clock$OffsetClock$$ExternalSyntheticBackport0.m(getLong(ChronoField.ERA), amountToAdd));
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public YearMonth plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return with(ChronoField.YEAR.checkValidIntValue(((long) this.year) + yearsToAdd), this.month);
    }

    public YearMonth plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long calcMonths = (((long) this.year) * 12) + ((long) (this.month - 1)) + monthsToAdd;
        return with(ChronoField.YEAR.checkValidIntValue(Duration$$ExternalSyntheticBackport0.m(calcMonths, 12)), ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(calcMonths, 12)) + 1);
    }

    public YearMonth minus(TemporalAmount amountToSubtract) {
        return (YearMonth) amountToSubtract.subtractFrom(this);
    }

    public YearMonth minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public YearMonth minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract);
    }

    public YearMonth minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.chronology()) {
            return IsoChronology.INSTANCE;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.MONTHS;
        }
        return TemporalAccessor.CC.$default$query(this, temporalQuery);
    }

    public Temporal adjustInto(Temporal temporal) {
        if (Chronology.CC.from(temporal).equals(IsoChronology.INSTANCE)) {
            return temporal.with(ChronoField.PROLEPTIC_MONTH, getProlepticMonth());
        }
        throw new DateTimeException("Adjustment only supported on ISO date-time");
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        YearMonth end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        long monthsUntil = end.getProlepticMonth() - getProlepticMonth();
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return monthsUntil;
            case 2:
                return monthsUntil / 12;
            case 3:
                return monthsUntil / 120;
            case 4:
                return monthsUntil / 1200;
            case 5:
                return monthsUntil / 12000;
            case 6:
                return end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public LocalDate atDay(int dayOfMonth) {
        return LocalDate.of(this.year, this.month, dayOfMonth);
    }

    public LocalDate atEndOfMonth() {
        return LocalDate.of(this.year, this.month, lengthOfMonth());
    }

    public int compareTo(YearMonth other) {
        int cmp = this.year - other.year;
        if (cmp == 0) {
            return this.month - other.month;
        }
        return cmp;
    }

    public boolean isAfter(YearMonth other) {
        return compareTo(other) > 0;
    }

    public boolean isBefore(YearMonth other) {
        return compareTo(other) < 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof YearMonth)) {
            return false;
        }
        YearMonth other = (YearMonth) obj;
        if (this.year == other.year && this.month == other.month) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.year ^ (this.month << 27);
    }

    public String toString() {
        int absYear = Math.abs(this.year);
        StringBuilder buf = new StringBuilder(9);
        if (absYear < 1000) {
            int i = this.year;
            if (i < 0) {
                buf.append(i - 10000);
                buf.deleteCharAt(1);
            } else {
                buf.append(i + 10000);
                buf.deleteCharAt(0);
            }
        } else {
            buf.append(this.year);
        }
        buf.append(this.month < 10 ? "-0" : "-");
        buf.append(this.month);
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 12, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(this.year);
        out.writeByte(this.month);
    }

    static YearMonth readExternal(DataInput in) {
        return of(in.readInt(), (int) in.readByte());
    }
}
