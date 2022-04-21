package j$.time.chrono;

import j$.time.Clock;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.LocalTime;
import j$.time.Period$$ExternalSyntheticBackport0;
import j$.time.ZoneId;
import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;

public final class HijrahDate extends ChronoLocalDateImpl<HijrahDate> implements ChronoLocalDate, Serializable {
    private static final long serialVersionUID = -5207853542612002020L;
    private final transient HijrahChronology chrono;
    private final transient int dayOfMonth;
    private final transient int monthOfYear;
    private final transient int prolepticYear;

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public /* bridge */ /* synthetic */ long until(Temporal temporal, TemporalUnit temporalUnit) {
        return super.until(temporal, temporalUnit);
    }

    static HijrahDate of(HijrahChronology chrono2, int prolepticYear2, int monthOfYear2, int dayOfMonth2) {
        return new HijrahDate(chrono2, prolepticYear2, monthOfYear2, dayOfMonth2);
    }

    static HijrahDate ofEpochDay(HijrahChronology chrono2, long epochDay) {
        return new HijrahDate(chrono2, epochDay);
    }

    public static HijrahDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static HijrahDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static HijrahDate now(Clock clock) {
        return ofEpochDay(HijrahChronology.INSTANCE, LocalDate.now(clock).toEpochDay());
    }

    public static HijrahDate of(int prolepticYear2, int month, int dayOfMonth2) {
        return HijrahChronology.INSTANCE.date(prolepticYear2, month, dayOfMonth2);
    }

    public static HijrahDate from(TemporalAccessor temporal) {
        return HijrahChronology.INSTANCE.date(temporal);
    }

    private HijrahDate(HijrahChronology chrono2, int prolepticYear2, int monthOfYear2, int dayOfMonth2) {
        chrono2.getEpochDay(prolepticYear2, monthOfYear2, dayOfMonth2);
        this.chrono = chrono2;
        this.prolepticYear = prolepticYear2;
        this.monthOfYear = monthOfYear2;
        this.dayOfMonth = dayOfMonth2;
    }

    private HijrahDate(HijrahChronology chrono2, long epochDay) {
        int[] dateInfo = chrono2.getHijrahDateInfo((int) epochDay);
        this.chrono = chrono2;
        this.prolepticYear = dateInfo[0];
        this.monthOfYear = dateInfo[1];
        this.dayOfMonth = dateInfo[2];
    }

    public HijrahChronology getChronology() {
        return this.chrono;
    }

    public HijrahEra getEra() {
        return HijrahEra.AH;
    }

    public int lengthOfMonth() {
        return this.chrono.getMonthLength(this.prolepticYear, this.monthOfYear);
    }

