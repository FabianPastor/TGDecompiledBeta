package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.DayOfWeek;
import j$.time.Instant;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.ZoneId;
import j$.time.chrono.Chronology;
import j$.time.format.ResolverStyle;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjusters;
import j$.time.temporal.TemporalField;
import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public abstract class AbstractChronology implements Chronology {
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_ID = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Chronology> CHRONOS_BY_TYPE = new ConcurrentHashMap<>();
    static final Comparator<ChronoLocalDate> DATE_ORDER = AbstractChronology$$ExternalSyntheticLambda0.INSTANCE;
    static final Comparator<ChronoLocalDateTime<? extends ChronoLocalDate>> DATE_TIME_ORDER = AbstractChronology$$ExternalSyntheticLambda1.INSTANCE;
    static final Comparator<ChronoZonedDateTime<?>> INSTANT_ORDER = AbstractChronology$$ExternalSyntheticLambda2.INSTANCE;
    private static final Locale JAPANESE_CALENDAR_LOCALE = new Locale("ja", "JP", "JP");

    public /* synthetic */ ChronoLocalDate date(Era era, int i, int i2, int i3) {
        return Chronology.CC.$default$date(this, era, i, i2, i3);
    }

    public /* synthetic */ ChronoLocalDate dateNow() {
        return Chronology.CC.$default$dateNow(this);
    }

    public /* synthetic */ ChronoLocalDate dateNow(Clock clock) {
        return Chronology.CC.$default$dateNow((Chronology) this, clock);
    }

    public /* synthetic */ ChronoLocalDate dateNow(ZoneId zoneId) {
        return Chronology.CC.$default$dateNow((Chronology) this, zoneId);
    }

    public /* synthetic */ ChronoLocalDate dateYearDay(Era era, int i, int i2) {
        return Chronology.CC.$default$dateYearDay(this, era, i, i2);
    }

    public /* synthetic */ String getDisplayName(TextStyle textStyle, Locale locale) {
        return Chronology.CC.$default$getDisplayName(this, textStyle, locale);
    }

    public /* synthetic */ ChronoLocalDateTime localDateTime(TemporalAccessor temporalAccessor) {
        return Chronology.CC.$default$localDateTime(this, temporalAccessor);
    }

    public /* synthetic */ ChronoPeriod period(int i, int i2, int i3) {
        return Chronology.CC.$default$period(this, i, i2, i3);
    }

    public /* synthetic */ ChronoZonedDateTime zonedDateTime(Instant instant, ZoneId zoneId) {
        return Chronology.CC.$default$zonedDateTime(this, instant, zoneId);
    }

    public /* synthetic */ ChronoZonedDateTime zonedDateTime(TemporalAccessor temporalAccessor) {
        return Chronology.CC.$default$zonedDateTime(this, temporalAccessor);
    }

    static /* synthetic */ int lambda$static$7f2d2d5b$1(ChronoLocalDate date1, ChronoLocalDate date2) {
        return (date1.toEpochDay() > date2.toEpochDay() ? 1 : (date1.toEpochDay() == date2.toEpochDay() ? 0 : -1));
    }

    static /* synthetic */ int lambda$static$b5a61975$1(ChronoLocalDateTime dateTime1, ChronoLocalDateTime dateTime2) {
        int cmp = (dateTime1.toLocalDate().toEpochDay() > dateTime2.toLocalDate().toEpochDay() ? 1 : (dateTime1.toLocalDate().toEpochDay() == dateTime2.toLocalDate().toEpochDay() ? 0 : -1));
        if (cmp == 0) {
            return (dateTime1.toLocalTime().toNanoOfDay() > dateTime2.toLocalTime().toNanoOfDay() ? 1 : (dateTime1.toLocalTime().toNanoOfDay() == dateTime2.toLocalTime().toNanoOfDay() ? 0 : -1));
        }
        return cmp;
    }

    static /* synthetic */ int lambda$static$2241CLASSNAME$1(ChronoZonedDateTime dateTime1, ChronoZonedDateTime dateTime2) {
        int cmp = (dateTime1.toEpochSecond() > dateTime2.toEpochSecond() ? 1 : (dateTime1.toEpochSecond() == dateTime2.toEpochSecond() ? 0 : -1));
        if (cmp == 0) {
            return (((long) dateTime1.toLocalTime().getNano()) > ((long) dateTime2.toLocalTime().getNano()) ? 1 : (((long) dateTime1.toLocalTime().getNano()) == ((long) dateTime2.toLocalTime().getNano()) ? 0 : -1));
        }
        return cmp;
    }

    static Chronology registerChrono(Chronology chrono) {
        return registerChrono(chrono, chrono.getId());
    }

    static Chronology registerChrono(Chronology chrono, String id) {
        String type;
        Chronology prev = CHRONOS_BY_ID.putIfAbsent(id, chrono);
        if (prev == null && (type = chrono.getCalendarType()) != null) {
            CHRONOS_BY_TYPE.putIfAbsent(type, chrono);
        }
        return prev;
    }

    private static boolean initCache() {
        if (CHRONOS_BY_ID.get("ISO") != null) {
            return false;
        }
        registerChrono(HijrahChronology.INSTANCE);
        registerChrono(JapaneseChronology.INSTANCE);
        registerChrono(MinguoChronology.INSTANCE);
        registerChrono(ThaiBuddhistChronology.INSTANCE);
        Iterator<java.time.chrono.AbstractChronology> it = ServiceLoader.load(AbstractChronology.class, (ClassLoader) null).iterator();
        while (it.hasNext()) {
            AbstractChronology chrono = (AbstractChronology) it.next();
            if (!chrono.getId().equals("ISO")) {
                registerChrono(chrono);
            }
        }
        registerChrono(IsoChronology.INSTANCE);
        return true;
    }

    static Chronology ofLocale(Locale locale) {
        Objects.requireNonNull(locale, "locale");
        String type = getCalendarType(locale);
        if (type == null || "iso".equals(type) || "iso8601".equals(type)) {
            return IsoChronology.INSTANCE;
        }
        do {
            Chronology chrono = CHRONOS_BY_TYPE.get(type);
            if (chrono != null) {
                return chrono;
            }
        } while (initCache());
        Iterator<java.time.chrono.Chronology> it = ServiceLoader.load(Chronology.class).iterator();
        while (it.hasNext()) {
            Chronology chrono2 = (Chronology) it.next();
            if (type.equals(chrono2.getCalendarType())) {
                return chrono2;
            }
        }
        throw new DateTimeException("Unknown calendar system: " + type);
    }

    private static String getCalendarType(Locale locale) {
        String type = locale.getUnicodeLocaleType("ca");
        if (type != null) {
            return type;
        }
        if (locale.equals(JAPANESE_CALENDAR_LOCALE)) {
            return "japanese";
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0022  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static j$.time.chrono.Chronology of(java.lang.String r4) {
        /*
            java.lang.String r0 = "id"
            j$.util.Objects.requireNonNull(r4, (java.lang.String) r0)
        L_0x0005:
            j$.time.chrono.Chronology r0 = of0(r4)
            if (r0 == 0) goto L_0x000c
            return r0
        L_0x000c:
            boolean r0 = initCache()
            if (r0 != 0) goto L_0x0056
            java.lang.Class<j$.time.chrono.Chronology> r0 = j$.time.chrono.Chronology.class
            java.util.ServiceLoader r0 = java.util.ServiceLoader.load(r0)
            java.util.Iterator r1 = r0.iterator()
        L_0x001c:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x003f
            java.lang.Object r2 = r1.next()
            j$.time.chrono.Chronology r2 = (j$.time.chrono.Chronology) r2
            java.lang.String r3 = r2.getId()
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x003e
            java.lang.String r3 = r2.getCalendarType()
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x003d
            goto L_0x003e
        L_0x003d:
            goto L_0x001c
        L_0x003e:
            return r2
        L_0x003f:
            j$.time.DateTimeException r1 = new j$.time.DateTimeException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown chronology: "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x0056:
            goto L_0x0005
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.chrono.AbstractChronology.of(java.lang.String):j$.time.chrono.Chronology");
    }

    private static Chronology of0(String id) {
        Chronology chrono = CHRONOS_BY_ID.get(id);
        if (chrono == null) {
            return CHRONOS_BY_TYPE.get(id);
        }
        return chrono;
    }

    static Set<Chronology> getAvailableChronologies() {
        initCache();
        HashSet<java.time.chrono.Chronology> chronos = new HashSet<>(CHRONOS_BY_ID.values());
        Iterator<java.time.chrono.Chronology> it = ServiceLoader.load(Chronology.class).iterator();
        while (it.hasNext()) {
            chronos.add((Chronology) it.next());
        }
        return chronos;
    }

    protected AbstractChronology() {
    }

    public ChronoLocalDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        if (fieldValues.containsKey(ChronoField.EPOCH_DAY)) {
            return dateEpochDay(fieldValues.remove(ChronoField.EPOCH_DAY).longValue());
        }
        resolveProlepticMonth(fieldValues, resolverStyle);
        ChronoLocalDate resolved = resolveYearOfEra(fieldValues, resolverStyle);
        if (resolved != null) {
            return resolved;
        }
        if (!fieldValues.containsKey(ChronoField.YEAR)) {
            return null;
        }
        if (fieldValues.containsKey(ChronoField.MONTH_OF_YEAR)) {
            if (fieldValues.containsKey(ChronoField.DAY_OF_MONTH)) {
                return resolveYMD(fieldValues, resolverStyle);
            }
            if (fieldValues.containsKey(ChronoField.ALIGNED_WEEK_OF_MONTH)) {
                if (fieldValues.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)) {
                    return resolveYMAA(fieldValues, resolverStyle);
                }
                if (fieldValues.containsKey(ChronoField.DAY_OF_WEEK)) {
                    return resolveYMAD(fieldValues, resolverStyle);
                }
            }
        }
        if (fieldValues.containsKey(ChronoField.DAY_OF_YEAR)) {
            return resolveYD(fieldValues, resolverStyle);
        }
        if (!fieldValues.containsKey(ChronoField.ALIGNED_WEEK_OF_YEAR)) {
            return null;
        }
        if (fieldValues.containsKey(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR)) {
            return resolveYAA(fieldValues, resolverStyle);
        }
        if (fieldValues.containsKey(ChronoField.DAY_OF_WEEK)) {
            return resolveYAD(fieldValues, resolverStyle);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void resolveProlepticMonth(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Long pMonth = fieldValues.remove(ChronoField.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.PROLEPTIC_MONTH.checkValidValue(pMonth.longValue());
            }
            ChronoLocalDate chronoDate = dateNow().with(ChronoField.DAY_OF_MONTH, 1).with(ChronoField.PROLEPTIC_MONTH, pMonth.longValue());
            addFieldValue(fieldValues, ChronoField.MONTH_OF_YEAR, (long) chronoDate.get(ChronoField.MONTH_OF_YEAR));
            addFieldValue(fieldValues, ChronoField.YEAR, (long) chronoDate.get(ChronoField.YEAR));
        }
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYearOfEra(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int yoe;
        Long yoeLong = fieldValues.remove(ChronoField.YEAR_OF_ERA);
        if (yoeLong != null) {
            Long eraLong = fieldValues.remove(ChronoField.ERA);
            if (resolverStyle != ResolverStyle.LENIENT) {
                yoe = range(ChronoField.YEAR_OF_ERA).checkValidIntValue(yoeLong.longValue(), ChronoField.YEAR_OF_ERA);
            } else {
                yoe = LocalDate$$ExternalSyntheticBackport0.m(yoeLong.longValue());
            }
            if (eraLong != null) {
                addFieldValue(fieldValues, ChronoField.YEAR, (long) prolepticYear(eraOf(range(ChronoField.ERA).checkValidIntValue(eraLong.longValue(), ChronoField.ERA)), yoe));
                return null;
            } else if (fieldValues.containsKey(ChronoField.YEAR)) {
                addFieldValue(fieldValues, ChronoField.YEAR, (long) prolepticYear(dateYearDay(range(ChronoField.YEAR).checkValidIntValue(fieldValues.get(ChronoField.YEAR).longValue(), ChronoField.YEAR), 1).getEra(), yoe));
                return null;
            } else if (resolverStyle == ResolverStyle.STRICT) {
                fieldValues.put(ChronoField.YEAR_OF_ERA, yoeLong);
                return null;
            } else {
                List<Era> eras = eras();
                if (eras.isEmpty()) {
                    addFieldValue(fieldValues, ChronoField.YEAR, (long) yoe);
                    return null;
                }
                addFieldValue(fieldValues, ChronoField.YEAR, (long) prolepticYear(eras.get(eras.size() - 1), yoe));
                return null;
            }
        } else if (!fieldValues.containsKey(ChronoField.ERA)) {
            return null;
        } else {
            range(ChronoField.ERA).checkValidValue(fieldValues.get(ChronoField.ERA).longValue(), ChronoField.ERA);
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYMD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1);
            return date(y, 1, 1).plus(months, ChronoUnit.MONTHS).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue(), 1), ChronoUnit.DAYS);
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        int dom = range(ChronoField.DAY_OF_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_MONTH).longValue(), ChronoField.DAY_OF_MONTH);
        if (resolverStyle != ResolverStyle.SMART) {
            return date(y, moy, dom);
        }
        try {
            return date(y, moy, dom);
        } catch (DateTimeException e) {
            return date(y, moy, 1).with(TemporalAdjusters.lastDayOfMonth());
        }
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle != ResolverStyle.LENIENT) {
            return dateYearDay(y, range(ChronoField.DAY_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.DAY_OF_YEAR).longValue(), ChronoField.DAY_OF_YEAR));
        }
        return dateYearDay(y, 1).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.DAY_OF_YEAR).longValue(), 1), ChronoUnit.DAYS);
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYMAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1);
            long weeks = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), 1);
            return date(y, 1, 1).plus(months, ChronoUnit.MONTHS).plus(weeks, ChronoUnit.WEEKS).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).longValue(), 1), ChronoUnit.DAYS);
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        ChronoLocalDate date = date(y, moy, 1).plus((long) (((range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH) - 1) * 7) + (range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) - 1)), ChronoUnit.DAYS);
        if (resolverStyle != ResolverStyle.STRICT || date.get(ChronoField.MONTH_OF_YEAR) == moy) {
            return date;
        }
        throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYMAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Map<TemporalField, Long> map = fieldValues;
        ResolverStyle resolverStyle2 = resolverStyle;
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle2 == ResolverStyle.LENIENT) {
            return resolveAligned(date(y, 1, 1), Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1), Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), 1), Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_WEEK).longValue(), 1));
        }
        int moy = range(ChronoField.MONTH_OF_YEAR).checkValidIntValue(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), ChronoField.MONTH_OF_YEAR);
        ChronoLocalDate date = date(y, moy, 1).plus((long) ((range(ChronoField.ALIGNED_WEEK_OF_MONTH).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_MONTH).longValue(), ChronoField.ALIGNED_WEEK_OF_MONTH) - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(range(ChronoField.DAY_OF_WEEK).checkValidIntValue(map.remove(ChronoField.DAY_OF_WEEK).longValue(), ChronoField.DAY_OF_WEEK))));
        if (resolverStyle2 != ResolverStyle.STRICT || date.get(ChronoField.MONTH_OF_YEAR) == moy) {
            return date;
        }
        throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYAA(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        int y = range(ChronoField.YEAR).checkValidIntValue(fieldValues.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle == ResolverStyle.LENIENT) {
            long weeks = Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), 1);
            return dateYearDay(y, 1).plus(weeks, ChronoUnit.WEEKS).plus(Instant$$ExternalSyntheticBackport0.m(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).longValue(), 1), ChronoUnit.DAYS);
        }
        ChronoLocalDate date = dateYearDay(y, 1).plus((long) (((range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR) - 1) * 7) + (range(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).checkValidIntValue(fieldValues.remove(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR).longValue(), ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) - 1)), ChronoUnit.DAYS);
        if (resolverStyle != ResolverStyle.STRICT || date.get(ChronoField.YEAR) == y) {
            return date;
        }
        throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveYAD(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        Map<TemporalField, Long> map = fieldValues;
        ResolverStyle resolverStyle2 = resolverStyle;
        int y = range(ChronoField.YEAR).checkValidIntValue(map.remove(ChronoField.YEAR).longValue(), ChronoField.YEAR);
        if (resolverStyle2 == ResolverStyle.LENIENT) {
            return resolveAligned(dateYearDay(y, 1), 0, Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), 1), Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_WEEK).longValue(), 1));
        }
        ChronoLocalDate date = dateYearDay(y, 1).plus((long) ((range(ChronoField.ALIGNED_WEEK_OF_YEAR).checkValidIntValue(map.remove(ChronoField.ALIGNED_WEEK_OF_YEAR).longValue(), ChronoField.ALIGNED_WEEK_OF_YEAR) - 1) * 7), ChronoUnit.DAYS).with(TemporalAdjusters.nextOrSame(DayOfWeek.of(range(ChronoField.DAY_OF_WEEK).checkValidIntValue(map.remove(ChronoField.DAY_OF_WEEK).longValue(), ChronoField.DAY_OF_WEEK))));
        if (resolverStyle2 != ResolverStyle.STRICT || date.get(ChronoField.YEAR) == y) {
            return date;
        }
        throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
    }

    /* access modifiers changed from: package-private */
    public ChronoLocalDate resolveAligned(ChronoLocalDate base, long months, long weeks, long dow) {
        ChronoLocalDate date = base.plus(months, ChronoUnit.MONTHS).plus(weeks, ChronoUnit.WEEKS);
        if (dow > 7) {
            date = date.plus((dow - 1) / 7, ChronoUnit.WEEKS);
            dow = ((dow - 1) % 7) + 1;
        } else if (dow < 1) {
            date = date.plus(Instant$$ExternalSyntheticBackport0.m(dow, 7) / 7, ChronoUnit.WEEKS);
            dow = ((6 + dow) % 7) + 1;
        }
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.of((int) dow)));
    }

    /* access modifiers changed from: package-private */
    public void addFieldValue(Map<TemporalField, Long> fieldValues, ChronoField field, long value) {
        Long old = fieldValues.get(field);
        if (old == null || old.longValue() == value) {
            fieldValues.put(field, Long.valueOf(value));
            return;
        }
        throw new DateTimeException("Conflict found: " + field + " " + old + " differs from " + field + " " + value);
    }

    public int compareTo(Chronology other) {
        return getId().compareTo(other.getId());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractChronology) || compareTo((Chronology) (AbstractChronology) obj) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getClass().hashCode() ^ getId().hashCode();
    }

    public String toString() {
        return getId();
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return new Ser((byte) 1, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        out.writeUTF(getId());
    }

    static Chronology readExternal(DataInput in) {
        return Chronology.CC.of(in.readUTF());
    }
}
