package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.function.BiConsumer;
import j$.util.function.ToLongFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda15 implements BiConsumer {
    public final /* synthetic */ ToLongFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda15(ToLongFunction toLongFunction) {
        this.f$0 = toLongFunction;
    }

    public final void accept(Object obj, Object obj2) {
        ((LongSummaryStatistics) obj).accept(this.f$0.applyAsLong(obj2));
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
