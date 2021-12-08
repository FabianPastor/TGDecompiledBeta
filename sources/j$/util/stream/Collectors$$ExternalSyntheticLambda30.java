package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda30 implements BinaryOperator {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda30 INSTANCE = new Collectors$$ExternalSyntheticLambda30();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda30() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return Collectors.lambda$throwingMerger$0(obj, obj2);
    }
}
