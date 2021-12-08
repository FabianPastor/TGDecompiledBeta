package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda43 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda43 INSTANCE = new Collectors$$ExternalSyntheticLambda43();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda43() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$summingInt$12((int[]) obj, (int[]) obj2);
    }
}
