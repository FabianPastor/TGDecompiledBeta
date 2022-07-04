package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.ResolverStyle;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface Chronology extends Comparable<Chronology> {
    int compareTo(Chronology chronology);

    ChronoLocalDate date(int i, int i2, int i3);

    ChronoLocalDate date(Era era, int i, int i2, int i3);

    ChronoLocalDate date(TemporalAccessor temporalAccessor);

    ChronoLocalDate dateEpochDay(long j);

    ChronoLocalDate dateNow();

    ChronoLocalDate dateNow(Clock clock);

    ChronoLocalDate dateNow(ZoneId zoneId);

    ChronoLocalDate dateYearDay(int i, int i2);

    ChronoLocalDate dateYearDay(Era era, int i, int i2);

    boolean equals(Object obj);

    Era eraOf(int i);

    List<Era> eras();

    String getCalendarType();

    String getDisplayName(TextStyle textStyle, Locale locale);

    String getId();

    int hashCode();

    boolean isLeapYear(long j);

    ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor temporalAccessor);

    ChronoPeriod period(int i, int i2, int i3);

    int prolepticYear(Era era, int i);

    ValueRange range(ChronoField chronoField);

    ChronoLocalDate resolveDate(Map<TemporalField, Long> map, ResolverStyle resolverStyle);

    String toString();

    ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant instant, ZoneId zoneId);

    ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor temporalAccessor);

    /* renamed from: j$.time.chrono.Chronology$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static Chronology from(TemporalAccessor temporal) {
            Objects.requireNonNull(temporal, "temporal");
            Chronology obj = (Chronology) temporal.query(TemporalQueries.chronology());
            return obj != null ? obj : IsoChronology.INSTANCE;
        }

        public static Chronology ofLocale(Locale locale) {
            return AbstractChronology.ofLocale(locale);
        }

        public static Chronology of(String id) {
            return AbstractChronology.of(id);
        }

        public static Set<Chronology> getAvailableChronologies() {
            return AbstractChronology.getAvailableChronologies();
        }

        public static ChronoLocalDate $default$date(Chronology _this, Era era, int yearOfEra, int month, int dayOfMonth) {
            return _this.date(_this.prolepticYear(era, yearOfEra), month, dayOfMonth);
        }

        public static ChronoLocalDate $default$dateYearDay(Chronology _this, Era era, int yearOfEra, int dayOfYear) {
            return _this.dateYearDay(_this.prolepticYear(era, yearOfEra), dayOfYear);
        }

        public static ChronoLocalDate $default$dateNow(Chronology _this) {
            return _this.dateNow(Clock.systemDefaultZone());
        }

        public static ChronoLocalDate $default$dateNow(Chronology _this, ZoneId zone) {
            return _this.dateNow(Clock.system(zone));
        }

        public static ChronoLocalDate $default$dateNow(Chronology _this, Clock clock) {
            Objects.requireNonNull(clock, "clock");
            return _this.date(LocalDate.now(clock));
        }

        public static ChronoLocalDateTime $default$localDateTime(Chronology _this, TemporalAccessor temporal) {
            try {
                return _this.date(temporal).atTime(LocalTime.from(temporal));
            } catch (DateTimeException ex) {
                throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + temporal.getClass(), ex);
            }
        }

        public static ChronoZonedDateTime $default$zonedDateTime(Chronology _this, TemporalAccessor temporal) {
            try {
                ZoneId zone = ZoneId.from(temporal);
                try {
                    return _this.zonedDateTime(Instant.from(temporal), zone);
                } catch (DateTimeException e) {
                    return ChronoZonedDateTimeImpl.ofBest(ChronoLocalDateTimeImpl.ensureValid(_this, _this.localDateTime(temporal)), zone, (ZoneOffset) null);
                }
            } catch (DateTimeException ex) {
                throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + temporal.getClass(), ex);
            }
        }

        public static ChronoZonedDateTime $default$zonedDateTime(Chronology _this, Instant instant, ZoneId zone) {
            return ChronoZonedDateTimeImpl.ofInstant(_this, instant, zone);
        }

        public static String $default$getDisplayName(Chronology _this, TextStyle style, Locale locale) {
            return new DateTimeFormatterBuilder().appendChronologyText(style).toFormatter(locale).format(new TemporalAccessor() {
                public /* synthetic */ int get(TemporalField temporalField) {
                    return TemporalAccessor.CC.$default$get(this, temporalField);
                }

                public /* synthetic */ ValueRange range(TemporalField temporalField) {
                    return TemporalAccessor.CC.$default$range(this, temporalField);
                }

                public boolean isSupported(TemporalField field) {
                    return false;
                }

                public long getLong(TemporalField field) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
                }

                public <R> R query(TemporalQuery<R> temporalQuery) {
                    if (temporalQuery == TemporalQueries.chronology()) {
                        return Chronology.this;
                    }
                    return TemporalAccessor.CC.$default$query(this, temporalQuery);
                }
            });
        }

        public static ChronoPeriod $default$period(Chronology _this, int years, int months, int days) {
            return new ChronoPeriodImpl(_this, years, months, days);
        }
    }
}
