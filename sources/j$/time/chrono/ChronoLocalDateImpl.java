package j$.time.chrono;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.LocalTime;
import j$.time.chrono.ChronoLocalDate;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.Serializable;

abstract class ChronoLocalDateImpl<D extends ChronoLocalDate> implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable {
    private static final long serialVersionUID = 6282433883239719096L;

    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        return ChronoLocalDate.CC.$default$adjustInto(this, temporal);
    }

    public /* synthetic */ ChronoLocalDateTime atTime(LocalTime localTime) {
        return ChronoLocalDate.CC.$default$atTime(this, localTime);
    }

    public /* synthetic */ int compareTo(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$compareTo((ChronoLocalDate) this, chronoLocalDate);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return compareTo((ChronoLocalDate) obj);
    }

    public /* synthetic */ String format(DateTimeFormatter dateTimeFormatter) {
        return ChronoLocalDate.CC.$default$format(this, dateTimeFormatter);
    }

    public /* synthetic */ int get(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$get(this, temporalField);
    }

    public /* synthetic */ Era getEra() {
        return ChronoLocalDate.CC.$default$getEra(this);
    }

    public /* synthetic */ boolean isAfter(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isAfter(this, chronoLocalDate);
    }

    public /* synthetic */ boolean isBefore(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isBefore(this, chronoLocalDate);
    }

    public /* synthetic */ boolean isEqual(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isEqual(this, chronoLocalDate);
    }

    public /* synthetic */ boolean isLeapYear() {
        return ChronoLocalDate.CC.$default$isLeapYear(this);
    }

    public /* synthetic */ boolean isSupported(TemporalField temporalField) {
        return ChronoLocalDate.CC.$default$isSupported((ChronoLocalDate) this, temporalField);
    }

    public /* synthetic */ boolean isSupported(TemporalUnit temporalUnit) {
        return ChronoLocalDate.CC.$default$isSupported((ChronoLocalDate) this, temporalUnit);
    }

    public /* synthetic */ int lengthOfYear() {
        return ChronoLocalDate.CC.$default$lengthOfYear(this);
    }

    /* access modifiers changed from: package-private */
    public abstract D plusDays(long j);

    /* access modifiers changed from: package-private */
    public abstract D plusMonths(long j);

    /* access modifiers changed from: package-private */
    public abstract D plusYears(long j);

    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return ChronoLocalDate.CC.$default$query(this, temporalQuery);
    }

    public /* synthetic */ ValueRange range(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$range(this, temporalField);
    }

    public /* synthetic */ long toEpochDay() {
        return ChronoLocalDate.CC.$default$toEpochDay(this);
    }

    static <D extends ChronoLocalDate> D ensureValid(Chronology chrono, Temporal temporal) {
        D other = (ChronoLocalDate) temporal;
        if (chrono.equals(other.getChronology())) {
            return other;
        }
        throw new ClassCastException("Chronology mismatch, expected: " + chrono.getId() + ", actual: " + other.getChronology().getId());
    }

    ChronoLocalDateImpl() {
    }

    public D with(TemporalAdjuster adjuster) {
        return ChronoLocalDate.CC.$default$with((ChronoLocalDate) this, adjuster);
    }

    public D with(TemporalField field, long value) {
        return ChronoLocalDate.CC.$default$with((ChronoLocalDate) this, field, value);
    }

    public D plus(TemporalAmount amount) {
        return ChronoLocalDate.CC.$default$plus((ChronoLocalDate) this, amount);
    }

    public D plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return ChronoLocalDate.CC.$default$plus((ChronoLocalDate) this, amountToAdd, unit);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
            case 1:
                return plusDays(amountToAdd);
            case 2:
                return plusDays(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 7));
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

    /* renamed from: j$.time.chrono.ChronoLocalDateImpl$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
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
        }
    }

    public D minus(TemporalAmount amount) {
        return ChronoLocalDate.CC.$default$minus((ChronoLocalDate) this, amount);
    }

    public D minus(long amountToSubtract, TemporalUnit unit) {
        return ChronoLocalDate.CC.$default$minus((ChronoLocalDate) this, amountToSubtract, unit);
    }

    /* access modifiers changed from: package-private */
    public D plusWeeks(long weeksToAdd) {
        return plusDays(Duration$$ExternalSyntheticBackport1.m(weeksToAdd, 7));
    }

    /* access modifiers changed from: package-private */
    public D minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? ((ChronoLocalDateImpl) plusYears(Long.MAX_VALUE)).plusYears(1) : plusYears(-yearsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public D minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? ((ChronoLocalDateImpl) plusMonths(Long.MAX_VALUE)).plusMonths(1) : plusMonths(-monthsToSubtract);
    }

    /* access modifiers changed from: package-private */
    public D minusWeeks(long weeksToSubtract) {
        return weeksToSubtract == Long.MIN_VALUE ? ((ChronoLocalDateImpl) plusWeeks(Long.MAX_VALUE)).plusWeeks(1) : plusWeeks(-weeksToSubtract);
    }

    /* access modifiers changed from: package-private */
    public D minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? ((ChronoLocalDateImpl) plusDays(Long.MAX_VALUE)).plusDays(1) : plusDays(-daysToSubtract);
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        Objects.requireNonNull(endExclusive, "endExclusive");
        ChronoLocalDate end = getChronology().date(endExclusive);
        if (unit instanceof ChronoUnit) {
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
        } else {
            Objects.requireNonNull(unit, "unit");
            return unit.between(this, end);
        }
    }

    private long daysUntil(ChronoLocalDate end) {
        return end.toEpochDay() - toEpochDay();
    }

    private long monthsUntil(ChronoLocalDate end) {
        if (getChronology().range(ChronoField.MONTH_OF_YEAR).getMaximum() == 12) {
            return (((end.getLong(ChronoField.PROLEPTIC_MONTH) * 32) + ((long) end.get(ChronoField.DAY_OF_MONTH))) - ((getLong(ChronoField.PROLEPTIC_MONTH) * 32) + ((long) get(ChronoField.DAY_OF_MONTH)))) / 32;
        }
        throw new IllegalStateException("ChronoLocalDateImpl only supports Chronologies with 12 months per year");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChronoLocalDate) || compareTo((ChronoLocalDate) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        long epDay = toEpochDay();
        return getChronology().hashCode() ^ ((int) ((epDay >>> 32) ^ epDay));
    }

    public String toString() {
        long yoe = getLong(ChronoField.YEAR_OF_ERA);
        long moy = getLong(ChronoField.MONTH_OF_YEAR);
        long dom = getLong(ChronoField.DAY_OF_MONTH);
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString());
        buf.append(" ");
        buf.append(getEra());
        buf.append(" ");
        buf.append(yoe);
        String str = "-0";
        buf.append(moy < 10 ? str : "-");
        buf.append(moy);
        if (dom >= 10) {
            str = "-";
        }
        buf.append(str);
        buf.append(dom);
        return buf.toString();
    }
}
