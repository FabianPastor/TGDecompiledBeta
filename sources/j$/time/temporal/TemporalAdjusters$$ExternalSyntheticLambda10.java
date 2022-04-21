package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda10 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda10 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda10();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda10() {
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_YEAR, 1);
    }
}
