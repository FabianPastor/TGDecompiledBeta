package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda11 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda11 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda11();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda11() {
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
    }
}
