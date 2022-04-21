package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda44 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda44 INSTANCE = new Collectors$$ExternalSyntheticLambda44();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda44() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$averagingInt$24((long[]) obj, (long[]) obj2);
    }
}
