package j$.time;

import j$.time.chrono.ChronoZonedDateTime;
import j$.time.chrono.Chronology;
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
import j$.time.zone.ZoneRules;
import j$.util.Objects;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

public final class ZonedDateTime implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {
    private static final long serialVersionUID = -6260982410461394882L;
    private final LocalDateTime dateTime;
    private final ZoneOffset offset;
    private final ZoneId zone;

    public /* synthetic */ int compareTo(ChronoZonedDateTime chronoZonedDateTime) {
        return ChronoZonedDateTime.CC.$default$compareTo((ChronoZonedDateTime) this, chronoZonedDateTime);
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return compareTo((ChronoZonedDateTime) obj);
    }

    public /* synthetic */ Chronology getChronology() {
        return ChronoZonedDateTime.CC.$default$getChronology(this);
    }

    public /* synthetic */ boolean isAfter(ChronoZonedDateTime chronoZonedDateTime) {
        return ChronoZonedDateTime.CC.$default$isAfter(this, chronoZonedDateTime);
    }

    public /* synthetic */ boolean isBefore(ChronoZonedDateTime chronoZonedDateTime) {
        return ChronoZonedDateTime.CC.$default$isBefore(this, chronoZonedDateTime);
    }

    public /* synthetic */ boolean isEqual(ChronoZonedDateTime chronoZonedDateTime) {
        return ChronoZonedDateTime.CC.$default$isEqual(this, chronoZonedDateTime);
    }

    public /* synthetic */ long toEpochSecond() {
        return ChronoZonedDateTime.CC.$default$toEpochSecond(this);
    }

    public /* synthetic */ Instant toInstant() {
        return ChronoZonedDateTime.CC.$default$toInstant(this);
    }

    public static ZonedDateTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static ZonedDateTime now(ZoneId zone2) {
        return now(Clock.system(zone2));
    }

