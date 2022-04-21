package j$.time.temporal;

import j$.time.LocalDate;
import j$.util.function.UnaryOperator;

public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda6 implements TemporalAdjuster {
    public final /* synthetic */ UnaryOperator f$0;

    public /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda6(UnaryOperator unaryOperator) {
        this.f$0 = unaryOperator;
    }

    public final Temporal adjustInto(Temporal temporal) {
        return temporal.with((LocalDate) this.f$0.apply(LocalDate.from(temporal)));
    }
}
