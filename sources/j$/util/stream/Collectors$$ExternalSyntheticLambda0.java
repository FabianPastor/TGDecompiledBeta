package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ BiConsumer f$0;
    public final /* synthetic */ Function f$1;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda0(BiConsumer biConsumer, Function function) {
        this.f$0 = biConsumer;
        this.f$1 = function;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$0.accept(obj, this.f$1.apply(obj2));
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
