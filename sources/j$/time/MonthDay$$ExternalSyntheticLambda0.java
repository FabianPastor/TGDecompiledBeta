package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;

public final /* synthetic */ class MonthDay$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ MonthDay$$ExternalSyntheticLambda0 INSTANCE = new MonthDay$$ExternalSyntheticLambda0();

    private /* synthetic */ MonthDay$$ExternalSyntheticLambda0() {
    }

    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return MonthDay.from(temporalAccessor);
    }
}
