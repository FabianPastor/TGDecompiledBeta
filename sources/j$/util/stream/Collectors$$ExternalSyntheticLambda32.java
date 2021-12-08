package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda32 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda32 INSTANCE = new Collectors$$ExternalSyntheticLambda32();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda32() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return ((StringBuilder) obj).append((StringBuilder) obj2);
    }
}
