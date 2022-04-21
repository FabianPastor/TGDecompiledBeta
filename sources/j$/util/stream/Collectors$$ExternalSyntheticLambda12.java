package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.BiConsumer;
import j$.util.function.ToIntFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda12 implements BiConsumer {
    public final /* synthetic */ ToIntFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda12(ToIntFunction toIntFunction) {
        this.f$0 = toIntFunction;
    }

    public final void accept(Object obj, Object obj2) {
        ((IntSummaryStatistics) obj).accept(this.f$0.applyAsInt(obj2));
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
