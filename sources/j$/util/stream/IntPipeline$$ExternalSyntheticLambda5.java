package j$.util.stream;

import j$.util.function.IntBinaryOperator;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda5 implements IntBinaryOperator {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda5 INSTANCE = new IntPipeline$$ExternalSyntheticLambda5();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda5() {
    }

    public final int applyAsInt(int i, int i2) {
        return Math.min(i, i2);
    }
}
