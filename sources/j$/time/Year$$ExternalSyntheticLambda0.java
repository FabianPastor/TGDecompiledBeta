package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;

public final /* synthetic */ class Year$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ Year$$ExternalSyntheticLambda0 INSTANCE = new Year$$ExternalSyntheticLambda0();

    private /* synthetic */ Year$$ExternalSyntheticLambda0() {
    }

    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return Year.from(temporalAccessor);
    }
}
