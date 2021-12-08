package j$.time.temporal;

import j$.time.format.ResolverStyle;
import j$.util.Objects;
import java.util.Locale;
import java.util.Map;

public interface TemporalField {
    <R extends Temporal> R adjustInto(R r, long j);

    TemporalUnit getBaseUnit();

    String getDisplayName(Locale locale);

    long getFrom(TemporalAccessor temporalAccessor);

    TemporalUnit getRangeUnit();

    boolean isDateBased();

    boolean isSupportedBy(TemporalAccessor temporalAccessor);

    boolean isTimeBased();

    ValueRange range();

    ValueRange rangeRefinedBy(TemporalAccessor temporalAccessor);

    TemporalAccessor resolve(Map<TemporalField, Long> map, TemporalAccessor temporalAccessor, ResolverStyle resolverStyle);

    String toString();

    /* renamed from: j$.time.temporal.TemporalField$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static String $default$getDisplayName(TemporalField _this, Locale locale) {
            Objects.requireNonNull(locale, "locale");
            return _this.toString();
        }

        public static TemporalAccessor $default$resolve(TemporalField _this, Map map, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
            return null;
        }
    }
}
