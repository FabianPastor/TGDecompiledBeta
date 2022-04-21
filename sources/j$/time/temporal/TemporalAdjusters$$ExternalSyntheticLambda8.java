package j$.time.temporal;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda8 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda8 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda8();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda8() {
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.DAY_OF_MONTH, 1).plus(1, ChronoUnit.MONTHS);
    }
}
