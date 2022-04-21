package j$.time;

import j$.time.chrono.ChronoLocalDateTime;
import j$.time.chrono.IsoChronology;
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
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Comparator;

public final class OffsetDateTime implements Temporal, TemporalAdjuster, Comparable<OffsetDateTime>, Serializable {
    public static final OffsetDateTime MAX = LocalDateTime.MAX.atOffset(ZoneOffset.MIN);
    public static final OffsetDateTime MIN = LocalDateTime.MIN.atOffset(ZoneOffset.MAX);
    private static final long serialVersionUID = 2287754244819255394L;
    private final LocalDateTime dateTime;
    private final ZoneOffset offset;

    public static Comparator<OffsetDateTime> timeLineOrder() {
        return OffsetDateTime$$ExternalSyntheticLambda1.INSTANCE;
    }

    /* access modifiers changed from: private */
    public static int compareInstant(OffsetDateTime datetime1, OffsetDateTime datetime2) {
        if (datetime1.getOffset().equals(datetime2.getOffset())) {
            return datetime1.toLocalDateTime().compareTo((ChronoLocalDateTime<?>) datetime2.toLocalDateTime());
        }
        int cmp = (datetime1.toEpochSecond() > datetime2.toEpochSecond() ? 1 : (datetime1.toEpochSecond() == datetime2.toEpochSecond() ? 0 : -1));
        if (cmp == 0) {
            return datetime1.toLocalTime().getNano() - datetime2.toLocalTime().getNano();
        }
        return cmp;
    }

    public static OffsetDateTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static OffsetDateTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static OffsetDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    public static OffsetDateTime of(LocalDate date, LocalTime time, ZoneOffset offset2) {
        return new OffsetDateTime(LocalDateTime.of(date, time), offset2);
    }

    public static OffsetDateTime of(LocalDateTime dateTime2, ZoneOffset offset2) {
        return new OffsetDateTime(dateTime2, offset2);
    }

