package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.Period;
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
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class JapaneseDate extends ChronoLocalDateImpl<JapaneseDate> implements ChronoLocalDate, Serializable {
    static final LocalDate MEIJI_6_ISODATE = LocalDate.of(1873, 1, 1);
    private static final long serialVersionUID = -305327627230580483L;
    private transient JapaneseEra era;
    private final transient LocalDate isoDate;
    private transient int yearOfEra;

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public /* bridge */ /* synthetic */ long until(Temporal temporal, TemporalUnit temporalUnit) {
        return super.until(temporal, temporalUnit);
    }

    public static JapaneseDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static JapaneseDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static JapaneseDate now(Clock clock) {
        return new JapaneseDate(LocalDate.now(clock));
    }

    public static JapaneseDate of(JapaneseEra era2, int yearOfEra2, int month, int dayOfMonth) {
        Objects.requireNonNull(era2, "era");
        LocalDate date = LocalDate.of((era2.getSince().getYear() + yearOfEra2) - 1, month, dayOfMonth);
        if (!date.isBefore(era2.getSince()) && era2 == JapaneseEra.from(date)) {
            return new JapaneseDate(era2, yearOfEra2, date);
        }
        throw new DateTimeException("year, month, and day not valid for Era");
    }

    public static JapaneseDate of(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    static JapaneseDate ofYearDay(JapaneseEra era2, int yearOfEra2, int dayOfYear) {
        LocalDate localdate;
        Objects.requireNonNull(era2, "era");
        if (yearOfEra2 == 1) {
            localdate = LocalDate.ofYearDay(era2.getSince().getYear(), (era2.getSince().getDayOfYear() + dayOfYear) - 1);
        } else {
            localdate = LocalDate.ofYearDay((era2.getSince().getYear() + yearOfEra2) - 1, dayOfYear);
        }
        if (!localdate.isBefore(era2.getSince()) && era2 == JapaneseEra.from(localdate)) {
            return new JapaneseDate(era2, yearOfEra2, localdate);
        }
        throw new DateTimeException("Invalid parameters");
    }

    public static JapaneseDate from(TemporalAccessor temporal) {
        return JapaneseChronology.INSTANCE.date(temporal);
    }

    JapaneseDate(LocalDate isoDate2) {
        if (!isoDate2.isBefore(MEIJI_6_ISODATE)) {
            this.era = JapaneseEra.from(isoDate2);
            this.yearOfEra = (isoDate2.getYear() - this.era.getSince().getYear()) + 1;
            this.isoDate = isoDate2;
            return;
        }
        throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
    }

    JapaneseDate(JapaneseEra era2, int year, LocalDate isoDate2) {
        if (!isoDate2.isBefore(MEIJI_6_ISODATE)) {
            this.era = era2;
            this.yearOfEra = year;
            this.isoDate = isoDate2;
            return;
        }
        throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
    }

    public JapaneseChronology getChronology() {
        return JapaneseChronology.INSTANCE;
    }

    public JapaneseEra getEra() {
        return this.era;
    }

    public int lengthOfMonth() {
        return this.isoDate.lengthOfMonth();
    }

    public int lengthOfYear() {
        int remaining;
        JapaneseEra nextEra = this.era.next();
        if (nextEra == null || nextEra.getSince().getYear() != this.isoDate.getYear()) {
            remaining = this.isoDate.lengthOfYear();
        } else {
            remaining = nextEra.getSince().getDayOfYear() - 1;
        }
        if (this.yearOfEra == 1) {
            return remaining - (this.era.getSince().getDayOfYear() - 1);
        }
        return remaining;
    }

    public boolean isSupported(TemporalField field) {
        if (field == ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH || field == ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR || field == ChronoField.ALIGNED_WEEK_OF_MONTH || field == ChronoField.ALIGNED_WEEK_OF_YEAR) {
            return false;
        }
        if (field instanceof ChronoField) {
            return field.isDateBased();
        }
        if (field == null || !field.isSupportedBy(this)) {
            return false;
        }
        return true;
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
                    int startYear = this.era.getSince().getYear();
                    JapaneseEra nextEra = this.era.next();
                    if (nextEra != null) {
                        return ValueRange.of(1, (long) ((nextEra.getSince().getYear() - startYear) + 1));
                    }
                    return ValueRange.of(1, (long) (NUM - startYear));
                default:
                    return getChronology().range(f);
            }
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        switch ((ChronoField) field) {
            case DAY_OF_YEAR:
                if (this.yearOfEra == 1) {
                    return (long) ((this.isoDate.getDayOfYear() - this.era.getSince().getDayOfYear()) + 1);
                }
                return (long) this.isoDate.getDayOfYear();
            case YEAR_OF_ERA:
                return (long) this.yearOfEra;
            case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            case ALIGNED_WEEK_OF_MONTH:
            case ALIGNED_WEEK_OF_YEAR:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            case ERA:
                return (long) this.era.getValue();
            default:
                return this.isoDate.getLong(field);
        }
    }

    public JapaneseDate with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (JapaneseDate) super.with(field, newValue);
        }
        ChronoField f = (ChronoField) field;
        if (getLong(f) == newValue) {
            return this;
        }
        switch (f) {
            case YEAR_OF_ERA:
            case ERA:
            case YEAR:
                int nvalue = getChronology().range(f).checkValidIntValue(newValue, f);
                switch (f) {
                    case YEAR_OF_ERA:
                        return withYear(nvalue);
                    case ERA:
                        return withYear(JapaneseEra.of(nvalue), this.yearOfEra);
                    case YEAR:
                        return with(this.isoDate.withYear(nvalue));
                }
        }
        return with(this.isoDate.with(field, newValue));
    }

    public JapaneseDate with(TemporalAdjuster adjuster) {
        return (JapaneseDate) super.with(adjuster);
    }

    public JapaneseDate plus(TemporalAmount amount) {
        return (JapaneseDate) super.plus(amount);
    }

    public JapaneseDate minus(TemporalAmount amount) {
        return (JapaneseDate) super.minus(amount);
    }

    private JapaneseDate withYear(JapaneseEra era2, int yearOfEra2) {
        return with(this.isoDate.withYear(JapaneseChronology.INSTANCE.prolepticYear(era2, yearOfEra2)));
    }

    private JapaneseDate withYear(int year) {
        return withYear(getEra(), year);
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate plusYears(long years) {
        return with(this.isoDate.plusYears(years));
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate plusMonths(long months) {
        return with(this.isoDate.plusMonths(months));
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate plusWeeks(long weeksToAdd) {
        return with(this.isoDate.plusWeeks(weeksToAdd));
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate plusDays(long days) {
        return with(this.isoDate.plusDays(days));
    }

    public JapaneseDate plus(long amountToAdd, TemporalUnit unit) {
        return (JapaneseDate) super.plus(amountToAdd, unit);
    }

    public JapaneseDate minus(long amountToAdd, TemporalUnit unit) {
        return (JapaneseDate) super.minus(amountToAdd, unit);
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate minusYears(long yearsToSubtract) {
        return (JapaneseDate) super.minusYears(yearsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate minusMonths(long monthsToSubtract) {
        return (JapaneseDate) super.minusMonths(monthsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate minusWeeks(long weeksToSubtract) {
        return (JapaneseDate) super.minusWeeks(weeksToSubtract);
    }

    /* access modifiers changed from: package-private */
    public JapaneseDate minusDays(long daysToSubtract) {
        return (JapaneseDate) super.minusDays(daysToSubtract);
    }

    private JapaneseDate with(LocalDate newDate) {
        return newDate.equals(this.isoDate) ? this : new JapaneseDate(newDate);
    }

    public final ChronoLocalDateTime<JapaneseDate> atTime(LocalTime localTime) {
        return super.atTime(localTime);
    }

    public ChronoPeriod until(ChronoLocalDate endDate) {
        Period period = this.isoDate.until(endDate);
        return getChronology().period(period.getYears(), period.getMonths(), period.getDays());
    }

    public long toEpochDay() {
        return this.isoDate.toEpochDay();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JapaneseDate) {
            return this.isoDate.equals(((JapaneseDate) obj).isoDate);
        }
        return false;
    }

    public int hashCode() {
        return getChronology().getId().hashCode() ^ this.isoDate.hashCode();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 4, this);
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(get(ChronoField.YEAR));
        out.writeByte(get(ChronoField.MONTH_OF_YEAR));
        out.writeByte(get(ChronoField.DAY_OF_MONTH));
    }

    static JapaneseDate readExternal(DataInput in) {
        return JapaneseChronology.INSTANCE.date(in.readInt(), in.readByte(), in.readByte());
    }
}
