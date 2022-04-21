package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda3 implements BinaryOperator {
    public final /* synthetic */ BiConsumer f$0;

    public /* synthetic */ DoublePipeline$$ExternalSyntheticLambda3(BiConsumer biConsumer) {
        this.f$0 = biConsumer;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return this.f$0.accept(obj, obj2);
    }
}
