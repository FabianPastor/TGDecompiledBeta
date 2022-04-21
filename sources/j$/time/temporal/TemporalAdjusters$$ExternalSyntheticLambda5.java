package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda5 implements TemporalAdjuster {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda5(int i, int i2) {
        this.f$0 = i;
        this.f$1 = i2;
    }

    public final Temporal adjustInto(Temporal temporal) {
        return TemporalAdjusters.lambda$dayOfWeekInMonth$8(this.f$0, this.f$1, temporal);
    }
}
