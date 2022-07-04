package j$.time.temporal;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.DayOfWeek;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.format.ResolverStyle;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public final class WeekFields implements Serializable {
    private static final ConcurrentMap<String, WeekFields> CACHE = new ConcurrentHashMap(4, 0.75f, 2);
    public static final WeekFields ISO = new WeekFields(DayOfWeek.MONDAY, 4);
    public static final WeekFields SUNDAY_START = of(DayOfWeek.SUNDAY, 1);
    public static final TemporalUnit WEEK_BASED_YEARS = IsoFields.WEEK_BASED_YEARS;
    private static final long serialVersionUID = -1177360819670808121L;
    /* access modifiers changed from: private */
    public final transient TemporalField dayOfWeek = ComputedDayOfField.ofDayOfWeekField(this);
    private final DayOfWeek firstDayOfWeek;
    private final int minimalDays;
    /* access modifiers changed from: private */
    public final transient TemporalField weekBasedYear = ComputedDayOfField.ofWeekBasedYearField(this);
    private final transient TemporalField weekOfMonth = ComputedDayOfField.ofWeekOfMonthField(this);
    /* access modifiers changed from: private */
    public final transient TemporalField weekOfWeekBasedYear = ComputedDayOfField.ofWeekOfWeekBasedYearField(this);
    private final transient TemporalField weekOfYear = ComputedDayOfField.ofWeekOfYearField(this);

    public static WeekFields of(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        Calendar cal = Calendar.getInstance(new Locale(locale.getLanguage(), locale.getCountry()));
        return of(DayOfWeek.SUNDAY.plus((long) (cal.getFirstDayOfWeek() - 1)), cal.getMinimalDaysInFirstWeek());
    }

    public static WeekFields of(DayOfWeek firstDayOfWeek2, int minimalDaysInFirstWeek) {
        String key = firstDayOfWeek2.toString() + minimalDaysInFirstWeek;
        ConcurrentMap<String, WeekFields> concurrentMap = CACHE;
        WeekFields rules = (WeekFields) concurrentMap.get(key);
        if (rules != null) {
            return rules;
        }
        concurrentMap.putIfAbsent(key, new WeekFields(firstDayOfWeek2, minimalDaysInFirstWeek));
        return (WeekFields) concurrentMap.get(key);
    }

    private WeekFields(DayOfWeek firstDayOfWeek2, int minimalDaysInFirstWeek) {
        Objects.requireNonNull(firstDayOfWeek2, "firstDayOfWeek");
        if (minimalDaysInFirstWeek < 1 || minimalDaysInFirstWeek > 7) {
            throw new IllegalArgumentException("Minimal number of days is invalid");
        }
        this.firstDayOfWeek = firstDayOfWeek2;
        this.minimalDays = minimalDaysInFirstWeek;
    }

    private void readObject(ObjectInputStream s) {
        s.defaultReadObject();
        if (this.firstDayOfWeek != null) {
            int i = this.minimalDays;
            if (i < 1 || i > 7) {
                throw new InvalidObjectException("Minimal number of days is invalid");
            }
            return;
        }
        throw new InvalidObjectException("firstDayOfWeek is null");
    }

    private Object readResolve() {
        try {
            return of(this.firstDayOfWeek, this.minimalDays);
        } catch (IllegalArgumentException iae) {
            throw new InvalidObjectException("Invalid serialized WeekFields: " + iae.getMessage());
        }
    }

    public DayOfWeek getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    public int getMinimalDaysInFirstWeek() {
        return this.minimalDays;
    }

    public TemporalField dayOfWeek() {
        return this.dayOfWeek;
    }

    public TemporalField weekOfMonth() {
        return this.weekOfMonth;
    }

    public TemporalField weekOfYear() {
        return this.weekOfYear;
    }

    public TemporalField weekOfWeekBasedYear() {
        return this.weekOfWeekBasedYear;
    }

    public TemporalField weekBasedYear() {
        return this.weekBasedYear;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WeekFields) || hashCode() != object.hashCode()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.firstDayOfWeek.ordinal() * 7) + this.minimalDays;
    }

    public String toString() {
        return "WeekFields[" + this.firstDayOfWeek + ',' + this.minimalDays + ']';
    }

    static class ComputedDayOfField implements TemporalField {
        private static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(1, 7);
        private static final ValueRange WEEK_OF_MONTH_RANGE = ValueRange.of(0, 1, 4, 6);
        private static final ValueRange WEEK_OF_WEEK_BASED_YEAR_RANGE = ValueRange.of(1, 52, 53);
        private static final ValueRange WEEK_OF_YEAR_RANGE = ValueRange.of(0, 1, 52, 54);
        private final TemporalUnit baseUnit;
        private final String name;
        private final ValueRange range;
        private final TemporalUnit rangeUnit;
        private final WeekFields weekDef;

        static ComputedDayOfField ofDayOfWeekField(WeekFields weekDef2) {
            return new ComputedDayOfField("DayOfWeek", weekDef2, ChronoUnit.DAYS, ChronoUnit.WEEKS, DAY_OF_WEEK_RANGE);
        }

        static ComputedDayOfField ofWeekOfMonthField(WeekFields weekDef2) {
            return new ComputedDayOfField("WeekOfMonth", weekDef2, ChronoUnit.WEEKS, ChronoUnit.MONTHS, WEEK_OF_MONTH_RANGE);
        }

        static ComputedDayOfField ofWeekOfYearField(WeekFields weekDef2) {
            return new ComputedDayOfField("WeekOfYear", weekDef2, ChronoUnit.WEEKS, ChronoUnit.YEARS, WEEK_OF_YEAR_RANGE);
        }

        static ComputedDayOfField ofWeekOfWeekBasedYearField(WeekFields weekDef2) {
            return new ComputedDayOfField("WeekOfWeekBasedYear", weekDef2, ChronoUnit.WEEKS, IsoFields.WEEK_BASED_YEARS, WEEK_OF_WEEK_BASED_YEAR_RANGE);
        }

        static ComputedDayOfField ofWeekBasedYearField(WeekFields weekDef2) {
            return new ComputedDayOfField("WeekBasedYear", weekDef2, IsoFields.WEEK_BASED_YEARS, ChronoUnit.FOREVER, ChronoField.YEAR.range());
        }

        private ChronoLocalDate ofWeekBasedYear(Chronology chrono, int yowby, int wowby, int dow) {
            ChronoLocalDate date = chrono.date(yowby, 1, 1);
            int offset = startOfWeekOffset(1, localizedDayOfWeek((TemporalAccessor) date));
            return date.plus((long) ((-offset) + (dow - 1) + ((Math.min(wowby, computeWeek(offset, this.weekDef.getMinimalDaysInFirstWeek() + date.lengthOfYear()) - 1) - 1) * 7)), ChronoUnit.DAYS);
        }

        private ComputedDayOfField(String name2, WeekFields weekDef2, TemporalUnit baseUnit2, TemporalUnit rangeUnit2, ValueRange range2) {
            this.name = name2;
            this.weekDef = weekDef2;
            this.baseUnit = baseUnit2;
            this.rangeUnit = rangeUnit2;
            this.range = range2;
        }

        public long getFrom(TemporalAccessor temporal) {
            if (this.rangeUnit == ChronoUnit.WEEKS) {
                return (long) localizedDayOfWeek(temporal);
            }
            if (this.rangeUnit == ChronoUnit.MONTHS) {
                return localizedWeekOfMonth(temporal);
            }
            if (this.rangeUnit == ChronoUnit.YEARS) {
                return localizedWeekOfYear(temporal);
            }
            if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
                return (long) localizedWeekOfWeekBasedYear(temporal);
            }
            if (this.rangeUnit == ChronoUnit.FOREVER) {
                return (long) localizedWeekBasedYear(temporal);
            }
            throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
        }

        private int localizedDayOfWeek(TemporalAccessor temporal) {
            return WeekFields$ComputedDayOfField$$ExternalSyntheticBackport0.m(temporal.get(ChronoField.DAY_OF_WEEK) - this.weekDef.getFirstDayOfWeek().getValue(), 7) + 1;
        }

        private int localizedDayOfWeek(int isoDow) {
            return WeekFields$ComputedDayOfField$$ExternalSyntheticBackport0.m(isoDow - this.weekDef.getFirstDayOfWeek().getValue(), 7) + 1;
        }

        private long localizedWeekOfMonth(TemporalAccessor temporal) {
            int dow = localizedDayOfWeek(temporal);
            int dom = temporal.get(ChronoField.DAY_OF_MONTH);
            return (long) computeWeek(startOfWeekOffset(dom, dow), dom);
        }

        private long localizedWeekOfYear(TemporalAccessor temporal) {
            int dow = localizedDayOfWeek(temporal);
            int doy = temporal.get(ChronoField.DAY_OF_YEAR);
            return (long) computeWeek(startOfWeekOffset(doy, dow), doy);
        }

        private int localizedWeekBasedYear(TemporalAccessor temporal) {
            int dow = localizedDayOfWeek(temporal);
            int year = temporal.get(ChronoField.YEAR);
            int doy = temporal.get(ChronoField.DAY_OF_YEAR);
            int offset = startOfWeekOffset(doy, dow);
            int week = computeWeek(offset, doy);
            if (week == 0) {
                return year - 1;
            }
            if (week >= computeWeek(offset, this.weekDef.getMinimalDaysInFirstWeek() + ((int) temporal.range(ChronoField.DAY_OF_YEAR).getMaximum()))) {
                return year + 1;
            }
            return year;
        }

        private int localizedWeekOfWeekBasedYear(TemporalAccessor temporal) {
            int dow = localizedDayOfWeek(temporal);
            int doy = temporal.get(ChronoField.DAY_OF_YEAR);
            int offset = startOfWeekOffset(doy, dow);
            int week = computeWeek(offset, doy);
            if (week == 0) {
                return localizedWeekOfWeekBasedYear(Chronology.CC.from(temporal).date(temporal).minus((long) doy, ChronoUnit.DAYS));
            }
            if (week <= 50) {
                return week;
            }
            int newYearWeek = computeWeek(offset, this.weekDef.getMinimalDaysInFirstWeek() + ((int) temporal.range(ChronoField.DAY_OF_YEAR).getMaximum()));
            if (week >= newYearWeek) {
                return (week - newYearWeek) + 1;
            }
            return week;
        }

        private int startOfWeekOffset(int day, int dow) {
            int weekStart = WeekFields$ComputedDayOfField$$ExternalSyntheticBackport0.m(day - dow, 7);
            int offset = -weekStart;
            if (weekStart + 1 > this.weekDef.getMinimalDaysInFirstWeek()) {
                return 7 - weekStart;
            }
            return offset;
        }

        private int computeWeek(int offset, int day) {
            return ((offset + 7) + (day - 1)) / 7;
        }

        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            int newVal = this.range.checkValidIntValue(newValue, this);
            int currentVal = temporal.get(this);
            if (newVal == currentVal) {
                return temporal;
            }
            if (this.rangeUnit != ChronoUnit.FOREVER) {
                return temporal.plus((long) (newVal - currentVal), this.baseUnit);
            }
            int idow = temporal.get(this.weekDef.dayOfWeek);
            return ofWeekBasedYear(Chronology.CC.from(temporal), (int) newValue, temporal.get(this.weekDef.weekOfWeekBasedYear), idow);
        }

        public ChronoLocalDate resolve(Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            int dow;
            Chronology chrono;
            Map<TemporalField, Long> map = fieldValues;
            long value = map.get(this).longValue();
            int newValue = LocalDate$$ExternalSyntheticBackport0.m(value);
            if (this.rangeUnit == ChronoUnit.WEEKS) {
                int checkedValue = this.range.checkValidIntValue(value, this);
                map.remove(this);
                map.put(ChronoField.DAY_OF_WEEK, Long.valueOf((long) (WeekFields$ComputedDayOfField$$ExternalSyntheticBackport0.m((this.weekDef.getFirstDayOfWeek().getValue() - 1) + (checkedValue - 1), 7) + 1)));
                return null;
            } else if (!map.containsKey(ChronoField.DAY_OF_WEEK)) {
                return null;
            } else {
                int dow2 = localizedDayOfWeek(ChronoField.DAY_OF_WEEK.checkValidIntValue(map.get(ChronoField.DAY_OF_WEEK).longValue()));
                Chronology chrono2 = Chronology.CC.from(partialTemporal);
                if (map.containsKey(ChronoField.YEAR)) {
                    int year = ChronoField.YEAR.checkValidIntValue(map.get(ChronoField.YEAR).longValue());
                    if (this.rangeUnit != ChronoUnit.MONTHS || !map.containsKey(ChronoField.MONTH_OF_YEAR)) {
                        chrono = chrono2;
                        dow = dow2;
                        if (this.rangeUnit == ChronoUnit.YEARS) {
                            return resolveWoY(fieldValues, chrono, year, (long) newValue, dow, resolverStyle);
                        }
                    } else {
                        Chronology chronology = chrono2;
                        int i = dow2;
                        return resolveWoM(fieldValues, chrono2, year, map.get(ChronoField.MONTH_OF_YEAR).longValue(), (long) newValue, dow2, resolverStyle);
                    }
                } else {
                    chrono = chrono2;
                    dow = dow2;
                    if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS || this.rangeUnit == ChronoUnit.FOREVER) {
                        if (!map.containsKey(this.weekDef.weekBasedYear)) {
                            ResolverStyle resolverStyle2 = resolverStyle;
                            Chronology chronology2 = chrono;
                            int i2 = dow;
                        } else if (map.containsKey(this.weekDef.weekOfWeekBasedYear)) {
                            return resolveWBY(map, chrono, dow, resolverStyle);
                        } else {
                            ResolverStyle resolverStyle3 = resolverStyle;
                            Chronology chronology3 = chrono;
                            int i3 = dow;
                        }
                        return null;
                    }
                }
                ResolverStyle resolverStyle4 = resolverStyle;
                Chronology chronology4 = chrono;
                int i4 = dow;
                return null;
            }
        }

        private ChronoLocalDate resolveWoM(Map<TemporalField, Long> fieldValues, Chronology chrono, int year, long month, long wom, int localDow, ResolverStyle resolverStyle) {
            ChronoLocalDate date;
            Map<TemporalField, Long> map = fieldValues;
            Chronology chronology = chrono;
            int i = year;
            long j = month;
            long j2 = wom;
            ResolverStyle resolverStyle2 = resolverStyle;
            if (resolverStyle2 == ResolverStyle.LENIENT) {
                ChronoLocalDate date2 = chronology.date(i, 1, 1).plus(Instant$$ExternalSyntheticBackport0.m(j, 1), ChronoUnit.MONTHS);
                long weeks = Instant$$ExternalSyntheticBackport0.m(j2, localizedWeekOfMonth(date2));
                long j3 = weeks;
                date = date2.plus(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(weeks, 7), (long) (localDow - localizedDayOfWeek((TemporalAccessor) date2))), ChronoUnit.DAYS);
            } else {
                ChronoLocalDate date3 = chronology.date(i, ChronoField.MONTH_OF_YEAR.checkValidIntValue(j), 1);
                ChronoLocalDate date4 = date3.plus((long) ((((int) (((long) this.range.checkValidIntValue(j2, this)) - localizedWeekOfMonth(date3))) * 7) + (localDow - localizedDayOfWeek((TemporalAccessor) date3))), ChronoUnit.DAYS);
                if (resolverStyle2 != ResolverStyle.STRICT || date4.getLong(ChronoField.MONTH_OF_YEAR) == j) {
                    date = date4;
                } else {
                    throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
                }
            }
            map.remove(this);
            map.remove(ChronoField.YEAR);
            map.remove(ChronoField.MONTH_OF_YEAR);
            map.remove(ChronoField.DAY_OF_WEEK);
            return date;
        }

        private ChronoLocalDate resolveWoY(Map<TemporalField, Long> fieldValues, Chronology chrono, int year, long woy, int localDow, ResolverStyle resolverStyle) {
            ChronoLocalDate date;
            Map<TemporalField, Long> map = fieldValues;
            int i = year;
            long j = woy;
            ResolverStyle resolverStyle2 = resolverStyle;
            ChronoLocalDate date2 = chrono.date(i, 1, 1);
            if (resolverStyle2 == ResolverStyle.LENIENT) {
                date = date2.plus(Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(Instant$$ExternalSyntheticBackport0.m(j, localizedWeekOfYear(date2)), 7), (long) (localDow - localizedDayOfWeek((TemporalAccessor) date2))), ChronoUnit.DAYS);
            } else {
                date = date2.plus((long) ((((int) (((long) this.range.checkValidIntValue(j, this)) - localizedWeekOfYear(date2))) * 7) + (localDow - localizedDayOfWeek((TemporalAccessor) date2))), ChronoUnit.DAYS);
                if (resolverStyle2 == ResolverStyle.STRICT && date.getLong(ChronoField.YEAR) != ((long) i)) {
                    throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
                }
            }
            map.remove(this);
            map.remove(ChronoField.YEAR);
            map.remove(ChronoField.DAY_OF_WEEK);
            return date;
        }

        private ChronoLocalDate resolveWBY(Map<TemporalField, Long> fieldValues, Chronology chrono, int localDow, ResolverStyle resolverStyle) {
            ChronoLocalDate date;
            int yowby = this.weekDef.weekBasedYear.range().checkValidIntValue(fieldValues.get(this.weekDef.weekBasedYear).longValue(), this.weekDef.weekBasedYear);
            if (resolverStyle == ResolverStyle.LENIENT) {
                date = ofWeekBasedYear(chrono, yowby, 1, localDow).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.get(this.weekDef.weekOfWeekBasedYear).longValue(), 1), ChronoUnit.WEEKS);
            } else {
                ChronoLocalDate date2 = ofWeekBasedYear(chrono, yowby, this.weekDef.weekOfWeekBasedYear.range().checkValidIntValue(fieldValues.get(this.weekDef.weekOfWeekBasedYear).longValue(), this.weekDef.weekOfWeekBasedYear), localDow);
                if (resolverStyle != ResolverStyle.STRICT || localizedWeekBasedYear(date2) == yowby) {
                    date = date2;
                } else {
                    throw new DateTimeException("Strict mode rejected resolved date as it is in a different week-based-year");
                }
            }
            fieldValues.remove(this);
            fieldValues.remove(this.weekDef.weekBasedYear);
            fieldValues.remove(this.weekDef.weekOfWeekBasedYear);
            fieldValues.remove(ChronoField.DAY_OF_WEEK);
            return date;
        }

        public String getDisplayName(Locale locale) {
            Objects.requireNonNull(locale, "locale");
            if (this.rangeUnit == ChronoUnit.YEARS) {
                return "Week";
            }
            return this.name;
        }

        public TemporalUnit getBaseUnit() {
            return this.baseUnit;
        }

        public TemporalUnit getRangeUnit() {
            return this.rangeUnit;
        }

        public boolean isDateBased() {
            return true;
        }

        public boolean isTimeBased() {
            return false;
        }

        public ValueRange range() {
            return this.range;
        }

        public boolean isSupportedBy(TemporalAccessor temporal) {
            if (!temporal.isSupported(ChronoField.DAY_OF_WEEK)) {
                return false;
            }
            if (this.rangeUnit == ChronoUnit.WEEKS) {
                return true;
            }
            if (this.rangeUnit == ChronoUnit.MONTHS) {
                return temporal.isSupported(ChronoField.DAY_OF_MONTH);
            }
            if (this.rangeUnit == ChronoUnit.YEARS) {
                return temporal.isSupported(ChronoField.DAY_OF_YEAR);
            }
            if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
                return temporal.isSupported(ChronoField.DAY_OF_YEAR);
            }
            if (this.rangeUnit == ChronoUnit.FOREVER) {
                return temporal.isSupported(ChronoField.YEAR);
            }
            return false;
        }

        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            if (this.rangeUnit == ChronoUnit.WEEKS) {
                return this.range;
            }
            if (this.rangeUnit == ChronoUnit.MONTHS) {
                return rangeByWeek(temporal, ChronoField.DAY_OF_MONTH);
            }
            if (this.rangeUnit == ChronoUnit.YEARS) {
                return rangeByWeek(temporal, ChronoField.DAY_OF_YEAR);
            }
            if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
                return rangeWeekOfWeekBasedYear(temporal);
            }
            if (this.rangeUnit == ChronoUnit.FOREVER) {
                return ChronoField.YEAR.range();
            }
            throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
        }

        private ValueRange rangeByWeek(TemporalAccessor temporal, TemporalField field) {
            int offset = startOfWeekOffset(temporal.get(field), localizedDayOfWeek(temporal));
            ValueRange fieldRange = temporal.range(field);
            return ValueRange.of((long) computeWeek(offset, (int) fieldRange.getMinimum()), (long) computeWeek(offset, (int) fieldRange.getMaximum()));
        }

        private ValueRange rangeWeekOfWeekBasedYear(TemporalAccessor temporal) {
            if (!temporal.isSupported(ChronoField.DAY_OF_YEAR)) {
                return WEEK_OF_YEAR_RANGE;
            }
            int dow = localizedDayOfWeek(temporal);
            int doy = temporal.get(ChronoField.DAY_OF_YEAR);
            int offset = startOfWeekOffset(doy, dow);
            int week = computeWeek(offset, doy);
            if (week == 0) {
                return rangeWeekOfWeekBasedYear(Chronology.CC.from(temporal).date(temporal).minus((long) (doy + 7), ChronoUnit.DAYS));
            }
            int yearLen = (int) temporal.range(ChronoField.DAY_OF_YEAR).getMaximum();
            int newYearWeek = computeWeek(offset, this.weekDef.getMinimalDaysInFirstWeek() + yearLen);
            if (week >= newYearWeek) {
                return rangeWeekOfWeekBasedYear(Chronology.CC.from(temporal).date(temporal).plus((long) ((yearLen - doy) + 1 + 7), ChronoUnit.DAYS));
            }
            return ValueRange.of(1, (long) (newYearWeek - 1));
        }

        public String toString() {
            return this.name + "[" + this.weekDef.toString() + "]";
        }
    }
}
