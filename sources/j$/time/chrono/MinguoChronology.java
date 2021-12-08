package j$.time.chrono;

import j$.time.Clock;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.ZoneId;
import j$.time.format.ResolverStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.ValueRange;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MinguoChronology extends AbstractChronology implements Serializable {
    public static final MinguoChronology INSTANCE = new MinguoChronology();
    static final int YEARS_DIFFERENCE = 1911;
    private static final long serialVersionUID = 1039765215346859963L;

    private MinguoChronology() {
    }

    public String getId() {
        return "Minguo";
    }

    public String getCalendarType() {
        return "roc";
    }

    public MinguoDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    public MinguoDate date(int prolepticYear, int month, int dayOfMonth) {
        return new MinguoDate(LocalDate.of(prolepticYear + 1911, month, dayOfMonth));
    }

    public MinguoDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    public MinguoDate dateYearDay(int prolepticYear, int dayOfYear) {
        return new MinguoDate(LocalDate.ofYearDay(prolepticYear + 1911, dayOfYear));
    }

    public MinguoDate dateEpochDay(long epochDay) {
        return new MinguoDate(LocalDate.ofEpochDay(epochDay));
    }

    public MinguoDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    public MinguoDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    public MinguoDate dateNow(Clock clock) {
        return date((TemporalAccessor) LocalDate.now(clock));
    }

    public MinguoDate date(TemporalAccessor temporal) {
        if (temporal instanceof MinguoDate) {
            return (MinguoDate) temporal;
        }
        return new MinguoDate(LocalDate.from(temporal));
    }

    public ChronoLocalDateTime<MinguoDate> localDateTime(TemporalAccessor temporal) {
        return super.localDateTime(temporal);
    }

    public ChronoZonedDateTime<MinguoDate> zonedDateTime(TemporalAccessor temporal) {
        return super.zonedDateTime(temporal);
    }

    public ChronoZonedDateTime<MinguoDate> zonedDateTime(Instant instant, ZoneId zone) {
        return super.zonedDateTime(instant, zone);
    }

    public boolean isLeapYear(long prolepticYear) {
        return IsoChronology.INSTANCE.isLeapYear(1911 + prolepticYear);
    }

    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof MinguoEra) {
            return era == MinguoEra.ROC ? yearOfEra : 1 - yearOfEra;
        }
        throw new ClassCastException("Era must be MinguoEra");
    }

    public MinguoEra eraOf(int eraValue) {
        return MinguoEra.of(eraValue);
    }

    public List<Era> eras() {
        return Arrays.asList(MinguoEra.values());
    }

    /* renamed from: j$.time.chrono.MinguoChronology$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.PROLEPTIC_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public ValueRange range(ChronoField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[field.ordinal()]) {
            case 1:
                ValueRange range = ChronoField.PROLEPTIC_MONTH.range();
                return ValueRange.of(range.getMinimum() - 22932, range.getMaximum() - 22932);
            case 2:
                ValueRange range2 = ChronoField.YEAR.range();
                return ValueRange.of(1, range2.getMaximum() - 1911, (-range2.getMinimum()) + 1 + 1911);
            case 3:
                ValueRange range3 = ChronoField.YEAR.range();
                return ValueRange.of(range3.getMinimum() - 1911, range3.getMaximum() - 1911);
            default:
                return field.range();
        }
    }

    public MinguoDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (MinguoDate) super.resolveDate(fieldValues, resolverStyle);
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return super.writeReplace();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
