package j$.time.temporal;

import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.Chronology;

public final class TemporalQueries {
    static final TemporalQuery<Chronology> CHRONO = TemporalQueries$$ExternalSyntheticLambda1.INSTANCE;
    static final TemporalQuery<LocalDate> LOCAL_DATE = TemporalQueries$$ExternalSyntheticLambda5.INSTANCE;
    static final TemporalQuery<LocalTime> LOCAL_TIME = TemporalQueries$$ExternalSyntheticLambda6.INSTANCE;
    static final TemporalQuery<ZoneOffset> OFFSET = TemporalQueries$$ExternalSyntheticLambda3.INSTANCE;
    static final TemporalQuery<TemporalUnit> PRECISION = TemporalQueries$$ExternalSyntheticLambda2.INSTANCE;
    static final TemporalQuery<ZoneId> ZONE = TemporalQueries$$ExternalSyntheticLambda4.INSTANCE;
    static final TemporalQuery<ZoneId> ZONE_ID = TemporalQueries$$ExternalSyntheticLambda0.INSTANCE;

    private TemporalQueries() {
    }

    public static TemporalQuery<ZoneId> zoneId() {
        return ZONE_ID;
    }

    public static TemporalQuery<Chronology> chronology() {
        return CHRONO;
    }

    public static TemporalQuery<TemporalUnit> precision() {
        return PRECISION;
    }

    public static TemporalQuery<ZoneId> zone() {
        return ZONE;
    }

    public static TemporalQuery<ZoneOffset> offset() {
        return OFFSET;
    }

    public static TemporalQuery<LocalDate> localDate() {
        return LOCAL_DATE;
    }

    public static TemporalQuery<LocalTime> localTime() {
        return LOCAL_TIME;
    }

    static /* synthetic */ ZoneId lambda$static$0(TemporalAccessor temporal) {
        return (ZoneId) temporal.query(ZONE_ID);
    }

    static /* synthetic */ Chronology lambda$static$1(TemporalAccessor temporal) {
        return (Chronology) temporal.query(CHRONO);
    }

    static /* synthetic */ TemporalUnit lambda$static$2(TemporalAccessor temporal) {
        return (TemporalUnit) temporal.query(PRECISION);
    }

    static /* synthetic */ ZoneOffset lambda$static$3(TemporalAccessor temporal) {
        if (temporal.isSupported(ChronoField.OFFSET_SECONDS)) {
            return ZoneOffset.ofTotalSeconds(temporal.get(ChronoField.OFFSET_SECONDS));
        }
        return null;
    }

    static /* synthetic */ ZoneId lambda$static$4(TemporalAccessor temporal) {
        ZoneId zone = (ZoneId) temporal.query(ZONE_ID);
        return zone != null ? zone : (ZoneId) temporal.query(OFFSET);
    }

    static /* synthetic */ LocalDate lambda$static$5(TemporalAccessor temporal) {
        if (temporal.isSupported(ChronoField.EPOCH_DAY)) {
            return LocalDate.ofEpochDay(temporal.getLong(ChronoField.EPOCH_DAY));
        }
        return null;
    }

    static /* synthetic */ LocalTime lambda$static$6(TemporalAccessor temporal) {
        if (temporal.isSupported(ChronoField.NANO_OF_DAY)) {
            return LocalTime.ofNanoOfDay(temporal.getLong(ChronoField.NANO_OF_DAY));
        }
        return null;
    }
}
