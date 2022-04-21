package j$.util.stream;

import j$.util.function.LongBinaryOperator;

public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda5 implements LongBinaryOperator {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda5 INSTANCE = new LongPipeline$$ExternalSyntheticLambda5();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda5() {
    }

    public final long applyAsLong(long j, long j2) {
        return Math.max(j, j2);
    }
}
