package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.ToDoubleFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda10 implements BiConsumer {
    public final /* synthetic */ ToDoubleFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda10(ToDoubleFunction toDoubleFunction) {
        this.f$0 = toDoubleFunction;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$averagingDouble$31(this.f$0, (double[]) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
