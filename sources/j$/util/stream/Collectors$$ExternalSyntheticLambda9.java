package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.function.BiConsumer;
import j$.util.function.ToDoubleFunction;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda9 implements BiConsumer {
    public final /* synthetic */ ToDoubleFunction f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda9(ToDoubleFunction toDoubleFunction) {
        this.f$0 = toDoubleFunction;
    }

    public final void accept(Object obj, Object obj2) {
        ((DoubleSummaryStatistics) obj).accept(this.f$0.applyAsDouble(obj2));
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
