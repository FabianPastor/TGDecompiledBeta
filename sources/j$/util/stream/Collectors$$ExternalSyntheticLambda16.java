package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.ToLongFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda16 implements BiConsumer {
    public final /* synthetic */ ToLongFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda16(ToLongFunction toLongFunction) {
        this.f$0 = toLongFunction;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$averagingLong$27(this.f$0, (long[]) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
