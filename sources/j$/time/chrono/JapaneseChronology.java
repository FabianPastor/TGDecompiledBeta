package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.ZoneId;
import j$.time.format.ResolverStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjusters;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class JapaneseChronology extends AbstractChronology implements Serializable {
    public static final JapaneseChronology INSTANCE = new JapaneseChronology();
    private static final long serialVersionUID = 459996390165777884L;

    private JapaneseChronology() {
    }

    public String getId() {
        return "Japanese";
    }

    public String getCalendarType() {
        return "japanese";
    }

    public JapaneseDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        if (era instanceof JapaneseEra) {
            return JapaneseDate.of((JapaneseEra) era, yearOfEra, month, dayOfMonth);
        }
        throw new ClassCastException("Era must be JapaneseEra");
    }

    public JapaneseDate date(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    public JapaneseDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return JapaneseDate.ofYearDay((JapaneseEra) era, yearOfEra, dayOfYear);
    }

    public JapaneseDate dateYearDay(int prolepticYear, int dayOfYear) {
        return new JapaneseDate(LocalDate.ofYearDay(prolepticYear, dayOfYear));
    }

    public JapaneseDate dateEpochDay(long epochDay) {
        return new JapaneseDate(LocalDate.ofEpochDay(epochDay));
    }

    public JapaneseDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    public JapaneseDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    public JapaneseDate dateNow(Clock clock) {
        return date((TemporalAccessor) LocalDate.now(clock));
    }

    public JapaneseDate date(TemporalAccessor temporal) {
        if (temporal instanceof JapaneseDate) {
            return (JapaneseDate) temporal;
        }
        return new JapaneseDate(LocalDate.from(temporal));
    }

    public ChronoLocalDateTime<JapaneseDate> localDateTime(TemporalAccessor temporal) {
        return super.localDateTime(temporal);
    }

    public ChronoZonedDateTime<JapaneseDate> zonedDateTime(TemporalAccessor temporal) {
        return super.zonedDateTime(temporal);
    }

    public ChronoZonedDateTime<JapaneseDate> zonedDateTime(Instant instant, ZoneId zone) {
        return super.zonedDateTime(instant, zone);
    }

    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(prolepticYear);
    }

    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof JapaneseEra) {
            JapaneseEra jera = (JapaneseEra) era;
            int gregorianYear = (jera.getSince().getYear() + yearOfEra) - 1;
            if (yearOfEra == 1) {
                return gregorianYear;
            }
            if (gregorianYear >= -NUM && gregorianYear <= NUM && gregorianYear >= jera.getSince().getYear() && era == JapaneseEra.from(LocalDate.of(gregorianYear, 1, 1))) {
                return gregorianYear;
            }
            throw new DateTimeException("Invalid yearOfEra value");
        }
        throw new ClassCastException("Era must be JapaneseEra");
    }

    public JapaneseEra eraOf(int eraValue) {
        return JapaneseEra.of(eraValue);
    }

    public List<Era> eras() {
        return Arrays.asList(JapaneseEra.values());
    }

    /* renamed from: j$.time.chrono.JapaneseChronology$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_YEAR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_YEAR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public ValueRange range(ChronoField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[field.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            case 5:
                return ValueRange.of(1, JapaneseEra.shortestYearsOfEra(), (long) (NUM - JapaneseEra.getCurrentEra().getSince().getYear()));
            case 6:
                return ValueRange.of(1, JapaneseEra.shortestDaysOfYear(), ChronoField.DAY_OF_YEAR.range().getMaximum());
            case 7:
                return ValueRange.of((long) JapaneseDate.MEIJI_6_ISODATE.getYear(), NUM);
            case 8:
                return ValueRange.of((long) JapaneseEra.MEIJI.getValue(), (long) JapaneseEra.getCurrentEra().getValue());
            default:
                return field.range();
        }
    }

    public JapaneseDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (JapaneseDate) super.resolveDate(fieldValues, resolverStyle);
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long eraLong = fieldValues.get(ChronoField.ERA);
        JapaneseEra era = null;
        if (eraLong != null) {
            era = eraOf(range(ChronoField.ERA).checkValidIntValue(eraLong.longValue(), ChronoField.ERA));
        }
        Long yoeLong = fieldValues.get(ChronoField.YEAR_OF_ERA);
        int yoe = 0;
        if (yoeLong != null) {
            yoe = range(ChronoField.YEAR_OF_ERA).checkValidIntValue(yoeLong.longValue(), ChronoField.YEAR_OF_ERA);
        }
        if (era == null && yoeLong != null && !fieldValues.containsKey(ChronoField.YEAR) && resolverStyle != ResolverStyle.STRICT) {
            era = JapaneseEra.values()[JapaneseEra.values().length - 1];
        }
        if (yoeLong == null || era == null) {
            return null;
        }
        if (fieldValues.containsKey(ChronoField.MONTH_OF_YEAR) && fieldValues.containsKey(ChronoField.DAY_OF_MONTH)) {
            return resolveYMD(era, yoe, fieldValues, resolverStyle);
        }
        if (fieldValues.containsKey(ChronoField.DAY_OF_YEAR)) {
            return resolveYD(era, yoe, fieldValues, resolverStyle);
        }
        return null;
    }

    private int prolepticYearLenient(JapaneseEra era, int yearOfEra) {
        return (era.getSince().getYear() + yearOfEra) - 1;
    }

    private ChronoLocalDate resolveYMD(JapaneseEra era, int yoe, Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        JapaneseDate result;
        fieldValues.remove(ChronoField.ERA);
        fieldValues.remove(ChronoField.YEAR_OF_ERA);
        if (resolverStyle == ResolverStyle.LENIENT) {
            int y = prolepticYearLenient(era, yoe);
            long months = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1);
            return date(y, 1, 1).plus(months, (TemporalUnit) ChronoUnit.MONTHS).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue(), 1), (TemporalUnit) ChronoUnit.DAYS);
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        int dom = range(ChronoField.DAY_OF_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue(), ChronoField.DAY_OF_MONTH);
        if (resolverStyle != ResolverStyle.SMART) {
            return date((Era) era, yoe, moy, dom);
        }
        if (yoe >= 1) {
            int y2 = prolepticYearLenient(era, yoe);
            try {
                result = date(y2, moy, dom);
            } catch (DateTimeException e) {
                result = date(y2, moy, 1).with(TemporalAdjusters.lastDayOfMonth());
            }
            if (result.getEra() == era || result.get(ChronoField.YEAR_OF_ERA) <= 1 || yoe <= 1) {
                return result;
            }
            throw new DateTimeException("Invalid YearOfEra for Era: " + era + " " + yoe);
        }
        throw new DateTimeException("Invalid YearOfEra: " + yoe);
    }

    private ChronoLocalDate resolveYD(JapaneseEra era, int yoe, Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        fieldValues.remove(ChronoField.ERA);
        fieldValues.remove(ChronoField.YEAR_OF_ERA);
        if (resolverStyle != ResolverStyle.LENIENT) {
            return dateYearDay((Era) era, yoe, range(ChronoField.DAY_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_YEAR).longValue(), ChronoField.DAY_OF_YEAR));
        }
        int y = prolepticYearLenient(era, yoe);
        return dateYearDay(y, 1).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.DAY_OF_YEAR).longValue(), 1), (TemporalUnit) ChronoUnit.DAYS);
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return super.writeReplace();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
