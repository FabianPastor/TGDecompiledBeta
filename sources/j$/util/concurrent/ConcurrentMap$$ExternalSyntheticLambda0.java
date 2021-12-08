package j$.util.concurrent;

import j$.util.concurrent.ConcurrentMap;
import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class ConcurrentMap$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ ConcurrentMap f$0;
    public final /* synthetic */ BiFunction f$1;

    public /* synthetic */ ConcurrentMap$$ExternalSyntheticLambda0(ConcurrentMap concurrentMap, BiFunction biFunction) {
        this.f$0 = concurrentMap;
        this.f$1 = biFunction;
    }

    public final void accept(Object obj, Object obj2) {
        ConcurrentMap.CC.lambda$replaceAll$0(this.f$0, this.f$1, obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
