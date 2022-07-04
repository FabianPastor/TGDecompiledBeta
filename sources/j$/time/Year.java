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

public final class Year implements Temporal, TemporalAdjuster, Comparable<Year>, Serializable {
    public static final int MAX_VALUE = NUM;
    public static final int MIN_VALUE = -NUM;
    private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).toFormatter();
    private static final long serialVersionUID = -23038383694477807L;
    private final int year;

    public static Year now() {
        return now(Clock.systemDefaultZone());
    }

    public static Year now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static Year now(Clock clock) {
        return of(LocalDate.now(clock).getYear());
    }

    public static Year of(int isoYear) {
        ChronoField.YEAR.checkValidValue((long) isoYear);
        return new Year(isoYear);
    }

    public static Year from(TemporalAccessor temporal) {
        if (temporal instanceof Year) {
            return (Year) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            if (!IsoChronology.INSTANCE.equals(Chronology.CC.from(temporal))) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(ChronoField.YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Year from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static Year parse(CharSequence text) {
        return parse(text, PARSER);
    }

    public static Year parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (Year) formatter.parse(text, Year$$ExternalSyntheticLambda0.INSTANCE);
    }

    public static boolean isLeap(long year2) {
        return (3 & year2) == 0 && (year2 % 100 != 0 || year2 % 400 == 0);
    }

    private Year(int year2) {
        this.year = year2;
    }

    public int getValue() {
        return this.year;
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.YEAR || field == ChronoField.YEAR_OF_ERA || field == ChronoField.ERA) {
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
            if (unit == ChronoUnit.YEARS || unit == ChronoUnit.DECADES || unit == ChronoUnit.CENTURIES || unit == ChronoUnit.MILLENNIA || unit == ChronoUnit.ERAS) {
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
        return ValueRange.of(1, this.year <= 0 ? NUM : NUM);
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
                int i2 = this.year;
                if (i2 < 1) {
                    i2 = 1 - i2;
                }
                return (long) i2;
            case 2:
                return (long) this.year;
            case 3:
                if (this.year < 1) {
                    i = 0;
                }
                return (long) i;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public boolean isLeap() {
        return isLeap((long) this.year);
    }

    public boolean isValidMonthDay(MonthDay monthDay) {
        return monthDay != null && monthDay.isValidYear(this.year);
    }

    public int length() {
        return isLeap() ? 366 : 365;
    }

    public Year with(TemporalAdjuster adjuster) {
        return (Year) adjuster.adjustInto(this);
    }

    public Year with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (Year) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        f.checkValidValue(newValue);
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return of((int) (this.year < 1 ? 1 - newValue : newValue));
            case 2:
                return of((int) newValue);
            case 3:
                return getLong(ChronoField.ERA) == newValue ? this : of(1 - this.year);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public Year plus(TemporalAmount amountToAdd) {
        return (Year) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.Year$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.YEARS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DECADES.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.CENTURIES.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLENNIA.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.ERAS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.YEAR_OF_ERA.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public Year plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (Year) unit.addTo(this, amountToAdd);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusYears(amountToAdd);
            case 2:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 10));
            case 3:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 100));
            case 4:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 1000));
            case 5:
                return with((TemporalField) ChronoField.ERA, Clock$OffsetClock$$ExternalSyntheticBackport0.m(getLong(ChronoField.ERA), amountToAdd));
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public Year plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return of(ChronoField.YEAR.checkValidIntValue(((long) this.year) + yearsToAdd));
    }

    public Year minus(TemporalAmount amountToSubtract) {
        return (Year) amountToSubtract.subtractFrom(this);
    }

    public Year minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public Year minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.chronology()) {
            return IsoChronology.INSTANCE;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.YEARS;
        }
        return TemporalAccessor.CC.$default$query(this, temporalQuery);
    }

    public Temporal adjustInto(Temporal temporal) {
        if (Chronology.CC.from(temporal).equals(IsoChronology.INSTANCE)) {
            return temporal.with(ChronoField.YEAR, (long) this.year);
        }
        throw new DateTimeException("Adjustment only supported on ISO date-time");
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        Year end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        long yearsUntil = ((long) end.year) - ((long) this.year);
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return yearsUntil;
            case 2:
                return yearsUntil / 10;
            case 3:
                return yearsUntil / 100;
            case 4:
                return yearsUntil / 1000;
            case 5:
                return end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public LocalDate atDay(int dayOfYear) {
        return LocalDate.ofYearDay(this.year, dayOfYear);
    }

    public YearMonth atMonth(Month month) {
        return YearMonth.of(this.year, month);
    }

    public YearMonth atMonth(int month) {
        return YearMonth.of(this.year, month);
    }

    public LocalDate atMonthDay(MonthDay monthDay) {
        return monthDay.atYear(this.year);
    }

    public int compareTo(Year other) {
        return this.year - other.year;
    }

    public boolean isAfter(Year other) {
        return this.year > other.year;
    }

    public boolean isBefore(Year other) {
        return this.year < other.year;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Year) || this.year != ((Year) obj).year) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.year;
    }

    public String toString() {
        return Integer.toString(this.year);
    }

    private Object writeReplace() {
        return new Ser((byte) 11, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(this.year);
    }

    static Year readExternal(DataInput in) {
        return of(in.readInt());
    }
}
