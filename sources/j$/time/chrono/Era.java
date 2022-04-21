package j$.time.chrono;

import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import java.util.Locale;

public interface Era extends TemporalAccessor, TemporalAdjuster {
    Temporal adjustInto(Temporal temporal);

    int get(TemporalField temporalField);

    String getDisplayName(TextStyle textStyle, Locale locale);

    long getLong(TemporalField temporalField);

    int getValue();

    boolean isSupported(TemporalField temporalField);

    <R> R query(TemporalQuery<R> temporalQuery);

    ValueRange range(TemporalField temporalField);

    /* renamed from: j$.time.chrono.Era$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static boolean $default$isSupported(Era _this, TemporalField field) {
            if (field instanceof ChronoField) {
                if (field == ChronoField.ERA) {
                    return true;
                }
                return false;
            } else if (field == null || !field.isSupportedBy(_this)) {
                return false;
            } else {
                return true;
            }
        }

        public static ValueRange $default$range(Era _this, TemporalField field) {
            return TemporalAccessor.CC.$default$range(_this, field);
        }

        public static int $default$get(Era _this, TemporalField field) {
            if (field == ChronoField.ERA) {
                return _this.getValue();
            }
            return TemporalAccessor.CC.$default$get(_this, field);
        }

        public static long $default$getLong(Era _this, TemporalField field) {
            if (field == ChronoField.ERA) {
                return (long) _this.getValue();
            }
            if (!(field instanceof ChronoField)) {
                return field.getFrom(_this);
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }

        public static <R> Object $default$query(Era _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.ERAS;
            }
            return TemporalAccessor.CC.$default$query(_this, temporalQuery);
        }

        public static Temporal $default$adjustInto(Era _this, Temporal temporal) {
            return temporal.with(ChronoField.ERA, (long) _this.getValue());
        }

        public static String $default$getDisplayName(Era _this, TextStyle style, Locale locale) {
            return new DateTimeFormatterBuilder().appendText((TemporalField) ChronoField.ERA, style).toFormatter(locale).format(_this);
        }
    }
}
