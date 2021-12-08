package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda46 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda46 INSTANCE = new Collectors$$ExternalSyntheticLambda46();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda46() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$summingLong$16((long[]) obj, (long[]) obj2);
    }
}
