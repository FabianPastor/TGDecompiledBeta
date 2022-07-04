package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda9 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda9 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda9();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda9() {
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_YEAR, 1).plus(1, ChronoUnit.YEARS);
    }
}
