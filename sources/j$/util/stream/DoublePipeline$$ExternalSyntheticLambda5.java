package j$.util.stream;

import j$.util.function.DoubleBinaryOperator;

public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda5 implements DoubleBinaryOperator {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda5 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda5();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda5() {
    }

    public final double applyAsDouble(double d, double d2) {
        return Math.min(d, d2);
    }
}
