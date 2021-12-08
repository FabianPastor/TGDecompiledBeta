package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.function.BiConsumer;

public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda0 INSTANCE = new LongPipeline$$ExternalSyntheticLambda0();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((LongSummaryStatistics) obj).combine((LongSummaryStatistics) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
