package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.BiConsumer;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda0 INSTANCE = new IntPipeline$$ExternalSyntheticLambda0();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((IntSummaryStatistics) obj).combine((IntSummaryStatistics) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
