package j$.util.stream;

import j$.util.function.IntBinaryOperator;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda4 implements IntBinaryOperator {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda4 INSTANCE = new IntPipeline$$ExternalSyntheticLambda4();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda4() {
    }

    public final int applyAsInt(int i, int i2) {
        return Math.max(i, i2);
    }
}
