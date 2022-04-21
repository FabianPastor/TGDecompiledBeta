package j$.time.chrono;

import j$.time.Clock;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Instant;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.Month;
import j$.time.Period;
import j$.time.Year;
import j$.time.ZoneId;
import j$.time.ZonedDateTime;
import j$.time.format.ResolverStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class IsoChronology extends AbstractChronology implements Serializable {
    public static final IsoChronology INSTANCE = new IsoChronology();
    private static final long serialVersionUID = -1440403870442975015L;

    private IsoChronology() {
    }

    public String getId() {
        return "ISO";
    }

    public String getCalendarType() {
        return "iso8601";
    }

    public LocalDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    public LocalDate date(int prolepticYear, int month, int dayOfMonth) {
        return LocalDate.of(prolepticYear, month, dayOfMonth);
    }

    public LocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    public LocalDate dateYearDay(int prolepticYear, int dayOfYear) {
        return LocalDate.ofYearDay(prolepticYear, dayOfYear);
    }

    public LocalDate dateEpochDay(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    public LocalDate date(TemporalAccessor temporal) {
        return LocalDate.from(temporal);
    }

    public LocalDateTime localDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }

    public ZonedDateTime zonedDateTime(TemporalAccessor temporal) {
        return ZonedDateTime.from(temporal);
    }

    public ZonedDateTime zonedDateTime(Instant instant, ZoneId zone) {
        return ZonedDateTime.ofInstant(instant, zone);
    }

    public LocalDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    public LocalDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    public LocalDate dateNow(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return date((TemporalAccessor) LocalDate.now(clock));
    }

    public boolean isLeapYear(long prolepticYear) {
        return (3 & prolepticYear) == 0 && (prolepticYear % 100 != 0 || prolepticYear % 400 == 0);
    }

    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof IsoEra) {
            return era == IsoEra.CE ? yearOfEra : 1 - yearOfEra;
        }
        throw new ClassCastException("Era must be IsoEra");
    }

    public IsoEra eraOf(int eraValue) {
        return IsoEra.of(eraValue);
    }

    public List<Era> eras() {
        return Arrays.asList(IsoEra.values());
    }

    public LocalDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        return (LocalDate) super.resolveDate(fieldValues, resolverStyle);
    }

    /* access modifiers changed from: package-private */
    public void resolveProlepticMonth(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long pMonth = fieldValues.remove(ChronoField.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.PROLEPTIC_MONTH.checkValidValue(pMonth.longValue());
            }
            addFieldValue(fieldValues, ChronoField.MONTH_OF_YEAR, Clock$TickClock$$ExternalSyntheticBackport0.m(pMonth.longValue(), 12) + 1);
            addFieldValue(fieldValues, ChronoField.YEAR, Duration$$ExternalSyntheticBackport0.m(pMonth.longValue(), 12));
        }
    }

    /* access modifiers changed from: package-private */
    public LocalDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long yoeLong = fieldValues.remove(ChronoField.YEAR_OF_ERA);
        if (yoeLong != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.YEAR_OF_ERA.checkValidValue(yoeLong.longValue());
            }
            Long era = fieldValues.remove(ChronoField.ERA);
            if (era == null) {
                Long year = fieldValues.get(ChronoField.YEAR);
                if (resolverStyle != ResolverStyle.STRICT) {
                    addFieldValue(fieldValues, ChronoField.YEAR, (year == null || year.longValue() > 0) ? yoeLong.longValue() : Instant$$ExternalSyntheticBackport0.m(1, yoeLong.longValue()));
                    return null;
                } else if (year != null) {
                    ChronoField chronoField = ChronoField.YEAR;
                    int i = (year.longValue() > 0 ? 1 : (year.longValue() == 0 ? 0 : -1));
                    long longValue = yoeLong.longValue();
                    if (i <= 0) {
                        longValue = Instant$$ExternalSyntheticBackport0.m(1, longValue);
                    }
                    addFieldValue(fieldValues, chronoField, longValue);
                    return null;
                } else {
                    fieldValues.put(ChronoField.YEAR_OF_ERA, yoeLong);
                    return null;
                }
            } else if (era.longValue() == 1) {
                addFieldValue(fieldValues, ChronoField.YEAR, yoeLong.longValue());
                return null;
            } else if (era.longValue() == 0) {
                addFieldValue(fieldValues, ChronoField.YEAR, Instant$$ExternalSyntheticBackport0.m(1, yoeLong.longValue()));
                return null;
            } else {
                throw new DateTimeException("Invalid value for era: " + era);
            }
        } else if (!fieldValues.containsKey(ChronoField.ERA)) {
            return null;
        } else {
            ChronoField.ERA.checkValidValue(fieldValues.get(ChronoField.ERA).longValue());
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public LocalDate resolveYMD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = ChronoField.YEAR.checkValidIntValue(fieldValues.remove(ChronoField.YEAR).longValue());
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1);
            return LocalDate.of(y, 1, 1).plusMonths(months).plusDays(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue(), 1));
        }
        int moy = ChronoField.MONTH_OF_YEAR.checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue());
        int dom = ChronoField.DAY_OF_MONTH.checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue());
        if (resolverStyle == ResolverStyle.SMART) {
            if (moy == 4 || moy == 6 || moy == 9 || moy == 11) {
                dom = Math.min(dom, 30);
            } else if (moy == 2) {
                dom = Math.min(dom, Month.FEBRUARY.length(Year.isLeap((long) y)));
            }
        }
        return LocalDate.of(y, moy, dom);
    }

    public ValueRange range(ChronoField field) {
        return field.range();
    }

    public Period period(int years, int months, int days) {
        return Period.of(years, months, days);
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return super.writeReplace();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
