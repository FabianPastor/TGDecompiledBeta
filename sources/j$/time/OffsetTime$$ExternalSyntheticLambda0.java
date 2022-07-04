package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;

public final /* synthetic */ class OffsetTime$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ OffsetTime$$ExternalSyntheticLambda0 INSTANCE = new OffsetTime$$ExternalSyntheticLambda0();

    private /* synthetic */ OffsetTime$$ExternalSyntheticLambda0() {
    }

    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return OffsetTime.from(temporalAccessor);
    }
}
