package j$.time.temporal;

import j$.time.DateTimeException;
import j$.util.Objects;

public interface TemporalAccessor {
    int get(TemporalField temporalField);

    long getLong(TemporalField temporalField);

    boolean isSupported(TemporalField temporalField);

    <R> R query(TemporalQuery<R> temporalQuery);

    ValueRange range(TemporalField temporalField);

    /* renamed from: j$.time.temporal.TemporalAccessor$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static ValueRange $default$range(TemporalAccessor _this, TemporalField field) {
            if (!(field instanceof ChronoField)) {
                Objects.requireNonNull(field, "field");
                return field.rangeRefinedBy(_this);
            } else if (_this.isSupported(field)) {
                return field.range();
            } else {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }

        public static int $default$get(TemporalAccessor _this, TemporalField field) {
            ValueRange range = _this.range(field);
            if (range.isIntValue()) {
                long value = _this.getLong(field);
                if (range.isValidValue(value)) {
                    return (int) value;
                }
                throw new DateTimeException("Invalid value for " + field + " (valid values " + range + "): " + value);
            }
            throw new UnsupportedTemporalTypeException("Invalid field " + field + " for get() method, use getLong() instead");
        }

        public static <R> Object $default$query(TemporalAccessor _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.precision()) {
                return null;
            }
            return temporalQuery.queryFrom(_this);
        }
    }
}
