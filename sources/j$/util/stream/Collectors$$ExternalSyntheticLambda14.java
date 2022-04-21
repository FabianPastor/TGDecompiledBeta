package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.ToIntFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda14 implements BiConsumer {
    public final /* synthetic */ ToIntFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda14(ToIntFunction toIntFunction) {
        this.f$0 = toIntFunction;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$averagingInt$23(this.f$0, (long[]) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
