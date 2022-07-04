package j$.time;

import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import java.util.Locale;

public enum Month implements TemporalAccessor, TemporalAdjuster {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;
    
    private static final Month[] ENUMS = null;

    static {
        ENUMS = values();
    }

    public static Month of(int month) {
        if (month >= 1 && month <= 12) {
            return ENUMS[month - 1];
        }
        throw new DateTimeException("Invalid value for MonthOfYear: " + month);
    }

    public static Month from(TemporalAccessor temporal) {
        if (temporal instanceof Month) {
            return (Month) temporal;
        }
        try {
            if (!IsoChronology.INSTANCE.equals(Chronology.CC.from(temporal))) {
                temporal = LocalDate.from(temporal);
            }
            return of(temporal.get(ChronoField.MONTH_OF_YEAR));
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Month from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public int getValue() {
        return ordinal() + 1;
    }

    public String getDisplayName(TextStyle style, Locale locale) {
        return new DateTimeFormatterBuilder().appendText((TemporalField) ChronoField.MONTH_OF_YEAR, style).toFormatter(locale).format(this);
    }

    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.MONTH_OF_YEAR) {
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
        if (field == ChronoField.MONTH_OF_YEAR) {
            return field.range();
        }
        return TemporalAccessor.CC.$default$range(this, field);
    }

    public int get(TemporalField field) {
        if (field == ChronoField.MONTH_OF_YEAR) {
            return getValue();
        }
        return TemporalAccessor.CC.$default$get(this, field);
    }

    public long getLong(TemporalField field) {
        if (field == ChronoField.MONTH_OF_YEAR) {
            return (long) getValue();
        }
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    public Month plus(long months) {
        return ENUMS[(ordinal() + (((int) (months % 12)) + 12)) % 12];
    }

    public Month minus(long months) {
        return plus(-(months % 12));
    }

    /* renamed from: j$.time.Month$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$Month = null;

        static {
            int[] iArr = new int[Month.values().length];
            $SwitchMap$java$time$Month = iArr;
            try {
                iArr[Month.FEBRUARY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$Month[Month.APRIL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$Month[Month.JUNE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$Month[Month.SEPTEMBER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$Month[Month.NOVEMBER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$Month[Month.JANUARY.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$Month[Month.MARCH.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$Month[Month.MAY.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$Month[Month.JULY.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$Month[Month.AUGUST.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$Month[Month.OCTOBER.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$Month[Month.DECEMBER.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public int length(boolean leapYear) {
        switch (AnonymousClass1.$SwitchMap$java$time$Month[ordinal()]) {
            case 1:
                return leapYear ? 29 : 28;
            case 2:
            case 3:
            case 4:
            case 5:
                return 30;
            default:
                return 31;
        }
    }

    public int minLength() {
        switch (AnonymousClass1.$SwitchMap$java$time$Month[ordinal()]) {
            case 1:
                return 28;
            case 2:
            case 3:
            case 4:
            case 5:
                return 30;
            default:
                return 31;
        }
    }

    public int maxLength() {
        switch (AnonymousClass1.$SwitchMap$java$time$Month[ordinal()]) {
            case 1:
                return 29;
            case 2:
            case 3:
            case 4:
            case 5:
                return 30;
            default:
                return 31;
        }
    }

    public int firstDayOfYear(boolean leapYear) {
        int leap = leapYear;
        switch (AnonymousClass1.$SwitchMap$java$time$Month[ordinal()]) {
            case 1:
                return 32;
            case 2:
                return ((int) leap) + true;
            case 3:
                return leap + 152;
            case 4:
                return leap + 244;
            case 5:
                return leap + 305;
            case 6:
                return 1;
            case 7:
                return leap + 60;
            case 8:
                return leap + 121;
            case 9:
                return leap + 182;
            case 10:
                return leap + 213;
            case 11:
                return leap + 274;
            default:
                return leap + 335;
        }
    }

    public Month firstMonthOfQuarter() {
        return ENUMS[(ordinal() / 3) * 3];
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
            return temporal.with(ChronoField.MONTH_OF_YEAR, (long) getValue());
        }
        throw new DateTimeException("Adjustment only supported on ISO date-time");
    }
}
