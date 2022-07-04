package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;

public final /* synthetic */ class Instant$$ExternalSyntheticLambda1 implements TemporalQuery {
    public static final /* synthetic */ Instant$$ExternalSyntheticLambda1 INSTANCE = new Instant$$ExternalSyntheticLambda1();

    private /* synthetic */ Instant$$ExternalSyntheticLambda1() {
    }

    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return Instant.from(temporalAccessor);
    }
}