    public int lengthOfYear() {
        return this.chrono.getYearLength(this.prolepticYear);
    }

    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.rangeRefinedBy(this);
        }
        if (isSupported(field)) {
            ChronoField f = (ChronoField) field;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    return ValueRange.of(1, (long) lengthOfMonth());
                case 2:
                    return ValueRange.of(1, (long) lengthOfYear());
                case 3:
                    return ValueRange.of(1, 5);
                default:
                    return getChronology().range(f);
            }
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    /* renamed from: j$.time.chrono.HijrahDate$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.DAY_OF_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_WEEK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.EPOCH_DAY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_YEAR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MONTH_OF_YEAR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.PROLEPTIC_MONTH.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return (long) this.dayOfMonth;
            case 2:
                return (long) getDayOfYear();
            case 3:
                return (long) (((this.dayOfMonth - 1) / 7) + 1);
            case 4:
                return (long) getDayOfWeek();
            case 5:
                return (long) (((getDayOfWeek() - 1) % 7) + 1);
            case 6:
                return (long) (((getDayOfYear() - 1) % 7) + 1);
            case 7:
                return toEpochDay();
            case 8:
                return (long) (((getDayOfYear() - 1) / 7) + 1);
            case 9:
                return (long) this.monthOfYear;
            case 10:
                return getProlepticMonth();
            case 11:
                return (long) this.prolepticYear;
            case 12:
                return (long) this.prolepticYear;
            case 13:
                return (long) getEraValue();
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    private long getProlepticMonth() {
        return ((((long) this.prolepticYear) * 12) + ((long) this.monthOfYear)) - 1;
    }

    public HijrahDate with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (HijrahDate) super.with(field, newValue);
        }
        ChronoField f = (ChronoField) field;
        this.chrono.range(f).checkValidValue(newValue, f);
        int nvalue = (int) newValue;
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return resolvePreviousValid(this.prolepticYear, this.monthOfYear, nvalue);
            case 2:
                return plusDays((long) (Math.min(nvalue, lengthOfYear()) - getDayOfYear()));
            case 3:
                return plusDays((newValue - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH)) * 7);
            case 4:
                return plusDays(newValue - ((long) getDayOfWeek()));
            case 5:
                return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
            case 6:
                return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
            case 7:
                return new HijrahDate(this.chrono, newValue);
            case 8:
                return plusDays((newValue - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR)) * 7);
            case 9:
                return resolvePreviousValid(this.prolepticYear, nvalue, this.dayOfMonth);
            case 10:
                return plusMonths(newValue - getProlepticMonth());
            case 11:
                return resolvePreviousValid(this.prolepticYear >= 1 ? nvalue : 1 - nvalue, this.monthOfYear, this.dayOfMonth);
            case 12:
                return resolvePreviousValid(nvalue, this.monthOfYear, this.dayOfMonth);
            case 13:
                return resolvePreviousValid(1 - this.prolepticYear, this.monthOfYear, this.dayOfMonth);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    private HijrahDate resolvePreviousValid(int prolepticYear2, int month, int day) {
        int monthDays = this.chrono.getMonthLength(prolepticYear2, month);
        if (day > monthDays) {
            day = monthDays;
        }
        return of(this.chrono, prolepticYear2, month, day);
    }

    public HijrahDate with(TemporalAdjuster adjuster) {
        return (HijrahDate) super.with(adjuster);
    }

    public HijrahDate withVariant(HijrahChronology chronology) {
        if (this.chrono == chronology) {
            return this;
        }
        int monthDays = chronology.getDayOfYear(this.prolepticYear, this.monthOfYear);
        int i = this.prolepticYear;
        int i2 = this.monthOfYear;
        int i3 = this.dayOfMonth;
        if (i3 > monthDays) {
            i3 = monthDays;
        }
        return of(chronology, i, i2, i3);
    }

    public HijrahDate plus(TemporalAmount amount) {
        return (HijrahDate) super.plus(amount);
    }

    public HijrahDate minus(TemporalAmount amount) {
        return (HijrahDate) super.minus(amount);
    }

    public long toEpochDay() {
        return this.chrono.getEpochDay(this.prolepticYear, this.monthOfYear, this.dayOfMonth);
    }

    private int getDayOfYear() {
        return this.chrono.getDayOfYear(this.prolepticYear, this.monthOfYear) + this.dayOfMonth;
    }

    private int getDayOfWeek() {
        return ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(toEpochDay() + 3, 7)) + 1;
    }

    private int getEraValue() {
        return this.prolepticYear > 1 ? 1 : 0;
    }

    public boolean isLeapYear() {
        return this.chrono.isLeapYear((long) this.prolepticYear);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate plusYears(long years) {
        if (years == 0) {
            return this;
        }
        return resolvePreviousValid(Period$$ExternalSyntheticBackport0.m(this.prolepticYear, (int) years), this.monthOfYear, this.dayOfMonth);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long calcMonths = (((long) this.prolepticYear) * 12) + ((long) (this.monthOfYear - 1)) + monthsToAdd;
        return resolvePreviousValid(this.chrono.checkValidYear(Duration$$ExternalSyntheticBackport0.m(calcMonths, 12)), ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(calcMonths, 12)) + 1, this.dayOfMonth);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate plusWeeks(long weeksToAdd) {
        return (HijrahDate) super.plusWeeks(weeksToAdd);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate plusDays(long days) {
        return new HijrahDate(this.chrono, toEpochDay() + days);
    }

    public HijrahDate plus(long amountToAdd, TemporalUnit unit) {
        return (HijrahDate) super.plus(amountToAdd, unit);
    }

    public HijrahDate minus(long amountToSubtract, TemporalUnit unit) {
        return (HijrahDate) super.minus(amountToSubtract, unit);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate minusYears(long yearsToSubtract) {
        return (HijrahDate) super.minusYears(yearsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate minusMonths(long monthsToSubtract) {
        return (HijrahDate) super.minusMonths(monthsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate minusWeeks(long weeksToSubtract) {
        return (HijrahDate) super.minusWeeks(weeksToSubtract);
    }

    /* access modifiers changed from: package-private */
    public HijrahDate minusDays(long daysToSubtract) {
        return (HijrahDate) super.minusDays(daysToSubtract);
    }

    public final ChronoLocalDateTime<HijrahDate> atTime(LocalTime localTime) {
        return super.atTime(localTime);
    }

    public ChronoPeriod until(ChronoLocalDate endDate) {
        HijrahDate end = getChronology().date((TemporalAccessor) endDate);
        long totalMonths = (long) (((end.prolepticYear - this.prolepticYear) * 12) + (end.monthOfYear - this.monthOfYear));
        int days = end.dayOfMonth - this.dayOfMonth;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            days = (int) (end.toEpochDay() - plusMonths(totalMonths).toEpochDay());
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        return getChronology().period(LocalDate$$ExternalSyntheticBackport0.m(totalMonths / 12), (int) (totalMonths % 12), days);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HijrahDate)) {
            return false;
        }
        HijrahDate otherDate = (HijrahDate) obj;
        if (this.prolepticYear == otherDate.prolepticYear && this.monthOfYear == otherDate.monthOfYear && this.dayOfMonth == otherDate.dayOfMonth && getChronology().equals(otherDate.getChronology())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int yearValue = this.prolepticYear;
        int monthValue = this.monthOfYear;
        return (getChronology().getId().hashCode() ^ (yearValue & -2048)) ^ (((yearValue << 11) + (monthValue << 6)) + this.dayOfMonth);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 6, this);
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(ObjectOutput out) {
        out.writeObject(getChronology());
        out.writeInt(get(ChronoField.YEAR));
        out.writeByte(get(ChronoField.MONTH_OF_YEAR));
        out.writeByte(get(ChronoField.DAY_OF_MONTH));
    }

    static HijrahDate readExternal(ObjectInput in) {
        return ((HijrahChronology) in.readObject()).date(in.readInt(), in.readByte(), in.readByte());
    }
}
