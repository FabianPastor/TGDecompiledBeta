package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda12 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda12 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda12();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda12() {
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_YEAR, temporal.range(ChronoField.DAY_OF_YEAR).getMaximum());
    }
}
