package j$.time.temporal;

import j$.time.DateTimeException;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.format.ResolverStyle;
import java.util.Map;

public final class JulianFields {
    public static final TemporalField JULIAN_DAY = Field.JULIAN_DAY;
    private static final long JULIAN_DAY_OFFSET = 2440588;
    public static final TemporalField MODIFIED_JULIAN_DAY = Field.MODIFIED_JULIAN_DAY;
    public static final TemporalField RATA_DIE = Field.RATA_DIE;

    private JulianFields() {
        throw new AssertionError("Not instantiable");
    }

    private enum Field implements TemporalField {
        JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 2440588),
        MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587),
        RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163);
        
        private static final long serialVersionUID = -7501623920830201812L;
        private final transient TemporalUnit baseUnit;
        private final transient String name;
        private final transient long offset;
        private final transient ValueRange range;
        private final transient TemporalUnit rangeUnit;

        private Field(String name2, TemporalUnit baseUnit2, TemporalUnit rangeUnit2, long offset2) {
            this.name = name2;
            this.baseUnit = baseUnit2;
            this.rangeUnit = rangeUnit2;
            this.range = ValueRange.of(-365243219162L + offset2, 365241780471L + offset2);
            this.offset = offset2;
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
            return temporal.isSupported(ChronoField.EPOCH_DAY);
        }

        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            if (isSupportedBy(temporal)) {
                return range();
            }
            throw new DateTimeException("Unsupported field: " + this);
        }

        public long getFrom(TemporalAccessor temporal) {
            return temporal.getLong(ChronoField.EPOCH_DAY) + this.offset;
        }

        public <R extends Temporal> R adjustInto(R temporal, long newValue) {
            if (range().isValidValue(newValue)) {
                return temporal.with(ChronoField.EPOCH_DAY, Instant$$ExternalSyntheticBackport0.m(newValue, this.offset));
            }
            throw new DateTimeException("Invalid value: " + this.name + " " + newValue);
        }

        public ChronoLocalDate resolve(Map<TemporalField, Long> fieldValues, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            long value = fieldValues.remove(this).longValue();
            Chronology chrono = Chronology.CC.from(partialTemporal);
            if (resolverStyle == ResolverStyle.LENIENT) {
                return chrono.dateEpochDay(Instant$$ExternalSyntheticBackport0.m(value, this.offset));
            }
            range().checkValidValue(value, this);
            return chrono.dateEpochDay(value - this.offset);
        }

        public String toString() {
            return this.name;
        }
    }
}
