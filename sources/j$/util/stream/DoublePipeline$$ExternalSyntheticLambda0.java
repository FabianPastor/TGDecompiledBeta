package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.function.BiConsumer;

public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda0 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda0();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((DoubleSummaryStatistics) obj).combine((DoubleSummaryStatistics) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
