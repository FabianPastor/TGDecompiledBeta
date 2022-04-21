package j$.time;

import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.IsoChronology;
import j$.time.chrono.IsoEra;
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
import j$.time.zone.ZoneOffsetTransition;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class LocalDate implements Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
    static final long DAYS_0000_TO_1970 = 719528;
    private static final int DAYS_PER_CYCLE = 146097;
    public static final LocalDate MAX = of(NUM, 12, 31);
    public static final LocalDate MIN = of(-NUM, 1, 1);
    private static final long serialVersionUID = 2942565459149668126L;
    private final short day;
    private final short month;
    private final int year;

    public static LocalDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static LocalDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static LocalDate now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        return ofEpochDay(Duration$$ExternalSyntheticBackport0.m(now.getEpochSecond() + ((long) clock.getZone().getRules().getOffset(now).getTotalSeconds()), 86400));
    }

    public static LocalDate of(int year2, Month month2, int dayOfMonth) {
        ChronoField.YEAR.checkValidValue((long) year2);
        Objects.requireNonNull(month2, "month");
        ChronoField.DAY_OF_MONTH.checkValidValue((long) dayOfMonth);
        return create(year2, month2.getValue(), dayOfMonth);
    }

    public static LocalDate of(int year2, int month2, int dayOfMonth) {
        ChronoField.YEAR.checkValidValue((long) year2);
        ChronoField.MONTH_OF_YEAR.checkValidValue((long) month2);
        ChronoField.DAY_OF_MONTH.checkValidValue((long) dayOfMonth);
        return create(year2, month2, dayOfMonth);
    }

    public static LocalDate ofYearDay(int year2, int dayOfYear) {
        ChronoField.YEAR.checkValidValue((long) year2);
        ChronoField.DAY_OF_YEAR.checkValidValue((long) dayOfYear);
        boolean leap = IsoChronology.INSTANCE.isLeapYear((long) year2);
        if (dayOfYear != 366 || leap) {
            Month moy = Month.of(((dayOfYear - 1) / 31) + 1);
            if (dayOfYear > (moy.firstDayOfYear(leap) + moy.length(leap)) - 1) {
                moy = moy.plus(1);
            }
            return new LocalDate(year2, moy.getValue(), (dayOfYear - moy.firstDayOfYear(leap)) + 1);
        }
        throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + year2 + "' is not a leap year");
    }

    public static LocalDate ofEpochDay(long epochDay) {
        long zeroDay = (epochDay + 719528) - 60;
        long adjust = 0;
        if (zeroDay < 0) {
            long adjustCycles = ((zeroDay + 1) / 146097) - 1;
            adjust = adjustCycles * 400;
            zeroDay += (-adjustCycles) * 146097;
        }
        long yearEst = ((zeroDay * 400) + 591) / 146097;
        long doyEst = zeroDay - ((((yearEst * 365) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        if (doyEst < 0) {
            yearEst--;
            doyEst = zeroDay - ((((365 * yearEst) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        }
        int marchDoy0 = (int) doyEst;
        int marchMonth0 = ((marchDoy0 * 5) + 2) / 153;
        return new LocalDate(ChronoField.YEAR.checkValidIntValue(yearEst + adjust + ((long) (marchMonth0 / 10))), ((marchMonth0 + 2) % 12) + 1, (marchDoy0 - (((marchMonth0 * 306) + 5) / 10)) + 1);
    }

    public static LocalDate from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        LocalDate date = (LocalDate) temporal.query(TemporalQueries.localDate());
        if (date != null) {
            return date;
        }
        throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
    }

    public static LocalDate parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (LocalDate) formatter.parse(text, LocalDate$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static LocalDate create(int year2, int month2, int dayOfMonth) {
        int i = 28;
        if (dayOfMonth > 28) {
            int dom = 31;
            switch (month2) {
                case 2:
                    if (IsoChronology.INSTANCE.isLeapYear((long) year2)) {
                        i = 29;
                    }
                    dom = i;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dom = 30;
                    break;
            }
            if (dayOfMonth > dom) {
                if (dayOfMonth == 29) {
                    throw new DateTimeException("Invalid date 'February 29' as '" + year2 + "' is not a leap year");
                }
                throw new DateTimeException("Invalid date '" + Month.of(month2).name() + " " + dayOfMonth + "'");
            }
        }
        return new LocalDate(year2, month2, dayOfMonth);
    }

    private static LocalDate resolvePreviousValid(int year2, int month2, int day2) {
        switch (month2) {
            case 2:
                day2 = Math.min(day2, IsoChronology.INSTANCE.isLeapYear((long) year2) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day2 = Math.min(day2, 30);
                break;
        }
        return new LocalDate(year2, month2, day2);
    }

    private LocalDate(int year2, int month2, int dayOfMonth) {
        this.year = year2;
        this.month = (short) month2;
        this.day = (short) dayOfMonth;
    }

    public boolean isSupported(TemporalField field) {
        return ChronoLocalDate.CC.$default$isSupported((ChronoLocalDate) this, field);
    }

    public boolean isSupported(TemporalUnit unit) {
        return ChronoLocalDate.CC.$default$isSupported((ChronoLocalDate) this, unit);
    }

    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.rangeRefinedBy(this);
        }
        ChronoField f = (ChronoField) field;
        if (f.isDateBased()) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    return ValueRange.of(1, (long) lengthOfMonth());
                case 2:
                    return ValueRange.of(1, (long) lengthOfYear());
                case 3:
                    return ValueRange.of(1, (getMonth() != Month.FEBRUARY || isLeapYear()) ? 5 : 4);
                case 4:
                    return ValueRange.of(1, getYear() <= 0 ? NUM : NUM);
                default:
                    return field.range();
            }
        } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
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
        if (field == ChronoField.EPOCH_DAY) {
            return toEpochDay();
        }
        if (field == ChronoField.PROLEPTIC_MONTH) {
            return getProlepticMonth();
        }
        return (long) get0(field);
    }

    private int get0(TemporalField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return this.day;
            case 2:
                return getDayOfYear();
            case 3:
                return ((this.day - 1) / 7) + 1;
            case 4:
                int i = this.year;
                return i >= 1 ? i : 1 - i;
            case 5:
                return getDayOfWeek().getValue();
            case 6:
                return ((this.day - 1) % 7) + 1;
            case 7:
                return ((getDayOfYear() - 1) % 7) + 1;
            case 8:
                throw new UnsupportedTemporalTypeException("Invalid field 'EpochDay' for get() method, use getLong() instead");
            case 9:
                return ((getDayOfYear() - 1) / 7) + 1;
            case 10:
                return this.month;
            case 11:
                throw new UnsupportedTemporalTypeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
            case 12:
                return this.year;
            case 13:
                if (this.year >= 1) {
                    return 1;
                }
                return 0;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    private long getProlepticMonth() {
        return ((((long) this.year) * 12) + ((long) this.month)) - 1;
    }

    public IsoChronology getChronology() {
        return IsoChronology.INSTANCE;
    }

    public IsoEra getEra() {
        return getYear() >= 1 ? IsoEra.CE : IsoEra.BCE;
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

    public int getDayOfMonth() {
        return this.day;
    }

    public int getDayOfYear() {
        return (getMonth().firstDayOfYear(isLeapYear()) + this.day) - 1;
    }

    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.of(((int) Clock$TickClock$$ExternalSyntheticBackport0.m(toEpochDay() + 3, 7)) + 1);
    }

    public boolean isLeapYear() {
        return IsoChronology.INSTANCE.isLeapYear((long) this.year);
    }

    public int lengthOfMonth() {
        switch (this.month) {
            case 2:
                return isLeapYear() ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    public int lengthOfYear() {
        return isLeapYear() ? 366 : 365;
    }

    public LocalDate with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return (LocalDate) adjuster;
        }
        return (LocalDate) adjuster.adjustInto(this);
    }

    public LocalDate with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (LocalDate) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        f.checkValidValue(newValue);
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return withDayOfMonth((int) newValue);
            case 2:
                return withDayOfYear((int) newValue);
            case 3:
                return plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
            case 4:
                return withYear((int) (this.year >= 1 ? newValue : 1 - newValue));
            case 5:
                return plusDays(newValue - ((long) getDayOfWeek().getValue()));
            case 6:
                return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
            case 7:
                return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
            case 8:
                return ofEpochDay(newValue);
            case 9:
                return plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
            case 10:
                return withMonth((int) newValue);
            case 11:
                return plusMonths(newValue - getProlepticMonth());
            case 12:
                return withYear((int) newValue);
            case 13:
                return getLong(ChronoField.ERA) == newValue ? this : withYear(1 - this.year);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public LocalDate withYear(int year2) {
        if (this.year == year2) {
            return this;
        }
        ChronoField.YEAR.checkValidValue((long) year2);
        return resolvePreviousValid(year2, this.month, this.day);
    }

    public LocalDate withMonth(int month2) {
        if (this.month == month2) {
            return this;
        }
        ChronoField.MONTH_OF_YEAR.checkValidValue((long) month2);
        return resolvePreviousValid(this.year, month2, this.day);
    }

    public LocalDate withDayOfMonth(int dayOfMonth) {
        if (this.day == dayOfMonth) {
            return this;
        }
        return of(this.year, (int) this.month, dayOfMonth);
    }

    public LocalDate withDayOfYear(int dayOfYear) {
        if (getDayOfYear() == dayOfYear) {
            return this;
        }
        return ofYearDay(this.year, dayOfYear);
    }

    public LocalDate plus(TemporalAmount amountToAdd) {
        if (amountToAdd instanceof Period) {
            Period periodToAdd = (Period) amountToAdd;
            return plusMonths(periodToAdd.toTotalMonths()).plusDays((long) periodToAdd.getDays());
        }
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        return (LocalDate) amountToAdd.addTo(this);
    }

    public LocalDate plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (LocalDate) unit.addTo(this, amountToAdd);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusDays(amountToAdd);
            case 2:
                return plusWeeks(amountToAdd);
            case 3:
                return plusMonths(amountToAdd);
            case 4:
                return plusYears(amountToAdd);
            case 5:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 10));
            case 6:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 100));
            case 7:
                return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 1000));
            case 8:
                return with((TemporalField) ChronoField.ERA, Clock$OffsetClock$$ExternalSyntheticBackport0.m(getLong(ChronoField.ERA), amountToAdd));
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    /* renamed from: j$.time.LocalDate$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.DAYS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.WEEKS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MONTHS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.YEARS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DECADES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.CENTURIES.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLENNIA.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.ERAS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.DAY_OF_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 4;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_WEEK.ordinal()] = 5;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH.ordinal()] = 6;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR.ordinal()] = 7;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.EPOCH_DAY.ordinal()] = 8;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_YEAR.ordinal()] = 9;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MONTH_OF_YEAR.ordinal()] = 10;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.PROLEPTIC_MONTH.ordinal()] = 11;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 12;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 13;
            } catch (NoSuchFieldError e21) {
            }
        }
    }

    public LocalDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return resolvePreviousValid(ChronoField.YEAR.checkValidIntValue(((long) this.year) + yearsToAdd), this.month, this.day);
    }

    public LocalDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long calcMonths = (((long) this.year) * 12) + ((long) (this.month - 1)) + monthsToAdd;
        return resolvePreviousValid(ChronoField.YEAR.checkValidIntValue(Duration$$ExternalSyntheticBackport0.m(calcMonths, 12)), ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(calcMonths, 12)) + 1, this.day);
    }

    public LocalDate plusWeeks(long weeksToAdd) {
        return plusDays(Duration$$ExternalSyntheticBackport1.m(weeksToAdd, 7));
    }

    public LocalDate plusDays(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        return ofEpochDay(Clock$OffsetClock$$ExternalSyntheticBackport0.m(toEpochDay(), daysToAdd));
    }

    public LocalDate minus(TemporalAmount amountToSubtract) {
        if (amountToSubtract instanceof Period) {
            Period periodToSubtract = (Period) amountToSubtract;
            return minusMonths(periodToSubtract.toTotalMonths()).minusDays((long) periodToSubtract.getDays());
        }
        Objects.requireNonNull(amountToSubtract, "amountToSubtract");
        return (LocalDate) amountToSubtract.subtractFrom(this);
    }

    public LocalDate minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public LocalDate minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-yearsToSubtract);
    }

    public LocalDate minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-monthsToSubtract);
    }

    public LocalDate minusWeeks(long weeksToSubtract) {
        return weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeksToSubtract);
    }

    public LocalDate minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-daysToSubtract);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.localDate()) {
            return this;
        }
        return ChronoLocalDate.CC.$default$query(this, temporalQuery);
    }

    public Temporal adjustInto(Temporal temporal) {
        return ChronoLocalDate.CC.$default$adjustInto(this, temporal);
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        LocalDate end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return daysUntil(end);
            case 2:
                return daysUntil(end) / 7;
            case 3:
                return monthsUntil(end);
            case 4:
                return monthsUntil(end) / 12;
            case 5:
                return monthsUntil(end) / 120;
            case 6:
                return monthsUntil(end) / 1200;
            case 7:
                return monthsUntil(end) / 12000;
            case 8:
                return end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
            default:
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
        }
    }

    /* access modifiers changed from: package-private */
    public long daysUntil(LocalDate end) {
        return end.toEpochDay() - toEpochDay();
    }

    private long monthsUntil(LocalDate end) {
        return (((end.getProlepticMonth() * 32) + ((long) end.getDayOfMonth())) - ((getProlepticMonth() * 32) + ((long) getDayOfMonth()))) / 32;
    }

    public Period until(ChronoLocalDate endDateExclusive) {
        LocalDate end = from(endDateExclusive);
        long totalMonths = end.getProlepticMonth() - getProlepticMonth();
        int days = end.day - this.day;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            days = (int) (end.toEpochDay() - plusMonths(totalMonths).toEpochDay());
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        return Period.of(LocalDate$$ExternalSyntheticBackport0.m(totalMonths / 12), (int) (totalMonths % 12), days);
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public LocalDateTime atTime(LocalTime time) {
        return LocalDateTime.of(this, time);
    }

    public LocalDateTime atTime(int hour, int minute) {
        return atTime(LocalTime.of(hour, minute));
    }

    public LocalDateTime atTime(int hour, int minute, int second) {
        return atTime(LocalTime.of(hour, minute, second));
    }

    public LocalDateTime atTime(int hour, int minute, int second, int nanoOfSecond) {
        return atTime(LocalTime.of(hour, minute, second, nanoOfSecond));
    }

    public OffsetDateTime atTime(OffsetTime time) {
        return OffsetDateTime.of(LocalDateTime.of(this, time.toLocalTime()), time.getOffset());
    }

    public LocalDateTime atStartOfDay() {
        return LocalDateTime.of(this, LocalTime.MIDNIGHT);
    }

    public ZonedDateTime atStartOfDay(ZoneId zone) {
        ZoneOffsetTransition trans;
        Objects.requireNonNull(zone, "zone");
        LocalDateTime ldt = atTime(LocalTime.MIDNIGHT);
        if (!(zone instanceof ZoneOffset) && (trans = zone.getRules().getTransition(ldt)) != null && trans.isGap()) {
            ldt = trans.getDateTimeAfter();
        }
        return ZonedDateTime.of(ldt, zone);
    }

    public long toEpochDay() {
        long total;
        long y = (long) this.year;
        long m = (long) this.month;
        long total2 = 0 + (365 * y);
        if (y >= 0) {
            total = total2 + (((3 + y) / 4) - ((99 + y) / 100)) + ((399 + y) / 400);
        } else {
            total = total2 - (((y / -4) - (y / -100)) + (y / -400));
        }
        long total3 = total + (((367 * m) - 362) / 12) + ((long) (this.day - 1));
        if (m > 2) {
            total3--;
            if (!isLeapYear()) {
                total3--;
            }
        }
        return total3 - 719528;
    }

    public int compareTo(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other);
        }
        return ChronoLocalDate.CC.$default$compareTo((ChronoLocalDate) this, other);
    }

    /* access modifiers changed from: package-private */
    public int compareTo0(LocalDate otherDate) {
        int cmp = this.year - otherDate.year;
        if (cmp != 0) {
            return cmp;
        }
        int cmp2 = this.month - otherDate.month;
        if (cmp2 == 0) {
            return this.day - otherDate.day;
        }
        return cmp2;
    }

    public boolean isAfter(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) > 0;
        }
        return ChronoLocalDate.CC.$default$isAfter(this, other);
    }

    public boolean isBefore(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) < 0;
        }
        return ChronoLocalDate.CC.$default$isBefore(this, other);
    }

    public boolean isEqual(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) == 0;
        }
        return ChronoLocalDate.CC.$default$isEqual(this, other);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalDate) || compareTo0((LocalDate) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int yearValue = this.year;
        return (yearValue & -2048) ^ (((yearValue << 11) + (this.month << 6)) + this.day);
    }

    public String toString() {
        int yearValue = this.year;
        int monthValue = this.month;
        int dayValue = this.day;
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(10);
        if (absYear >= 1000) {
            if (yearValue > 9999) {
                buf.append('+');
            }
            buf.append(yearValue);
        } else if (yearValue < 0) {
            buf.append(yearValue - 10000);
            buf.deleteCharAt(1);
        } else {
            buf.append(yearValue + 10000);
            buf.deleteCharAt(0);
        }
        String str = "-0";
        buf.append(monthValue < 10 ? str : "-");
        buf.append(monthValue);
        if (dayValue >= 10) {
            str = "-";
        }
        buf.append(str);
        buf.append(dayValue);
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 3, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeInt(this.year);
        out.writeByte(this.month);
        out.writeByte(this.day);
    }

    static LocalDate readExternal(DataInput in) {
        return of(in.readInt(), in.readByte(), in.readByte());
    }
}
