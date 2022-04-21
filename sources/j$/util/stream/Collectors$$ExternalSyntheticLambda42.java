package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda42 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda42 INSTANCE = new Collectors$$ExternalSyntheticLambda42();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda42() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$summingDouble$20((double[]) obj, (double[]) obj2);
    }
}