    public static ZonedDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return ofInstant(clock.instant(), clock.getZone());
    }

    public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone2) {
        return of(LocalDateTime.of(date, time), zone2);
    }

    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zone2) {
        return ofLocal(localDateTime, zone2, (ZoneOffset) null);
    }

    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zone2) {
        return ofLocal(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond), zone2, (ZoneOffset) null);
    }

    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneId zone2, ZoneOffset preferredOffset) {
        ZoneOffset offset2;
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(zone2, "zone");
        if (zone2 instanceof ZoneOffset) {
            return new ZonedDateTime(localDateTime, (ZoneOffset) zone2, zone2);
        }
        ZoneRules rules = zone2.getRules();
        List<ZoneOffset> validOffsets = rules.getValidOffsets(localDateTime);
        if (validOffsets.size() == 1) {
            offset2 = validOffsets.get(0);
        } else if (validOffsets.size() == 0) {
            ZoneOffsetTransition trans = rules.getTransition(localDateTime);
            localDateTime = localDateTime.plusSeconds(trans.getDuration().getSeconds());
            offset2 = trans.getOffsetAfter();
        } else if (preferredOffset == null || !validOffsets.contains(preferredOffset)) {
            offset2 = (ZoneOffset) Objects.requireNonNull(validOffsets.get(0), "offset");
        } else {
            offset2 = preferredOffset;
        }
        return new ZonedDateTime(localDateTime, offset2, zone2);
    }

    public static ZonedDateTime ofInstant(Instant instant, ZoneId zone2) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone2, "zone");
        return create(instant.getEpochSecond(), instant.getNano(), zone2);
    }

    public static ZonedDateTime ofInstant(LocalDateTime localDateTime, ZoneOffset offset2, ZoneId zone2) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset2, "offset");
        Objects.requireNonNull(zone2, "zone");
        if (zone2.getRules().isValidOffset(localDateTime, offset2)) {
            return new ZonedDateTime(localDateTime, offset2, zone2);
        }
        return create(localDateTime.toEpochSecond(offset2), localDateTime.getNano(), zone2);
    }

    private static ZonedDateTime create(long epochSecond, int nanoOfSecond, ZoneId zone2) {
        ZoneOffset offset2 = zone2.getRules().getOffset(Instant.ofEpochSecond(epochSecond, (long) nanoOfSecond));
        return new ZonedDateTime(LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset2), offset2, zone2);
    }

    public static ZonedDateTime ofStrict(LocalDateTime localDateTime, ZoneOffset offset2, ZoneId zone2) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset2, "offset");
        Objects.requireNonNull(zone2, "zone");
        ZoneRules rules = zone2.getRules();
        if (rules.isValidOffset(localDateTime, offset2)) {
            return new ZonedDateTime(localDateTime, offset2, zone2);
        }
        ZoneOffsetTransition trans = rules.getTransition(localDateTime);
        if (trans == null || !trans.isGap()) {
            throw new DateTimeException("ZoneOffset '" + offset2 + "' is not valid for LocalDateTime '" + localDateTime + "' in zone '" + zone2 + "'");
        }
        throw new DateTimeException("LocalDateTime '" + localDateTime + "' does not exist in zone '" + zone2 + "' due to a gap in the local time-line, typically caused by daylight savings");
    }

    private static ZonedDateTime ofLenient(LocalDateTime localDateTime, ZoneOffset offset2, ZoneId zone2) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        Objects.requireNonNull(offset2, "offset");
        Objects.requireNonNull(zone2, "zone");
        if (!(zone2 instanceof ZoneOffset) || offset2.equals(zone2)) {
            return new ZonedDateTime(localDateTime, offset2, zone2);
        }
        throw new IllegalArgumentException("ZoneId must match ZoneOffset");
    }

    public static ZonedDateTime from(TemporalAccessor temporal) {
        if (temporal instanceof ZonedDateTime) {
            return (ZonedDateTime) temporal;
        }
        try {
            ZoneId zone2 = ZoneId.from(temporal);
            if (temporal.isSupported(ChronoField.INSTANT_SECONDS)) {
                return create(temporal.getLong(ChronoField.INSTANT_SECONDS), temporal.get(ChronoField.NANO_OF_SECOND), zone2);
            }
            return of(LocalDate.from(temporal), LocalTime.from(temporal), zone2);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain ZonedDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static ZonedDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public static ZonedDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (ZonedDateTime) formatter.parse(text, ZonedDateTime$$ExternalSyntheticLambda0.INSTANCE);
    }

    private ZonedDateTime(LocalDateTime dateTime2, ZoneOffset offset2, ZoneId zone2) {
        this.dateTime = dateTime2;
        this.offset = offset2;
        this.zone = zone2;
    }

    private ZonedDateTime resolveLocal(LocalDateTime newDateTime) {
        return ofLocal(newDateTime, this.zone, this.offset);
    }

    private ZonedDateTime resolveInstant(LocalDateTime newDateTime) {
        return ofInstant(newDateTime, this.offset, this.zone);
    }

    private ZonedDateTime resolveOffset(ZoneOffset offset2) {
        if (offset2.equals(this.offset) || !this.zone.getRules().isValidOffset(this.dateTime, offset2)) {
            return this;
        }
        return new ZonedDateTime(this.dateTime, offset2, this.zone);
    }

    public boolean isSupported(TemporalField field) {
        return (field instanceof ChronoField) || (field != null && field.isSupportedBy(this));
    }

    public boolean isSupported(TemporalUnit unit) {
        return ChronoZonedDateTime.CC.$default$isSupported(this, unit);
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

    /* renamed from: j$.time.ZonedDateTime$1  reason: invalid class name */
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
            return ChronoZonedDateTime.CC.$default$get(this, field);
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

    public ZonedDateTime withEarlierOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(this.dateTime);
        if (trans != null && trans.isOverlap()) {
            ZoneOffset earlierOffset = trans.getOffsetBefore();
            if (!earlierOffset.equals(this.offset)) {
                return new ZonedDateTime(this.dateTime, earlierOffset, this.zone);
            }
        }
        return this;
    }

    public ZonedDateTime withLaterOffsetAtOverlap() {
        ZoneOffsetTransition trans = getZone().getRules().getTransition(toLocalDateTime());
        if (trans != null) {
            ZoneOffset laterOffset = trans.getOffsetAfter();
            if (!laterOffset.equals(this.offset)) {
                return new ZonedDateTime(this.dateTime, laterOffset, this.zone);
            }
        }
        return this;
    }

    public ZoneId getZone() {
        return this.zone;
    }

    public ZonedDateTime withZoneSameLocal(ZoneId zone2) {
        Objects.requireNonNull(zone2, "zone");
        return this.zone.equals(zone2) ? this : ofLocal(this.dateTime, zone2, this.offset);
    }

    public ZonedDateTime withZoneSameInstant(ZoneId zone2) {
        Objects.requireNonNull(zone2, "zone");
        if (this.zone.equals(zone2)) {
            return this;
        }
        return create(this.dateTime.toEpochSecond(this.offset), this.dateTime.getNano(), zone2);
    }

    public ZonedDateTime withFixedOffsetZone() {
        if (this.zone.equals(this.offset)) {
            return this;
        }
        LocalDateTime localDateTime = this.dateTime;
        ZoneOffset zoneOffset = this.offset;
        return new ZonedDateTime(localDateTime, zoneOffset, zoneOffset);
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

    public ZonedDateTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return resolveLocal(LocalDateTime.of((LocalDate) adjuster, this.dateTime.toLocalTime()));
        }
        if (adjuster instanceof LocalTime) {
            return resolveLocal(LocalDateTime.of(this.dateTime.toLocalDate(), (LocalTime) adjuster));
        }
        if (adjuster instanceof LocalDateTime) {
            return resolveLocal((LocalDateTime) adjuster);
        }
        if (adjuster instanceof OffsetDateTime) {
            OffsetDateTime odt = (OffsetDateTime) adjuster;
            return ofLocal(odt.toLocalDateTime(), this.zone, odt.getOffset());
        } else if (adjuster instanceof Instant) {
            Instant instant = (Instant) adjuster;
            return create(instant.getEpochSecond(), instant.getNano(), this.zone);
        } else if (adjuster instanceof ZoneOffset) {
            return resolveOffset((ZoneOffset) adjuster);
        } else {
            return (ZonedDateTime) adjuster.adjustInto(this);
        }
    }

    public ZonedDateTime with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField)) {
            return (ZonedDateTime) field.adjustInto(this, newValue);
        }
        ChronoField f = (ChronoField) field;
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
            case 1:
                return create(newValue, getNano(), this.zone);
            case 2:
                return resolveOffset(ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)));
            default:
                return resolveLocal(this.dateTime.with(field, newValue));
        }
    }

    public ZonedDateTime withYear(int year) {
        return resolveLocal(this.dateTime.withYear(year));
    }

    public ZonedDateTime withMonth(int month) {
        return resolveLocal(this.dateTime.withMonth(month));
    }

    public ZonedDateTime withDayOfMonth(int dayOfMonth) {
        return resolveLocal(this.dateTime.withDayOfMonth(dayOfMonth));
    }

    public ZonedDateTime withDayOfYear(int dayOfYear) {
        return resolveLocal(this.dateTime.withDayOfYear(dayOfYear));
    }

    public ZonedDateTime withHour(int hour) {
        return resolveLocal(this.dateTime.withHour(hour));
    }

    public ZonedDateTime withMinute(int minute) {
        return resolveLocal(this.dateTime.withMinute(minute));
    }

    public ZonedDateTime withSecond(int second) {
        return resolveLocal(this.dateTime.withSecond(second));
    }

    public ZonedDateTime withNano(int nanoOfSecond) {
        return resolveLocal(this.dateTime.withNano(nanoOfSecond));
    }

    public ZonedDateTime truncatedTo(TemporalUnit unit) {
        return resolveLocal(this.dateTime.truncatedTo(unit));
    }

    public ZonedDateTime plus(TemporalAmount amountToAdd) {
        if (amountToAdd instanceof Period) {
            return resolveLocal(this.dateTime.plus((TemporalAmount) (Period) amountToAdd));
        }
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        return (ZonedDateTime) amountToAdd.addTo(this);
    }

    public ZonedDateTime plus(long amountToAdd, TemporalUnit unit) {
        if (!(unit instanceof ChronoUnit)) {
            return (ZonedDateTime) unit.addTo(this, amountToAdd);
        }
        if (unit.isDateBased()) {
            return resolveLocal(this.dateTime.plus(amountToAdd, unit));
        }
        return resolveInstant(this.dateTime.plus(amountToAdd, unit));
    }

    public ZonedDateTime plusYears(long years) {
        return resolveLocal(this.dateTime.plusYears(years));
    }

    public ZonedDateTime plusMonths(long months) {
        return resolveLocal(this.dateTime.plusMonths(months));
    }

    public ZonedDateTime plusWeeks(long weeks) {
        return resolveLocal(this.dateTime.plusWeeks(weeks));
    }

    public ZonedDateTime plusDays(long days) {
        return resolveLocal(this.dateTime.plusDays(days));
    }

    public ZonedDateTime plusHours(long hours) {
        return resolveInstant(this.dateTime.plusHours(hours));
    }

    public ZonedDateTime plusMinutes(long minutes) {
        return resolveInstant(this.dateTime.plusMinutes(minutes));
    }

    public ZonedDateTime plusSeconds(long seconds) {
        return resolveInstant(this.dateTime.plusSeconds(seconds));
    }

    public ZonedDateTime plusNanos(long nanos) {
        return resolveInstant(this.dateTime.plusNanos(nanos));
    }

    public ZonedDateTime minus(TemporalAmount amountToSubtract) {
        if (amountToSubtract instanceof Period) {
            return resolveLocal(this.dateTime.minus((TemporalAmount) (Period) amountToSubtract));
        }
        Objects.requireNonNull(amountToSubtract, "amountToSubtract");
        return (ZonedDateTime) amountToSubtract.subtractFrom(this);
    }

    public ZonedDateTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1, unit) : plus(-amountToSubtract, unit);
    }

    public ZonedDateTime minusYears(long years) {
        return years == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1) : plusYears(-years);
    }

    public ZonedDateTime minusMonths(long months) {
        return months == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1) : plusMonths(-months);
    }

    public ZonedDateTime minusWeeks(long weeks) {
        return weeks == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1) : plusWeeks(-weeks);
    }

    public ZonedDateTime minusDays(long days) {
        return days == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1) : plusDays(-days);
    }

    public ZonedDateTime minusHours(long hours) {
        return hours == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1) : plusHours(-hours);
    }

    public ZonedDateTime minusMinutes(long minutes) {
        return minutes == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1) : plusMinutes(-minutes);
    }

    public ZonedDateTime minusSeconds(long seconds) {
        return seconds == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1) : plusSeconds(-seconds);
    }

    public ZonedDateTime minusNanos(long nanos) {
        return nanos == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1) : plusNanos(-nanos);
    }

    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.localDate()) {
            return toLocalDate();
        }
        return ChronoZonedDateTime.CC.$default$query(this, temporalQuery);
    }

    public long until(Temporal endExclusive, TemporalUnit unit) {
        ZonedDateTime end = from(endExclusive);
        if (!(unit instanceof ChronoUnit)) {
            return unit.between(this, end);
        }
        ZonedDateTime end2 = end.withZoneSameInstant(this.zone);
        if (unit.isDateBased()) {
            return this.dateTime.until(end2.dateTime, unit);
        }
        return toOffsetDateTime().until(end2.toOffsetDateTime(), unit);
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public OffsetDateTime toOffsetDateTime() {
        return OffsetDateTime.of(this.dateTime, this.offset);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ZonedDateTime)) {
            return false;
        }
        ZonedDateTime other = (ZonedDateTime) obj;
        if (!this.dateTime.equals(other.dateTime) || !this.offset.equals(other.offset) || !this.zone.equals(other.zone)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.dateTime.hashCode() ^ this.offset.hashCode()) ^ Integer.rotateLeft(this.zone.hashCode(), 3);
    }

    public String toString() {
        String str = this.dateTime.toString() + this.offset.toString();
        if (this.offset == this.zone) {
            return str;
        }
        return str + '[' + this.zone.toString() + ']';
    }

    private Object writeReplace() {
        return new Ser((byte) 6, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* access modifiers changed from: package-private */
    public void writeExternal(DataOutput out) {
        this.dateTime.writeExternal(out);
        this.offset.writeExternal(out);
        this.zone.write(out);
    }

    static ZonedDateTime readExternal(ObjectInput in) {
        return ofLenient(LocalDateTime.readExternal(in), ZoneOffset.readExternal(in), (ZoneId) Ser.read(in));
    }
}