    public static OffsetDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset2) {
        return new OffsetDateTime(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond), offset2);
    }

    public static OffsetDateTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        ZoneOffset offset2 = zone.getRules().getOffset(instant);
        return new OffsetDateTime(LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), offset2), offset2);
    }

    public static OffsetDateTime from(TemporalAccessor temporal) {
        if (temporal instanceof OffsetDateTime) {
            return (OffsetDateTime) temporal;
        }
        try {
            ZoneOffset offset2 = ZoneOffset.from(temporal);
            LocalDate date = (LocalDate) temporal.query(TemporalQueries.localDate());
            LocalTime time = (LocalTime) temporal.query(TemporalQueries.localTime());
            if (date == null || time == null) {
                return ofInstant(Instant.from(temporal), offset2);
            }
            return of(date, time, offset2);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static OffsetDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static OffsetDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (OffsetDateTime) formatter.parse(text, OffsetDateTime$$ExternalSyntheticLambda0.INSTANCE);
    }

    private OffsetDateTime(LocalDateTime dateTime2, ZoneOffset offset2) {
        this.dateTime = (LocalDateTime) Objects.requireNonNull(dateTime2, "dateTime");
        this.offset = (ZoneOffset) Objects.requireNonNull(offset2, "offset");
    }

    private OffsetDateTime with(LocalDateTime dateTime2, ZoneOffset offset2) {
        if (this.dateTime != dateTime2 || !this.offset.equals(offset2)) {
            return new OffsetDateTime(dateTime2, offset2);
        }
        return this;
    }

    public boolean isSupported(TemporalField field) {
        return (field instanceof ChronoField) || (field != null && field.isSupportedBy(this));
    }

    public boolean isSupported(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            if (unit != ChronoUnit.FOREVER) {
                return true;
            }
            return false;
        } else if (unit == null || !unit.isSupportedBy(this)) {
            return false;
        } else {
            return true;
        }
    }

    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.rangeRefinedBy(this);
        }
        if (field == ChronoField.INSTANT_SECONDS || field == ChronoField.OFFSET_SECONDS) {
            return field.range();
        }
        return this.dateTime.range(field);
    }

    /* renamed from: j$.time.OffsetDateTime$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.INSTANT_SECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.OFFSET_SECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public int get(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return TemporalAccessor.CC.$default$get(this, field);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
            case 2:
                return getOffset().getTotalSeconds();
            default:
                return this.dateTime.get(field);
        }
    }

    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field.getFrom(this);
        }
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return toEpochSecond();
            case 2:
                return (long) getOffset().getTotalSeconds();
            default:
                return this.dateTime.getLong(field);
        }
    }

    public ZoneOffset getOffset() {
        return this.offset;
    }

    public OffsetDateTime withOffsetSameLocal(ZoneOffset offset2) {
        return with(this.dateTime, offset2);
    }

    public OffsetDateTime withOffsetSameInstant(ZoneOffset offset2) {
        if (offset2.equals(this.offset)) {
            return this;
        }
        return new OffsetDateTime(this.dateTime.plusSeconds((long) (offset2.getTotalSeconds() - this.offset.getTotalSeconds())), offset2);
    }

    public LocalDateTime toLocalDateTime() {
        return this.dateTime;
    }

    public LocalDate toLocalDate() {
        return this.dateTime.toLocalDate();
    }

    public int getYear() {
        return this.dateTime.getYear();
    }

    public int getMonthValue() {
        return this.dateTime.getMonthValue();
    }

    public Month getMonth() {
        return this.dateTime.getMonth();
    }

    public int getDayOfMonth() {
        return this.dateTime.getDayOfMonth();
    }

    public int getDayOfYear() {
        return this.dateTime.getDayOfYear();
    }

    public DayOfWeek getDayOfWeek() {
        return this.dateTime.getDayOfWeek();
    }

    public LocalTime toLocalTime() {
        return this.dateTime.toLocalTime();
    }

    public int getHour() {
        return this.dateTime.getHour();
    }

    public int getMinute() {
        return this.dateTime.getMinute();
    }

    public int getSecond() {
        return this.dateTime.getSecond();
    }

    public int getNano() {
        return this.dateTime.getNano();
    }

    public OffsetDateTime with(TemporalAdjuster adjuster) {
        if ((adjuster instanceof LocalDate) || (adjuster instanceof LocalTime) || (adjuster instanceof LocalDateTime)) {
            return with(this.dateTime.with(adjuster), this.offset);
        }
        if (adjuster instanceof Instant) {
            return ofInstant((Instant) adjuster, this.offset);
        }
        if (adjuster instanceof ZoneOffset) {
            return with(this.dateTime, (ZoneOffset) adjuster);
        }
        if (adjuster instanceof OffsetDateTime) {
            return (OffsetDateTime) adjuster;
        }
        return (OffsetDateTime) adjuster.adjustInto(this);
    }

    public OffsetDateTime with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (OffsetDateTime) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return ofInstant(Instant.ofEpochSecond(newValue, (long) getNano()), this.offset);
            case 2:
                return with(this.dateTime, ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)));
            default:
                return with(this.dateTime.with(field, newValue), this.offset);
        }
    }

    public OffsetDateTime withYear(int year) {
        return with(this.dateTime.withYear(year), this.offset);
    }

    public OffsetDateTime withMonth(int month) {
        return with(this.dateTime.withMonth(month), this.offset);
    }

    public OffsetDateTime withDayOfMonth(int dayOfMonth) {
        return with(this.dateTime.withDayOfMonth(dayOfMonth), this.offset);
    }

    public OffsetDateTime withDayOfYear(int dayOfYear) {
        return with(this.dateTime.withDayOfYear(dayOfYear), this.offset);
    }

    public OffsetDateTime withHour(int hour) {
        return with(this.dateTime.withHour(hour), this.offset);
    }

    public OffsetDateTime withMinute(int minute) {
        return with(this.dateTime.withMinute(minute), this.offset);
    }

    public OffsetDateTime withSecond(int second) {
        return with(this.dateTime.withSecond(second), this.offset);
    }

    public OffsetDateTime withNano(int nanoOfSecond) {
        return with(this.dateTime.withNano(nanoOfSecond), this.offset);
    }

    public OffsetDateTime truncatedTo(TemporalUnit unit) {
        return with(this.dateTime.truncatedTo(unit), this.offset);
    }

    public OffsetDateTime plus(TemporalAmount amountToAdd) {
        return (OffsetDateTime) amountToAdd.addTo(this);
    }

    public OffsetDateTime plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(this.dateTime.plus(amountToAdd, unit), this.offset);
        }
        return (OffsetDateTime) unit.addTo(this, amountToAdd);
    }

    public OffsetDateTime plusYears(long years) {
        return with(this.dateTime.plusYears(years), this.offset);
    }

    public OffsetDateTime plusMonths(long months) {
        return with(this.dateTime.plusMonths(months), this.offset);
    }

    public OffsetDateTime plusWeeks(long weeks) {
        return with(this.dateTime.plusWeeks(weeks), this.offset);
    }

    public OffsetDateTime plusDays(long days) {
        return with(this.dateTime.plusDays(days), this.offset);
    }

    public OffsetDateTime plusHours(long hours) {
        return with(this.dateTime.plusHours(hours), this.offset);
    }

    public OffsetDateTime plusMinutes(long minutes) {
        return with(this.dateTime.plusMinutes(minutes), this.offset);
    }

    public OffsetDateTime plusSeconds(long seconds) {
        return with(this.dateTime.plusSeconds(seconds), this.offset);
    }

    public OffsetDateTime plusNanos(long nanos) {
        return with(this.dateTime.plusNanos(nanos), this.offset);
    }

    public OffsetDateTime minus(TemporalAmount amountToSubtract) {
        return (OffsetDateTime) amountToSubtract.subtractFrom(this);
    }

    public OffsetDateTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public OffsetDateTime minusYears(long years) {
        return years == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-years);
    }

    public OffsetDateTime minusMonths(long months) {
        return months == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-months);
    }

    public OffsetDateTime minusWeeks(long weeks) {
        return weeks == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeks);
    }

    public OffsetDateTime minusDays(long days) {
        return days == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-days);
    }

    public OffsetDateTime minusHours(long hours) {
        return hours == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-hours);
    }

    public OffsetDateTime minusMinutes(long minutes) {
        return minutes == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-minutes);
    }

    public OffsetDateTime minusSeconds(long seconds) {
        return seconds == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-seconds);
    }

    public OffsetDateTime minusNanos(long nanos) {
        return nanos == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-nanos);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.zone()) {
            return getOffset();
        }
        if (temporalQuery == TemporalQueries.zoneId()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.localDate()) {
            return toLocalDate();
        }
        if (temporalQuery == TemporalQueries.localTime()) {
            return toLocalTime();
        }
        if (temporalQuery == TemporalQueries.chronology()) {
            return IsoChronology.INSTANCE;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return ChronoUnit.NANOS;
        }
        return temporalQuery.queryFrom(this);
    }

    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, (long) getOffset().getTotalSeconds());
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        OffsetDateTime end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        return this.dateTime.until(end.withOffsetSameInstant(this.offset).dateTime, unit);
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public ZonedDateTime atZoneSameInstant(ZoneId zone) {
        return ZonedDateTime.ofInstant(this.dateTime, this.offset, zone);
    }

    public ZonedDateTime atZoneSimilarLocal(ZoneId zone) {
        return ZonedDateTime.ofLocal(this.dateTime, zone, this.offset);
    }

    public OffsetTime toOffsetTime() {
        return OffsetTime.of(this.dateTime.toLocalTime(), this.offset);
    }

    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.of(this.dateTime, this.offset);
    }

    public Instant toInstant() {
        return this.dateTime.toInstant(this.offset);
    }

    public long toEpochSecond() {
        return this.dateTime.toEpochSecond(this.offset);
    }

    public int compareTo(OffsetDateTime other) {
        int cmp = compareInstant(this, other);
        if (cmp == 0) {
            return toLocalDateTime().compareTo((ChronoLocalDateTime<?>) other.toLocalDateTime());
        }
        return cmp;
    }

    public boolean isAfter(OffsetDateTime other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec > otherEpochSec || (thisEpochSec == otherEpochSec && toLocalTime().getNano() > other.toLocalTime().getNano());
    }

    public boolean isBefore(OffsetDateTime other) {
        long thisEpochSec = toEpochSecond();
        long otherEpochSec = other.toEpochSecond();
        return thisEpochSec < otherEpochSec || (thisEpochSec == otherEpochSec && toLocalTime().getNano() < other.toLocalTime().getNano());
    }

    public boolean isEqual(OffsetDateTime other) {
        return toEpochSecond() == other.toEpochSecond() && toLocalTime().getNano() == other.toLocalTime().getNano();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OffsetDateTime)) {
            return false;
        }
        OffsetDateTime other = (OffsetDateTime) obj;
        if (!this.dateTime.equals(other.dateTime) || !this.offset.equals(other.offset)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.dateTime.hashCode() ^ this.offset.hashCode();
    }

    public String toString() {
        return this.dateTime.toString() + this.offset.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 10, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(ObjectOutput out) {
        this.dateTime.writeExternal(out);
        this.offset.writeExternal(out);
    }

    static OffsetDateTime readExternal(ObjectInput in) {
        return of(LocalDateTime.readExternal(in), ZoneOffset.readExternal(in));
    }
}
